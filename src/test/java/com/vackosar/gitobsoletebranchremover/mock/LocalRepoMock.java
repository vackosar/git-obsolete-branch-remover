package com.vackosar.gitobsoletebranchremover.mock;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class LocalRepoMock implements AutoCloseable, RepoMock {

    public static final File PATH = new File("tmp/local");
    private static final String MESSAGE = "test";
    private static final String FILENAME = "test.txt";
    public static final File FILE = new File(PATH.getPath() + "/" + FILENAME);
    public static final String REPODIRNAME = ".git";
    private static final String ALL_FILES = ".";
    private static final Path KEY = Paths.get(System.getProperty("user.home") + "/.ssh/id_rsa");

    private final Git git;

    public LocalRepoMock() throws GitAPIException {
        try {
            close();
        } catch (RuntimeException e) {
            //
        }
        PATH.mkdir();
        git = Git.init().setDirectory(PATH).call();
        git.commit().setMessage(MESSAGE).call();
    }

    public LocalRepoMock(String remote) throws GitAPIException {
        try {
            close();
        } catch (RuntimeException e) {
            //
        }
        PATH.mkdir();
        git = Git
                .cloneRepository()
                .setDirectory(PATH)
                .setURI(remote)
                .call();
    }

    public Git get() {
        return git;
    }

    @Override
    public void close() {
        if (git != null) {
            git.getRepository().close();
            git.close();
        }
        delete(PATH);
    }

    public void commitFile() throws GitAPIException, IOException {
        FILE.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        assertArrayEquals(new String[]{REPODIRNAME, FILE.getName()}, PATH.list());
    }

    public String commitRandomFile() throws IOException, GitAPIException {
        final File randomFile = new File(PATH.getPath() + "/" + new Integer(new Random().nextInt()) + ".txt");
        randomFile.createNewFile();
        git.add().addFilepattern(ALL_FILES).call();
        git.commit().setMessage(MESSAGE).call();
        return randomFile.getName();
    }


    public void deleteFile() {
        FILE.delete();
    }

    void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new RuntimeException("Failed to delete file: " + f);
        }
    }

}
