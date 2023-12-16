import FoodTypes.FoodTypes;

import java.util.List;

public class Stats {
    private double currentAwareness; // Initialising each variable
    private double organismsEncountered;
    private List<Double> awarenessHistory;
    private int age;
    private FoodTypes preferredFood;
    private String preferredFoodString;
    private FoodTypes eatenFood;
    private List<FoodTypes> recentFood;
    private double foodFactor;
    private double sizeFactor;
    private double size;
    private double speed;
    private double startSpeed;
    private int currentTemp;
    private int preferredTemp;
    private List<Integer> recentTemp;
    private double tempFactor;
    private String preferredTerrain;
    private String secondPreferredTerrain;
    private String thirdPreferredTerrain;
    private double health;
    private boolean dead;
    private int ageLastBred;
    private int generation;
    private int organismsMet;
    private int totalOrganismsMet;
    private String tsb;
    private int lastAttack;
    // Getter and setter for each
    public double getCurrentAwareness(){
        return currentAwareness;
    }
    public void setCurrentAwareness(double currentAwareness){
        this.currentAwareness = currentAwareness;
    }
    public double getOrganismsEncountered(){
        return organismsEncountered;
    }
    public void setOrganismsEncountered(double organismsEncountered){
        this.organismsEncountered = organismsEncountered;
    }
    public List<Double> getAwarenessHistory() {
        return awarenessHistory;
    }
    public void setAwarenessHistory(List<Double> awarenessHistory){
        this.awarenessHistory = awarenessHistory;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public FoodTypes getPreferredFood(){
        return preferredFood;
    }
    public void setPreferredFood(FoodTypes preferredFood){
        this.preferredFood = preferredFood;
    }
    public FoodTypes getEatenFood(){
        return eatenFood;
    }
    public void setEatenFood(FoodTypes eatenFood){
        this.eatenFood = eatenFood;
    }
    public List<FoodTypes> getRecentFood(){
        return recentFood;
    }
    public void setRecentFood(List<FoodTypes> recentFood){
        this.recentFood = recentFood;
    }
    public double getFoodFactor(){
        return foodFactor;
    }
    public void setFoodFactor(double foodFactor){
        this.foodFactor = foodFactor;
    }
    public double getSizeFactor(){
        return sizeFactor;
    }
    public void setSizeFactor(double sizeFactor){
        this.sizeFactor = sizeFactor;
    }
    public double getSize(){
        return size;
    }
    public void setSize(double size){
        this.size = size;
    }
    public double getSpeed(){
        return speed;
    }
    public void setSpeed(double speed){
        this.speed = speed;
    }
    public int getCurrentTemp(){
        return currentTemp;
    }
    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }
    public int getPreferredTemp(){
        return preferredTemp;
    }
    public void setPreferredTemp(int preferredTemp) {
        this.preferredTemp = preferredTemp;
    }
    public List<Integer> getRecentTemp(){
        return recentTemp;
    }
    public void setRecentTemp(List<Integer> recentTemp){
        this.recentTemp = recentTemp;
    }
    public double getTempFactor(){
        return tempFactor;
    }
    public void setTempFactor(double tempFactor) {
        this.tempFactor = tempFactor;
    }

    public String getPreferredTerrain() {
        return preferredTerrain;
    }

    public void setPreferredTerrain(String preferredTerrain) {
        this.preferredTerrain = preferredTerrain;
    }

    public String getSecondPreferredTerrain() {
        return secondPreferredTerrain;
    }

    public void setSecondPreferredTerrain(String secondPreferredTerrain) {
        this.secondPreferredTerrain = secondPreferredTerrain;
    }

    public String getThirdPreferredTerrain() {
        return thirdPreferredTerrain;
    }

    public void setThirdPreferredTerrain(String thirdPreferredTerrain) {
        this.thirdPreferredTerrain = thirdPreferredTerrain;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getAgeLastBred() {
        return ageLastBred;
    }

    public void setAgeLastBred(int ageLastBred) {
        this.ageLastBred = ageLastBred;
    }

    public String getPreferredFoodString() {
        return preferredFoodString;
    }

    public void setPreferredFoodString(String preferredFoodString) {
        this.preferredFoodString = preferredFoodString;
    }

    public int getGeneration() {
        return generation;
    }

    public double getStartSpeed() {
        return startSpeed;
    }

    public void setStartSpeed(double startSpeed) {
        this.startSpeed = startSpeed;
    }

    public int getOrganismsMet() {
        return organismsMet;
    }

    public void setOrganismsMet(int organismsMet) {
        this.organismsMet = organismsMet;
    }

    public String getTSB() {
        return tsb;
    }

    public void setTSB(String TSB) {
        this.tsb = TSB;
    }

    public int getTotalOrganismsMet() {
        return totalOrganismsMet;
    }

    public void setTotalOrganismsMet(int totalOrganismsMet) {
        this.totalOrganismsMet = totalOrganismsMet;
    }

    public int getLastAttack() {
        return lastAttack;
    }

    public void setLastAttack(int lastAttack) {
        this.lastAttack = lastAttack;
    }
}
