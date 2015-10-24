package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GitIT {

    private static final File DIR = new File("./tmp/");
    private static final String FILENAME = "test.txt";
    private static final File FILE = new File(DIR.getPath() + "/" + FILENAME);
    private static final String REPODIRNAME = ".git";
    private static final String STEP_BACK = "HEAD";
    private static final String ALL_FILES = ".";
    private static final String MESSAGE = "test";
    private enum branches {
        branch1,
        branch2,
        master;

        @Override
        public String toString() {
            return "refs/heads/" + super.name();
        }

        public static Object[] list() {
            return Stream.of(branches.values()).map(branches -> branches.toString()).toArray();
        }
    }

    @Before
    public void createTmpDir() throws IOException {
        try {
            cleanUp();
        } catch (IOException e) {
            // do nothing
        }
        DIR.mkdir();
    }

    @After
    public void cleanUp() throws IOException {
        delete(DIR);
    }

    @Test
    public void commitModifyAndReset() throws GitAPIException, IOException {
        final Git git = commitFile();
        FILE.delete();
        git.reset().setRef(STEP_BACK).setMode(ResetCommand.ResetType.HARD).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, DIR.list());
    }

    private Git commitFile() throws GitAPIException, IOException {
        final Git git = initialize();
        FILE.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, DIR.list());
        return git;
    }

    private Git initialize() throws GitAPIException {
        final Git git = Git.init().setDirectory(DIR).call();
        git.commit().setMessage(MESSAGE).call();
        Assert.assertArrayEquals(new String[]{REPODIRNAME}, DIR.list());
        return git;
    }

    @Test
    public void listBranches() throws GitAPIException {
        final Git git = initialize();
        git.branchCreate().setName(branches.branch1.name()).call();
        git.branchCreate().setName(branches.branch2.name()).call();
        final List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        Assert.assertArrayEquals(branches.list(), list.stream().map((ref) -> ref.getName()).toArray());
    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}
