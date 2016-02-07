package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.boundary.Main;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainTest {

    private static final String USER_DIR = "user.dir";
    private static final Path ORIG_WORK_DIR = Paths.get(System.getProperty(USER_DIR));
    private Path workDir;

    @Test
    public void list() throws Exception {
        try (
                RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                LocalRepoMock localRepoMock = new LocalRepoMock(RemoteRepoMock.REPO_URL);
        ){
            workDir = ORIG_WORK_DIR.resolve("tmp/local");
            System.setProperty(USER_DIR, workDir.toString());
//            RemoteRepoMock.configureRemote(localRepoMock.get());
            Main.main(new String[]{});
        }
    }
}
