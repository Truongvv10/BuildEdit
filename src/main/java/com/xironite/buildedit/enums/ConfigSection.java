package com.xironite.buildedit.enums;

public enum ConfigSection {

    PREFIX("prefix"),

    TARGET_HELP("messages.commands.help.target"),
    RELOAD("messages.commands.help"),

    SELECTION_POS1("messages.positions.pos1.selected"),
    SELECTION_POS2("messages.positions.pos2.selected"),
    NOT_SELECTION_POS1("messages.positions.pos1.not-selected"),
    NOT_SELECTION_POS2("messages.positions.pos2.not-selected"),

    SYNTAX_WAND("messages.commands.wand.syntax"),
    DESC_WAND("messages.commands.wand.description"),
    TARGET_WAND("messages.commands.wand.target"),
    EXECUTOR_WAND("messages.commands.wand.executor"),

    SYNTAX_SET("messages.commands.set.syntax"),
    DESC_SET("messages.commands.set.description"),

    SYNTAX_HELP("messages.commands.help.syntax"),
    DESC_HELP("messages.commands.help.description"),

    SYNTAX_RELOAD("messages.commands.help.syntax"),
    DESC_RELOAD("messages.commands.help.description"),

    ACTION_STATUS_START("messages.status.start"),
    ACTION_STATUS_FINISH("messages.status.finish"),
    ACTION_STATUS_FAILED("messages.status.failed"),
    ACTION_STATUS_ONGOING("messages.status.ongoing"),

    ACTION_ERROR("messages.error"),
    ACTION_INVALID("messages.invalid"),
    ACTION_OFFLINE("messages.offline"),
    ACTION_NO_PERMISSION("messages.no-permission"),
    ACTION_MISSING("messages.missing.target"),
    ACTION_MISSING_DELIMITER("messages.missing.delimiter"),
    ACTION_MISSING_SEPARATOR("messages.missing.separator"),
    ACTION_NO_WAND("messages.no-wand"),

    ITEM_WANDS("wands"),
    ITEM_WAND_MATERIAL("wands.$1.material"),
    ITEM_WAND_NAME("wands.$1.name"),
    ITEM_WAND_LORE("wands.$1.lore"),
    ITEM_WAND_USAGES("wands.$1.usages"),
    ;

    public final String value;
    ConfigSection(String value) {
        this.value = value;
    }
}
