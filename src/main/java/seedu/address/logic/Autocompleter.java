package seedu.address.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.UndoCommand;

/**
 * Implements methods to autocomplete commands and fields in a user query
 */
public class Autocompleter {

    private Logic logic;

    private final Set<String> commandsList = new HashSet<>();
    private Set<String> names;
    private Set<String> phones;
    private Set<String> emails;
    private Set<String> adresses;

    public Autocompleter(Logic logic) {
        this.logic = logic;
        updateCommands();
        updateFields();
    }

    /**
     * @param input the user query
     * @return the longest possible prefix to append to the last word of the query
     */
    public String autocomplete(String input) {

        String[] words = input.trim().split(" ");
        //List<String>
        Set<String> possibilities = new HashSet<>();

        //trying to complete a command
        if (words.length == 1 && words[0].length() > 0) {
            possibilities = generatePossibleSuffixes(words[0], commandsList);
        } else if (words.length > 1) { //trying to complete a field

            int lastFieldIndex = input.lastIndexOf('/');
            //testing wether the field is an option starting by 'x/'
            if (lastFieldIndex > 0 && input.substring(lastFieldIndex).length() > 0) {
                String field = input.substring(lastFieldIndex + 1);
                Set<String> fieldsList;

                switch (input.charAt(lastFieldIndex - 1)) {
                case 'n':
                    fieldsList = names;
                    break;

                case 'p':
                    fieldsList = phones;
                    break;

                case 'e':
                    fieldsList = emails;
                    break;

                case 'a':
                    fieldsList = adresses;
                    break;

                default:
                    fieldsList = new HashSet<>();
                    break;
                }

                possibilities = generatePossibleSuffixes(field, fieldsList);
            } else {
                //the last word of the query doesn't correspond to a command
                // and doesn't correspond to an option starting with 'x/'
                //so we're trying to match the last word to a name of the adress book

                possibilities = generatePossibleSuffixes(words[words.length - 1], names);
                if (possibilities.size() == 0) {
                    possibilities = generatePossibleSuffixes(words[words.length - 1], generateNames());
                }
            }
        }
        String longestCommonPrefix = "";

        if (possibilities.size() > 0) {
            String p = (String) possibilities.toArray()[0];

            for (int i = 0; i < p.length(); i++) {
                boolean isPrefixOkay = true;

                for (String possibility : possibilities) {
                    if (!possibility.startsWith(longestCommonPrefix + p.charAt(i))) {
                        isPrefixOkay = false;
                    }
                }
                if (!isPrefixOkay) {
                    break;
                }

                longestCommonPrefix += p.charAt(i);
            }
        }
        return longestCommonPrefix;
    }

    /**
     *
     * @param lastWord the last word of the query we're trying to complete
     * @param possibleWords the set of all possible words according to the query type
     * @return the set of all possible suffixes to the last word
     */
    private Set<String> generatePossibleSuffixes(String lastWord, Set<String> possibleWords) {
        Set<String> suffixes = new HashSet<>();

        for (String word : possibleWords) {
            if (word.startsWith(lastWord)) {
                //we only append the end of the command
                suffixes.add(word.substring(lastWord.length()));
            }
        }
        return suffixes;
    }

    /**
     * Update the sets of fields
     */
    private void updateFields() {
        names = logic.getFilteredPersonList().stream()
                .map(person -> person.getName().toString()).collect(Collectors.toSet());
        phones = logic.getFilteredPersonList().stream()
                .map(person -> person.getPhone().toString()).collect(Collectors.toSet());
        emails = logic.getFilteredPersonList().stream()
                .map(person -> person.getEmail().toString()).collect(Collectors.toSet());
        adresses = logic.getFilteredPersonList().stream()
                .map(person -> person.getAddress().toString()).collect(Collectors.toSet());
    }

    /**
     * Updates the set of commands
     */
    private void updateCommands() {
        commandsList.addAll(Arrays.asList(AddCommand.COMMAND_WORD,
                ClearCommand.COMMAND_WORD,
                DeleteCommand.COMMAND_WORD,
                EditCommand.COMMAND_WORD,
                ExitCommand.COMMAND_WORD,
                FindCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD,
                HistoryCommand.COMMAND_WORD,
                ListCommand.COMMAND_WORD,
                RedoCommand.COMMAND_WORD,
                SelectCommand.COMMAND_WORD,
                UndoCommand.COMMAND_WORD));
    }

    /**
     *
     * @return the set of all the different words included in the set of names
     */
    private Set<String> generateNames() {
        Set<String> names = new HashSet<>();

        for (String fullName : this.names) {
            String[] words = fullName.split(" ");
            names.addAll(Arrays.asList(words));
        }
        return names;
    }
}
