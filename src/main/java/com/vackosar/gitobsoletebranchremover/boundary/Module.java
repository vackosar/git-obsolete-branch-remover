package com.vackosar.gitobsoletebranchremover.boundary;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.vackosar.gitobsoletebranchremover.control.SshTrasportCallback;
import com.vackosar.gitobsoletebranchremover.entity.Action;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.entity.BranchType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Module extends AbstractModule {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String[] args;

    public Module(String[] args) {
        this.args = args;
    }

    @Provides @Singleton
    public Git provideGit(Path workDir, Arguments arguments, SshTrasportCallback callback) throws GitAPIException {
        try {
            final FileRepositoryBuilder builder = new FileRepositoryBuilder();
            final FileRepositoryBuilder gitDir = builder.findGitDir(workDir.toFile());
            if (gitDir == null) {
                throw new IllegalArgumentException("Git repository root directory not found ascending from current working directory:'" + workDir + "'.");
            }
            Git git = Git.wrap(builder.build());
            if (arguments.branchType == BranchType.remote) {
                fetch(arguments, git, callback);
            }
            return git;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetch(Arguments arguments, Git git, SshTrasportCallback callback) throws GitAPIException {
        try {
            git
                .fetch()
                .setTransportConfigCallback(callback)
                .call();
        } catch (TransportException e) {
            if (arguments.action == Action.list) {
                log.warn("Failed to connect to the remote. Will rely on current state.");
            } else {
                throw e;
            }
        }
    }

    @Provides @Singleton
    public Path provideWorkDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    @Provides @Singleton
    public Arguments provideArguments() {return new Arguments(args);}

    @Override
    protected void configure() {}
}
