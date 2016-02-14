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
            obsoleteness = parseObsoleteness(args);
            action = Action.list;
            branchType = BranchType.remote;
            key = Optional.empty();
        } else if (args.length == 2) {
            obsoleteness = parseObsoleteness(args);
            action = parseAction(args);
            branchType = BranchType.remote;
            key = Optional.empty();
        } else if (args.length == 3) {
            obsoleteness = parseObsoleteness(args);
            action = parseAction(args);
            branchType = parseBranchType(args);
            key = Optional.empty();
        } else if (args.length == 4) {
            obsoleteness = parseObsoleteness(args);
            action = parseAction(args);
            branchType = parseBranchType(args);
            key = parseKey(args);
        } else {
            System.err.println("Usage: ");
            System.err.println("  gpn [number of days to obsolete day] [--list|--remove] [--local|--remote] [key path]");
            throw new IllegalArgumentException("Invalid arguments.");
        }
    }

    private Optional<Path> parseKey(String[] args) {
        return Optional.of(Paths.get(args[3]));
    }

    private BranchType parseBranchType(String[] args) {
        return BranchType.valueOf(args[2].substring(2));
    }

    private Action parseAction(String[] args) {
        return Action.valueOf(args[1].substring(2));
    }

    private Obsoleteness parseObsoleteness(String[] args) {
        return new Obsoleteness(Integer.valueOf(args[0]));
    }
}
