package com.vackosar.gitproblemnotifier.control;

import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.Function;

public class ObsoleteBranchInfoExtractor implements Function<Map.Entry<String, RevCommit>, BranchInfo> {

    @Override
    public BranchInfo apply(Map.Entry<String, RevCommit> entry) {
        final LocalDate commitTime = extractCommitTime(entry);
        final String branchName = entry.getKey();
        final String email = extractEmail(entry);
        return new BranchInfo(commitTime, branchName, email);
    }

    private String extractEmail(Map.Entry<String, RevCommit> entry) {
        return entry.getValue().getAuthorIdent().getEmailAddress();
    }

    private LocalDate extractCommitTime(Map.Entry<String, RevCommit> entry) {
        return LocalDateTime
                .ofInstant(extractInstant(entry), ZoneOffset.systemDefault())
                .toLocalDate();
    }

    private Instant extractInstant(Map.Entry<String, RevCommit> entry) {
        return entry.getValue().getAuthorIdent().getWhen().toInstant();
    }
}
