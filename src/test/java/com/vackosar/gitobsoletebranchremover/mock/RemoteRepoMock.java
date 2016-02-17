package com.vackosar.gitobsoletebranchremover.mock;

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

public class RemoteRepoMock implements AutoCloseable, RepoMock {

    private static final String REPO_NAME = "repo.git";
    private static int port = 9418;
    public String repoUrl = null;
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
        repoUrl = "git://localhost:" + port + "/" + REPO_NAME;
        start();
        port++;
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
            server = new Daemon(new InetSocketAddress(port));
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

    public void configureRemote(Git git) throws URISyntaxException, IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin" ,"fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin" ,"push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish(repoUrl);
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
    }

    public Git get() {
        return new Git(resolver.getRepo(REPO_NAME));
    }
}
