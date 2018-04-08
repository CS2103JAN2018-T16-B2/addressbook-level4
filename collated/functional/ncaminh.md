# ncaminh
###### \java\seedu\address\commons\events\ui\ShowDefaultPageEvent.java
``` java
/**
 * Show default page
 */
public class ShowDefaultPageEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### \java\seedu\address\commons\events\ui\ShowMultiLocationFromHeadQuarterEvent.java
``` java
/**
 * Show Google map route from HQ to many locations
 */
public class ShowMultiLocationFromHeadQuarterEvent extends BaseEvent {

    public final List<String> sortedList;

    public ShowMultiLocationFromHeadQuarterEvent(List<String> sortedList) {
        this.sortedList = sortedList;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### \java\seedu\address\commons\events\ui\ShowRouteFromOneToAnotherEvent.java
``` java
/**
 * Show Google map route from HQ to many locations
 */
public class ShowRouteFromOneToAnotherEvent extends BaseEvent {

    public final List<String> sortedList;

    public ShowRouteFromOneToAnotherEvent(List<String> sortedList) {
        this.sortedList = sortedList;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### \java\seedu\address\logic\commands\DistanceCommand.java
``` java
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

    public static final String MESSAGE_DISTANCE_FROM_HQ_SUCCESS = "Distance from Head quarter to this Person: %1$s km";
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
            origin = "Kent Ridge MRT";
            destination = person.getAddress().toString();
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
            personNameOrigin = personOrigin.getName().toString();
            personNameDestination = personDestination.getName().toString();

        }

