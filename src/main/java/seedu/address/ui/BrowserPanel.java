package seedu.address.ui;

import static seedu.address.logic.commands.SelectCommand.MESSAGE_SELECT_PERSON_SUCCESS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import seedu.address.MainApp;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.GameEvent;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.commons.events.ui.ShowDefaultPageEvent;
import seedu.address.commons.events.ui.ShowMultiLocationFromHeadQuarterEvent;
import seedu.address.commons.events.ui.ShowRouteFromHeadQuarterToOneEvent;
import seedu.address.commons.events.ui.ShowRouteFromOneToAnotherEvent;
import seedu.address.logic.GetDistance;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.Person;

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "default.html";
    public static final String HQ_ADDRESS = "Kent Ridge MRT";
    public static final String SEARCH_PAGE_URL =
            "https://www.google.com.sg/maps/dir/Kent+Ridge+MRT+Station/";

    private static final String FXML = "BrowserPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private Text additionalInfo;

    @FXML
    private WebView browser;

    public BrowserPanel() {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage();
        registerAsAnEventHandler(this);
    }

    //@@author ncaminh
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
            createFolderIfNeeded();
            createScriptIfNeeded();
            readPersonNameScript(person);
        } catch (IOException e) {
            logger.warning("Unable to read Introduce person script");
        }
    }

    private void readPersonNameScript(Person person) throws IOException {
        logger.info("Running welcome script");
        Runtime.getRuntime().exec("wscript.exe script\\ReadPersonName.vbs"
                + " " + person.getName().fullName);
    }

    private void createScriptIfNeeded() throws IOException {
        File f = new File("script\\ReadPersonName.vbs");
        if (!f.exists()) {
            File file1 = new File("script\\ReadPersonName.txt");
            logger.info("Creating script ReadPersonName.txt");
            file1.createNewFile();
            logger.info("Writing to ReadPersonName.txt");
            PrintWriter writer = new PrintWriter("script\\ReadPersonName.txt", "UTF-8");
            writer.println("name = WScript.Arguments(0)");
            writer.println("speaks=\"This is \" + name");
            writer.println("CreateObject(\"sapi.spvoice\").Speak speaks");
            writer.close();
            logger.info("Converting ReadPersonName.txt to ReadPersonName.vbs");
            File file2 = new File("script\\ReadPersonName.vbs");
            file1.renameTo(file2);
        }
    }

    private void createFolderIfNeeded() {
        File dir = new File("script");
        if (!dir.exists()) {
            logger.info("Creating script directory");
            boolean successful = dir.mkdirs();
        }
    }
    //@@author
    /**
     * Loads a HTML file based on given URL.
     * @param url
     */
    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
        logger.info("Loaded " + url);
    }

    /**
     * Loads a default HTML file has pigeon-icon at the center and has background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
        additionalInfo.setText("+ Additional information will be displayed here.");
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        String destination = event.getNewSelection().person.getAddress().toString();

        GetDistance distance = new GetDistance();
        try {
            Double duration = distance.getTime(HQ_ADDRESS, destination);
            additionalInfo.setText("Estimated Required Time for Deliveries: "
                    + duration + "mins");
        } catch (Exception e) {
            additionalInfo.setText("This person address cannot be found.");
        }

        String personName = event.getNewSelection().person.getName().toString();
        EventsCenter.getInstance().post(new NewResultAvailableEvent(String.format(MESSAGE_SELECT_PERSON_SUCCESS,
                personName)));
        loadPersonDirection(event.getNewSelection().person);
    }

    //@@author ncaminh
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
    public void handleShowFromHeadQuaterToOneEvent(ShowRouteFromHeadQuarterToOneEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        StringBuilder url = new StringBuilder(SEARCH_PAGE_URL);
        url.append(event.destination);
        loadPage(url.toString() + "?dg=dbrw&newdg=1");
    }

    @Subscribe
    public void handleShowFromOneToAnotherEvent(ShowRouteFromOneToAnotherEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        StringBuilder url = new StringBuilder("https://www.google.com.sg/maps/dir/");
        for (String address: event.sortedList) {
            url.append(address);
            url.append("/");
        }
        url.deleteCharAt(url.length() - 1);
        additionalInfo.setText("Estimated Required Time for Deliveries: "
                + FilterCommand.getDuration(event.sortedList));
        loadPage(url.toString() + "?dg=dbrw&newdg=1");
    }

    @Subscribe
    public void handleGameEvent(GameEvent event) {

        URL gamePath = MainApp.class.getResource("/games/Snake.html");
        loadPage(gamePath.toExternalForm());
        additionalInfo.setText("+ Additional information will be displayed here.");
    }
}
