package com.vackosar.gitobsoletebranchremover.entity;

import org.eclipse.jgit.lib.Constants;

import java.util.function.Predicate;

public enum BranchType implements Predicate<BranchInfo> {
    local {
        @Override
        public boolean test(BranchInfo b) {
            return local == b.branchType;
        }
    }, remote {
        @Override
        public boolean test(BranchInfo b) {
            return remote == b.branchType;
        }
    }, all {
        @Override
        public boolean test(BranchInfo branchInfo) {
            return true;
        }
    };

    public static BranchType parse(String branchName) {
        if (branchName.startsWith(Constants.R_REMOTES)) {
            return remote;
        } else if (branchName.startsWith(Constants.R_HEADS)) {
            return local;
        } else {
            throw new IllegalArgumentException("Unsupported prefix branch name of branch name: " + branchName + ".");
        }
    }
}
