package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.GetDistance;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.ui.testutil.EventsCollectorRule;

/**
 * Contains integration tests (interaction with the Model) for {@code DistanceCommand}.
 */
public class DistanceCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        List<Person> lastShownList = model.getFilteredPersonList();
        Index lastPersonIndex = Index.fromOneBased(lastShownList.size());

        Person firstPerson = lastShownList.get(0);
        Person secondPerson = lastShownList.get(1);
        Person lastPerson = lastShownList.get(lastPersonIndex.getZeroBased());

        assertOnePersonExecutionSuccess(firstPerson, INDEX_FIRST_PERSON);
        assertOnePersonExecutionSuccess(secondPerson, INDEX_SECOND_PERSON);
        assertOnePersonExecutionSuccess(lastPerson, lastPersonIndex);

        assertTwoPersonExecutionSuccess(firstPerson, INDEX_FIRST_PERSON, secondPerson, INDEX_SECOND_PERSON);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        List<Person> lastShownList = model.getFilteredPersonList();

        assertOnePersonExecutionSuccess(lastShownList.get(0), INDEX_FIRST_PERSON);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundsIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        SelectCommand selectFirstCommand = new SelectCommand(INDEX_FIRST_PERSON);
        SelectCommand selectSecondCommand = new SelectCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(selectFirstCommand.equals(selectFirstCommand));

        // same values -> returns true
        SelectCommand selectFirstCommandCopy = new SelectCommand(INDEX_FIRST_PERSON);
        assertTrue(selectFirstCommand.equals(selectFirstCommandCopy));

        // different types -> returns false
        assertFalse(selectFirstCommand.equals(1));

        // null -> returns false
        assertFalse(selectFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(selectFirstCommand.equals(selectSecondCommand));
    }

    /**
     * Executes a {@code DistanceCommand} with the given {@code person and index}
     */
    private void assertOnePersonExecutionSuccess(Person person, Index index) {
        DistanceCommand distanceCommand = prepareOnePersonCommand(index);

        try {
            CommandResult commandResult = distanceCommand.execute();
            String address = person.getAddress().toString();
            String headQuarterAddress = "Kent Ridge MRT";
            GetDistance route = new GetDistance();
            Double distance = route.getDistance(headQuarterAddress, address);

            assertEquals(String.format(DistanceCommand.MESSAGE_DISTANCE_FROM_HQ_SUCCESS, distance),
                    commandResult.feedbackToUser);
        } catch (Exception ce) {
            System.out.println(ce.getMessage());
            throw new IllegalArgumentException("Execution of command should not fail.", ce);
        }
    }

    /**
     * Executes a {@code DistanceCommand} with the given {@code persons and indexes}
     */
    private void assertTwoPersonExecutionSuccess(Person personAtOrigin, Index originIndex,
                                                 Person personAtDestination, Index destinationIndex) {
        DistanceCommand distanceCommand = prepareTwoPersonsCommand(originIndex, destinationIndex);

        try {
            CommandResult commandResult = distanceCommand.execute();
            String addressOrigin = personAtOrigin.getAddress().toString();
            String addressDestination = personAtDestination.getAddress().toString();
            String nameOrigin = personAtOrigin.getName().fullName;
            String nameDestination = personAtDestination.getName().fullName;
            GetDistance route = new GetDistance();
            Double distance = route.getDistance(addressOrigin, addressDestination);

            assertEquals(String.format(DistanceCommand.MESSAGE_DISTANCE_FROM_PERSON_SUCCESS,
                    nameOrigin, nameDestination, distance),
                    commandResult.feedbackToUser);
        } catch (Exception ce) {
            System.out.println(ce.getMessage());
            throw new IllegalArgumentException("Execution of command should not fail.", ce);
        }
    }

    /**
     * Executes a {@code DistanceCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        DistanceCommand distanceCommand = prepareOnePersonCommand(index);

        try {
            distanceCommand.execute();
            fail("The expected CommandException was not thrown.");
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        }
    }

    /**
     * Returns a {@code DistanceCommand} with one parameter {@code index}.
     */
    private DistanceCommand prepareOnePersonCommand(Index index) {
        DistanceCommand distanceCommand = new DistanceCommand(index);
        distanceCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return distanceCommand;
    }

    /**
     * Returns a {@code DistanceCommand} with two parameters {@code index}.
     */
    private DistanceCommand prepareTwoPersonsCommand(Index originIndex, Index destinationIndex) {
        DistanceCommand distanceCommand = new DistanceCommand(originIndex, destinationIndex);
        distanceCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return distanceCommand;
    }
}
