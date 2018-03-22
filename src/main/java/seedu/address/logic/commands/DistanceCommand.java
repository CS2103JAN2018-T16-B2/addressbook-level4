package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.RouteOptimization;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;

/**
 * Finds the distance from the headquarter to a person address
 * using his or her last displayed index from the address book.
 */
public class DistanceCommand extends Command {

    public static final String COMMAND_WORD = "distance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds the distance from the headquarter to a person address "
            + "by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DISTANCE_PERSON_SUCCESS = "Distance from Head quarter to this Person: %1$s km";

    private final Index targetIndex;

    public DistanceCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        int indexZeroBased = targetIndex.getZeroBased();
        Person person = lastShownList.get(indexZeroBased);
        String address = person.getAddress().toString();
        RouteOptimization route = new RouteOptimization();
        String headQuarterAddress = "Kent Ridge MRT";

        try {
            Double distance = route.getDistance(headQuarterAddress, address);
            return new CommandResult(String.format(MESSAGE_DISTANCE_PERSON_SUCCESS, distance));
        } catch (Exception e) {
            throw new CommandException(Messages.MESSAGE_PERSON_ADDRESS_CANNOT_FIND);
        }


    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DistanceCommand // instanceof handles nulls
                && this.targetIndex.equals(((DistanceCommand) other).targetIndex)); // state check
    }
}
