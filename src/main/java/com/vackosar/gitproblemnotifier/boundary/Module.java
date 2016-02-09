package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vackosar.gitproblemnotifier.control.ObsoletePredicate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Module extends AbstractModule {

    private static final File WORK_DIR = new File(System.getProperty("user.dir"));
    private final Integer days;
    private final Path key;
    public static final Path NO_KEY = null;

    public Module(Integer days, Path key) {
        this.days = days;
        this.key = key;
    }

    @Provides
    @Singleton
    public Git provideGit() throws GitAPIException {
        try {
            final Git git = Git.open(WORK_DIR);
            git.fetch().setTransportConfigCallback(createTransportConfigCallback(key));
            return git;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TransportConfigCallback createTransportConfigCallback(Path key) {
        if (key == NO_KEY) {
            return transport -> {};
        }
        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                ((SshTransport) transport).setSshSessionFactory(new JschConfigSessionFactory() {

                    @Override
                    protected void configure(OpenSshConfig.Host hc, Session session) {}

                    @Override
                    protected JSch createDefaultJSch(FS fs) throws JSchException {
                        final JSch defaultJSch = super.createDefaultJSch(fs);
                        defaultJSch.addIdentity(key.toString());
                        return defaultJSch;
                    }
                });
            }
        };
    }

    @Provides
    @Singleton
    public ObsoletePredicate provideObsoletePredicate(Git git) {
        return new ObsoletePredicate(git, days);
    }

    @Override
    protected void configure() {}

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
