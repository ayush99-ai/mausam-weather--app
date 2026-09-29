package com.example.event_planner;

/**
 * Data model representing a single day in the extended weather forecast.
 * Contains meteorology attributes relevant to outdoor event planning.
 */
public class ForecastDay {

    private String dayName;
    private String date;
    private String condition;
    private int highTemp;
    private int lowTemp;
    private int rainProbability;
    private int windSpeed;
    private int comfortIndex;
    private String suitability;
    private String recommendation;

    public ForecastDay(String dayName, String date, String condition, int highTemp,
                       int lowTemp, int rainProbability, int windSpeed, int comfortIndex,
                       String suitability, String recommendation) {
        this.dayName = dayName;
        this.date = date;
        this.condition = condition;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.rainProbability = rainProbability;
        this.windSpeed = windSpeed;
        this.comfortIndex = comfortIndex;
        this.suitability = suitability;
        this.recommendation = recommendation;
    }

    public String getDayName() {
        return dayName;
    }

    public String getDate() {
        return date;
    }

    public String getCondition() {
        return condition;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public int getRainProbability() {
        return rainProbability;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getComfortIndex() {
        return comfortIndex;
    }

    public String getSuitability() {
        return suitability;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
