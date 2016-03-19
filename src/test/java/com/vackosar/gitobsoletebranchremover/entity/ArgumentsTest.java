package com.vackosar.gitobsoletebranchremover.entity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.security.Permission;

public class ArgumentsTest {
    @Test public void parse() {
        new Arguments(new String[] {"30"});
        new Arguments(new String[] {"30", "--list"});
        new Arguments(new String[] {"30", "--list", "--remote"});
    }

    @Test(expected = ExitException.class) public void stopAndPrintHelp() throws IOException {
        new Arguments(new String[] {"XX"});
    }

    protected static class ExitException extends SecurityException {}

    private static class NoExitSecurityManager extends SecurityManager {
        @Override public void checkPermission(Permission perm) {}
        @Override public void checkPermission(Permission perm, Object context) {}
        @Override public void checkExit(int status) {throw new ExitException();}
    }

    @BeforeClass
    public static void setUp() throws Exception {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.setSecurityManager(null);
    }

}
