package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Singleton;
import com.vackosar.gitobsoletebranchremover.entity.Action;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.entity.BranchInfo;
import com.vackosar.gitobsoletebranchremover.entity.BranchType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.transport.RefSpec;
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
            remove(branchInfo);
        } else {
            System.out.println(branchInfo.toOutputLine());
        }
    }

    private void remove(BranchInfo branchInfo) {
        try {
            git.branchDelete().setBranchNames(branchInfo.getFullBranchName()).call();
            if (arguments.branchType == BranchType.remote) {
                RefSpec refSpec = new RefSpec()
                        .setSource(null)
                        .setDestination("refs/heads/" + branchInfo.branchName);
                git.push().setRefSpecs(refSpec).setRemote(branchInfo.remoteName.get()).call();
            }
        } catch (NotMergedException e) {
            log.error("Branch '" + branchInfo.getFullBranchName() + "' was not removed because it contains unmerged changes.");
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
