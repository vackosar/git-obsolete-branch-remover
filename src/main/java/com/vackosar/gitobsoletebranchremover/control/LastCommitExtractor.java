package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class LastCommitExtractor implements Function<Ref, Map.Entry<String, RevCommit>> {

    private final RevWalk walk;

    @Inject
    public LastCommitExtractor(Git git) {
        walk = new RevWalk(git.getRepository());
    }

    @Override
    public Map.Entry<String, RevCommit> apply(Ref ref) {
        try {
            final RevCommit commit = walk.parseCommit(ref.getObjectId());
            final String refName = ref.getName();
            return new AbstractMap.SimpleImmutableEntry<>(refName, commit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
