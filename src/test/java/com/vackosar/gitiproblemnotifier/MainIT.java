package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class MainIT {

    @Test
    public void fetch() throws GitAPIException {
        try (LocalRepoMock localRepoMock = new LocalRepoMock(RemoteSshRepo.URL);){}
    }

}
