package com.vackosar.gitiproblemnotifier;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileRepositoryBuilderIT {

    private static final String MASTER = "master";
    private static final String PATH = "./tmp/";

    @Before
    public void createTmpDir() {
        new File(PATH).mkdir();
    }

    @After
    public void removeTmpDir() {
        new File(PATH).delete();
    }

    @Test
    @Ignore
    public void createRepository() throws IOException, GitAPIException {
        System.out.println(Arrays.toString(new File(".").list()));
        final Repository repository = new FileRepositoryBuilder().setGitDir(new File(PATH)).build();
        Assert.assertEquals(MASTER, repository.getBranch());
    }
}
