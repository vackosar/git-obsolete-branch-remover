package com.vackosar.gitobsoletebranchremover.entity;

import com.vackosar.gitobsoletebranchremover.control.Obsoleteness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Arguments {

    private Logger log = LoggerFactory.getLogger(getClass());

    public final Obsoleteness obsoleteness;
    public final Action action;
    public final BranchType branchType;
    public final Optional<Path> key;

    public Arguments(String[] args) {
        try {
            if (!(args.length >= 1) || !(args.length <= 4)) {
                throw new IllegalArgumentException("Wrong number of arguments.");
            }
            obsoleteness = parseObsoleteness(args);
            action = parseAction(args);
            branchType = parseBranchType(args);
            key = parseKey(args);
        } catch (Exception e) {
            System.err.println("Usage: ");
            System.err.println("  gpn [number of days to obsolete day] [--list|--remove] [--local|--remote] [key path]");
            throw new IllegalArgumentException("Invalid arguments.", e);
        }
        log.info("Performing " + action + " of " + branchType + " branches " + obsoleteness + ".");
    }

    private Optional<Path> parseKey(String[] args) {
        if (args.length >= 4) {
            return Optional.of(Paths.get(args[3]));
        } else {
            return Optional.empty();
        }
    }

    private BranchType parseBranchType(String[] args) {
        if (args.length >= 3) {
            return BranchType.valueOf(args[2].substring(2));
        } else {
            return BranchType.local;
        }
    }

    private Action parseAction(String[] args) {
        if (args.length >= 2) {
            return Action.valueOf(args[1].substring(2));
        } else {
            return Action.list;
        }
    }

    private Obsoleteness parseObsoleteness(String[] args) {
        return new Obsoleteness(Integer.valueOf(args[0]));
    }
}
