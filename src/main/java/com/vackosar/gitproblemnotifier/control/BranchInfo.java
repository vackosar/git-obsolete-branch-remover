package com.vackosar.gitproblemnotifier.control;

import java.time.LocalDate;

public class BranchInfo {
    public final LocalDate lastCommit;
    public final String branchName;
    public final String email;

    public BranchInfo(LocalDate lastCommit, String branchName, String email) {
        this.lastCommit = lastCommit;
        this.branchName = branchName;
        this.email = email;
    }

    public String convertToOutputLine() {
        return branchName.replaceFirst("refs/remotes/origin/", "")
                + "\t" + lastCommit
                + "\t" + email;
    }

}
