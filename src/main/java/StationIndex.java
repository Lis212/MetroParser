import core.Line;
import core.Station;

import java.util.*;
import java.util.stream.Collectors;

public class StationIndex {
    private final Map<String, Line> number2Line;
    private final TreeSet<Station> stations;
    private final Map<Station, TreeSet<Station>> connections;

    public StationIndex() {
        this.number2Line = new HashMap<>();
        this.stations = new TreeSet<>();
        this.connections = new TreeMap<>();
    }

    void addStation(Station station) {
        stations.add(station);
    }

    void addLine(Line line) {
        number2Line.put(line.getNumber(), line);
    }

    Line getLine(String numberLine) {
        return number2Line.get(numberLine);
    }

    Station getStation(String name, String lineNumber) {
        Station query = new Station(name, getLine(lineNumber));
        Station station = stations.ceiling(query);
        return (station != null && station.equals(query)) ? station : null;
    }

    public Set<Station> getStations() {
        return stations;
    }

    void addConnection(List<Station> connectionStation) {
        for (Station station : connectionStation) {
            if (!connections.containsKey(station)) {
                connections.put(station, new TreeSet<>());
            }
            TreeSet<Station> connectedStations = connections.get(station);
            connectedStations.addAll(connectionStation.stream()
                    .filter(s -> !s.equals(station)).collect(Collectors.toList()));
        }
    }

    public Map<String, Line> getLines() {
        return number2Line;
    }
}
