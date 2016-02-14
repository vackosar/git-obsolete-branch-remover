package com.vackosar.gitproblemnotifier.control;

import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.util.stream.Stream;

public class Branches {

    @Inject private Git git;
    @Inject private LastCommitExtractor lastCommitExtractor;
    @Inject private BranchInfoExtractor branchInfoExtractor;

    public Stream<BranchInfo> stream() throws GitAPIException {
        return git
                .branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .stream()
                .map(lastCommitExtractor)
                .map(branchInfoExtractor);
    }
}
