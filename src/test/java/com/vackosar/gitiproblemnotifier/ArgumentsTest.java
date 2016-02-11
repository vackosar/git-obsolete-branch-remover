package com.vackosar.gitiproblemnotifier;

import com.vackosar.gitproblemnotifier.control.Arguments;
import org.junit.Test;

public class ArgumentsTest {
    @Test
    public void parse() {
        new Arguments(new String[] {"30"});
        new Arguments(new String[] {"-i", "/", "30"});
    }
}
