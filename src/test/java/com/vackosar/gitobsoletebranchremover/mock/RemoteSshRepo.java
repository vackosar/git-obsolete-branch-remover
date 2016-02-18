package com.vackosar.gitobsoletebranchremover.mock;

import com.vackosar.gitobsoletebranchremover.control.SshTrasportCallback;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class RemoteSshRepo implements AutoCloseable {

    public static final String URL = "ssh://ubuntu@192.168.56.1/home/ubuntu/gpn";

    public RemoteSshRepo(SshTrasportCallback trasportCallback) throws GitAPIException {
        try (RemoteRepoMock remoteRepoMock =  new RemoteRepoMock(false)) {
            configureRemote(remoteRepoMock.get());
            remoteRepoMock.get().push().setTransportConfigCallback(trasportCallback).setPushAll().setForce(true).call();
        }
    }

    public static void configureRemote(Git git) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {

    }
}
