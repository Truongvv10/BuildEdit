package com.xironite.buildedit.models.enums;

public enum ConfigSection {

    PREFIX("prefix"),

    // General Commands

    TARGET_HELP("messages.commands.help.target"),

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

    SYNTAX_HELP("messages.commands.help.syntax"),
    DESC_HELP("messages.commands.help.description"),

    // Edit Commands

    SELECTION_POS1("messages.positions.pos1.selected"),
    SELECTION_POS2("messages.positions.pos2.selected"),
    NOT_SELECTION_POS1("messages.positions.pos1.not-selected"),
    NOT_SELECTION_POS2("messages.positions.pos2.not-selected"),

    SYNTAX_SET("messages.commands.set.syntax"),
    DESC_SET("messages.commands.set.description"),

    SYNTAX_WALLS("messages.commands.walls.syntax"),
    DESC_WALLS("messages.commands.walls.description"),

    SYNTAX_REPLACE("messages.commands.replace.syntax"),
    DESC_REPLACE("messages.commands.replace.description"),

    // Clipboard Commands

    SYNTAX_COPY("messages.commands.clipboard.copy.syntax"),
    DESC_COPY("messages.commands.clipboard.copy.description"),
    EXECUTOR_COPY("messages.commands.clipboard.copy.executor"),

    SYNTAX_PASTE("messages.commands.clipboard.paste.syntax"),
    DESC_PASTE("messages.commands.clipboard.paste.description"),
    EXECUTOR_PASTE("messages.commands.clipboard.paste.executor"),

    SYNTAX_ROTATE("messages.commands.clipboard.rotate.syntax"),
    DESC_ROTATE("messages.commands.clipboard.rotate.description"),
    EXECUTOR_ROTATE("messages.commands.clipboard.rotate.executor"),

    CLIPBOARD_STATUS("messages.commands.clipboard.status"),
    CLIPBOARD_COPYING("messages.commands.clipboard.action.copy"),
    CLIPBOARD_PASTING("messages.commands.clipboard.action.paste"),
    CLIPBOARD_ROTATING("messages.commands.clipboard.action.rotate"),
    CLIPBOARD_NONE("messages.commands.clipboard.action.none"),
    CLIPBOARD_COMPLETED("messages.commands.clipboard.action.completed"),
    CLIPBOARD_FAILED("messages.commands.clipboard.action.failed"),

    // Status Messages

    ACTION_STATUS_START("messages.status.start"),
    ACTION_STATUS_FINISH("messages.status.finish"),
    ACTION_STATUS_FAILED("messages.status.failed"),
    ACTION_STATUS_ONGOING("messages.status.ongoing"),

    // Action Messages

    ACTION_ERROR("messages.error"),
    ACTION_ERROR_NUMBER("messages.error-number"),
    ACTION_INVALID("messages.invalid"),
    ACTION_INVALID_BLOCKS("messages.invalid-blocks"),
    ACTION_OFFLINE("messages.offline"),
    ACTION_MAX_SIZE("messages.max-size"),
    ACTION_MISSING("messages.missing.target"),
    ACTION_MISSING_DELIMITER("messages.missing.delimiter"),
    ACTION_MISSING_SEPARATOR("messages.missing.separator"),
    ACTION_NO_USAGES("messages.no-usages"),
    ACTION_NO_WAND("messages.no-wand"),
    ACTION_NO_PERMISSION("messages.no-permission"),
    ACTION_INVALID_WORLD("messages.invalid-world"),

    // Base Item

    ITEM_MATERIAL("material"),
    ITEM_DISPLAY("name"),
    ITEM_LORE("lore"),
    ITEM_MODEL("model"),
    ITEM_ENCHANTS("enchants"),
    ITEM_FLAGS("flags"),
    ITEM_SLOT("slot"),

    // Wand Item

    ITEM_WANDS("wands"),
    ITEM_WAND_MAX_SIZE("limits.max-size"),
    ITEM_WAND_MAX_SECONDS("limits.max-seconds"),
    ITEM_WAND_USAGES("limits.usages"),
    ITEM_WAND_PERMISSION("permission"),
    ITEM_WAND_WORLDS("worlds"),
    ITEM_WAND_RECIPE("recipe"),
    ITEM_WAND_SELECTION_MESSAGE("settings.show-selection"),
    ITEM_WAND_TIMINGS_MESSAGE("settings.show-timings"),

    // Hooks

    HOOKS_PACKET_EVENT_ENABLED("plugins.packet-events.enabled"),
    HOOKS_WORLD_GUARD_ENABLED("plugins.world-guard.enabled"),;

    public final String value;
    ConfigSection(String value) {
        this.value = value;
    }
}
