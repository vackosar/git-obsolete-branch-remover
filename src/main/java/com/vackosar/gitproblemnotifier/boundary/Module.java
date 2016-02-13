package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.SshTrasportCallback;
import com.vackosar.gitproblemnotifier.entity.Arguments;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Module extends AbstractModule {

    public static final Path NO_KEY = null;

    private final Arguments args;

    public Module(Arguments args) {
        this.args = args;
    }

    @Provides
    @Singleton
    public Git provideGit(Path workDir) throws GitAPIException {
        try {
            final FileRepositoryBuilder builder = new FileRepositoryBuilder();
            final FileRepositoryBuilder gitDir = builder.findGitDir(workDir.toFile());
            if (gitDir == null) {
                throw new IllegalArgumentException("Git repository root directory not found ascending from current working directory:'" + workDir + "'.");
            }
            Git git = Git.wrap(builder.build());
            if (args.key != NO_KEY) {
                fetch(git);
            }
            return git;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetch(Git git) throws GitAPIException {
        git
                .fetch()
                .setTransportConfigCallback(createTransportConfigCallback(args.key))
                .call();
    }

    @Provides
    @Singleton
    public Path provideWorkDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    @Provides @Singleton public Arguments provideArguments() {return args;}

    private TransportConfigCallback createTransportConfigCallback(Path key) {
        return new SshTrasportCallback(key);
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
