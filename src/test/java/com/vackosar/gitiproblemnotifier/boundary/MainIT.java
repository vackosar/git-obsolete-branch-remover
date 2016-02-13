package com.vackosar.gitiproblemnotifier.boundary;

import com.vackosar.gitiproblemnotifier.mock.LocalRepoMock;
import com.vackosar.gitiproblemnotifier.mock.RemoteSshRepo;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class MainIT {

    @Test
    public void fetch() throws GitAPIException {
        try (LocalRepoMock localRepoMock = new LocalRepoMock(RemoteSshRepo.URL);){}
    }

}
