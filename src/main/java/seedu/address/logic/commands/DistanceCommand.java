package seedu.address.logic.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.commons.events.ui.ShowRouteFromHeadQuarterToOneEvent;
import seedu.address.commons.events.ui.ShowRouteFromOneToAnotherEvent;
import seedu.address.logic.GetDistance;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;

//@@author ncaminh
/**
 * Finds the distance from the headquarter to a person address
 * using his or her last displayed index from the address book.
 */
public class DistanceCommand extends Command {

    public static final String COMMAND_WORD = "distance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds the distance from the headquarter to a person address "
            + "or the distance from a person address to another person address "
            + "by the index number(s) used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1 2";

    public static final String MESSAGE_DISTANCE_FROM_HQ_FAILURE = "Unable to find %1$s's address";
    public static final String MESSAGE_DISTANCE_FROM_HQ_SUCCESS = "Distance from Head quarter to %1$s: %2$s km";
    public static final String MESSAGE_DISTANCE_FROM_PERSON_FAILURE = "Unable to find at least one person's address";
    public static final String MESSAGE_DISTANCE_FROM_PERSON_SUCCESS = "Distance from %1$s to %2$s: %3$s km";

    private Index targetIndexOrigin = null;
    private Index targetIndexDestination;

    /**
     * constructor for DistanceCommand calculating distance from HQ to a person
     * @param targetIndex
     */
    public DistanceCommand(Index targetIndex) {
        this.targetIndexDestination = targetIndex;
    }

    /**
     * constructor for DistanceCommand calculating distance from a person to another person
     * @param targetIndexOrigin
     * @param targetIndexDestination
     */
    public DistanceCommand(Index targetIndexOrigin, Index targetIndexDestination) {
        this.targetIndexOrigin = targetIndexOrigin;
        this.targetIndexDestination = targetIndexDestination;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();
        String origin;
        String destination;
        String personNameOrigin = "";
        String personNameDestination = "";

        //case 1: get distance from HQ to a person address
        if (targetIndexOrigin == null) {
            if (targetIndexDestination.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            int indexZeroBasedDestination = targetIndexDestination.getZeroBased();
            Person person = lastShownList.get(indexZeroBasedDestination);
            String personName = person.getName().toString();
            origin = "Kent Ridge MRT";
            destination = person.getAddress().toString();

            //Trim address
            if (destination.indexOf('#') > 2) {
                int stringCutIndex;
                stringCutIndex = destination.indexOf('#') - 2;
                destination = destination.substring(0, stringCutIndex);
            }

            GetDistance route = new GetDistance();
            Double distance = route.getDistance(origin, destination);

            EventsCenter.getInstance().post(new ShowRouteFromHeadQuarterToOneEvent(destination));

            if (distance == -1) {
                return new CommandResult(String.format(MESSAGE_DISTANCE_FROM_HQ_FAILURE,
                        personName));
            }

            return new CommandResult(String.format
                    (MESSAGE_DISTANCE_FROM_HQ_SUCCESS, personName, distance));

        } else {
            //case 2: get distance from a person address to another person address
            if (targetIndexOrigin.getZeroBased() >= lastShownList.size()
                    || targetIndexDestination.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            int indexZeroBasedOrigin = targetIndexOrigin.getZeroBased();
            int indexZeroBasedDestination = targetIndexDestination.getZeroBased();
            Person personOrigin = lastShownList.get(indexZeroBasedOrigin);
            Person personDestination = lastShownList.get(indexZeroBasedDestination);
            origin = personOrigin.getAddress().toString();
            destination = personDestination.getAddress().toString();

            //Trim addresses
            if (origin.indexOf('#') > 2) {
                int stringCutIndex;
                stringCutIndex = origin.indexOf('#') - 2;
                origin = origin.substring(0, stringCutIndex);
            }

            if (destination.indexOf('#') > 2) {
                int stringCutIndex;
                stringCutIndex = destination.indexOf('#') - 2;
                destination = destination.substring(0, stringCutIndex);
            }

            personNameOrigin = personOrigin.getName().toString();
            personNameDestination = personDestination.getName().toString();

            GetDistance route = new GetDistance();
            Double distance = route.getDistance(origin, destination);

            List<String> addressesList = new ArrayList<>();
            addressesList.add(origin);
            addressesList.add(destination);
            EventsCenter.getInstance().post(new ShowRouteFromOneToAnotherEvent(addressesList));

            if (distance == -1) {
                return new CommandResult(String.format(MESSAGE_DISTANCE_FROM_PERSON_FAILURE));
            }

            return new CommandResult(String.format(
                    MESSAGE_DISTANCE_FROM_PERSON_SUCCESS, personNameOrigin, personNameDestination, distance));

        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DistanceCommand // instanceof handles nulls
                && this.targetIndexDestination.equals(((DistanceCommand) other).targetIndexDestination)); // state check
    }
}
