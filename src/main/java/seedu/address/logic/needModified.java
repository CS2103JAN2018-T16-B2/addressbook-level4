package seedu.address.logic;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class needModified {
    /**
     * Fake person class for easy testing algo purpose, need to import proper Person class
     */
    static class FakePerson {
        String name;
        String address;
        FakePerson(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }

    /**
     * Node to save sorted list and store minimum distance travel from the first node of the list to all the other node
     */
    static class Node {
        List<FakePerson> personsList;
        double distance = 0;
        void setPersonsList(List<FakePerson> list) {
            personsList = list;
        }

        void setDistance(double d) {
            distance = d;
        }

        public List<FakePerson> getPersonsList() {
            return personsList;
        }

        double getDistance() {
            return distance;
        }
    }

    /**
     * In my recursive method, I need to build a persons list that has been removed a specific person
     *
     */
    private static List<FakePerson> wantedList(List<FakePerson> personsList, int indexToRemove) {
        List<FakePerson> temp = new ArrayList<FakePerson>(personsList);
        temp.remove(indexToRemove);
        return temp;
    }

    /**
     * Recursion method
     *
     */
    private static Node minPath(List<FakePerson> filteredList) throws InterruptedException, ApiException, IOException {
        List<FakePerson> list = new ArrayList<FakePerson>();
        List<FakePerson> holder = new ArrayList<FakePerson>();
        Node node = new Node();

        /*
          Base case for recursion
         */
        if (filteredList.size() == 0) return null;
        if (filteredList.size() == 1) {
            list.add(filteredList.get(0));
            node.setPersonsList(list);
            node.setDistance(0);
            return node;
        }

        list.add(filteredList.get(0));
        double minDistance = 99999999.2;

        for (int i = 1; i < filteredList.size(); i++) {
            double tempDistance_1 = gD(filteredList.get(0), filteredList.get(i));
            List<FakePerson> tempList_1 = wantedList(filteredList, i);
            tempList_1.set(0, filteredList.get(i));


            double tempDistance_2 = minPath(tempList_1).getDistance();

            if (minDistance > tempDistance_1 + tempDistance_2) {
                minDistance = tempDistance_1 + tempDistance_2;
                holder = tempList_1;
            }
        }

        list.addAll(holder);
        node.setPersonsList(list);
        node.setDistance(minDistance);

        return node;
    }

    private static double gD(FakePerson a, FakePerson b) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBWyCJkCym1dSouzHX_FxLk6Tj11C7F0Ao")
                .build();

        String[] origin = {a.getAddress()};
        String[] destination = {b.getAddress()};

        DistanceMatrix matrix =
                DistanceMatrixApi.getDistanceMatrix(context, origin, destination).await();
        String distance = matrix.rows[0].elements[0].distance.toString();
        String distanceOnlyNumber = distance.substring(0,distance.length()-3);
        return Double.parseDouble(distanceOnlyNumber);
    }

    public static void main(String []args) throws InterruptedException, ApiException, IOException {
        FakePerson a = new FakePerson("Alice","Kent Ridge MRT");
        FakePerson b = new FakePerson("Clara","Newton MRT");
        FakePerson c = new FakePerson("Bob","Buona Vista MRT");
        FakePerson d = new FakePerson("Darrel", "Litte India MRT");

        List<FakePerson> list = new ArrayList<FakePerson>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);

        Node mainNode = minPath(list);

        double minDistance;
        minDistance = mainNode.getDistance();
        System.out.println("Min distance is " + minDistance);

        System.out.print("Route is: ");
        for (FakePerson p: mainNode.getPersonsList()) {
            System.out.print(p.getName() + " ");
        }
    }
}
