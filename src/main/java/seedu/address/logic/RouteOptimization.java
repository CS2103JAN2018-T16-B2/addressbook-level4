package seedu.address.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Person;


/**
 * logic for the shortest delivery route
 */
public class RouteOptimization {

    // when filter the data and run the map or route function we can call this class to get the optimized route that can
    // then be passed into the mapping in UI


    //Get Addresses
    public Map<String, String> getAddresses(Model model) {
        List<Person> lastShownList = model.getFilteredPersonList();
        List<String> filteredAddresses = new ArrayList<>();
        Map<String, String> filtered = new HashMap<>();
        int stringCutIndex;
        String addressWithoutUnit;

        //need to figure otu what the key should be to make sure we know what the hashmap is storing
        for (int i = 0; i < lastShownList.size(); i++) {
            Address address = lastShownList.get(i).getAddress();
            String name = lastShownList.get(i).getName().toString();
            String addressValue = address.value.trim();
            if (addressValue.indexOf('#') > 2) {
                stringCutIndex = addressValue.indexOf('#') - 2;
                addressWithoutUnit = addressValue.substring(0, stringCutIndex);
            } else {
                addressWithoutUnit = addressValue;
            }

            filtered.put(name, addressWithoutUnit);
        }

        //filtered address is this list of address that we need to optimize
        getAllDistances(filtered);
        return filtered;

    }

    public Map<String, Double> getAllDistances(Map<String, String> filtered) {
        Map<String, Double> allDistances = new HashMap<>();
        GetDistance distance = new GetDistance();

        for (Map.Entry<String, String> entry1 : filtered.entrySet()) {
            String key1 = entry1.getKey();
            int hash1 = System.identityHashCode(key1);
            String value1 = entry1.getValue();
            for (Map.Entry<String, String> entry2 : filtered.entrySet()) {
                String key2 = entry2.getKey();
                if (hash1 > System.identityHashCode(key2)) {
                    continue;
                }
                String value2 = entry2.getValue();
                allDistances.put(labelRoutes(value1, value2, filtered), distance.getDistance(value1, value2));
            }
        }
        return allDistances;
    }

    /**
     *
     * @param origin - starting point
     * @param destination - ending point
     * @param filtered - map of person and their address
     * @return
     */

    public String labelRoutes(String origin, String destination, Map<String, String> filtered) {
        String originKey = "";
        String destinationKey = "";
        String routeKey;
        //only works if unique key val pair
        for (Map.Entry<String, String> entry : filtered.entrySet()) {
            if (origin.equals(entry.getValue())) {
                originKey = entry.getKey();
            }
            if (destination.equals(entry.getValue())) {
                destinationKey = entry.getKey();
            }
        }
        routeKey = originKey + "_" + destinationKey;
        return routeKey;
    }
}

