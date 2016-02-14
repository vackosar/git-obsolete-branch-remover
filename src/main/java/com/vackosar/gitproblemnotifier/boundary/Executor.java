package com.vackosar.gitproblemnotifier.boundary;

import com.vackosar.gitproblemnotifier.control.BranchInfoExtractor;
import com.vackosar.gitproblemnotifier.control.EssentialBranchesFilter;
import com.vackosar.gitproblemnotifier.control.LastCommitExtractor;
import com.vackosar.gitproblemnotifier.control.Processor;
import com.vackosar.gitproblemnotifier.entity.Arguments;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import javax.inject.Inject;
import java.util.stream.Stream;

public class Executor {

    @Inject private Git git;
    @Inject private Arguments arguments;
    @Inject private LastCommitExtractor lastCommitExtractor;
    @Inject private EssentialBranchesFilter essentialBranchesFilter;
    @Inject private BranchInfoExtractor branchInfoExtractor;
    @Inject private Processor processor;

    public void execute() throws GitAPIException {
        streamBranches()
                .map(lastCommitExtractor)
                .map(branchInfoExtractor)
                .filter(essentialBranchesFilter)
                .filter(arguments.branchType)
                .filter(arguments.obsoleteness)
                .sorted(this::compareEmails)
                .forEach(processor);
        git.getRepository().close();
        git.close();
    }

    private Stream<Ref> streamBranches() throws GitAPIException {
        return git
                .branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .stream();
    }

    private int compareEmails(BranchInfo b1, BranchInfo b2) {
        return b1.email.compareTo(b2.email);
    }
}
