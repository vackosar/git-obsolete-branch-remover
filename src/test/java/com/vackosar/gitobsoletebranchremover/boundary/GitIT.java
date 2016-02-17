package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteRepoMock;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        final RemoteRepoMock remoteRepoMock = new RemoteRepoMock(true);
        final Git git = localRepoMock.get();
        remoteRepoMock.configureRemote(git);
        git.push().call();
        localRepoMock.close();
        localRepoMock = new LocalRepoMock();
        remoteRepoMock.configureRemote(git);
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
        final RevCommit commit = walk.parseCommit(allRefs.get("HEAD").getObjectId());
        final RevTree tree = commit.getTree();
        final TreeWalk treeWalk = new TreeWalk(git.getRepository());
        for (RevCommit parentCommit : commit.getParents()) {
            final ObjectId parentId = parentCommit.getId();
            final RevTree parentTree = walk.parseCommit(parentId).getTree();
            treeWalk.addTree(parentTree);
        }
        treeWalk.addTree(tree);
        treeWalk.setFilter(TreeFilter.ANY_DIFF);
        Assert.assertTrue(treeWalk.next());
        assertTreeIsFlat(treeWalk);
        Assert.assertEquals(fileName, treeWalk.getPathString());
        Assert.assertFalse(treeWalk.next());
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

    private int getSecondsFromEpoch() {
        return (int) (new Date().getTime() / 1000);
    }
}
