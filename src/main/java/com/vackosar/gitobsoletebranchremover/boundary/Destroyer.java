package com.vackosar.gitobsoletebranchremover.boundary;

import com.google.inject.Singleton;
import com.vackosar.gitobsoletebranchremover.control.BranchInfoExtractor;
import org.eclipse.jgit.api.Git;

import javax.inject.Inject;

@Singleton
public class Destroyer {

    @Inject private Git git;
    @Inject private BranchInfoExtractor branchInfoExtractor;

    public void act() {
        git.getRepository().close();
        git.close();
//        branchInfoExtractor.close();
    }

}
