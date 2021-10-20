package core;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class StationToJson {
    private final TreeMap<String, List<String>> stations;
    private final TreeSet<Line> lines;
    private final List<TreeSet<Station>> connections;

    public StationToJson() {
        this.stations = new TreeMap<>();
        this.lines = new TreeSet<>();
        this.connections = new ArrayList<>();
    }

    public TreeMap<String, List<String>> getStations() {
        return stations;
    }

    public TreeSet<Line> getLines() {
        return lines;
    }

    public void setStations(String lineNumb, List<String> stationNames) {
        stations.put(lineNumb, stationNames);
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void addConnection(TreeSet<Station> stations) {
        connections.add(stations);
    }

    public Line getLineByNumber(String number) {
        for (Line line : lines) {
            if (line.getNumber().equalsIgnoreCase(number)) return line;
        }
        return null;
    }
}
