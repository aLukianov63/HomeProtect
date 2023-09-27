package net.alukianov.homeprotect.core.util.flag;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class Flag {

    public static final int MAX_FLAGS = 14;

    public static final FlagType[] ALL_FLAGS = new FlagType[]{
            FlagType.BUILD, FlagType.BREAK, FlagType.VAULT, FlagType.INTERACT, FlagType.EXPLODE, FlagType.PVP,
            FlagType.MONSTER, FlagType.MOB, FlagType.SPREAD, FlagType.PLANTS, FlagType.DROP, FlagType.CHAT,
            FlagType.COMMAND, FlagType.MOVE
    };

    private final FlagType type;

    public Flag(FlagType type) {
        this.type = type;
    }

    public Flag(String type) {
        if (isFlag(type)) {
            this.type = FlagType.valueOf(type);
        } else {
            this.type = FlagType.NONE;
        }
    }

    public static boolean isFlag(final String flag) {
        for (FlagType flagType : ALL_FLAGS) {
            if (flagType.type.equals(flag)) return true;
        }
        return false;
    }

    @Contract(" -> new")
    public static @NotNull @Unmodifiable Set<Flag> defaultFlags() {
        return Set.of(new Flag(FlagType.BUILD), new Flag(FlagType.BREAK), new Flag(FlagType.VAULT),
                new Flag(FlagType.INTERACT));
    }

    public FlagType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Flag flag)) return false;

        return new EqualsBuilder().append(type, flag.type).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(type).toHashCode();
    }
}


