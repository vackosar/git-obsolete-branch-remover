package com.vackosar.gitproblemnotifier.boundary;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
    public static void main(String[] args) throws GitAPIException {
        Injector injector = Guice.createInjector(new Module(args[0]));
        injector.getInstance(ObsoleteBranchesLister.class)
                .listObsolete().stream()
                .map(branchInfo -> branchInfo.toString())
                .forEach(System.out::println);
    }
}
