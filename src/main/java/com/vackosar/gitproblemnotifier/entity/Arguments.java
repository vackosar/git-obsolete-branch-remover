package com.vackosar.gitproblemnotifier.entity;

import com.vackosar.gitproblemnotifier.control.Obsoleteness;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Arguments {

    public final Obsoleteness obsoleteness;
    public final Action action;
    public final BranchType branchType;
    public final Optional<Path> key;

    public Arguments(String[] args) {
        if (args.length == 1) {
            obsoleteness = new Obsoleteness(Integer.valueOf(args[0]));
            action = Action.list;
            branchType = BranchType.remote;
            key = Optional.empty();
        } else if (args.length == 2) {
            obsoleteness = new Obsoleteness(Integer.valueOf(args[0]));
            action = Action.valueOf(args[1].substring(2));
            branchType = BranchType.remote;
            key = Optional.empty();
        } else if (args.length == 3) {
            obsoleteness = new Obsoleteness(Integer.valueOf(args[0]));
            action = Action.valueOf(args[1].substring(2));
            branchType = BranchType.valueOf(args[2].substring(1));
            key = Optional.empty();
        } else if (args.length == 4) {
            obsoleteness = new Obsoleteness(Integer.valueOf(args[0]));
            action = Action.valueOf(args[1].substring(2));
            branchType = BranchType.valueOf(args[2].substring(2));
            key = Optional.of(Paths.get(args[3]));
        } else {
            System.err.println("Usage example: ");
            System.err.println("  gpn [number of days to obsolete day] [--list|--remove] [--local|--remote] [key path]");
            throw new IllegalArgumentException("Invalid arguments.");
        }
    }
}
