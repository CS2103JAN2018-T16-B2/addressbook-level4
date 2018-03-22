package seedu.address.logic;

import java.io.IOException;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;

/**
 * Testing retrieve distance data
 */
public class RouteOptimization {
    /**
     * get driving distance from origin to destination
     */
    public double getDistance(String origin, String destination)
            throws InterruptedException, ApiException, IOException {

        GeoApiContext context = buildGeoContext();

        String[] origins = {origin};
        String[] destinations = {destination};

        DistanceMatrix matrix =
                DistanceMatrixApi.getDistanceMatrix(context, origins, destinations).await();
        String distance = matrix.rows[0].elements[0].distance.toString();
        String distanceWithoutUnit = distance.substring(0, distance.length() -  3);

        return Double.parseDouble(distanceWithoutUnit);
    }

    /**
     * Build Google Map API Geo context with API key
     */
    private static GeoApiContext buildGeoContext() {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBWyCJkCym1dSouzHX_FxLk6Tj11C7F0Ao")
                .build();

        return context;
    }
}
