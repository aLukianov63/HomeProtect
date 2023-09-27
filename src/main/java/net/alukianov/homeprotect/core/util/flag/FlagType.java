package net.alukianov.homeprotect.core.util.flag;

public enum FlagType {

    NONE("none"),
    BUILD("build"),
    BREAK("break"),
    VAULT("vault"),
    INTERACT("inter"),
    EXPLODE("explode"),
    PVP("pvp"),
    MONSTER("monster"),
    MOB("mob"),
    SPREAD("spread"),
    PLANTS("plant"),
    DROP("drop"),
    CHAT("chat"),
    COMMAND("cmd"),
    MOVE("move");
    public final String type;

    FlagType(String type) {
        this.type = type;
    }

}
