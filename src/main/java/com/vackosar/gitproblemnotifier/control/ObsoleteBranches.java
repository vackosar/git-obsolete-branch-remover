package com.vackosar.gitproblemnotifier.control;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.util.stream.Stream;

@Singleton
public class ObsoleteBranches {

    @Inject private Git git;
    @Inject private ObsoletePredicate obsoletePredicate;
    @Inject private LastCommitExtractor lastCommitExtractor;
    @Inject private ObsoleteBranchInfoExtractor obsoleteBranchInfoExtractor;

    public Stream<BranchInfo> stream() throws GitAPIException {
        return git
                .branchList()
                .setListMode(ListBranchCommand.ListMode.REMOTE)
                .call()
                .stream()
                .map(lastCommitExtractor)
                .map(obsoleteBranchInfoExtractor)
                .filter(obsoletePredicate)
                .sorted((o1,o2)->o1.email.compareTo(o2.email));
    }
}
