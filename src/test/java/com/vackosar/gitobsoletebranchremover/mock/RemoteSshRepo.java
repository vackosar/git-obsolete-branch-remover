package com.vackosar.gitobsoletebranchremover.mock;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.IOException;
import java.net.URISyntaxException;

public class RemoteSshRepo implements AutoCloseable {

    public static final String URL = "ssh://ubuntu@192.168.56.1/home/ubuntu/gpn";

    public RemoteSshRepo() throws Exception {
        try (RemoteRepoMock remoteRepoMock =  new RemoteRepoMock(false)) {
            configureRemote(remoteRepoMock.get());
            remoteRepoMock.get().push().setPushAll().setForce(true).call();
        }
    }

    public static void configureRemote(Git git) throws URISyntaxException, IOException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin" ,"fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin" ,"push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish(URL);
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
    }

    @Override
    public void close() throws Exception {

    }
}
