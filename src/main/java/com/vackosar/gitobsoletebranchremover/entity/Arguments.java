package com.vackosar.gitobsoletebranchremover.entity;

import com.vackosar.gitobsoletebranchremover.control.EnumOptionGroup;
import com.vackosar.gitobsoletebranchremover.control.Obsoleteness;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Arguments {

    public static final Option KEY = OptionBuilder.hasArg(true).withArgName("path to private key").withLongOpt("key").create("k");

    private Logger log = LoggerFactory.getLogger(getClass());

    public final Obsoleteness obsoleteness;
    public final Action action;
    public final BranchType branchType;
    public final Optional<Path> key;
    private EnumOptionGroup<Action> actionGroup;
    private EnumOptionGroup<BranchType> branchTypeGroup;

    public Arguments(String[] args) {
        CommandLine line;
        try {
            line = createCommandLine(args);
            checkArgSize(line);
            obsoleteness = parseObsoleteness(line);
            action = actionGroup.getSelected();
            branchType = branchTypeGroup.getSelected();
            key = parseKey(line);
        } catch (Exception e) {
            new HelpFormatter().printHelp("  gpn [number of days to obsolete day] [OPTIONS]", createOptions());
            System.exit(1);
            throw new RuntimeException(e);
        }
        log.info("Performing " + action + " of " + branchType + " branches " + obsoleteness + ".");
    }

    private Options createOptions() {
        actionGroup = new EnumOptionGroup(Action.class, Action.list);
        branchTypeGroup = new EnumOptionGroup(BranchType.class, BranchType.local);
        return new Options()
                .addOptionGroup(actionGroup.group())
                .addOptionGroup(branchTypeGroup.group())
                .addOption(KEY);
    }

    private CommandLine createCommandLine(String[] args) throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new BasicParser();
        return parser.parse(options, args);
    }

    private void checkArgSize(CommandLine line) {
        String[] args = line.getArgs();

        if (!(args.length >= 1) || !(args.length <= 4)) {
            throw new IllegalArgumentException("Wrong number of arguments.");
        }
    }

    private Optional<Path> parseKey(CommandLine line) {
        if (line.hasOption(KEY.getOpt())) {
            return Optional.of(Paths.get(line.getOptionValue(KEY.getOpt())));
        } else {
            return Optional.empty();
        }
    }

    private Obsoleteness parseObsoleteness(CommandLine line) {
        return new Obsoleteness(Integer.valueOf(line.getArgs()[0]));
    }
}
