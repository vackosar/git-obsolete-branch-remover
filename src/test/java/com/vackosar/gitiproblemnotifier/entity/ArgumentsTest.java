package com.vackosar.gitiproblemnotifier.entity;

import com.vackosar.gitproblemnotifier.entity.Arguments;
import org.junit.Test;

public class ArgumentsTest {
    @Test
    public void parse() {
        new Arguments(new String[] {"30"});
        new Arguments(new String[] {"-i", "/", "30"});
    }
}
