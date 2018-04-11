package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.GameEvent;

//@@author ncaminh
/**
 * Show game on to the display panel
 */
public class GameCommand extends Command {

    public static final String COMMAND_WORD = "game";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Play \"The snake\" game.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_GAME_MESSAGE = "Opened \"The snake\" game.";

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new GameEvent());
        return new CommandResult(SHOWING_GAME_MESSAGE);
    }
}
