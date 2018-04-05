package seedu.address.logic;

import java.io.IOException;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;

/**
 * Testing retrieve distance data
 */
public class GetDistance {

    //@@author ncaminh
    public DistanceMatrix getMatrix(String origin, String destination) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBWyCJkCym1dSouzHX_FxLk6Tj11C7F0Ao")
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
        //@@author meerakanani10
        int space = distance.indexOf(" ");
        String units = distance.substring(space + 1, distance.length());
        double metres;
        distanceWithoutUnit = distance.substring(0, space);
        if (units.equals("m")) {
            metres = Double.parseDouble(distanceWithoutUnit) / 1000.0;
            return metres;
        } else {
            return Double.parseDouble(distanceWithoutUnit);
        }
    }


    public double getTime(String origin, String destination) {
        String durationWithoutUnit = "";
        DistanceMatrix matrix = null;
        matrix = getMatrix(origin, destination);
        String duration = matrix.rows[0].elements[0].duration.toString();
        int space = duration.indexOf(" ");
        String units = duration.substring(space + 1, duration.length());
        durationWithoutUnit = duration.substring(0, space);
        return Double.parseDouble(durationWithoutUnit);
    }
}
