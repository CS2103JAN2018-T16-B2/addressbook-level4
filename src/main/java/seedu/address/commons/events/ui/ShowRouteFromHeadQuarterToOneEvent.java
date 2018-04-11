package seedu.address.commons.events.ui;

import java.util.List;

import seedu.address.commons.events.BaseEvent;

//@@author ncaminh
/**
 * Show Google map route from HQ to many locations
 */
public class ShowRouteFromHeadQuarterToOneEvent extends BaseEvent {

    public final String destination;

    public ShowRouteFromHeadQuarterToOneEvent(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
