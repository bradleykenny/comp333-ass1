package assg1p1;

import java.io.*;
import java.util.*;


public class RailNetwork {

	//private final double THRESHOLD = 0.000001;
	
	private TreeMap<String,Station> stationList;
	private HashMap<ArrayList<String>, Integer> lookupCost;
	private HashMap<ArrayList<String>, Integer> lookupSol;
	
	public RailNetwork(String trainData, String connectionData) {
		stationList = new TreeMap<>();
		lookupCost = new HashMap<>();
		lookupSol = new HashMap<>();
		
		try {
			readStationData(trainData);
			readConnectionData(connectionData);
		}
		catch (IOException e) {
			System.out.println("Exception encountered: " + e);
		}
	}
	
	/**
	 * Reads the CSV file containing information about the stations and 
	 * populate the TreeMap<String,Station> stationList. Each row of 
	 * the CSV file contains the name, latitude and longitude coordinates
	 * of the station.
	 * 
	 * You need to make a Station object for each row and add it to the 
	 * TreeMap<String,Station> stationList where the key value is the 
	 * name of the station (as a String).
	 * 
	 * @param infile	   the path to the file
	 * @throws IOException if the file is not found
	 */
	public void readStationData(String infile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(infile));
		in.readLine(); // remove headers
		while (in.ready()) {
			String[] temp = in.readLine().split(",");
			stationList.put(temp[0], new Station(temp[0], Double.parseDouble(temp[1]), Double.parseDouble(temp[2])));
		} in.close();
	}
	/**
	 * Reads the CSV file containing information about connectivity between 
	 * adjacent stations, and update the stations in stationList so that each
	 * Station object has a list of adjacent stations.
	 * 
	 * Each row contains two Strings separated by comma. To obtain the distance
	 * between the two stations, you need to use the latitude and longitude 
	 * coordinates together with the computeDistance() methods defined below
	 * 
	 * @param infile	   the path to the file
	 * @throws IOException if the file is not found
	 */	
	public void readConnectionData(String infile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(infile));
		while (in.ready()) {
			String[] temp = in.readLine().split(",");
			int distance = computeDistance(temp[0], temp[1]);

			stationList.get(temp[0]).addNeighbour(stationList.get(temp[1]), distance);
			stationList.get(temp[1]).addNeighbour(stationList.get(temp[0]), distance);
		} in.close();
	}
	
	/**
	 * Given the latitude and longitude coordinates of two locations x and y, 
	 * return the distance between x and y in metres using Haversine formula,
	 * rounded down to the nearest integer.
	 * 
	 * Note that two more methods are provided below for your convenience 
	 * and you should not directly call this method
	 * 
	 * source://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
	 * 
	 * @param lat1 latitude coordinate of x
	 * @param lon1 longitude coordinate of x
	 * @param lat2 latitude coordinate of y
	 * @param lon2 longitude coordinate of y
	 * @return distance between
	 */
	public static int computeDistance(double lat1, double lon1, double lat2, double lon2) {
        // distance between latitudes and longitudes 
        double dLat = Math.toRadians(lat2 - lat1); 
        double dLon = Math.toRadians(lon2 - lon1); 
  
        // convert to radians 
        lat1 = Math.toRadians(lat1); 
        lat2 = Math.toRadians(lat2); 
  
        // apply formulae 
        double a = Math.pow(Math.sin(dLat / 2), 2) +  
                   Math.pow(Math.sin(dLon / 2), 2) *  
                   Math.cos(lat1) *  
                   Math.cos(lat2); 
        double rad = 6371.0;
        Double c = 2 * Math.asin(Math.sqrt(a)); 
        Double distance = rad * c * 1000;
        return distance.intValue(); 
	}	
	
	/**
	 * Compute the distance between two stations in metres, where the stations
	 * are given as String objects
	 * 
	 * @param a		the first station
	 * @param b		the second station
	 * @return		the distance between the two stations in metres
	 */
	public int computeDistance(String a, String b) {
		Station u = stationList.get(a);
		Station v = stationList.get(b);
		return computeDistance(u.getLatitude(),u.getLongitude(),
							   v.getLatitude(),v.getLongitude());
	}
	
	/**
	 * Compute the distance between two stations in metres, where the stations
	 * are given as Station objects
	 * 
	 * @param a		the first station
	 * @param b		the second station
	 * @return		the distance between the two stations in metres
	 */
	public int computeDistance(Station a, Station b) {
		return computeDistance(a.getLatitude(),a.getLongitude(),
							   b.getLatitude(),b.getLongitude());
	}
	
	/**
	 * The method finds the shortest route (in terms of distance travelled) 
	 * between the origin station and the destination station.
	 * The route is returned as an ArrayList<String> containing the names of 
	 * the stations along the route, including the origin and the destination 
	 * stations.
	 * 
	 * If the route cannot be completed (there is no path between origin and
	 * destination), then return an empty ArrayList<String>
	 * 
	 * If the origin or the destination stations are not in the list of stations,
	 * return an empty ArrayList<String>. 
	 * 
	 * If the origin and the destination stations are the same, return an 
	 * ArrayList<String> containing the station.
	 * 
	 * @param origin		the starting station
	 * @param destination	the destination station
	 * @return
	 */
	public ArrayList<String> routeMinDistance(String origin, String destination) {
		if (!stationList.containsKey(origin) || !stationList.containsKey(destination)) {
			return new ArrayList<String>();
		}
		
		if (origin.equals(destination)) {
			ArrayList<String> ans = new ArrayList<String>();
			ans.add(origin);
			return ans;
		}

		HashMap<String, Integer> dist = new HashMap<>(); // Value is shortest distance from Key to d
		HashMap<String, String> parents = new HashMap<>(); // Value is the closest station to the Key (in direction o -> d)
  
		// Initialisation of values
        for (String s : stationList.keySet()) { 
            dist.put(s, Integer.MAX_VALUE);
			stationList.get(s).setUnmarked();
			parents.put(s, "");
        } 
  
        // Distance of origin to itself is always 0 
        dist.replace(origin, 0);
		
        for (int i = 0; i < stationList.size(); i++) { 
            int min = Integer.MAX_VALUE;
			String nextShortest = null; 
  
			// Find next best station in terms of distance
			for (String possibleStation : stationList.keySet()) {
				if (!stationList.get(possibleStation).isMarked() && dist.get(possibleStation) <= min) { 
					min = dist.get(possibleStation); 
					nextShortest = possibleStation; 
				} 
			} stationList.get(nextShortest).setMarked();
  
            // Updating distance of adjacent stations to picked station
            for (Station adj : stationList.get(nextShortest).getAdjacentStations().keySet()) { 
                if (!stationList.get(adj.getName()).isMarked() && stationList.get(nextShortest).getAdjacentStations().containsKey(adj) && dist.get(nextShortest) != Integer.MAX_VALUE && (dist.get(nextShortest) + stationList.get(nextShortest).getAdjacentStations().get(adj)) < (dist.get(adj.getName()))) {
					dist.replace(adj.getName(), dist.get(nextShortest) + stationList.get(nextShortest).getAdjacentStations().get(adj)); // Update dist to reflect new shortest distance to this path
					parents.replace(adj.getName(), nextShortest); // Update parent to be the new best path to this station
					// Once we get to the destination, stop and return
					if (adj.getName().equals(destination)) {
						ArrayList<String> temp = getStops(parents, origin, destination);
						return temp;
					}
				}
			}
        } 

		// If don't find anything, return empty ArrayList.
		return new ArrayList<>();
	}

	// Helper method to get all stops in the parents HashMap
	public ArrayList<String> getStops(HashMap<String, String> parents, String origin, String destination) {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(destination);
		String curr = parents.get(destination);
		while (!curr.equals(origin)) {
			temp.add(0, curr);
			curr = parents.get(curr);
		} temp.add(0, curr);
		return temp;
	}

	/**
	 * The method finds the shortest route (in terms of distance travelled) 
	 * between the origin station and the destination station under the 
	 * condition that the route must not pass through any stations in 
	 * TreeSet<String> failures
	 * 
	 * The route is returned as an ArrayList<String> containing the names of 
	 * the stations along the route, including the origin and the destination 
	 * stations.
	 * 
	 * If the route cannot be completed (there is no path between origin and
	 * destination), then return an empty ArrayList<String>
	 * 
	 * If the origin or the destination stations are not in the list of stations,
	 * return an empty ArrayList<String>. 
	 * 
	 * If the origin and the destination stations are the same, return an 
	 * ArrayList<String> containing the station.
	 * 
	 * @param origin		the starting station
	 * @param destination	the destination station
	 * @param failures		the list of stations that cannot be used
	 * @return
	 */
	public ArrayList<String> routeMinDistance(String origin, String destination, TreeSet<String> failures){
		if (!stationList.containsKey(origin) ||  !stationList.containsKey(destination) ||
			failures.contains(origin) || failures.contains(destination)) {
			return new ArrayList<String>();
		}
		if (origin.equals(destination)) {
			ArrayList<String> ans = new ArrayList<String>();
			ans.add(origin);
			return ans;
		}
		
		HashMap<String, Integer> dist = new HashMap<>(); // Value is shortest distance from Key to d
		HashMap<String, String> parents = new HashMap<>(); // Value is the closest station to the Key (in direction o -> d)
  
		// Initialisation of values
        for (String s : stationList.keySet()) { 
            dist.put(s, Integer.MAX_VALUE);
			stationList.get(s).setUnmarked();
			parents.put(s, "");
        } 
  
        // Distance of origin to itself is always 0 
        dist.replace(origin, 0);
		
        for (int i = 0; i < stationList.size(); i++) { 
            int min = Integer.MAX_VALUE;
			String nextShortest = null; 
  
			// Find next best station in terms of distance
			for (String possibleStation : stationList.keySet()) {
				if (!stationList.get(possibleStation).isMarked() && dist.get(possibleStation) <= min) { 
					min = dist.get(possibleStation); 
					nextShortest = possibleStation; 
				} 
			} stationList.get(nextShortest).setMarked();
  
            // Updating distance of adjacent stations to picked station
            for (Station adj : stationList.get(nextShortest).getAdjacentStations().keySet()) { 
				if (failures.contains(adj.getName())) {
					continue;
				}
                if (!stationList.get(adj.getName()).isMarked() && stationList.get(nextShortest).getAdjacentStations().containsKey(adj) && dist.get(nextShortest) != Integer.MAX_VALUE && (dist.get(nextShortest) + stationList.get(nextShortest).getAdjacentStations().get(adj)) < (dist.get(adj.getName()))) {
					dist.replace(adj.getName(), dist.get(nextShortest) + stationList.get(nextShortest).getAdjacentStations().get(adj)); // Update dist to reflect new shortest distance to this path
					parents.replace(adj.getName(), nextShortest); // Update parent to be the new best path to this station
					// Once we get to the destination, stop and return
					if (adj.getName().equals(destination)) {
						ArrayList<String> temp = getStops(parents, origin, destination);
						return temp;
					}
				}
			}
		}
		
		// If don't find anything, return empty ArrayList.
		return new ArrayList<>();
	}
	
	/**
	 * The method finds the shortest route (in terms of number of stops)
	 * between the origin station and the destination station.
	 * The route is returned as an ArrayList<String> containing the names of 
	 * the stations along the route, including the origin and the destination 
	 * stations.
	 * 
	 * If the route cannot be completed (there is no path between origin and
	 * destination), then return an empty ArrayList<String>
	 * 
	 * If the origin or the destination stations are not in the list of stations,
	 * return an empty ArrayList<String>. 
	 * 
	 * If the origin and the destination stations are the same, return an 
	 * ArrayList<String> containing the station.
	 * 
	 * @param origin		the starting station
	 * @param destination	the destination station
	 * @return
	 */
	public ArrayList<String> routeMinStop(String origin, String destination){
		if (!stationList.containsKey(origin) || !stationList.containsKey(destination)) {
			return new ArrayList<String>();
		}
		if (origin.equals(destination)) {
			ArrayList<String> ans = new ArrayList<String>();
			ans.add(origin);
			return ans;
		}
		
		HashMap<String, Integer> stops = new HashMap<>(); // Value is num of stops from Key to d
		HashMap<String, String> parents = new HashMap<>(); // Value is the station with least stops to the Key (in direction o -> d)
  
		// Initialisation of values
        for (String s : stationList.keySet()) { 
            stops.put(s, Integer.MAX_VALUE);
			stationList.get(s).setUnmarked();
			parents.put(s, "");
        } 
  
        // Distance of origin to itself is always 1 stop (being itself) 
        stops.replace(origin, 1);
		
        for (int i = 0; i < stationList.size(); i++) { 
            int min = Integer.MAX_VALUE;
			String nextShortest = null; 
  
			// Find next best station in terms of least stops
			for (String possibleStation : stationList.keySet()) {
				if (!stationList.get(possibleStation).isMarked() && stops.get(possibleStation) <= min) { 
					min = stops.get(possibleStation); 
					nextShortest = possibleStation; 
				} 
			} stationList.get(nextShortest).setMarked();
  
            // Updating distance of adjacent stations to picked station
            for (Station adj : stationList.get(nextShortest).getAdjacentStations().keySet()) { 
				if (!stationList.get(adj.getName()).isMarked() && stationList.get(nextShortest).getAdjacentStations().containsKey(adj) && stops.get(nextShortest) != Integer.MAX_VALUE && (stops.get(nextShortest) + 1) < (stops.get(adj.getName()))) {
					stops.replace(adj.getName(), stops.get(nextShortest) + 1); // Update dist to reflect new num of stops to this shortest path
					parents.replace(adj.getName(), nextShortest); // Update parent to be new min stops in shortest path to this station
					// Once we get to the destination, stop and return
					if (adj.getName().equals(destination)) {
						ArrayList<String> temp = getStops(parents, origin, destination);
						return temp;
					}
				}
			}
		}
		
		// If don't find anything, return empty ArrayList.
		return new ArrayList<>();
	}
	


	/**
	 * The method finds the shortest route (in terms of number of stops)
	 * between the origin station and the destination station under the 
	 * condition that the route must not pass through any stations in 
	 * TreeSet<String> failures (i.e. the rail segment cannot be travelled on)
	 * 
	 * The route is returned as an ArrayList<String> containing the names of 
	 * the stations along the route, including the origin and the destination 
	 * stations.
	 * 
	 * If the route cannot be completed (there is no path between origin and
	 * destination), then return an empty ArrayList<String>
	 * 
	 * If the origin or the destination stations are not in the list of stations,
	 * return an empty ArrayList<String>. 
	 * 
	 * If the origin and the destination stations are the same, return an 
	 * ArrayList<String> containing the station.
	 * 
	 * @param origin		the starting station
	 * @param destination	the destination station
	 * @param failures		the list of stations that cannot be used
	 * @return
	 */
	public ArrayList<String> routeMinStop(String origin, String destination, TreeSet<String> failures){
		if (!stationList.containsKey(origin) || !stationList.containsKey(destination)) {
			return new ArrayList<String>();
		}
		if (origin.equals(destination)) {
			ArrayList<String> ans = new ArrayList<String>();
			ans.add(origin);
			return ans;
		}
		
		HashMap<String, Integer> stops = new HashMap<>(); // Value is num of stops from Key to d
		HashMap<String, String> parents = new HashMap<>(); // Value is the station with least stops to the Key (in direction o -> d)
  
		// Initialisation of values
        for (String s : stationList.keySet()) { 
            stops.put(s, Integer.MAX_VALUE);
			stationList.get(s).setUnmarked();
			parents.put(s, "");
        } 
  
        // Distance of origin to itself is always 1 stop (being itself) 
        stops.replace(origin, 1);
		
        for (int i = 0; i < stationList.size(); i++) { 
            int min = Integer.MAX_VALUE;
			String nextShortest = null; 
  
			// Find next best station in terms of least stops
			for (String possibleStation : stationList.keySet()) {
				if (!stationList.get(possibleStation).isMarked() && stops.get(possibleStation) <= min) { 
					min = stops.get(possibleStation); 
					nextShortest = possibleStation; 
				} 
			} stationList.get(nextShortest).setMarked();
  
            // Updating distance of adjacent stations to picked station
            for (Station adj : stationList.get(nextShortest).getAdjacentStations().keySet()) { 
				if (failures.contains(adj.getName())) {
					continue;
				}
				if (!stationList.get(adj.getName()).isMarked() && stationList.get(nextShortest).getAdjacentStations().containsKey(adj) && stops.get(nextShortest) != Integer.MAX_VALUE && 
				(stops.get(nextShortest) + 1) < (stops.get(adj.getName()))) {
					stops.replace(adj.getName(), stops.get(nextShortest) + 1); // Update dist to reflect new num of stops to this shortest path
					parents.replace(adj.getName(), nextShortest); // Update parent to be new min stops in shortest path to this station
					// Once we get to the destination, stop and return
					if (adj.getName().equals(destination)) {
						ArrayList<String> temp = getStops(parents, origin, destination);
						return temp;
					}
				}
			}
		}
		
		// If don't find anything, return empty ArrayList.
		return new ArrayList<>();
	}
	
	/**
	 * Given a route between two stations, compute the total distance 
	 * of this route.
	 * 
	 * @param path	the list of stations in the route (as String objects)
	 * @return		the length of the route between the first station
	 * 				and last station in the list	
	 */
	public int findTotalDistance(ArrayList<String> path) {
		int distance = 0;
		String prev = "";
		for (String s : path) {
			if (!prev.isEmpty()) {
				distance += stationList.get(prev).getAdjacentStations().get(stationList.get(s));
			} prev = s;
		}
		return distance;
	}
	
	
	/** 
	 * Given a route between two stations, compute the minimum total cost 
	 * of performing an exhaustive scan on this route, as described in the 
	 * assignment specification for Stage 2.
	 * 
	 * Return 0 if there are 2 or less stations in the route. 
	 * 
	 * @param route  the list of stations in the route (as String objects)
	 * @return		 the minimum cost of performing exhaustive scans
	 */
	public int optimalScanCost(ArrayList<String> route) {
		if (route == null || route.size() <= 2) {
			return 0;
		}

		int min_val = Integer.MAX_VALUE;
		
		for (int i = 1; i < route.size() - 1; i++) {
			ArrayList<String> temp1 = new ArrayList<>(route.subList(0, i+1));
			ArrayList<String> temp2 = new ArrayList<>(route.subList(i, route.size()));
			int rec1 = Integer.MAX_VALUE;
			int rec2 = Integer.MAX_VALUE;
			
			if (lookupCost.containsKey(temp1)) {
				rec1 = lookupCost.get(temp1);
			} else {
				rec1 = optimalScanCost(temp1);
				lookupCost.put(temp1, rec1);
			}

			if (lookupCost.containsKey(temp2)) {
				rec2 = lookupCost.get(temp2);
			} else {
				rec2 = optimalScanCost(temp2);
				lookupCost.put(temp2, rec2);
			}
			
			min_val = Math.min(min_val, findTotalDistance(route) + rec1 + rec2);
		}

        return min_val;
	}

	/***
	 * Given a route between two stations, return the list of stations (in the order
	 * that they were chosen) that gives the segmentation that leads to the minimum
	 * cost for performing an exhaustive scan on the the route (as described in the
	 * assignment specification for Stage 2.
	 * 
	 * Return an empty ArrayList if there are 2 or less stations in the route.
	 * 
	 * @param route
	 * @return
	 */
	public ArrayList<String> optimalScanSolution(ArrayList<String> route) {
		if (route == null || route.size() <= 2) {
			return new ArrayList<String>();
		}

		int min_val = Integer.MAX_VALUE;
		int index = -1;
		ArrayList<String> temp = new ArrayList<>();
		
		for (int i = 1; i < route.size() - 1; i++) {
			ArrayList<String> temp1 = new ArrayList<>(route.subList(0, i+1));
			ArrayList<String> temp2 = new ArrayList<>(route.subList(i, route.size()));
//			ArrayList<String> rec1 = optimalScanSolution(temp1);
//			ArrayList<String> rec2 = optimalScanSolution(temp2);
//			
//			if (min_val > findTotalDistance(route)) {
//				min_val = findTotalDistance(route);
//				index = i;
//
//				temp = new ArrayList<>();
//				if (rec1.size() > 2)
//					temp.addAll(rec1);
//				if (rec2.size() > 2)
//					temp.addAll(rec2);
//			}
		}

		if (index != -1) {
			temp.add(route.get(index));
		}
        return temp;
	} 
}
