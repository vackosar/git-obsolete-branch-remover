package com.vackosar.gitproblemnotifier.entity;

import com.vackosar.gitproblemnotifier.boundary.Module;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Arguments {

    public final Path key;
    public final Integer days;

    public Arguments(String[] args) {
        if (isSingleParam(args)) {
            key = Module.NO_KEY;
            days = Integer.valueOf(args[0]);
        } else if (isThreeParam(args)) {
            key = Paths.get(args[1]);
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
