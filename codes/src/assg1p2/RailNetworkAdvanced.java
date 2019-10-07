package assg1p2;

import java.io.*;
import java.util.*;

import assg1p1.Station;


public class RailNetworkAdvanced {

	private TreeMap<String, Station> stationList;
	private HashMap<String, Double> ratioLookup;
	private HashMap<String, Integer> distLookup;
	private ArrayList<String> routeLookup;

	// delete this before submitting
	public static void main(String[] args) {
		String stationData = "codes/src/data/station_data.csv";
		String connectionData = "codes/src/data/adjacent_stations.csv";
		String linesData = "codes/src/data/lines_data.csv";
		RailNetworkAdvanced rn = new RailNetworkAdvanced(stationData, connectionData, linesData);

		System.out.println(rn.routeMinStopWithRoutes("Richmond", "Blacktown"));
	}

	public RailNetworkAdvanced(String trainData, String connectionData, String lineData) {
		stationList = new TreeMap<>();
		ratioLookup = new HashMap<>();
		distLookup = new HashMap<>();
		routeLookup = new ArrayList<>();
		
		try {	
			readLinesData(lineData);
			readStationData(trainData);
			readConnectionData(connectionData);
		}
		catch (IOException e) {
			System.out.println("Exception encountered: " + e);
		}
	}
	
	/**
	 * Reads the CSV file containing information about the lines
	 * 
	 * @param infile
	 * @throws IOException
	 */

	HashMap<String, ArrayList<String>> trainLines = new HashMap<>();

