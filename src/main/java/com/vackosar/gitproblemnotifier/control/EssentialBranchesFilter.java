package com.vackosar.gitproblemnotifier.control;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;

import java.util.Arrays;
import java.util.function.Predicate;

@Singleton
public class EssentialBranchesFilter implements Predicate<BranchInfo> {
    @Override
    public boolean test(BranchInfo branchInfo) {
        return ! Arrays.asList("develop", "master")
                .contains(branchInfo.branchName);
    }
}
