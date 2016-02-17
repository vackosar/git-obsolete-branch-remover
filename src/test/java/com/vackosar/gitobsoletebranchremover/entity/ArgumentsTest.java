package com.vackosar.gitobsoletebranchremover.entity;

import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import org.junit.Test;

public class ArgumentsTest {
    @Test
    public void parse() {
        new Arguments(new String[] {"30"});
        new Arguments(new String[] {"30", "--list"});
        new Arguments(new String[] {"30", "--list", "--remote"});
    }
}
