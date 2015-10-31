package com.vackosar.gitproblemnotifier.control;

import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Predicate;

public class ObsoleteFilter implements Predicate<Map.Entry<String, RevCommit>> {

    private final RevWalk walk;
    private int differenceTime = 10;

    @Inject
    public ObsoleteFilter(Git git) {
        walk = new RevWalk(git.getRepository());
    }

    private int getSecondsFromEpoch() {
        return (int) (new Date().getTime() / 1000);
    }

    @Override
    public boolean test(Map.Entry<String, RevCommit> entry) {
        final int commitTime;
        commitTime = entry.getValue().getCommitTime();
        final int currentTime = getSecondsFromEpoch();
        return commitTime - currentTime > differenceTime;
    }
}
