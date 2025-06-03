package com.xironite.buildedit.models.enums;

public enum ConfigSection {

    PREFIX("prefix"),

    TARGET_HELP("messages.commands.help.target"),


    SELECTION_POS1("messages.positions.pos1.selected"),
    SELECTION_POS2("messages.positions.pos2.selected"),
    NOT_SELECTION_POS1("messages.positions.pos1.not-selected"),
    NOT_SELECTION_POS2("messages.positions.pos2.not-selected"),

    SYNTAX_RELOAD("messages.commands.reload.syntax"),
    DESC_RELOAD("messages.commands.reload.description"),
    TARGET_RELOAD("messages.commands.reload.target"),

    SYNTAX_WAND("messages.commands.wand.syntax"),
    DESC_WAND("messages.commands.wand.description"),
    TARGET_WAND("messages.commands.wand.target"),
    EXECUTOR_WAND("messages.commands.wand.executor"),

    SYNTAX_USAGE("messages.commands.usage.syntax"),
    DESC_USAGE("messages.commands.usage.description"),
    TARGET_USAGE("messages.commands.usage.target"),

    SYNTAX_SET("messages.commands.set.syntax"),
    DESC_SET("messages.commands.set.description"),

    SYNTAX_HELP("messages.commands.help.syntax"),
    DESC_HELP("messages.commands.help.description"),

    ACTION_STATUS_START("messages.status.start"),
    ACTION_STATUS_FINISH("messages.status.finish"),
    ACTION_STATUS_FAILED("messages.status.failed"),
    ACTION_STATUS_ONGOING("messages.status.ongoing"),

    ACTION_ERROR("messages.error"),
    ACTION_ERROR_NUMBER("messages.error-number"),
    ACTION_INVALID("messages.invalid"),
    ACTION_OFFLINE("messages.offline"),
    ACTION_MAX_SIZE("messages.max-size"),
    ACTION_MISSING("messages.missing.target"),
    ACTION_MISSING_DELIMITER("messages.missing.delimiter"),
    ACTION_MISSING_SEPARATOR("messages.missing.separator"),
    ACTION_NO_USAGES("messages.no-usages"),
    ACTION_NO_WAND("messages.no-wand"),
    ACTION_NO_PERMISSION("messages.no-permission"),

    ITEM_MATERIAL("material"),
    ITEM_DISPLAY("name"),
    ITEM_LORE("lore"),
    ITEM_MODEL("model"),
    ITEM_ENCHANTS("enchants"),
    ITEM_FLAGS("flags"),
    ITEM_SLOT("slot"),

    ITEM_WANDS("wands"),
    ITEM_WAND_MAX_SIZE("max-size"),
    ITEM_WAND_USAGES("usages"),
    ITEM_WAND_PERMISSION("permission"),
    ITEM_WAND_WORLDS("worlds"),
    ;

    public final String value;
    ConfigSection(String value) {
        this.value = value;
    }
}
