package com.vackosar.gitproblemnotifier.boundary;

import com.vackosar.gitproblemnotifier.control.ObsoleteBranches;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;

public class Executor {

    @Inject private Git git;
    @Inject private ObsoleteBranches obsoleteBranches;

    public void execute() throws GitAPIException {
        obsoleteBranches
                .stream()
                .map(BranchInfo::toOutputLine)
                .forEach(System.out::println);
        git.getRepository().close();
        git.close();
    }
}
