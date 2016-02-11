package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.ObsoletePredicate;
import com.vackosar.gitproblemnotifier.control.SshTrasportCallback;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;

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
            git
                    .fetch()
                    .setTransportConfigCallback(createTransportConfigCallback(key))
                    .call();
            return git;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TransportConfigCallback createTransportConfigCallback(Path key) {
        return new SshTrasportCallback(key);
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