        try {
            GetDistance route = new GetDistance();
            Double distance = route.getDistance(origin, destination);

            //case 1: get distance from HQ to a person address
            if (targetIndexOrigin == null) {
                EventsCenter.getInstance().post(new JumpToListRequestEvent(targetIndexDestination));
                return new CommandResult(String.format
                        (MESSAGE_DISTANCE_FROM_HQ_SUCCESS, distance));
            } else {
                //case 2: get distance from a person address to another person address
                List<String> addressesList = new ArrayList<>();
                addressesList.add(origin);
                addressesList.add(destination);
                EventsCenter.getInstance().post(new ShowRouteFromOneToAnotherEvent(addressesList));
                return new CommandResult(String.format(
                        MESSAGE_DISTANCE_FROM_PERSON_SUCCESS, personNameOrigin, personNameDestination, distance));
            }
        } catch (Exception e) {
            throw new CommandException(Messages.MESSAGE_PERSON_ADDRESS_CANNOT_FIND);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DistanceCommand // instanceof handles nulls
                && this.targetIndexDestination.equals(((DistanceCommand) other).targetIndexDestination)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\FilterCommand.java
``` java
        int numberOfPersonsListed = model.getFilteredPersonList().size();

        //no person matches date
        if (numberOfPersonsListed == 0) {
            EventsCenter.getInstance().post(new ShowDefaultPageEvent());
            return new CommandResult(getMessageForPersonListShownSummary(numberOfPersonsListed));
        }

        //all addresses cannot be found
        if (optimizedRoute.size() == 0) {
            EventsCenter.getInstance().post(new ShowDefaultPageEvent());
            String shown = getMessageForPersonListShownSummary(numberOfPersonsListed)
                    + "\nAll the addresses on "
                    + model.getFilteredPersonList().get(0).getDate().toString()
                    + " cannot be found.";
            return new CommandResult(shown);
        }

        EventsCenter.getInstance().post(new ShowMultiLocationFromHeadQuarterEvent(optimizedRoute));
        //some addresses are invalid
        if (optimizedRoute.size() < numberOfPersonsListed) {
            String shown = getMessageForPersonListShownSummary(numberOfPersonsListed)
                    + "\nAt least one address on "
                    + model.getFilteredPersonList().get(0).getDate().toString()
                    + " cannot be found.";
            return new CommandResult(shown);
        }

        //all addresses can be found
        return new CommandResult(getMessageForPersonListShownSummary(numberOfPersonsListed));
```
###### \java\seedu\address\logic\commands\GameCommand.java
``` java
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
```
###### \java\seedu\address\logic\GetDistance.java
``` java
    public DistanceMatrix getMatrix(String origin, String destination) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDga8lhEq6jOcAm03b4GGWR65GhWOrbOxg")
                .build();

        String[] origins = {origin};
        String[] destinations = {destination};

        DistanceMatrix matrix = null;
        try {
            matrix = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    /**
     * get driving distance from origin to destination
     */
    public double getDistance(String origin, String destination) {
        if (origin.equals(destination)) {
            return 0;
        }
        String distanceWithoutUnit = "";
        DistanceMatrix matrix = null;
        matrix = getMatrix(origin, destination);
        String distance;

        try {
            distance = matrix.rows[0].elements[0].distance.toString();
        } catch (NullPointerException e) {
            return -1.0;
        }
```
###### \java\seedu\address\logic\parser\DistanceCommandParser.java
``` java
/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class DistanceCommandParser implements Parser<DistanceCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DistanceCommand
     * and returns an DistanceCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DistanceCommand parse(String args) throws ParseException {
        String[] selectedIndexes = args.split(" ");
        try {
            if (selectedIndexes.length == 3) {
                Index firstIndex = ParserUtil.parseIndex(selectedIndexes[1].trim());
                Index secondIndex = ParserUtil.parseIndex(selectedIndexes[2].trim());
                return new DistanceCommand(firstIndex, secondIndex);
            } else {
                Index index = ParserUtil.parseIndex(args);
                return new DistanceCommand(index);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DistanceCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\RouteOptimization.java
``` java
    /**
     * Check whether the address can be found by Google Map API or not
     * @param address
     * @return
     */
    private boolean isFindableAddress(Address address) {
        String addressUnderCheck = address.toString();
        GetDistance distance = new GetDistance();
        if (distance.getDistance(HQ_ADDRESS, addressUnderCheck) == -1.0) {
            return false;
        }
        return true;
    }

```
###### \java\seedu\address\MainApp.java
``` java
    /**
     * read welcome "username" message
     */
    private void readWelcomeMessage() {
        try {
            Runtime.getRuntime().exec("wscript src\\main\\resources\\scripts\\Welcome.vbs");
        } catch (IOException e) {
            try {
                String[] args = {"osascript ",
                        "say \"Welcome user\" using \"Alex\" speaking rate 140 pitch 42 modulation 60"};
                Runtime.getRuntime().exec(args);
            } catch (IOException e1) {
                logger.warning("Unable to load welcome message.");
            }
        }
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    /**
     * Show direction from Kent Ridge MRT to the person address
     */
    private void loadPersonDirection(Person person) {
        String addressValue = person.getAddress().value.trim();
        int stringCutIndex;
        String addressWithoutUnit;

        if (addressValue.indexOf('#') > 2) {
            stringCutIndex = addressValue.indexOf('#') - 2;
            addressWithoutUnit = addressValue.substring(0, stringCutIndex);
        } else {
            addressWithoutUnit = addressValue;
        }

        readPersonName(person);
        loadPage(SEARCH_PAGE_URL + addressWithoutUnit + "?dg=dbrw&newdg=1");
    }

    /**
     * Run script that read person name
     * @param person
     */
    private void readPersonName(Person person) {
        try {
            Runtime.getRuntime().exec("wscript src\\main\\resources\\scripts\\ClickOnNameCard.vbs"
                    + " " + person.getName().fullName);
        } catch (IOException e) {
            try {
                Runtime.getRuntime().exec("osascript src\\main\\resources\\scripts\\ClickOnNameCardMac.scpt"
                        + " " + person.getName().fullName);
            } catch (IOException e1) {
                logger.warning("Unable to load welcome message.");
            }
        }
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    public void handleShowMultiLocationEvent(ShowMultiLocationFromHeadQuarterEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        StringBuilder url = new StringBuilder(SEARCH_PAGE_URL);
        for (String address: event.sortedList) {
            url.append(address);
            url.append("/");
        }

        List<String> temp = event.sortedList;
        temp.add(0, HQ_ADDRESS);
        additionalInfo.setText("Estimated Required Time for Deliveries: "
                + FilterCommand.getDuration(event.sortedList));
        loadPage(url.toString());
    }

    @Subscribe
    public void handleShowDefaultPageEvent(ShowDefaultPageEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadDefaultPage();
    }

    @Subscribe
    public void handleShowFromOneToAnotherEvent(ShowRouteFromOneToAnotherEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        StringBuilder url = new StringBuilder("https://www.google.com.sg/maps/dir/");
        for (String address: event.sortedList) {
            url.append(address);
            url.append("/");
        }
        additionalInfo.setText("Estimated Required Time for Deliveries: "
                + FilterCommand.getDuration(event.sortedList));
        loadPage(url.toString());
    }

    @Subscribe
    public void handleGameEvent(GameEvent event) {

        URL gamePath = MainApp.class.getResource("/games/Snake.html");
        loadPage(gamePath.toExternalForm());
        additionalInfo.setText("+ Additional information will be displayed here.");
    }
}
```
###### \resources\view\DarkTheme.css
``` css
.context-menu {
    -fx-background-color: white;
    -fx-background-insets: 0, 1, 2;
    -fx-background-radius: 0 0 0 0, 0 0 0 0, 0 0 0 0;
    -fx-padding: 0.5em 0em 0.5em 0em; /* 6 0 6 0 */

    -fx-border-color: #212121;
    -fx-border-style: solid;
}

.context-menu .separator {
    -fx-padding: 0.58333275em 1.333332em 0.58333275em 1.333332em;  /*7 16 7 16 */
}


.menu-item {
    -fx-background-color: transparent;
    -fx-padding: 0.666666em 1.333332em 0.666666em 1.333332em;   /*8 16 8 16 */
}

.menu-item .label {
    -fx-padding: 0em 1.333332em 0em 0em;
    -fx-text-fill: #212121;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
}

.menu-item:focused {
     -fx-background: -fx-accent;
     -fx-background-color: #dedede;
}

.menu-item:pressed
{
    -fx-background-color: #212121;
    -fx-text-fill: white;
}

.menu-item:pressed .label
{
    -fx-text-fill: white;
}

```
