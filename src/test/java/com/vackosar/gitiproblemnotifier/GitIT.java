package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class GitIT {

    private static final File LOCAL = new File("tmp/local");
    private static final File REMOTE = new File("tmp/remote");
    private static final String FILENAME = "test.txt";
    private static final File FILE = new File(LOCAL.getPath() + "/" + FILENAME);
    private static final String REPODIRNAME = ".git";
    private static final String STEP_BACK = "HEAD";
    private static final String ALL_FILES = ".";
    private static final String MESSAGE = "test";
    private static final String REF_HEAD = "HEAD";
    private Git git;

    private enum branches {
        branch1,
        branch2,
        master;

        @Override
        public String toString() {
            return "refs/heads/" + super.name();
        }

        public static Object[] list() {
            return Stream.of(branches.values()).map(branches::toString).toArray();
        }
    }

    @Before
    public void setUp() throws IOException, GitAPIException {
        try {
            cleanUp();
        } catch (IOException e) {
            // do nothing
        }
        LOCAL.mkdir();
        REMOTE.mkdir();
        git = initialize();
    }

    @After
    public void cleanUp() throws IOException {
        if (git != null) {
            git.getRepository().close();
            git.close();
        }
        delete(LOCAL);
        delete(REMOTE);
    }

    @Test
    public void commitModifyAndReset() throws GitAPIException, IOException {
        commitFile();
        FILE.delete();
        git.reset().setRef(STEP_BACK).setMode(ResetCommand.ResetType.HARD).call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
    }

    @Test
    public void pushAndPull() throws GitAPIException, IOException, InterruptedException, URISyntaxException {
        commitFile();
        git.log();
        startRemoteDaemon();
        configureRemote(git);
        git.push().call();
        git.close();
        delete(LOCAL);
        git = initialize();
        configureRemote(git);
        git.pull().call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
        git.getRepository().close();
        git.close();
    }

    private void commitFile() throws GitAPIException, IOException {
        FILE.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
    }

    private Git initialize() throws GitAPIException {
        final Git git = Git.init().setDirectory(LOCAL).call();
        git.commit().setMessage(MESSAGE).call();
        assertArrayEquals(new String[]{REPODIRNAME}, LOCAL.list());
        return git;
    }

    @Test
    public void listBranches() throws GitAPIException {
        git.branchCreate().setName(branches.branch1.name()).call();
        git.branchCreate().setName(branches.branch2.name()).call();
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        assertArrayEquals(branches.list(), list.stream().map(Ref::getName).toArray());
        final Object[] actuals = git.getRepository().getAllRefs().entrySet().stream().map(Map.Entry::getKey).filter(s -> ! s.equals(REF_HEAD)).toArray();
        System.out.println(Arrays.deepToString(actuals));
        assertArrayEquals(branches.list(), actuals);
    }

    @Test
    public void readCommitDate() throws GitAPIException, IOException, InterruptedException {
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        final Ref ref = list.get(0);
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(ref.getObjectId());
        Thread.sleep(1000);
        assertTrue(getSecondsFromEpoch() - commit.getCommitTime() > 0);
    }

    private void startRemoteDaemon() throws GitAPIException, IOException, URISyntaxException {
        Daemon server = new Daemon(new InetSocketAddress(9418));
        server.getService("git-receive-pack").setEnabled(true);
        server.setRepositoryResolver(new RepositoryResolverImplementation());
        server.start();
    }

    private void configureRemote(Git git) throws URISyntaxException, IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin" ,"fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin" ,"push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish("git://localhost/repo.git");
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
    }

    private static final class RepositoryResolverImplementation implements
            RepositoryResolver<DaemonClient> {
        @Override
        public Repository open(DaemonClient client, String name)
                throws RepositoryNotFoundException,
                ServiceNotAuthorizedException, ServiceNotEnabledException,
                ServiceMayNotContinueException {
            Repository repo = repositories.get(name);
            if (repo == null) {
                try {
                    repo = new InMemoryRepository(new DfsRepositoryDescription(name));
                    repo.create(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repositories.put(name, repo);
            }
            return repo;
        }
    }

    private static Map<String, Repository> repositories = new HashMap<>();

    private int getSecondsFromEpoch() {
        return (int) (new Date().getTime() / 1000);
    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new IOException("Failed to delete file: " + f);
        }
    }
}
