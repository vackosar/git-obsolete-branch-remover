package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vackosar.gitproblemnotifier.control.ObsoleteBranches;
import com.vackosar.gitproblemnotifier.entity.Arguments;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
    public static void main(String[] args) throws GitAPIException {
        final Arguments arguments = new Arguments(args);
        final Injector injector = Guice.createInjector(new Module(arguments));
        injector
                .getInstance(ObsoleteBranches.class)
                .stream()
                .map(BranchInfo::toOutputLine)
                .forEach(System.out::println);
        closeGit(injector);
    }

    private static void closeGit(Injector injector) {
        final Git git = injector.getInstance(Git.class);
        git.getRepository().close();
        git.close();
    }
}

