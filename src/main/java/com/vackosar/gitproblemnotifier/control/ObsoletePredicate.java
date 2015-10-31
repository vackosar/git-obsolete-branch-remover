package com.vackosar.gitproblemnotifier.control;

import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevWalk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ObsoletePredicate implements Predicate<BranchInfo> {

    private final RevWalk walk;
    private Duration difference = Duration.ofSeconds(0);

    @Inject
    public ObsoletePredicate(Git git) {
        walk = new RevWalk(git.getRepository());
    }

    private LocalDateTime getSecondsFromEpoch() {
        return LocalDateTime.now();
    }

    @Override
    public boolean test(BranchInfo entry) {
        return Duration.between(entry.lastCommit, LocalDateTime.now()).compareTo(difference) > 0;
    }
}
