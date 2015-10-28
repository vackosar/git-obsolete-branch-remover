package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.boundary.Main;
import org.junit.Test;

public class ObsoleteBranchesListerIT {

    @Test
    public void inject() {
        RemoteRepoMock.start();
        Main.main(new String[] {RemoteRepoMock.REPO_URL});
    }
}
