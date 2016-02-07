package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.boundary.Main;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test
    public void list() throws Exception {
        try (RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);){
            Main.main(new String[] {RemoteRepoMock.REPO_URL});
        }
    }
}
