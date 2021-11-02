import core.Line;
import core.Station;
import junit.framework.TestCase;

import java.util.Set;
import java.util.TreeSet;

public class StationIndexTest extends TestCase {
    StationIndex stationIndex = new StationIndex();
    TreeSet<Station> stations = new TreeSet<>();

    @Override
    protected void setUp() throws Exception {
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

        fruit.getStations().stream().forEach(station -> stationIndex.addStation(station));
        fruit.getStations().stream().forEach(station -> stations.add(station));
        color.getStations().stream().forEach(station -> stationIndex.addStation(station));
        color.getStations().stream().forEach(station -> stations.add(station));
        zoo.getStations().stream().forEach(station -> stationIndex.addStation(station));
        zoo.getStations().stream().forEach(station -> stations.add(station));
    }

    public void testGetStations() {
        Set expected = stations;
        Set actual = stationIndex.getStations();
        assertEquals(expected, actual);
    }

    public void testGetStation() {
        Line фруктовая = new Line("1", "Фруктовая");
        Station station = new Station("Яблочная", фруктовая);
        Station expected = station;
        Station actual = stationIndex.getStation(station.getName(), фруктовая.getNumber());
        assertEquals(expected, actual);
    }
}
