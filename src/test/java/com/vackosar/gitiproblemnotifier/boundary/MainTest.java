package com.vackosar.gitiproblemnotifier.boundary;

import com.vackosar.gitiproblemnotifier.mock.LocalRepoMock;
import com.vackosar.gitiproblemnotifier.mock.RemoteRepoMock;
import com.vackosar.gitproblemnotifier.boundary.Main;
import org.eclipse.jgit.api.ListBranchCommand;
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
    public void list() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
        ){
            Path workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            Main.main(new String[]{"30"});
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
            Main.main(new String[]{"30", "--remove"});
            final List<String> refNames = localRepoMock
                    .get()
                    .branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
                    .stream()
                    .map(Ref::getName)
                    .collect(Collectors.<String>toList());;
            final List<String> expected = Arrays.asList("refs/heads/master", "refs/remotes/origin/master");
            Assert.assertEquals(expected, refNames);
        }
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
