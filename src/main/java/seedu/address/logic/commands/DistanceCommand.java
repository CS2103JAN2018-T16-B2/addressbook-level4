package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.GetDistance;
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

    public static final String MESSAGE_DISTANCE_FROM_HQ_SUCCESS = "Distance from Head quarter to this Person: %1$s km";
    public static final String MESSAGE_DISTANCE_FROM_PERSON_SUCCESS = "Distance from %1$s to %2$s: %3$s km";

    private Index targetIndex_origin = null;
    private Index targetIndex_destination;

    /**
     * constructor for DistanceCommand calculating distance from HQ to a person
     * @param targetIndex
     */
    public DistanceCommand(Index targetIndex) {
        this.targetIndex_destination = targetIndex;
    }

    /**
     * constructor for DistanceCommand calculating distance from a person to another person
     * @param targetIndex_origin
     * @param targetIndex_destination
     */
    public DistanceCommand(Index targetIndex_origin, Index targetIndex_destination) {
        this.targetIndex_origin = targetIndex_origin;
        this.targetIndex_destination = targetIndex_destination;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();
        String origin;
        String destination;
        String personName_origin = "";
        String personName_destination = "";
        if (targetIndex_origin == null) {
            if (targetIndex_destination.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            int indexZeroBased_destination = targetIndex_destination.getZeroBased();
            Person person = lastShownList.get(indexZeroBased_destination);
            origin = "Kent Ridge MRT";
            destination = person.getAddress().toString();
        }

        else {
            if (targetIndex_origin.getZeroBased() >= lastShownList.size()
                    || targetIndex_destination.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            int indexZeroBased_origin = targetIndex_origin.getZeroBased();
            int indexZeroBased_destination = targetIndex_destination.getZeroBased();
            Person person_origin = lastShownList.get(indexZeroBased_origin);
            Person person_destination = lastShownList.get(indexZeroBased_destination);
            origin = person_origin.getAddress().toString();
            destination = person_destination.getAddress().toString();
            personName_origin = person_origin.getName().toString();
            personName_destination = person_destination.getName().toString();
        }

        try {
            GetDistance route = new GetDistance();
            Double distance = route.getDistance(origin, destination);
            return targetIndex_origin == null ? new CommandResult(String.format(MESSAGE_DISTANCE_FROM_HQ_SUCCESS, distance))
                    : new CommandResult(String.format(MESSAGE_DISTANCE_FROM_PERSON_SUCCESS, personName_origin, personName_destination, distance));
        } catch (Exception e) {
            throw new CommandException(Messages.MESSAGE_PERSON_ADDRESS_CANNOT_FIND);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DistanceCommand // instanceof handles nulls
                && this.targetIndex_destination.equals(((DistanceCommand) other).targetIndex_destination)); // state check
    }
}
