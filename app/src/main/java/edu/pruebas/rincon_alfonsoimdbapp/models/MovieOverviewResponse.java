package edu.pruebas.rincon_alfonsoimdbapp.models;

import com.google.gson.annotations.SerializedName;

public class MovieOverviewResponse {
    @SerializedName("data")
    private Data data;

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    // Getters y setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Data {
        @SerializedName("title")
        private Title title;

        public Title getTitle() {
            return title;
        }

        public void setTitle(Title title) {
            this.title = title;
        }
    }

    public static class Title {
        @SerializedName("id")
        private String id;

        @SerializedName("titleText")
        private TitleText titleText;

        @SerializedName("originalTitleText")
        private TitleText originalTitleText;

        @SerializedName("releaseYear")
        private ReleaseYear releaseYear;

        @SerializedName("releaseDate")
        private ReleaseDate releaseDate;

        @SerializedName("titleType")
        private TitleType titleType;

        @SerializedName("primaryImage")
        private PrimaryImage primaryImage;

        @SerializedName("ratingsSummary")
        private RatingsSummary ratingsSummary;

        @SerializedName("engagementStatistics")
        private EngagementStatistics engagementStatistics;

        @SerializedName("plot")
        private Plot plot;

        @SerializedName("certificate")
        private Certificate certificate;

        @SerializedName("runtime")
        private Runtime runtime;

        // Getters y setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public TitleText getTitleText() {
            return titleText;
        }

        public void setTitleText(TitleText titleText) {
            this.titleText = titleText;
        }

        public TitleText getOriginalTitleText() {
            return originalTitleText;
        }

        public void setOriginalTitleText(TitleText originalTitleText) {
            this.originalTitleText = originalTitleText;
        }

        public ReleaseYear getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(ReleaseYear releaseYear) {
            this.releaseYear = releaseYear;
        }

        public ReleaseDate getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(ReleaseDate releaseDate) {
            this.releaseDate = releaseDate;
        }

        public TitleType getTitleType() {
            return titleType;
        }

        public void setTitleType(TitleType titleType) {
            this.titleType = titleType;
        }

        public PrimaryImage getPrimaryImage() {
            return primaryImage;
        }

        public void setPrimaryImage(PrimaryImage primaryImage) {
            this.primaryImage = primaryImage;
        }

        public RatingsSummary getRatingsSummary() {
            return ratingsSummary;
        }

        public void setRatingsSummary(RatingsSummary ratingsSummary) {
            this.ratingsSummary = ratingsSummary;
        }

        public EngagementStatistics getEngagementStatistics() {
            return engagementStatistics;
        }

        public void setEngagementStatistics(EngagementStatistics engagementStatistics) {
            this.engagementStatistics = engagementStatistics;
        }

        public Plot getPlot() {
            return plot;
        }

        public void setPlot(Plot plot) {
            this.plot = plot;
        }

        public Certificate getCertificate() {
            return certificate;
        }

        public void setCertificate(Certificate certificate) {
            this.certificate = certificate;
        }

        public Runtime getRuntime() {
            return runtime;
        }

        public void setRuntime(Runtime runtime) {
            this.runtime = runtime;
        }
    }

    public static class TitleText {
        @SerializedName("text")
        private String text;

        // Getters y setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class ReleaseYear {
        @SerializedName("year")
        private int year;

        @SerializedName("endYear")
        private Integer endYear;

        // Getters y setters
        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public Integer getEndYear() {
            return endYear;
        }

        public void setEndYear(Integer endYear) {
            this.endYear = endYear;
        }
    }

    public static class ReleaseDate {
        @SerializedName("month")
        private int month;

        @SerializedName("day")
        private int day;

        @SerializedName("year")
        private int year;

        // Getters y setters
        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }

    public static class TitleType {
        @SerializedName("text")
        private String text;

        // Getters y setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class PrimaryImage {
        @SerializedName("url")
        private String url;

        // Getters y setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class RatingsSummary {
        @SerializedName("aggregateRating")
        private double aggregateRating;

        @SerializedName("voteCount")
        private int voteCount;

        // Getters y setters
        public double getAggregateRating() {
            return aggregateRating;
        }

        public void setAggregateRating(double aggregateRating) {
            this.aggregateRating = aggregateRating;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }
    }

    public static class EngagementStatistics {
        @SerializedName("watchlistStatistics")
        private WatchlistStatistics watchlistStatistics;

        // Getters y setters
        public WatchlistStatistics getWatchlistStatistics() {
            return watchlistStatistics;
        }

        public void setWatchlistStatistics(WatchlistStatistics watchlistStatistics) {
            this.watchlistStatistics = watchlistStatistics;
        }

        public static class WatchlistStatistics {
            @SerializedName("displayableCount")
            private DisplayableCount displayableCount;

            // Getters y setters
            public DisplayableCount getDisplayableCount() {
                return displayableCount;
            }

            public void setDisplayableCount(DisplayableCount displayableCount) {
                this.displayableCount = displayableCount;
            }

            public static class DisplayableCount {
                @SerializedName("text")
                private String text;

                // Getters y setters
                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }
            }
        }
    }

    public static class Plot {
        @SerializedName("plotText")
        private PlotText plotText;

        // Getters y setters
        public PlotText getPlotText() {
            return plotText;
        }

        public void setPlotText(PlotText plotText) {
            this.plotText = plotText;
        }

        public static class PlotText {
            @SerializedName("plainText")
            private String plainText;

            // Getters y setters
            public String getPlainText() {
                return plainText;
            }

            public void setPlainText(String plainText) {
                this.plainText = plainText;
            }
        }
    }

    public static class Certificate {
        @SerializedName("rating")
        private String rating;

        // Getters y setters
        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }

    public static class Runtime {
        @SerializedName("seconds")
        private int seconds;

        // Getters y setters
        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
    }
}
