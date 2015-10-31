package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.*;
import org.eclipse.jgit.api.Git;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ObsoleteBranchesLister {

    @Inject private Git git;
    @Inject private ObsoletePredicate obsoletePredicate;
    @Inject private CommitExtractor commitExtractor;
    @Inject private HeadBranchPredicate headBranchPredicate;
    @Inject private ObsoleteBranchInfoExtractor obsoleteBranchInfoExtractor;

    public List<BranchInfo> listObsolete() {
        return git.getRepository()
                .getAllRefs().entrySet().stream()
                .filter(headBranchPredicate)
                .map(commitExtractor)
                .map(obsoleteBranchInfoExtractor)
                .filter(obsoletePredicate)
                .collect(Collectors.<BranchInfo>toList());
    }
}
