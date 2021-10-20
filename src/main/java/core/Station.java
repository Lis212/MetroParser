package core;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("NullableProblems")
public class Station implements Comparable<Station> {
    private transient Line line;
    @SerializedName("line")
    private String numberLine;
    @SerializedName("station")
    private String name;

    public Station(String name, Line line) {
        this.name = name;
        this.line = line;
        this.numberLine = line.getNumber();
    }

    public Line getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Station station) {
        if (station.getLine() != null) {
            int lineComparison = this.getLine().compareTo(station.getLine());
            if (lineComparison != 0) {
                return lineComparison;
            }
        }
        return name.compareToIgnoreCase(station.getName());
    }


    public boolean equals(Station station) {
        return compareTo(station) == 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
