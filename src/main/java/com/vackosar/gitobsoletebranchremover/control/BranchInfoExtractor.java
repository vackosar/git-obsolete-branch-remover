package com.vackosar.gitobsoletebranchremover.control;

import com.vackosar.gitobsoletebranchremover.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BranchInfoExtractor implements Function<Map.Entry<String, RevCommit>, BranchInfo> {

    private final Git git;
    private final RevCommit developHead;

    Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    public BranchInfoExtractor(Git git) throws IOException {
        this.git = git;
        try (RevWalk walk = new RevWalk(git.getRepository());) {
            developHead = walk.parseCommit(getBase());
        }
    }

    private ObjectId getBase() throws IOException {
        final Optional<Map.Entry<String, ObjectId>> base = Arrays.asList(
                "refs/remotes/origin/develop",
                "refs/remotes/origin/master",
                "refs/heads/develop",
                "refs/heads/master")
                .stream()
                .map(this::resolveRef)
                .filter(ref -> ref.getValue() != null)
                .findFirst();
        if (base.isPresent()) {
            log.info("Merged defined as merged into " + base.get().getKey());
            return base.get().getValue();
        } else {
            throw new IllegalArgumentException("Failed to find remote or local develop or master branches.");
        }
    }

    private Map.Entry<String, ObjectId> resolveRef(String name) {
        return new AbstractMap.SimpleImmutableEntry<>(name, getRef(name));
    }

    private ObjectId getRef(String name) {
        try {
            return git.getRepository().resolve(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BranchInfo apply(Map.Entry<String, RevCommit> entry) {
        final LocalDate commitTime = extractCommitTime(entry);
        final String refName = entry.getKey();
        final String email = extractEmail(entry);
        final boolean merged = merged(entry.getValue());
        return new BranchInfo(commitTime, refName, email, merged);
    }

    private boolean merged(RevCommit commit) {
        try (RevWalk walk = new RevWalk(git.getRepository());) {
            if (! developHead.equals(commit)) {
                return walk.isMergedInto(walk.parseCommit(commit), walk.parseCommit(developHead));
            } else {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
