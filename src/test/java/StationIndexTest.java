import core.Line;
import core.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class StationIndexTest {
    StationIndex stationIndex = new StationIndex();
    TreeSet<Station> stations = new TreeSet<>();
    Map<String, Line> lines = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        Line fruit = new Line("1", "Фруктовая");
        Line color = new Line("2", "Цветная");
        Line zoo = new Line("3", "Зоопарковая");

        stationIndex.addLine(fruit);
        stationIndex.addLine(color);
        stationIndex.addLine(zoo);

        Station apple = new Station("Яблочная", fruit);
        fruit.addStation(apple);
        fruit.addStation(new Station("Грушевая", fruit));
        fruit.addStation(new Station("Персиковая", fruit));
        fruit.addStation(new Station("Лимонная", fruit));

        color.addStation(new Station("Зеленая", color));
        color.addStation(new Station("Синяя", color));
        color.addStation(new Station("Желтая", color));
        color.addStation(new Station("Красная", color));

        zoo.addStation(new Station("Ботанический сад", zoo));
        zoo.addStation(new Station("Террариум", zoo));
        zoo.addStation(new Station("Дельфинарий", zoo));
        zoo.addStation(new Station("Кошачий ряд", zoo));

        lines.put(fruit.getNumber(), fruit);
        lines.put(color.getNumber(), color);
        lines.put(zoo.getNumber(), zoo);


        fruit.getStations().stream().forEach(station -> stationIndex.addStation(station));
        fruit.getStations().stream().forEach(station -> stations.add(station));
        color.getStations().stream().forEach(station -> stationIndex.addStation(station));
        color.getStations().stream().forEach(station -> stations.add(station));
        zoo.getStations().stream().forEach(station -> stationIndex.addStation(station));
        zoo.getStations().stream().forEach(station -> stations.add(station));

        List<Station> connect1 = new ArrayList<>();
        connect1.add(stationIndex.getStation("Персиковая", fruit.getNumber()));
        connect1.add(stationIndex.getStation("Синяя", color.getNumber()));
        stationIndex.addConnection(connect1);

        List<Station> connect2 = new ArrayList<>();
        connect2.add(stationIndex.getStation("Красная", color.getNumber()));
        connect2.add(stationIndex.getStation("Ботанический сад", zoo.getNumber()));
        stationIndex.addConnection(connect2);

    }

    @Test
    public void testGetStations() {
        Set expected = stations;
        Set actual = stationIndex.getStations();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetStation() {
        Line фруктовая = new Line("1", "Фруктовая");
        Station station = new Station("Яблочная2", фруктовая);
        Station expected = station;
        stationIndex.addStation(station);
        Station actual = stationIndex.getStation(station.getName(), фруктовая.getNumber());
        assertEquals(expected, actual);
    }

    @Test
    public void testGetLines() {
        Map<String, Line> expected = lines;
        assertEquals(expected, stationIndex.getLines());
    }
}
