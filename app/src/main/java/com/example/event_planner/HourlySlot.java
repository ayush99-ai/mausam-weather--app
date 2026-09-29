package com.example.event_planner;

/**
 * Model representing an hourly weather timeline slot for outdoor events.
 */
public class HourlySlot {

    private String timeLabel;
    private int temperature;
    private int rainProbability;
    private String condition;
    private int comfortScore;
    private String rating;
    private String description;
    private boolean isRecommended;

    public HourlySlot(String timeLabel, int temperature, int rainProbability,
                      String condition, int comfortScore, String rating,
                      String description, boolean isRecommended) {
        this.timeLabel = timeLabel;
        this.temperature = temperature;
        this.rainProbability = rainProbability;
        this.condition = condition;
        this.comfortScore = comfortScore;
        this.rating = rating;
        this.description = description;
        this.isRecommended = isRecommended;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getRainProbability() {
        return rainProbability;
    }

    public String getCondition() {
        return condition;
    }

    public int getComfortScore() {
        return comfortScore;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRecommended() {
        return isRecommended;
    }
}
