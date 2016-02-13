package com.vackosar.gitproblemnotifier.entity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Arguments {

    public final Optional<Path> key;
    public final Integer days;

    public Arguments(String[] args) {
        if (isSingleParam(args)) {
            key = Optional.empty();
            days = Integer.valueOf(args[0]);
        } else if (isThreeParam(args)) {
            key = Optional.of(Paths.get(args[1]));
            days = Integer.valueOf(args[2]);
        } else {
            days = null;
            key = null;
            System.err.println("Usage examples: ");
            System.err.println("  gpn [number of days to obsolete day]");
            System.err.println("  gpn -i [key file path] [number of days to obsolete day]");
            Runtime.getRuntime().exit(1);
        }
    }

    private static boolean isThreeParam(String[] args) {
        return args.length == 3 && "-i".equals(args[0]) && Paths.get(args[1]).toFile().exists();
    }

    private static boolean isSingleParam(String[] args) {
        return args.length == 1;
    }

}
