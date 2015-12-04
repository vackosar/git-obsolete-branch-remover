package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class GitIT implements AutoCloseable {

    private static final String STEP_BACK = "HEAD";
    private static final String REF_HEAD = "HEAD";
    private LocalRepoMock localRepoMock;

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
        localRepoMock = new LocalRepoMock();
    }

    @After
    public void close() throws IOException {
        localRepoMock.close();
    }

    @Test
    public void commitModifyAndReset() throws GitAPIException, IOException {
        localRepoMock.commitFile();
        localRepoMock.deleteFile();
        localRepoMock.get().reset().setRef(STEP_BACK).setMode(ResetCommand.ResetType.HARD).call();
        assertFileIsPresent();
    }

    @Test
    public void pushAndPull() throws GitAPIException, IOException, InterruptedException, URISyntaxException {
        localRepoMock.commitFile();
        new RemoteRepoMock(true);
        final Git git = localRepoMock.get();
        configureRemote(git);
        git.push().call();
        localRepoMock.close();
        localRepoMock = new LocalRepoMock();
        configureRemote(git);
        final Git git2 = localRepoMock.get();
        git2.pull().call();
        assertFileIsPresent();
    }

    private void assertFileIsPresent() {
        assertArrayEquals(new String[]{localRepoMock.REPODIRNAME, localRepoMock.FILE.getName()}, localRepoMock.PATH.list());
    }

    @Test
    public void listFilesInLastCommit() throws IOException, GitAPIException {
        localRepoMock.commitRandomFile();
        localRepoMock.commitRandomFile();
        final String fileName = localRepoMock.commitRandomFile();
        final Git git = localRepoMock.get();
        final Map<String, Ref> allRefs = git.getRepository().getAllRefs();
        final RevWalk walk = new RevWalk(git.getRepository());
        final RevTree tree = walk.parseCommit(allRefs.get("HEAD").getObjectId()).getTree();
        final TreeWalk treeWalk = new TreeWalk(git.getRepository());
        final ObjectId parentId = walk.parseCommit(allRefs.get("HEAD").getObjectId()).getParents()[0].getId();
        final RevTree parentTree = walk.parseCommit(parentId).getTree();
        treeWalk.addTree(tree);
        treeWalk.addTree(parentTree);
        treeWalk.setFilter(TreeFilter.ANY_DIFF);
        while (treeWalk.next()) {
            assertTreeIsFlat(treeWalk);
            Assert.assertEquals(fileName, treeWalk.getPathString());
        }
    }

    private void assertTreeIsFlat(TreeWalk treeWalk) {
        Assert.assertEquals(0, treeWalk.getDepth());
    }

    @Test
    public void listBranches() throws GitAPIException {
        final Git git = localRepoMock.get();
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
        final Git git = localRepoMock.get();
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        final Ref ref = list.get(0);
        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(ref.getObjectId());
        Thread.sleep(1000);
        assertTrue(getSecondsFromEpoch() - commit.getCommitTime() > 0);
    }

    private void configureRemote(Git git) throws URISyntaxException, IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin" ,"fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin" ,"push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish(RemoteRepoMock.REPO_URL);
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
    }

    private int getSecondsFromEpoch() {
        return (int) (new Date().getTime() / 1000);
    }
}
