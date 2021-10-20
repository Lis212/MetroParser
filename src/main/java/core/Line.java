package core;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class Line implements Comparable<Line>{
    private String number;
    private String name;
    private transient List<Station> stations;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        stations = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Line line)
    {
        return number.compareTo(line.getNumber());
    }
}
