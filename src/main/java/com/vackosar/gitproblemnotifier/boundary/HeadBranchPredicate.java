package com.vackosar.gitproblemnotifier.boundary;

import org.eclipse.jgit.lib.Ref;

import java.util.Map;
import java.util.function.Predicate;

public class HeadBranchPredicate implements Predicate<Map.Entry<String, Ref>> {

    private static final String BRANCH_NAME = "HEAD";

    @Override
    public boolean test(Map.Entry<String, Ref> entry) {
        return BRANCH_NAME.equals(entry.getKey());
    }
}
