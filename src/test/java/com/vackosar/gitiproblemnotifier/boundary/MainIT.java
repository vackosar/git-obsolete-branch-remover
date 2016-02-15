package com.vackosar.gitiproblemnotifier.boundary;

import com.vackosar.gitiproblemnotifier.mock.LocalRepoMock;
import com.vackosar.gitiproblemnotifier.mock.RemoteSshRepo;
import org.junit.Test;

public class MainIT {

    @Test
    public void fetch() throws Exception {
        try (
                RemoteSshRepo remoteSshRepo = new RemoteSshRepo();
                LocalRepoMock localRepoMock = new LocalRepoMock(remoteSshRepo.URL);){

        }
    }

}
