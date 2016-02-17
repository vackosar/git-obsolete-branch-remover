package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteSshRepo;
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
