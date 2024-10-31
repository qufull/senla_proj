public class Plants extends Organism {
    private int waterRate;
    private int sunlightRate;

    public Plants(String name,int growthRate, int sunlightRate) {
        super(name,"Plant");
        this.waterRate = growthRate;
        this.sunlightRate = sunlightRate;
    }

    public int getWaterRate() {
        return waterRate;
    }

    public String getName() {
        return name;
    }
    public int getSunlightRate() {
        return sunlightRate;
    }

    @Override
    public String toString() {
        return name +","+ waterRate + ","+ sunlightRate;
    }


}
