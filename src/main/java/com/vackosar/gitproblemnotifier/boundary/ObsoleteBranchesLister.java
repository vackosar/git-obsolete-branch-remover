package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ObsoleteBranchesLister {

    @Inject private Git git;
    @Inject private ObsoletePredicate obsoletePredicate;
    @Inject private CommitExtractor commitExtractor;
    @Inject private ObsoleteBranchInfoExtractor obsoleteBranchInfoExtractor;

    public List<BranchInfo> listObsolete() throws GitAPIException {
        return git
                .branchList()
                .setListMode(ListBranchCommand.ListMode.REMOTE)
                .call()
                .stream()
                .map(commitExtractor)
                .map(obsoleteBranchInfoExtractor)
                .filter(obsoletePredicate)
                .collect(Collectors.<BranchInfo>toList());
    }
}
