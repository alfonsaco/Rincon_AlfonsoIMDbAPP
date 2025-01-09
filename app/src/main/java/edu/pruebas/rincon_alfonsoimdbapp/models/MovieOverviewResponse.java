package edu.pruebas.rincon_alfonsoimdbapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieOverviewResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("title")
        private Title title;

        @SerializedName("plot")
        private Plot plot;

        @SerializedName("productionStatus")
        private ProductionStatus productionStatus;

        @SerializedName("ratingsSummary")
        private RatingsSummary ratingsSummary;

        @SerializedName("reviews")
        private Reviews reviews;

        @SerializedName("metacritic")
        private Metacritic metacritic;

        @SerializedName("certificate")
        private Certificate certificate;

        @SerializedName("runtime")
        private Runtime runtime;

        @SerializedName("primaryImage")
        private Image primaryImage;

        public Title getTitle() {
            return title;
        }

        public Plot getPlot() {
            return plot;
        }

        public ProductionStatus getProductionStatus() {
            return productionStatus;
        }

        public RatingsSummary getRatingsSummary() {
            return ratingsSummary;
        }

        public Reviews getReviews() {
            return reviews;
        }

        public Metacritic getMetacritic() {
            return metacritic;
        }

        public Certificate getCertificate() {
            return certificate;
        }

        public Runtime getRuntime() {
            return runtime;
        }

        public Image getPrimaryImage() {
            return primaryImage;
        }
    }

    public static class Title {
        @SerializedName("id")
        private String id;

        @SerializedName("titleText")
        private TitleText titleText;

        @SerializedName("releaseYear")
        private YearRange releaseYear;

        @SerializedName("releaseDate")
        private ReleaseDate releaseDate;

        public String getId() {
            return id;
        }

        public TitleText getTitleText() {
            return titleText;
        }

        public YearRange getReleaseYear() {
            return releaseYear;
        }

        public ReleaseDate getReleaseDate() {
            return releaseDate;
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

        public int getYear() {
            return year;
        }
    }

    public static class ReleaseDate {
        @SerializedName("month")
        private int month;

        @SerializedName("day")
        private int day;

        @SerializedName("year")
        private int year;

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getYear() {
            return year;
        }
    }

    public static class Plot {
        @SerializedName("plotText")
        private PlotText plotText;

        public PlotText getPlotText() {
            return plotText;
        }
    }

    public static class PlotText {
        @SerializedName("plainText")
        private String plainText;

        public String getPlainText() {
            return plainText;
        }
    }

    public static class ProductionStatus {
        @SerializedName("currentProductionStage")
        private CurrentProductionStage currentProductionStage;

        public CurrentProductionStage getCurrentProductionStage() {
            return currentProductionStage;
        }
    }

    public static class CurrentProductionStage {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class RatingsSummary {
        @SerializedName("aggregateRating")
        private double aggregateRating;

        @SerializedName("voteCount")
        private int voteCount;

        public double getAggregateRating() {
            return aggregateRating;
        }

        public int getVoteCount() {
            return voteCount;
        }
    }

    public static class Reviews {
        @SerializedName("total")
        private int total;

        public int getTotal() {
            return total;
        }
    }

    public static class Metacritic {
        @SerializedName("metascore")
        private Metascore metascore;

        @SerializedName("url")
        private String url;

        public Metascore getMetascore() {
            return metascore;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class Metascore {
        @SerializedName("score")
        private int score;

        @SerializedName("reviewCount")
        private int reviewCount;

        public int getScore() {
            return score;
        }

        public int getReviewCount() {
            return reviewCount;
        }
    }

    public static class Certificate {
        @SerializedName("rating")
        private String rating;

        @SerializedName("ratingReason")
        private String ratingReason;

        public String getRating() {
            return rating;
        }

        public String getRatingReason() {
            return ratingReason;
        }
    }

    public static class Runtime {
        @SerializedName("seconds")
        private int seconds;

        public int getSeconds() {
            return seconds;
        }
    }

    public static class Image {
        @SerializedName("url")
        private String url;

        @SerializedName("height")
        private int height;

        @SerializedName("width")
        private int width;

        public String getUrl() {
            return url;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }
}
