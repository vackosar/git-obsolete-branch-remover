package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Singleton;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.DepthWalk;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ObsoleteBranchesLister {

    @Inject
    private Git git;

    public List<Map.Entry<Ref, DepthWalk.Commit>> listObsolete() {
        Objects.nonNull(git);
        return null;
    }
}
