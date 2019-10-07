package assg1p2;

import java.io.*;
import java.util.*;

import assg1p2.Station;


public class RailNetworkAdvanced {

	private TreeMap<String, Station> stationList;
	private TreeMap<String, Line> lineList;
	private TreeMap<String, HashMap<Integer, String>> trainMap;

	public RailNetworkAdvanced(String trainData, String connectionData, String lineData) {
		stationList = new TreeMap<>();
		lineList = new TreeMap<>();
		trainMap = new TreeMap<>();
		
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


	public void readLinesData(String infile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(infile));
		in.readLine(); // remove headers
		while (in.ready()) {
			String[] split = in.readLine().split(",");
			Line temp = new Line(split[0], split[1], split[2], split[3], Integer.parseInt(split[4]));
			lineList.put(split[0], temp);
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
		String[] lineNames = { "T1E", "T1R", "T1B", "T2P", "T2L", "T3B", "T3L", "T4C", "T4W", "T5", "T6", "T7", "T8M", "T8R", "T9", "M" };
		while (in.ready()) {
			String[] temp = in.readLine().split(",");
			stationList.put(temp[0], new Station(temp[0], Double.parseDouble(temp[1]), Double.parseDouble(temp[2])));
			for (int i = 3; i < temp.length - 1; i++) {
				if (!temp[i].isEmpty()) {
					stationList.get(temp[0]).addLine(lineNames[i-3], Integer.parseInt(temp[i]));
					if (!trainMap.containsKey(lineNames[i-3])) {
						HashMap<Integer, String> newMap = new HashMap<>();
						newMap.put(Integer.parseInt(temp[i]), temp[0]);
						trainMap.put(lineNames[i-3], newMap);
					}
					else {
						trainMap.get(lineNames[i-3]).put(Integer.parseInt(temp[i]), temp[0]);
					}
				}
			}
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
	 * source://www.Nksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
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
		int d1 = findTotalDistance(routeMinDistance(origin, destination));
		int d2 = computeDistance(origin, destination);
		return (double) d1 / d2;
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
		HashMap<String, HashMap<String, Double>> ratios = new HashMap<>();
		HashMap<String, HashMap<String, Integer>> distances = new HashMap<>();

		// set distances to distance between them if adj or MAX_VALUE if not
		for (String a : stationList.keySet()) {
			for (String b : stationList.keySet()) {
				if (!distances.containsKey(a)) {
					if (stationList.get(a).getAdjacentStations().containsKey(stationList.get(b))) {
						HashMap<String, Integer> temp = new HashMap<>();
						temp.put(b, stationList.get(a).getAdjacentStations().get(stationList.get(b)));
						distances.put(a, temp);
					} else {
						HashMap<String, Integer> temp = new HashMap<>();
						temp.put(b, Integer.MAX_VALUE);
						distances.put(a, temp);
					}
				} else {
					if (stationList.get(a).getAdjacentStations().containsKey(stationList.get(b))) {
						distances.get(a).put(b, stationList.get(a).getAdjacentStations().get(stationList.get(b)));
					} else {
						distances.get(a).put(b, Integer.MAX_VALUE);
					}
				}
			}
		}

		// find the shortest distance for each pair
		for (String c : stationList.keySet()) { 
			for (String a : stationList.keySet()) {
				for (String b : stationList.keySet()) {
					int newVal = distances.get(a).get(c) + distances.get(c).get(b);
					if (newVal < distances.get(a).get(b) && newVal >= 0) {
						distances.get(a).replace(b, newVal);
					}
				}
			}
		}

		// calculate ratio on each final distance
		for (String a : stationList.keySet()) {
			for (String b : stationList.keySet()) {
				if (!ratios.containsKey(a)) {
					HashMap<String, Double> temp = new HashMap<>();
					temp.put(b, (double) distances.get(a).get(b) / computeDistance(a, b));
					ratios.put(a, temp);
				} else {
					ratios.get(a).put(b, (double) distances.get(a).get(b) / computeDistance(a, b));
				}
			}
		}

		return ratios;
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

		HashMap<String, HashMap<String, String>> mapper = new HashMap<>();
		Queue<Station> pq = new LinkedList<Station>();

		ArrayList<String> answer = new ArrayList<>();

		pq.add(stationList.get(origin));

		while (pq.size() != stationList.keySet().size()) { 
			Station parentStation = pq.poll();
			HashMap<String, String> lineNeighbors = getLineNeighbors(parentStation);
			// iterate over all neighbours on all possible lines for this station
			for (String neighbour: lineNeighbors.keySet()) {
				Station adjStation = stationList.get(neighbour); 
				if (!adjStation.isMarked()) {
					HashMap<String, String> otherStation = new HashMap<>();
					otherStation.put(parentStation.getName(), lineNeighbors.get(neighbour));
					mapper.put(adjStation.getName(), otherStation);
					adjStation.setMarked();
					pq.add(adjStation);
				}

				// once we find destination, backtrack
				if(adjStation.getName().equals(destination)) {
					String prevLine = ""; // tracks trainline
					while (!destination.equals(origin)) {									
						for (String parent : mapper.get(destination).keySet()) {
							if (prevLine.isEmpty()) {
								prevLine = mapper.get(destination).get(parent);
							}
							
							// if prevLine doesnt match current line, add line change info
							if (!mapper.get(destination).get(parent).equals(prevLine)) {
								answer.add(0, destination);
								answer.add(0, lineInfo(parent, destination, prevLine));
							}
							
							answer.add(0, destination);
							if (parent.equals(origin)) {
								answer.add(0, parent);
								answer.add(0, lineInfo(parent, destination, prevLine));
							}
							
							prevLine = mapper.get(destination).get(parent);
							destination = parent; // update to be previous on line
						}
					} 
					return answer;
				}
			}
		} 

		return new ArrayList<>();
	}

	/* 
	 * Helper function desined to return the line information, including direction of travel.
	 * The function operates by getting two stations position on the line, and using
	 * these numbers to calculate the direction of travel. 
	 */
	public String lineInfo(String par, String destination, String prevLine) {
		int parVal = 0;
		int desVal = 0;
		for (Integer s : trainMap.get(prevLine).keySet()) {
			if (trainMap.get(prevLine).get(s).equals(destination)) {
				desVal = s;
			}
			if (trainMap.get(prevLine).get(s).equals(par)) {
				parVal = s;
			}
		}

		return lineList.get(prevLine).getLineDirection(desVal - parVal);
	}

	/*
	 * Helper function to find all neighbouring stations on all lines for a particular
	 * station. 
	 * 
	 * @return a HashMap containing these values. Key being the station name and value being
	 * the line.
	 */
	public HashMap<String, String> getLineNeighbors(Station s) {
		HashMap<String, String> neighbors = new HashMap<>();
		for (String temp : s.getLines().keySet()) {
			int stop = s.getLines().get(temp);
			if (stop == 1) {
				neighbors.put(trainMap.get(temp).get(stop + 1), temp);
			}
			else if (stop == trainMap.get(temp).size()) {
				neighbors.put(trainMap.get(temp).get(stop - 1), temp);
			}
			else {
				neighbors.put(trainMap.get(temp).get(stop + 1), temp);
				neighbors.put(trainMap.get(temp).get(stop - 1), temp);
			}
		}
		return neighbors;
	}
}
