package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RepoMock;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainTest {

    private static final String USER_DIR = "user.dir";
    private static final Path ORIG_WORK_DIR = Paths.get(System.getProperty(USER_DIR));

    @Test
    public void listRemote() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Main.main(new String[]{"0", "--list", "--remote"});
            final String[] actual = out.toString().split(System.lineSeparator());
            final String[] expected = {
                    "branch1\t2015-11-01\tvackosar@github.com\tmerged",
                    "branch2\t2015-11-01\tvackosar@github.com\tmerged",
                    "branch3\t2016-02-27\tvackosar@github.com\tunmerged",
            };
            Assert.assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void remove() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Main.main(new String[]{"0", "--remove", "--remote", "."});
            Assert.assertEquals(new HashSet<>(Arrays.asList("refs/remotes/origin/branch3", "refs/heads/master", "refs/remotes/origin/master")), collectRefs(localRepoMock));
            Assert.assertEquals(new HashSet<>(Arrays.asList("refs/heads/branch3", "refs/heads/master")), collectRefs(remoteRepoMock));
        }
    }

    private Set<String> collectRefs(RepoMock mock) throws GitAPIException {
        return mock
                .get()
                .branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .stream()
                .map(Ref::getName)
                .collect(Collectors.<String>toSet());
    }

    @Test
    public void listLocal() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            localRepoMock.get().checkout().setCreateBranch(true).setName("branch1").setStartPoint("origin/branch1").call();
            localRepoMock.get().checkout().setCreateBranch(true).setName("branch3").setStartPoint("origin/branch3").call();
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Main.main(new String[]{"0", "--list", "--local"});
            final List<String> actual = Arrays.asList(out.toString().split(System.lineSeparator()));
            final List<String> expected = Arrays.asList(
                    "branch1\t2015-11-01\tvackosar@github.com\tmerged",
                    "branch3\t2016-02-27\tvackosar@github.com\tunmerged"
            );
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void forceRemoveRemote() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            localRepoMock.get().checkout().setCreateBranch(true).setName("branch1").setStartPoint("origin/branch1").call();
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            workDir.resolve("newFile").toFile().createNewFile();
            localRepoMock.get().add().addFilepattern(".").call();
            localRepoMock.get().commit().setMessage("Add new file for unit test.").call();
            localRepoMock.get().push().call();
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Assert.assertEquals(new HashSet<>(Arrays.asList("refs/heads/branch1", "refs/heads/branch2", "refs/heads/branch3", "refs/heads/master")), collectRefs(remoteRepoMock));
            Main.main(new String[]{"0", "--remove", "--remote"});
            Assert.assertEquals(new HashSet<>(Arrays.asList("refs/heads/master", "refs/heads/branch1", "refs/heads/branch3")), collectRefs(remoteRepoMock));
            Main.main(new String[]{"0", "--forceremove", "--remote"});
            Assert.assertEquals(new HashSet<>(Arrays.asList("refs/heads/master")), collectRefs(remoteRepoMock));
        }
    }

}
