package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.boundary.Main;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.IOException;

public class ObsoleteBranchesListerIT {

    @Test
    public void inject() throws GitAPIException, IOException {
        final RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
        Main.main(new String[] {RemoteRepoMock.REPO_URL});
    }
}
