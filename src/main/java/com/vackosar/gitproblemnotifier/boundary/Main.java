package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vackosar.gitproblemnotifier.control.BranchInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws GitAPIException {
        Path key;
        if (isSingleParam(args)) {
            key = Module.NO_KEY;
        } else if (isThreeParam(args)) {
            key = Paths.get(args[1]);
        } else {
            System.err.println("Usage examples: ");
            System.err.println("  gpn [number of days to obsolete day]");
            System.err.println("  gpn -i [key file path] [number of days to obsolete day]");
            Runtime.getRuntime().exit(1);
            return;
        }
        Integer days = Integer.valueOf(args[0]);
        final Injector injector = Guice.createInjector(new Module(days, key));
        injector
                .getInstance(ObsoleteBranchesLister.class)
                .listObsolete().stream()
                .map(BranchInfo::convertToOutputLine)
                .forEach(System.out::println);
        closeGit(injector);
    }

    private static boolean isThreeParam(String[] args) {
        return args.length == 3 && "-i".equals(args[0]) && Paths.get(args[1]).toFile().exists();
    }

    private static boolean isSingleParam(String[] args) {
        return args.length == 1;
    }

    private static void closeGit(Injector injector) {
        final Git git = injector.getInstance(Git.class);
        git.getRepository().close();
        git.close();
    }
}

