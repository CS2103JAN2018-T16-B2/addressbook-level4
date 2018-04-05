package seedu.address.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Person;

//@@author meerakanani10
/**
 * logic for the shortest delivery route
 */
public class RouteOptimization {
    public static final String HQ_ADDRESS = "Kent Ridge MRT";

    /**
     *
     * @param model
     * @return
     */
    public List<String> getAddresses(Model model) {
        List<Person> lastShownList = model.getFilteredPersonList();
        List<String> filteredAddresses = new ArrayList<>();
        List<String> optimizedRoute = new ArrayList<>();
        String addressWithoutUnit;
        String startingPoint;

        if (lastShownList.size() == 1) {
            Address address = lastShownList.get(0).getAddress();
            if (isFindableAddress(address)) {
                String name = lastShownList.get(0).getName().toString();
                addressWithoutUnit = removeUnit(address);
                optimizedRoute.add(addressWithoutUnit);
            }
        } else {
            //need to figure out what the key should be to make sure we know what the hashmap is storing
            for (int i = 0; i < lastShownList.size(); i++) {
                Address address = lastShownList.get(i).getAddress();
                if (isFindableAddress(address)) {
                    addressWithoutUnit = removeUnit(address);
                    filteredAddresses.add(addressWithoutUnit);
                }
            }
            if (!filteredAddresses.isEmpty()) {
                optimizedRoute = getStartingAddress(filteredAddresses, optimizedRoute);
                filteredAddresses = removeAddress(optimizedRoute.get(0), filteredAddresses);
                if (!filteredAddresses.isEmpty()) {
                    optimizedRoute = getDistances(filteredAddresses, optimizedRoute.get(0), optimizedRoute);
                }
            }
        }
        return optimizedRoute;
    }

    //@@author ncaminh
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

    //@@author meerakanani10
    public List<String> getStartingAddress(List<String> filteredAddresses, List<String> optimizedRoute) {
        Map<String, Double> startingRoute = new LinkedHashMap<>();
        GetDistance distance = new GetDistance();
        SortAddresses sort = new SortAddresses();
        Map<String, Double> dummy = new LinkedHashMap<>();
        String first;


        for (int i = 0; i < filteredAddresses.size(); i++) {
            String destination = filteredAddresses.get(i);
            String origin = HQ_ADDRESS;
            startingRoute.put(labelRoutes(origin, destination), distance.getDistance(origin, destination));
        }
        dummy = sort.cleanSorted(sort.sortByComparator(startingRoute));
        sort.printMap(dummy);
        Map.Entry<String, Double> entry = dummy.entrySet().iterator().next();
        first = entry.getKey().split("_")[1];
        optimizedRoute.add(first);
        return optimizedRoute;

    }

    /**
     *
     * @param address
     * @param filteredAddresses
     * @return
     */
    public List<String> removeAddress(String address, List<String> filteredAddresses) {
        for (int i = 0; i < filteredAddresses.size(); i++) {
            if (filteredAddresses.get(i).equals(address)) {
                filteredAddresses.remove(i);
                break;
            }
        }
        return filteredAddresses;
    }

    public List<String> getDistances(List<String> filteredAddresses, String origin, List<String> optimizedRoute) {
        Map<String, Double> paths = new LinkedHashMap<>();
        Map<String, Double> dummy = new HashMap<>();
        SortAddresses sort = new SortAddresses();
        String next;
        GetDistance distance = new GetDistance();
        for (int i = 0; i < filteredAddresses.size(); i++) {
            String destination = filteredAddresses.get(i);
            paths.put(labelRoutes(origin, destination), distance.getDistance(origin, destination));
        }
        dummy = sort.cleanSorted(sort.sortByComparator(paths));
        if( dummy.entrySet().iterator().hasNext()) {
            Map.Entry<String, Double> entry = dummy.entrySet().iterator().next();
            next = entry.getKey().split("_")[1];
            optimizedRoute.add(next);
            filteredAddresses = removeAddress(next, filteredAddresses);
            if (filteredAddresses.size() != 0) {
                optimizedRoute = getDistances(filteredAddresses, next, optimizedRoute);
            }
        }
        return optimizedRoute;
    }

    /**
     *
     * @param origin - starting point
     * @param destination - ending point
     * @return
     */
    public String labelRoutes(String origin, String destination) {
        String routeKey;
        routeKey = origin + "_" + destination;
        return routeKey;
    }

    /**
     *
     * @param combinedAddresses - the key from the hashmaps
     * @return the addresses split, in an array.
     */
    public String[] splitLabel(String combinedAddresses) {
        String[] addresses = combinedAddresses.split("_");
        return addresses;
    }

    /**
     *
     * @param address address to be edited
     * @return
     */
    public String removeUnit(Address address) {
        String addressValue = address.value.trim();
        int stringCutIndex;
        String addressWithoutUnit;
        if (addressValue.indexOf('#') > 2) {
            stringCutIndex = addressValue.indexOf('#') - 2;
            addressWithoutUnit = addressValue.substring(0, stringCutIndex);
        } else {
            addressWithoutUnit = addressValue;
        }
        return addressWithoutUnit;
    }
}

