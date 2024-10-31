public class Animal extends Organism{
    private int hunger;
    private int hungerThreshold;
    private int food_consumption;
    private String foodType;


    public Animal(String name, String foodType, int foodConsumption, int hungerThreshold) {
        super(name, "Animal");
        this.food_consumption = foodConsumption;
        this.foodType = foodType;
        this.hunger = 0;
        this.hungerThreshold = hungerThreshold;
    }

    public Animal(String name, String foodType, int foodConsumption, int hungerThreshold,int hunger) {
        super(name, "Animal");
        this.food_consumption = foodConsumption;
        this.foodType = foodType;
        this.hunger = hunger;
        this.hungerThreshold = hungerThreshold;
    }

    public String getFoodType() {
        return foodType;
    }

    public String getName() {
        return name;
    }

    public void eat() {
        hunger = 0;  // Обнуляем голод при еде
    }

    public void starve() {
        hunger += food_consumption;
    }


    public int getHunger() {
        return hunger;
    }

    public int getHungerThreshold() {
        return hungerThreshold;
    }

    public int getFoodConsumption() {
        return food_consumption;
    }

    @Override
    public String toString() {
        return name + ","+ foodType  + "," + food_consumption + "," + hungerThreshold + "," + hunger;
    }

}
