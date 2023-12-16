public class Organism {
    private int organismId; // Declaring each variable
    private Stats stats;
    private String currentTerrain;
    private int xCoordinate;
    private int yCoordinate;
    private int sex; // 1 is male, 0 is female
    private int fatherID;
    private int motherID;
    private boolean predator;
    // Getter and setter methods for each
    public int getOrganismId() { // Getter and setter methods for each variable
        return organismId;
    }
    public void setOrganismId(int organismId) {
        this.organismId = organismId;
    }
    public Stats getStats() {
        return stats;
    }
    public void setStats(Stats stats) {
        this.stats = stats;
    }
    public String getCurrentTerrain() {
        return currentTerrain;
    }
    public void setCurrentTerrain(String currentTerrain) {
        this.currentTerrain = currentTerrain;
    }
    public int getxCoordinate() {
        return xCoordinate;
    }
    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }
    public int getyCoordinate() {
        return yCoordinate;
    }
    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    public void setSex(int sex){
        this.sex = sex;
    }
    public int getSex() {
        return sex;
    }
    public int getFatherID() {
        return fatherID;
    }
    public void setFatherID(int fatherID) {
        this.fatherID = fatherID;
    }
    public int getMotherID() {
        return motherID;
    }
    public void setMotherID(int motherID) {
        this.motherID = motherID;
    }

    public boolean isPredator() {
        return predator;
    }

    public void setPredator(boolean predator) {
        this.predator = predator;
    }
}