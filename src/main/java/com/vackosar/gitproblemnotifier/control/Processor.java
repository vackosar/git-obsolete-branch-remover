package com.vackosar.gitproblemnotifier.control;

import com.google.inject.Singleton;
import com.vackosar.gitproblemnotifier.entity.Action;
import com.vackosar.gitproblemnotifier.entity.Arguments;
import com.vackosar.gitproblemnotifier.entity.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Consumer;

@Singleton
public class Processor implements Consumer<BranchInfo> {

    Logger log = LoggerFactory.getLogger(getClass());

    @Inject private Git git;
    @Inject private Arguments arguments;

    @Override
    public void accept(BranchInfo branchInfo) {
        if (arguments.action == Action.remove) {
            try {
                git.branchDelete().setBranchNames(branchInfo.getFullBranchName()).call();
            } catch (NotMergedException e) {
                log.error("Branch '" + branchInfo.getFullBranchName() + "' was not removed because it contains unmerged changes.");
            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(branchInfo.toOutputLine());
        }
    }
}
