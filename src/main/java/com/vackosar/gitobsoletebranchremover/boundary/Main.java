package com.vackosar.gitobsoletebranchremover.boundary;

import com.google.inject.Guice;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
    public static void main(String[] args) throws GitAPIException {
        Guice
                .createInjector(new Module(args))
                .getInstance(Executor.class)
                .execute();
    }
}

