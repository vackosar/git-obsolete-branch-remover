package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.boundary.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainTest {

    private static final String USER_DIR = "user.dir";
    private static final Path ORIG_WORK_DIR = Paths.get(System.getProperty(USER_DIR));

    @Test
    public void list() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(RemoteRepoMock.REPO_URL);
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
                    "master\t2015-11-01\tvackosar@github.com"
            };
            Assert.assertArrayEquals(expected, actual);
        }
    }
}
