package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.control.ExtractCommit;
import com.vackosar.gitproblemnotifier.control.ObsoletePredicate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.inject.Inject;
import java.util.Map.Entry;
import java.util.Set;

@Singleton
public class ObsoleteBranchesLister {

    @Inject private Git git;
    @Inject private ObsoletePredicate obsoletePredicate;
    @Inject private ExtractCommit extractCommit;

    public Set<Entry<Ref, RevCommit>> listObsolete() {
        git.getRepository()
                .getAllRefs().entrySet().stream()
                .filter(entry -> "HEAD".equals(entry.getKey()))
                .map(extractCommit)
                .filter(obsoletePredicate);
        return null;
    }
}
