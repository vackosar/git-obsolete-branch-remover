package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class Module extends AbstractModule {

    private static final File REPO = new File("tmp/repo");
    private final String remote;

    public Module(String remote) {
        this.remote = remote;
    }

    @Provides
    @Singleton
    public Git provideGit() throws GitAPIException {
        return Git.cloneRepository().setDirectory(REPO).setURI(remote).call();
    }

    @Override
    protected void configure() {
    }
}
