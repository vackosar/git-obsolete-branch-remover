package com.vackosar.gitiproblemnotifier.mock;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;

public class RemoteRepoMock implements AutoCloseable {

    public static final String REPO_URL = "git://localhost/repo.git";
    private static final File DATA_ZIP = new File("src/test/resources/template.zip");
    private static final File REPO_DIR = new File("tmp/remote");
    private boolean bare;
    private Daemon server;
    private RepoResolver resolver;

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
            server = new Daemon(new InetSocketAddress(9418));
            server.getService("git-receive-pack").setEnabled(true);
            resolver = new RepoResolver(REPO_DIR, bare);
            server.setRepositoryResolver(resolver);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTestingData() {
        new UnZiper().act(DATA_ZIP, REPO_DIR);
    }

    @Override
    public void close() throws Exception {
        server.stop();
        resolver.close();
        delete(REPO_DIR);
    }

    public static void configureRemote(Git git) throws URISyntaxException, IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin" ,"fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin" ,"push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish(RemoteRepoMock.REPO_URL);
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
    }
}
