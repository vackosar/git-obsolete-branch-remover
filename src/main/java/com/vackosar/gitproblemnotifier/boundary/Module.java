package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.ObsoletePredicate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Module extends AbstractModule {

    private static final File WORK_DIR = new File(System.getProperty("user.dir"));
    private final Integer days;

    public Module(Integer days) {
        this.days = days;
    }

    @Provides
    @Singleton
    public Git provideGit() throws GitAPIException {
        try {
            return Git.open(WORK_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