	public void readLinesData(String infile) throws IOException {
		// update for lines data: { Code, Line, Start, End, StationCount }
		BufferedReader in = new BufferedReader(new FileReader(infile));
		in.readLine(); // remove headers
		while (in.ready()) {
			ArrayList<String> data = new ArrayList<String>();
			String[] temp = in.readLine().split(",");
			for(String items: temp){
				data.add(items);
				trainLines.put(temp[0], data);
			}
		} 
		in.close();
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
	public void readStationData(String infile) throws IOException {
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
	public void readConnectionData(String infile) throws IOException {
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
	 * @return distance betwee
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
	public ArrayList<String> routeMinDistance(String origin, String destination){
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
		
        for (String s : stationList.keySet()) { 
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
	 * Return the ratio between the length of the shortest route between two
	 * stations (in terms of distance) and the actual distance between the 
	 * two stations (computed using computeDistance())
	 * 
	 * In other words, 
	 * let d1 = distance of shortest route between the two stations as computed
	 *          by the method routeMinDistance() (from Stage 1).
	 * let d2 = distance between two stations as computed by the method
	 *          computeDistance() 
	 *          
	 * The method returns d1/d2 (as a double)
	 * 
	 * @param origin		the starting station
	 * @param destination 	the ending station
	 * @return	s			the ratio d1/d2 as explained above
	 */
	public double computeRatio(String origin, String destination) {
		if (origin.equals(destination)) {
			return 0;
		}
		String name = getCombinedName(origin, destination);
		if(!ratioLookup.containsKey(name)){
			routeLookup = new ArrayList<>(routeMinDistance(origin, destination));
			for(int i = 0; i<routeLookup.size(); i++){
				List<String> subList = routeLookup.subList(i, routeLookup.size());
				ArrayList<String> path = new ArrayList<>(subList);
				mapRatios(path);
			}
		}
		if(ratioLookup.containsKey(name))
			return ratioLookup.get(name);
		else
			return (double) 0;
	}

	public void mapRatios(ArrayList<String> route){
		if(route.size()<2){
			return;
		}

		String head = route.get(0);
		String tail = route.get(route.size()-1);
		double d2 = computeDistance(head, tail);
		String name = getCombinedName(head, tail);
		
		if (!ratioLookup.containsKey(name)) {
			
			//if (!distLookup.containsKey(name)) {
			int d1 = findTotalDistance(route);
			route.remove(route.size() - 1);
			//	distLookup.put(name, d1);
			//} 
			//else {
			//	route.remove(route.size()-1);
			//	d1 = distLookup.get(name);
			//}
			ratioLookup.put(name, d1/d2);
		}
		else{
			route.remove(0);
		}
		mapRatios(route);
	}
	public String getCombinedName(String n1, String n2) {
		String name;
		if (n1.charAt(0) > n2.charAt(0)) {
			name = n2 + "-" + n1;
		} else {
			name = n1 + "-" + n2;
		} 
		
		return name;
	}
	
	
	/**
	 * Return the ratio as computed by computeRatio() method for all 
	 * pairs of station in the rail network.
	 * 
	 * The ratios should be stored in a HashMap<String,HashMap<String,Double>>,
	 * that is, the ratio between station a and b can be obtained by calling
	 * 
	 *    computeAllRatio().get(a).get(b)
	 * 
	 * @return a hashmap containing the ratios
	 */
	public HashMap<String,HashMap<String,Double>> computeAllRatio() {
		HashMap<String, HashMap<String, Double>> allDistMap = new HashMap<String, HashMap<String, Double>>(); 
		
		// there will be duplicity as get(i, j) will be the same as get(j, i)
		// if j == i, 
		// when we create a allDistMap for j, i; create for i, j?

		for (String i: stationList.keySet()) {	
			for (String j: stationList.keySet()) {
				if (!i.equals(j)) {
					// Chatswood -> Roseville
					// Station j is not found in main Map (allDistMap)
					// := create j in mainMap
					if (allDistMap.containsKey(i) && allDistMap.containsKey(j)) {
						if (allDistMap.get(i).containsKey(j) && allDistMap.get(j).containsKey(i)) {
							continue;
						}
					}

					double ratio = computeRatio(i, j);

					if (!allDistMap.containsKey(i)) {
						HashMap<String, Double> iToJ = new HashMap<>();
						iToJ.put(j, ratio);
						allDistMap.put(i, iToJ);
					} else {
						allDistMap.get(i).put(j, ratio);
					}
					
					if (!allDistMap.containsKey(j)) {	
						HashMap<String, Double> jToI = new HashMap<>();
						jToI.put(i, ratio);
						allDistMap.put(j, jToI);
					} else {
						allDistMap.get(j).put(i, ratio);
					}
				}
			}
		}

		//System.out.println(ratioLookup);
		return allDistMap;
	}
	
	/**
	 * The method finds the shortest route (in terms of number of stops)
	 * between the origin station and the destination station, taking 
	 * into account the available routes in the rail network.
	 * 
	 * The route is returned as an ArrayList<String> containing the lines taken,
	 * any transfer between lines, and the names of the stations on each line,
	 * including the origin and the destination stations.
	 * 
	 * Please see the assignment specification for more information.
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
	 * @param destination	the end station
	 * @return				the route taken
	 */
	public ArrayList<String> routeMinStopWithRoutes(String origin, String destination) {
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
						System.out.println(temp);
						return temp;
					}
				}
			}
		}
		
		// If don't find anything, return empty ArrayList.
		return new ArrayList<>();



		// using routeMinStop, find the shortest path from origin to destination; in an ArrayList
		// Regarding lines data, need to create a Map that contains all the stations in a line?
		// As example states: Beecroft -> Chatswood: 
		// T9[Beecroft, Cheltenham, Epping] -> Metro[Epping, Mq uni, mq park, Nth Ryde, Chatswood]
		// should have a functionality that gets train line associated to that stop
		// Station.getname(chatswood).getLine() = T1, T9, Metro

		// case 1: Redfern -> Strathfield: T1, T2, T9
		// must be able to return T1 (direct line)
		// maybe a lookup table? look up origin's line and choose the least # stops to destination
		// List will return Redfern -> Strathfield
		// look up train line list and look up Redfern, BFS the list of train lines and find the lowest # of stations
	
		// Above will havve trouble if a swap is required; Beecroft -> Chatswood
		// From original arrayList, O(n) and list all the station lines attached to stations
		// https://www.geeksforgeeks.org/shortest-path-for-directed-acyclic-graphs/
		

		return null;
	}

}
