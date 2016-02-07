package com.vackosar.gitproblemnotifier.control;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevWalk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ObsoletePredicate implements Predicate<BranchInfo> {

    private final RevWalk walk;
    private Duration difference = Duration.ofDays(0);

    public ObsoletePredicate(Git git, Integer days) {
        walk = new RevWalk(git.getRepository());
        difference = Duration.ofDays(days);
    }

    @Override
    public boolean test(BranchInfo entry) {
        return Duration.between(entry.lastCommit.atStartOfDay(), LocalDateTime.now()).compareTo(difference) > 0;
    }
}
