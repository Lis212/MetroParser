import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Line;
import core.Station;
import core.StationToJson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Main {
    private static StationToJson stationToJson = new StationToJson();
    private static StationIndex stationIndex;

    public static void main(String[] args) {
        try {
            Document origin = Jsoup.connect("https://www.moscowmap.ru/metro.html#lines").get();

            Elements select = origin.getElementsByTag("span");
            parseLinesFromHtmlToObject(select);

            select = origin.getElementsByClass("js-metro-stations t-metrostation-list-table");
            parseStationsFromHtmlToObject(select);

            parseConnectionsFromHtmlToObject(select);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(stationToJson);
        File jsonFile = new File("src/main/resources/moscowmetro.json");
        try {
            PrintWriter pr = new PrintWriter(jsonFile);
            pr.write(json);
            pr.flush();
            pr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        printStationCount(jsonFile);
        createStationIndex(json);
        printStationCountFromStationIndex();
    }

    /**
     * The method prints to the console the number of stations
     * on each metro line using the class StationIndex
     */
    private static void printStationCountFromStationIndex() {
        Collection<Line> lines = stationIndex.getLines().values();
        for (Line line : lines) {
            System.out.println("SI -> on Line: " + line.getName() + " stations: " + line.getStations().size());
        }
    }

    /**
     * The method prints to the console the number of stations
     * on each metro line using the class StationToJson and library GSON
     * First it reads JSON into a StringBuilder,
     * then parses into an object of the StationToJson class
     *
     * @param jsonFile generated JSON file
     */

    private static void printStationCount(File jsonFile) {
        Gson unpack = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder builder = new StringBuilder();
        try {
            Files.readAllLines(Path.of(jsonFile.getPath()))
                    .forEach(l -> builder.append(l).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StationToJson unpackJson = unpack.fromJson(builder.toString(), StationToJson.class);
        Set<Map.Entry<String, List<String>>> entries = unpackJson.getStations().entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            int size = entry.getValue().size();
            System.out.println("In Moscow underground on Line: " + stationToJson.getLineByNumber(entry.getKey()) + " stations: " + size);
        }
    }

    /**
     * The method processes the elements and gets their child tags
     * in order to get the stations at which there are transfers.
     * div -> p -> a -> span
     * Writes everything to a class object StationToJson
     *
     * @param select - a set of elements from the table of stations from the site
     */
    private static void parseConnectionsFromHtmlToObject(Elements select) {
        TreeSet<Station> connectStation = new TreeSet<>();
        select.stream()
                .flatMap(div -> div.children().stream())
                .flatMap(p -> p.children().stream())
                .flatMap(a -> a.children().stream())
                .filter(span -> !span.attr("title").isEmpty())
                .forEach(span -> {
                    Element divTag = span.parent().parent().parent();
                    Element aTag = span.parent();

                    Line currentLine = stationToJson.getLineByNumber(divTag.attr("data-line"));
                    String nameCurrentStation = aTag.getElementsByAttributeValue("class", "name").text();
                    connectStation.add(new Station(nameCurrentStation, currentLine));

                    String numberConnectedLine = getClearNumberLine(span.attr("class"));
                    String nameConnectedStation = getClearNameStation(span.attr("title"));
                    connectStation.add(new Station(nameConnectedStation, stationToJson.getLineByNumber(numberConnectedLine)));
                });
        if (!connectStation.isEmpty()) stationToJson.addConnection(connectStation);
    }

    /**
     * The method converts the value from the tag to the station name.
     * @param str - tag value
     * @return station name
     */
    private static String getClearNameStation(String str) {
        return str.substring(
                str.indexOf('\u00AB') + 1,
                str.lastIndexOf('\u00BB'));
    }

    /**
     * The method converts the value from the tag to the line number.
     * @param str - tag value
     * @return number line
     */
    private static String getClearNumberLine(String str) {
        return str.substring(str.lastIndexOf('-') + 1);
    }

    /**
     * The method gets a list of elements by the span tag
     * and searches for a regular expression match to get the line number and name
     * Writes everything to a class object StationToJson
     *
     * @param elements - elements by tag "span"
     */
    private static void parseLinesFromHtmlToObject(Elements elements) {
        for (Element e : elements) {
            if (e.attr("class").matches("^js-metro-line t-metrostation-list-header t-icon-metroln ln-[D]?[0-9]+[A]?$")) {
                Line line = new Line(e.attr("data-line"), e.text());
                stationToJson.addLine(line);
            }
        }
    }

    /**
     * The method processes the elements and compares their child tags with the existing metro lines.
     * When found, adds a line to the list of stations
     *
     * @param selectStation - a set of elements from the table of stations from the site
     */
    private static void parseStationsFromHtmlToObject(Elements selectStation) {
        for (Element e : selectStation) {
            Elements childrens = e.children();
            stationToJson.getLines().stream()
                    .filter(line -> line.getNumber().equalsIgnoreCase(e.attr("data-line")))
                    .forEach(line -> {
                        for (Element children : childrens) {
                            line.addStation(new Station(children.getElementsByClass("name").text(), line));
                        }
                        List<String> stationNames = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
                        stationToJson.setStations(line.getNumber(), stationNames);

                    });
        }
    }

    /**
     * The method create object StationIndex.class.
     * Populates it with data from a string in a JSON file
     *
     * @param json - string from JSON file
     */
    private static void createStationIndex(String json) {
        stationIndex = new StationIndex();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(json);

            JSONArray linesArray = (JSONArray) jsonData.get("lines");
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);

            JSONArray connectionsArray = (JSONArray) jsonData.get("connections");
            parseConnections(connectionsArray);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The method processes the received JSON array from the file using the "connections" key.
     * Writes them to an object of the StationIndex class
     *
     * @param connectionsArray - array by "connections" key from JSON
     */
    private static void parseConnections(JSONArray connectionsArray) {
        connectionsArray.forEach(jsonElement -> {
            JSONArray connection = (JSONArray) jsonElement;
            List<Station> connectionStation = new ArrayList<>();
            connection.forEach(item -> {
                JSONObject itemObject = (JSONObject) item;
                String lineNumber = itemObject.get("line").toString();
                String stationName = (String) itemObject.get("station");

                Station station = stationIndex.getStation(stationName, lineNumber);
                if (station == null) {
                    throw new IllegalArgumentException("Wrong arguments!");
                }
                connectionStation.add(station);
            });
            stationIndex.addConnection(connectionStation);
        });
    }

    /**
     * The method handles multiple "stations".
     * Writes them to an object of the StationIndex class
     *
     * @param stationsObject - set by "stations" key from JSON
     */
    private static void parseStations(JSONObject stationsObject) {
        stationsObject.keySet().forEach(lineNumberObject ->
        {
            String lineNumber = lineNumberObject.toString();
            Line line = stationIndex.getLine(lineNumber);
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            stationsArray.forEach(stationObject -> {
                Station station = new Station((String) stationObject, line);
                stationIndex.addStation(station);
                line.addStation(station);
            });
        });
    }

    /**
     * The method processes the received JSON array from the file using the "lines" key.
     * Writes them to an object of the StationIndex class
     *
     * @param linesArray - array by "lines" key from JSON
     */
    private static void parseLines(JSONArray linesArray) {
        linesArray.forEach(lineObject -> {
            JSONObject lineJsonObject = (JSONObject) lineObject;
            Line line = new Line(
                    (lineJsonObject.get("number")).toString(),
                    (String) lineJsonObject.get("name")
            );
            stationIndex.addLine(line);
        });
    }
}
