package com.neuSpring18.dao;

import com.neuSpring18.dto.Filter;
import com.neuSpring18.dto.InventoryContext;
import com.neuSpring18.dto.Vehicle;
import com.neuSpring18.io.UserIO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class VehicleManagerImple implements VehicleManager {

    @Override
    public Collection<Vehicle> searchVehiclesByFilter(String dealerID, Filter filter) {

        String search = filter.getSearch();
        String minPrice = filter.getMinPrice();
        String maxPrice = filter.getMaxPrice();
        String minYear = filter.getMinYear();
        String maxYear = filter.getMaxYear();
        String make = filter.getMake();
        List<String> categoryList = filter.getCategory();
        List<String> typeList = filter.getType();

        UserIO userIO = new UserIO();
        List<String> vehiclesFromDealer = userIO.getAllBasedOnMode("All", dealerID);
        vehiclesFromDealer.remove(0);
        Collection<Vehicle> filteredVehicles = new ArrayList<Vehicle>();

        if (search != null && !search.equals("")) {

            for (String v : vehiclesFromDealer) {

                Vehicle vehicle = Vehicle.generateVehicle(v);
                if (searchFilter(vehicle, search))
                    filteredVehicles.add(vehicle);
            }

        } else {

            for (String v : vehiclesFromDealer) {

                Vehicle vehicle = Vehicle.generateVehicle(v);
                if (minPriceFilter(vehicle, minPrice) && maxPriceFilter(vehicle, maxPrice)
                        && minYearFilter(vehicle, minYear) && maxYearFilter(vehicle, maxYear)
                        && makeFilter(vehicle, make) && categoryFilter(vehicle, categoryList)
                        && typeFilter(vehicle, typeList)) {
                    filteredVehicles.add(vehicle);
                }
            }
        }
        return filteredVehicles;
    }

    private boolean searchFilter(Vehicle vehicle, String search) {

        if (search == null || search.equals(""))
            return true;

        for (String s : search.toLowerCase().split(" +")) {
            if (!vehicle.toSearchString().toLowerCase().contains(s) || s.contains("~"))
                return false;
        }

        return true;
    }

    private boolean minPriceFilter(Vehicle vehicle, String minPrice) {
        return minPrice == null || minPrice.equals("") || vehicle.getPrice() >= Double.parseDouble(minPrice);
    }

    private boolean maxPriceFilter(Vehicle vehicle, String maxPrice) {
        return maxPrice == null || maxPrice.equals("") || vehicle.getPrice() <= Double.parseDouble(maxPrice);
    }

    private boolean minYearFilter(Vehicle vehicle, String minYear) {
        return minYear == null || minYear.equals("") || vehicle.getYear() >= Integer.parseInt(minYear);
    }

    private boolean maxYearFilter(Vehicle vehicle, String maxYear) {
        return maxYear == null || maxYear.equals("") || vehicle.getYear() <= Integer.parseInt(maxYear);
    }

    private boolean makeFilter(Vehicle vehicle, String make) {
        return make == null || make.equals("") || vehicle.getMake().contains(make);
    }

    private boolean categoryFilter(Vehicle vehicle, List<String> categoryList) {

        if (categoryList == null || categoryList.isEmpty())
            return true;

        for (String s : categoryList) {
            if (s.equals(vehicle.getCategory().toString()) || s.equals(""))
                return true;
        }

        return false;
    }

    private boolean typeFilter(Vehicle vehicle, List<String> typeList) {

        if (typeList == null || typeList.isEmpty())
            return true;

        for (String s : typeList) {
            if (s.equals(vehicle.getBodyType().toString()) || s.equals(""))
                return true;
        }

        return false;
    }

    @Override
    public Collection<Vehicle> getVehiclesFromDealer(String dealerID) {

        UserIO userIO = new UserIO();
        List<String> vehiclesFromDealer = userIO.getAllBasedOnMode("All", dealerID);
        vehiclesFromDealer.remove(0);
        Collection<Vehicle> vehicles = new ArrayList<Vehicle>();
        for (String v : vehiclesFromDealer) {
            vehicles.add(Vehicle.generateVehicle(v));
        }
        return vehicles;
    }

    @Override
    public InventoryContext getContext(String dealerID) {

        InventoryContext ic = new InventoryContext();
        Collection<Vehicle> vehicles = new ArrayList<>(getVehiclesFromDealer(dealerID));
        ic.setTotalCount(vehicles.size());

        HashSet<String> makeSet = new HashSet<>();
        HashSet<String> typeSet = new HashSet<>();

        for (Vehicle v : vehicles) {
            String make = v.getMake();
            String type = v.getBodyType().toString();
            addToList(makeSet, make);
            addToList(typeSet, type);
        }

        ic.setMakes(new ArrayList<>(makeSet));
        ic.setTypes(new ArrayList<>(typeSet));
        return ic;
    }

    private void addToList(HashSet<String> set, String string) {
        if (!set.contains(string))
            set.add(string);
    }

    @Override
    public String addVehicle(String dealerID, Vehicle v) {
        UserIO userIO = new UserIO();
        return userIO.addVehicleToDealer(dealerID, v.toString());
    }

    @Override
    public boolean editVehicle(String dealerID, Vehicle v) {
        UserIO userIO = new UserIO();
        return userIO.editVehicleOfDealer(dealerID, v.getId(), v.toString());
    }

    @Override
    public boolean deleteVehicle(String dealerID, String vehicleID) {
        UserIO userIO = new UserIO();
        return userIO.deleteVehicleFromDealer(dealerID, vehicleID);
    }
}
