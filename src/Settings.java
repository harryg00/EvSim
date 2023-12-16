public class Settings {
    private double seed; // Initialising each variable
    private int currentYear;
    private int windowWidth;
    private int windowHeight;
    private boolean predatorPreyEnabled;
    private boolean seasonsEnabled;
    private String currentSeason;
    // Getter and setter for each
    public double getSeed() {
        return seed;
    }

    public void setSeed(double seed) {
        this.seed = seed;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public boolean isSeasonsEnabled() {
        return seasonsEnabled;
    }

    public boolean isPredatorPreyEnabled() {
        return predatorPreyEnabled;
    }

    public void setSeasonsEnabled(boolean seasonsEnabled) {
        this.seasonsEnabled = seasonsEnabled;
    }
    public void setPredatorPreyEnabled(boolean predatorPreyEnabled) {
        this.predatorPreyEnabled = predatorPreyEnabled;
    }

    public String getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(String season) {
        this.currentSeason = season;
    }
}
