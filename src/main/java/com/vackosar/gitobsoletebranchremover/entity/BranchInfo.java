package com.vackosar.gitobsoletebranchremover.entity;

import org.eclipse.jgit.lib.Constants;

import java.time.LocalDate;
import java.util.Optional;

public class BranchInfo implements Comparable<BranchInfo> {

    public final LocalDate lastCommit;
    public final String branchName;
    public final Optional<String> remoteName;
    public final String email;
    public final BranchType branchType;

    public BranchInfo(LocalDate lastCommit, String refName, String email) {
        this.lastCommit = lastCommit;
        this.branchType = BranchType.parse(refName);
        this.branchName = parseBranchName(refName, branchType);
        this.remoteName = parseRemoteName(refName, branchType);
        this.email = email;
    }

    public String getFullBranchName() {
        if (branchType == BranchType.remote) {
            return remoteName.get() + "/" + branchName;
        } else {
            return branchName;
        }
    }

    public String toOutputLine() {
        return branchName + "\t" + lastCommit + "\t" + email;
    }

    public Optional<String> parseRemoteName(String refName, BranchType type) {
        if (type == BranchType.remote) {
            return Optional.of(removePrefix(refName).replaceFirst("^([^/]*)/.*$", "$1"));
        } else {
            return Optional.empty();
        }
    }

    public String parseBranchName(String refName, BranchType type) {
        if (type == BranchType.remote) {
            return removePrefix(refName).replaceFirst("^[^/]*/(.*)$", "$1");
        } else {
            return removePrefix(refName).replaceFirst("^(.*)$", "$1");
        }
    }

    public static String removePrefix(String branchName) {
        return branchName.replaceFirst("^" + Constants.R_HEADS + "|" + "^" + Constants.R_REMOTES, "");
    }

    @Override
    public int compareTo(BranchInfo o) {
        return this.email.compareTo(o.email);
    }
}
