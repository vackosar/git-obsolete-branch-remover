package com.vackosar.gitobsoletebranchremover.control;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;

public class EnumOptionGroup<T extends Enum> {

    private final Class<T> enumeration;
    private final T defaultValue;
    private final OptionGroup group;

    public EnumOptionGroup(Class<T> enumeration, T defaultValue) {
        this.enumeration = enumeration;
        this.defaultValue = defaultValue;
        group = createGroup();
    }

    private Option option(Enum e) {
        return OptionBuilder.withLongOpt(e.name()).create(e.name());
    }

    private OptionGroup createGroup() {
        OptionGroup group = new OptionGroup();
        for (Enum value: enumeration.getEnumConstants()) {
            group.addOption(option(value));
        }
        return group;
    }

    public OptionGroup group() {
        return group;
    }

    public T getSelected() {
        if (group.getSelected() == null) {
            return defaultValue;
        } else {
            return parseSelected();
        }
    }

    private T parseSelected() {
        for (T value: enumeration.getEnumConstants()) {
            if (value.name().equals(group.getSelected())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid selected " + group.getSelected());
    }
}
