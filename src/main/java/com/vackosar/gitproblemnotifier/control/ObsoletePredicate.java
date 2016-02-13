package com.vackosar.gitproblemnotifier.control;

import com.vackosar.gitproblemnotifier.entity.Arguments;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ObsoletePredicate implements Predicate<BranchInfo> {

    private Duration difference = Duration.ofDays(0);

    @Inject
    public ObsoletePredicate(Arguments arguments) {
        difference = Duration.ofDays(arguments.days);
    }

    @Override
    public boolean test(BranchInfo entry) {
        return Duration.between(entry.lastCommit.atStartOfDay(), LocalDateTime.now()).compareTo(difference) > 0;
    }
}
