package com.xironite.buildedit.enums;

public enum ConfigSection {

    PREFIX("prefix"),

    HELP("messages.commands.help.target"),
    RELOAD("messages.commands.help"),
    WAND("messages.commands.wand.target"),
    WAND2("messages.commands.wand.executor"),

    SELECTION_POS1("messages.positions.pos1"),
    SELECTION_POS2("messages.positions.pos2"),

    SYNTAX_WAND("messages.commands.wand.syntax"),
    DESC_WAND("messages.commands.wand.description"),
    SYNTAX_SET("messages.commands.set.syntax"),
    DESC_SET("messages.commands.set.description"),
    SYNTAX_HELP("messages.commands.help.syntax"),
    DESC_HELP("messages.commands.help.description"),
    SYNTAX_RELOAD("messages.commands.help.syntax"),
    DESC_RELOAD("messages.commands.help.description"),

    ACTION_SUCCESS("messages.success"),
    ACTION_FAILED("messages.failed"),
    ACTION_IN_PROGRESS("messages.ongoing"),
    ACTION_ERROR("messages.error"),
    ACTION_INVALID("messages.invalid"),
    ACTION_OFFLINE("messages.offline"),
    ACTION_NO_PERMISSION("messages.no-permission"),
    ACTION_MISSING("messages.missing.target"),
    ACTION_MISSING_DELIMITER("messages.missing.delimiter"),
    ACTION_MISSING_SEPARATOR("messages.missing.separator"),

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
