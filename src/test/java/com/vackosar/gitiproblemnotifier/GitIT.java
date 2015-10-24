package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

public class GitIT {

    private static final File DIR = new File("./tmp/");
    private static final String FILENAME = "test.txt";
    private static final File FILE = new File(DIR.getPath() + "/" + FILENAME);
    private static final String REPODIRNAME = ".git";
    private static final String STEP_BACK = "HEAD^";
    private static final String ALL_FILES = ".";
    private static final String MESSAGE = "test";

    @Before
    public void createTmpDir() {
        DIR.mkdir();
    }

    @After
    public void cleanUp() {
        FILE.delete();
        DIR.delete();
    }

    @Test
    public void commitModifyAndReset() throws GitAPIException, IOException {
        final Git git = initialize();
        FILE.delete();
        git.reset().setRef(STEP_BACK).setMode(ResetCommand.ResetType.HARD).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, DIR.list());
    }

    @Test
    private Git initialize() throws GitAPIException, IOException {
        final Git git = Git.init().setDirectory(DIR).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME}, DIR.list());
        FILE.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, DIR.list());
        return git;
    }
}
