package assg1p2;

public class Line implements Comparable<Line> {
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

	public String getCode() {
		return this.code;
	}

	/* compareTo method since we are implementing Comparable */
	@Override
	public int compareTo(Line other) {
		if (other != null)
			return this.code.compareTo(other.getCode());
		else
			return 1;
	}
}