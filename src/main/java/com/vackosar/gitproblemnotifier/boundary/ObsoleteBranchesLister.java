package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.BranchInfo;
import com.vackosar.gitproblemnotifier.control.CommitExtractor;
import com.vackosar.gitproblemnotifier.control.ObsoleteBranchInfoExtractor;
import com.vackosar.gitproblemnotifier.control.ObsoletePredicate;
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
                .sorted((o1,o2)->o1.email.compareTo(o2.email))
                .collect(Collectors.<BranchInfo>toList());
    }
}
