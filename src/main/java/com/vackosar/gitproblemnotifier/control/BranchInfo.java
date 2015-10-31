package com.vackosar.gitproblemnotifier.control;

import java.time.LocalDateTime;

public class BranchInfo {
    public final LocalDateTime lastCommit;
    public final String branchName;
    public final String email;

    public BranchInfo(LocalDateTime lastCommit, String branchName, String email) {
        this.lastCommit = lastCommit;
        this.branchName = branchName;
        this.email = email;
    }

    @Override
    public String toString() {
        return "BranchInfo{" +
                "lastCommit=" + lastCommit +
                ", branchName='" + branchName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
