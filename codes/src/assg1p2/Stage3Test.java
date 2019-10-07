package assg1p2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class Stage3Test {

	
	Double epsilon = 0.000001;

	RailNetworkAdvanced r;
	@Before public void initialize() {
		String stationData = "src/data/station_data.csv";
		String connectionData = "src/data/adjacent_stations.csv";
		String linesData = "src/data/lines_data.csv";
		r = new RailNetworkAdvanced(stationData,connectionData,linesData);
	}	
	
	@Test
	public void computeRatioTest1() {

		String origin = "Parramatta";
		String destination = "Strathfield";
		
		Double expected = 1.097584920479088;
		Double actual = r.computeRatio(origin, destination);

		assertEquals(expected,actual,epsilon);
	}
	
	@Test
	public void computeRatioTest2() {

		String origin = "Bankstown";
		String destination = "Hornsby";
		
		Double expected = 1.3356612123918958;
		Double actual = r.computeRatio(origin, destination);

		assertEquals(expected,actual,epsilon);
	}
	
	@Test
	public void computeRatioTest3() {

		String origin = "Richmond";
		String destination = "Hurstville";
		
		Double expected = 1.3687600644122384;
		Double actual = r.computeRatio(origin, destination);

		assertEquals(expected,actual,epsilon);
	}
	
	@Test
	public void computeRatioTest4() {

		String origin = "Hurstville";
		String destination = "Richmond";
		
		Double expected = 1.3687600644122384;
		Double actual = r.computeRatio(origin, destination);

		assertEquals(expected,actual,epsilon);
	}
	
	@Test(timeout=25000)
	public void computeAllRatioTest1() {

		String origin = "Parramatta";
		String destination = "Strathfield";
		
		HashMap<String,HashMap<String,Double>> ans = r.computeAllRatio();
		Double expected = 1.097584920479088;
		Double actual = ans.get(origin).get(destination);
		
		assertEquals(178,ans.size());
		assertEquals(expected,actual,epsilon);
	}

	@Test(timeout=3000)
	public void computeAllRatioTest2() {
		String origin = "Bankstown";
		String destination = "Hornsby";
		
		HashMap<String,HashMap<String,Double>> ans = r.computeAllRatio();
		Double expected = 1.3356612123918958;
		Double actual = ans.get(origin).get(destination);

		assertEquals(178,ans.size());
		assertEquals(expected,actual,epsilon);
	}
	
	@Test(timeout=1000)
	public void computeAllRatioTest3() {
		String origin = "Richmond";
		String destination = "Hurstville";
		
		HashMap<String,HashMap<String,Double>> ans = r.computeAllRatio();
		Double expected = 1.3687600644122384;
		Double actual = ans.get(origin).get(destination);

		assertEquals(178,ans.size());
		assertEquals(expected,actual,epsilon);
	}
	
	@Test(timeout=1000)
	public void computeAllRatioTest4() {
		String origin = "Hurstville";
		String destination = "Richmond";
		
		HashMap<String,HashMap<String,Double>> ans = r.computeAllRatio();
		Double expected = 1.3687600644122384;
		Double actual = ans.get(origin).get(destination);

		assertEquals(178,ans.size());
		assertEquals(expected,actual,epsilon);
	}

	@Test
	public void routeMinStopWithRoutesTest1() {
		String origin = "Beecroft";
		String destination = "Chatswood";
		String[] expected = {"T9 towards Gordon from Hornsby", "Beecroft", "Cheltenham", "Epping",
							"Metro towards Chatswood from Epping", "Epping", "Macquarie University",
							"Macquarie Park", "North Ryde", "Chatswood"};
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}

	@Test
	public void routeMinStopWithRoutesTest2() {
		String origin = "Chatswood";
		String destination = "Beecroft";
		String[] expected = {"Metro towards Epping from Chatswood", "Chatswood", "North Ryde", "Macquarie Park",
							"Macquarie University", "Epping", "T9 towards Hornsby from Gordon",
							"Epping", "Cheltenham", "Beecroft"};
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}

	@Test
	public void routeMinStopWithRoutesTest3() {
		String origin = "Epping";
		String destination = "Strathfield";
		String[] expected = {"T9 towards Hornsby from Gordon", "Epping", "Eastwood", "Denistone",
							"West Ryde", "Meadowbank", "Rhodes", "Concord West",
							"North Strathfield", "Strathfield"};
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}

	@Test
	public void routeMinStopWithRoutesTest4() {
		String origin = "Epping";
		String destination = "Redfern";
		String[] expected = {"T9 towards Hornsby from Gordon", "Epping", "Eastwood", "Denistone",
							"West Ryde", "Meadowbank", "Rhodes", "Concord West",
							"North Strathfield", "Strathfield", "T1 towards Berowra from Lidcombe",
							"Strathfield", "Redfern"};
							// "T1 towards Berowra from Richmond"
							// "T1 towards Berowra from Emu Plains"
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}

	@Test
	public void routeMinStopWithRoutesTest5() {
		String origin = "Hornsby";
		String destination = "Ashfield";
		String[] expected = {"T9 towards Hornsby from Gordon", "Epping", "Eastwood", "Denistone",
							"West Ryde", "Meadowbank", "Rhodes", "Concord West",
							"North Strathfield", "Strathfield", "T2 towards Museum from Parramatta",
							"Strathfield", "Burwood", "Croydon", "Ashfield"};
							// "T2 towards Museum from Leppington"
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}

	@Test
	public void routeMinStopWithRoutesTest6() {
		String origin = "Macarthur";
		String destination = "Liverpool";
		String[] expected = {"T8 towards Town Hall from Macarthur", "Macarthur", "Campbelltown", "Leumeah", "Minto",
							"Ingleburn", "Macquarie Fields", "Glenfield", "T2 towards Museum from Leppington",
							"Glenfield", "Casula", "Liverpool"};
		
		ArrayList<String> actual = r.routeMinStopWithRoutes(origin, destination);
		assertArrayEquals(expected, actual.toArray());
	}
}