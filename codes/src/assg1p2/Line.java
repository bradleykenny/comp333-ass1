package assg1p2;

public class Line {
	String code;
	String line;
	String start;
	String end;
	int stationCount;

	public Line(String code, String line, String start, String end, int stationCount) {
		this.code = code;
		this.line = line;
		this.start = start;
		this.end = end;
		this.stationCount = stationCount;
	}

	public String getName(int direction) {
		String name;
		if (direction >= 0) {
			name = start + " " + end;
		} else {
			name = end + " " +  start;
		}
		return name;
	}


}