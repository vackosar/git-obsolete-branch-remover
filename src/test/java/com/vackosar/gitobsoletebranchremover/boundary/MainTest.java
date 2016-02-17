package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RepoMock;
import com.vackosar.gitobsoletebranchremover.boundary.Main;
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
import java.util.List;
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
            Main.main(new String[]{"30", "--list", "--remote"});
            final String[] actual = out.toString().split(System.lineSeparator());
            final String[] expected = {
                    "branch1\t2015-11-01\tvackosar@github.com",
                    "branch2\t2015-11-01\tvackosar@github.com",
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
            Main.main(new String[]{"30", "--remove", "--remote", "."});
            Assert.assertEquals(Arrays.asList("refs/heads/master", "refs/remotes/origin/master"), listRefs(localRepoMock));
            Assert.assertEquals(Arrays.asList("refs/heads/master"), listRefs(remoteRepoMock));
        }
    }

    private List<String> listRefs(RepoMock mock) throws GitAPIException {
        return mock
                .get()
                .branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .stream()
                .map(Ref::getName)
                .collect(Collectors.<String>toList());
    }

    @Test
    public void listLocal() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            localRepoMock.get().checkout().setCreateBranch(true).setName("branch1").call();
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Main.main(new String[]{"30", "--list", "--local"});
            final String[] actual = out.toString().split(System.lineSeparator());
            final String[] expected = {
                    "branch1\t2015-11-01\tvackosar@github.com",
            };
            Assert.assertArrayEquals(expected, actual);
        }
    }

}
