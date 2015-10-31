package com.vackosar.gitproblemnotifier.boundary;

import com.google.common.base.Functions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vackosar.gitproblemnotifier.control.BranchInfo;

import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new Module(args[0]));
        injector.getInstance(ObsoleteBranchesLister.class)
                .listObsolete().stream()
                .map(branchInfo -> branchInfo.toString())
                .forEach(System.out::println);
    }
}
