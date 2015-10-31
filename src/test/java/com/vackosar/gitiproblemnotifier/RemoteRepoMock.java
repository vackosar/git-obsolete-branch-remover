package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.transport.Daemon;

import java.io.File;
import java.net.InetSocketAddress;

public class RemoteRepoMock {

    public static final String REPO_URL = "git://localhost/repo.git";
    private static final File DATA_ZIP = new File("src/test/resource/.git.zip");
    private static final File REPO_DIR = new File("tmp/remote");
    private boolean bare;

    public RemoteRepoMock(boolean bare) {
        this.bare = bare;
        if (bare) {
            try {delete(REPO_DIR);} catch (Exception e) {}
            REPO_DIR.mkdir();
        } else {
            prepareTestingData();
        }
        start();
    }

    private void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new RuntimeException("Failed to delete file: " + f);
        }
    }


    private void start() {
        try {
            Daemon server = new Daemon(new InetSocketAddress(9418));
            server.getService("git-receive-pack").setEnabled(true);
            server.setRepositoryResolver(new RepoResolver(REPO_DIR, bare));
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTestingData() {
        new UnZiper().act(DATA_ZIP, REPO_DIR);
    }
}
