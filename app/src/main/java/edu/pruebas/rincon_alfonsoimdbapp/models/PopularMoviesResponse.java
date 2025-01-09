package edu.pruebas.rincon_alfonsoimdbapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PopularMoviesResponse {
    @SerializedName("data")
    private Data data;

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    public Data getData() {
        return data;
    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static class Data {
        @SerializedName("topMeterTitles")
        private TopMeterTitles topMeterTitles;

        public TopMeterTitles getTopMeterTitles() {
            return topMeterTitles;
        }
    }

    public static class TopMeterTitles {
        @SerializedName("edges")
        private List<Edge> edges;

        public List<Edge> getEdges() {
            return edges;
        }
    }

    public static class Edge {
        @SerializedName("node")
        private Node node;

        public Node getNode() {
            return node;
        }
    }

    public static class Node {
        @SerializedName("id")
        private String id;

        @SerializedName("titleText")
        private TitleText titleText;

        @SerializedName("originalTitleText")
        private TitleText originalTitleText;

        @SerializedName("releaseYear")
        private YearRange releaseYear;

        @SerializedName("releaseDate")
        private ReleaseDate releaseDate;

        @SerializedName("titleType")
        private TitleType titleType;

        @SerializedName("primaryImage")
        private PrimaryImage primaryImage;

        @SerializedName("meterRanking")
        private MeterRanking meterRanking;

        public String getId() {
            return id;
        }

        public TitleText getTitleText() {
            return titleText;
        }

        public TitleText getOriginalTitleText() {
            return originalTitleText;
        }

        public YearRange getReleaseYear() {
            return releaseYear;
        }

        public ReleaseDate getReleaseDate() {
            return releaseDate;
        }

        public TitleType getTitleType() {
            return titleType;
        }

        public PrimaryImage getPrimaryImage() {
            return primaryImage;
        }

        public MeterRanking getMeterRanking() {
            return meterRanking;
        }
    }

    public static class TitleText {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class YearRange {
        @SerializedName("year")
        private int year;

        @SerializedName("endYear")
        private Integer endYear;

        public int getYear() {
            return year;
        }

        public Integer getEndYear() {
            return endYear;
        }
    }

    public static class ReleaseDate {
        @SerializedName("month")
        private int month;

        @SerializedName("day")
        private int day;

        @SerializedName("year")
        private int year;

        @SerializedName("country")
        private Country country;

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getYear() {
            return year;
        }

        public Country getCountry() {
            return country;
        }
    }

    public static class Country {
        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }
    }

    public static class TitleType {
        @SerializedName("id")
        private String id;

        @SerializedName("text")
        private String text;

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }
    }

    public static class PrimaryImage {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }

    public static class MeterRanking {
        @SerializedName("currentRank")
        private int currentRank;

        @SerializedName("rankChange")
        private RankChange rankChange;

        public int getCurrentRank() {
            return currentRank;
        }

        public RankChange getRankChange() {
            return rankChange;
        }
    }

    public static class RankChange {
        @SerializedName("changeDirection")
        private String changeDirection;

        @SerializedName("difference")
        private int difference;

        public String getChangeDirection() {
            return changeDirection;
        }

        public int getDifference() {
            return difference;
        }
    }
}