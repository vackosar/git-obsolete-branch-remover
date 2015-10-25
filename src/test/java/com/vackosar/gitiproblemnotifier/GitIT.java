package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

public class GitIT {

    private static final File LOCAL = new File("tmp/local");
    private static final File REMOTE = new File("tmp/remote");
    private static final String FILENAME = "test.txt";
    private static final File FILE = new File(LOCAL.getPath() + "/" + FILENAME);
    private static final String REPODIRNAME = ".git";
    private static final String STEP_BACK = "HEAD";
    private static final String ALL_FILES = ".";
    private static final String MESSAGE = "test";
    private enum branches {
        branch1,
        branch2,
        master;

        @Override
        public String toString() {
            return "refs/heads/" + super.name();
        }

        public static Object[] list() {
            return Stream.of(branches.values()).map(branches -> branches.toString()).toArray();
        }
    }

    @Before
    public void createTmpDir() throws IOException {
        cleanUp();
        LOCAL.mkdir();
        REMOTE.mkdir();
    }

    @After
    public void cleanUp() {
        try {
            delete(LOCAL);
            delete(REMOTE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void commitModifyAndReset() throws GitAPIException, IOException {
        final Git git = commitFile();
        FILE.delete();
        git.reset().setRef(STEP_BACK).setMode(ResetCommand.ResetType.HARD).call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
    }

    @Test
    public void pushAndPull() throws GitAPIException, IOException, InterruptedException, URISyntaxException {
        final Git git = commitFile();
        git.log();
        startRemoteDaemon();
        configureRemote(git);
        final Iterable<PushResult> results = git.push().call();
        git.close();
        delete(LOCAL);
        final Git git2 = initialize();
        configureRemote(git2);
        git2.pull().call();
        git2.close();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
    }

    private Git commitFile() throws GitAPIException, IOException {
        final Git git = initialize();
        FILE.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, LOCAL.list());
        return git;
    }

    private Git initialize() throws GitAPIException {
        final Git git = Git.init().setDirectory(LOCAL).call();
        git.commit().setMessage(MESSAGE).call();
        assertArrayEquals(new String[]{REPODIRNAME}, LOCAL.list());
        return git;
    }

    @Test
    public void listBranches() throws GitAPIException {
        final Git git = initialize();
        git.branchCreate().setName(branches.branch1.name()).call();
        git.branchCreate().setName(branches.branch2.name()).call();
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        assertArrayEquals(branches.list(), list.stream().map((ref) -> ref.getName()).toArray());
    }

    @Test
    public void readCommitDate() throws GitAPIException, IOException, InterruptedException {
        final Git git = initialize();
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        final Ref ref = list.get(0);
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(ref.getObjectId());
        Thread.sleep(1000);
        assertTrue(getSecondsFromEpoch() - commit.getCommitTime() > 0);
    }

    private void startRemoteDaemon() throws GitAPIException, IOException, URISyntaxException {
        Daemon server = new Daemon(new InetSocketAddress(9418));
        boolean uploadsEnabled = true;
        server.getService("git-receive-pack").setEnabled(uploadsEnabled);
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
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}
