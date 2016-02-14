package com.vackosar.gitproblemnotifier.boundary;

import com.vackosar.gitproblemnotifier.control.Branches;
import com.vackosar.gitproblemnotifier.control.Processor;
import com.vackosar.gitproblemnotifier.entity.Arguments;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;

public class Executor {

    @Inject private Branches branches;
    @Inject private Arguments arguments;
    @Inject private Processor processor;
    @Inject private Git git;

    public void execute() throws GitAPIException {
        branches.stream()
                .filter(arguments.branchType)
                .filter(arguments.obsoleteness)
                .sorted()
                .forEach(processor);
        git.getRepository().close();
        git.close();
    }
}
