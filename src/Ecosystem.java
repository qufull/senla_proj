import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ecosystem {


    private List<Plants> plants;
    private List<Animal> animals;
    private int water;
    private int sunlight;
    private Random random;
    private FileWriter logWriter;

    public Ecosystem() {
        this.plants = new ArrayList<>();
        this.animals = new ArrayList<>();
        this.water = 300;
        this.sunlight = 200;
        this.random = new Random();
    }


    public void log(String fileName) {
        EcosystemManager.initializeLog(fileName + "/" + fileName + "_log.txt");
    }

    public void loadResources(String resourcesFilename) {
        int[] resources = EcosystemManager.loadResources(resourcesFilename + "/" + resourcesFilename + "_resources.txt");
        this.water = resources[0];
        this.sunlight = resources[1];
    }

    public void loadPlants(String plantFilename) {
        plants = EcosystemManager.loadPlants(plantFilename + "/" + plantFilename + "_plants.txt");
    }

    public void loadAnimals(String animalFilename) {
        animals = EcosystemManager.loadAnimals(animalFilename + "/" + animalFilename + "_animals.txt");
    }


    public void addPlant(String name,int sunlightRate,int waterRate ) {
        Plants plant = new Plants(name, sunlightRate, waterRate);
        plants.add(plant);
    }

    public void addAnimal(String name,  String foodType, int foodConsumption,int hungerThreshold) {
        Animal animal = new Animal(name, foodType, foodConsumption, hungerThreshold);
        animals.add(animal);

    }


    public void simulate(int days) {

        for (int day = 1; day <= days; day++) {
            EcosystemManager.logEvent("День " + day + " - Начальные условия");
            EcosystemManager.logEvent("Вода: " + water + ", Солнечный свет: " + sunlight);
            EcosystemManager.logEvent("Количество животных: " + animals.size() + ", Количество растений: " + plants.size());

            growPlants();
            animalsEat();
            reduceResources();
            applyRandomEvents();

            EcosystemManager.logEvent("Конец дня " + day);
            EcosystemManager.logEvent("Оставшаяся вода: " + water + ", Оставшийся солнечный свет: " + sunlight);
            EcosystemManager.logEvent("Количество животных: " + animals.size() + ", Количество растений: " + plants.size());
            EcosystemManager.logEvent(" ");
        }
    }


    private void reduceResources() {
        water += 50;
        sunlight += 20;
        if (water < 0) water = 0;
        if (sunlight < 0) sunlight = 0;
    }

    private void growPlants() {
        if (water > 0 && sunlight > 0) {
            for (Plants plant : plants) {
                water -= plant.getWaterRate(); // Растения потребляют воду
                sunlight -= plant.getSunlightRate();
            }
             // И солнечный свет
        }
    }

    private void animalsEat() {
        List<Animal> toDelete = new ArrayList<>();  // Список для удаления животных
            // Список для добавления новых животных (размножение)

        var iterator = animals.iterator();  // Итератор для обхода списка животных

        while (iterator.hasNext()) {
            Animal animal = iterator.next();

            // Если голод животного превышает порог в 2 раза, оно умирает
            if (animal.getHunger() >= animal.getHungerThreshold() * 1.5) {
                toDelete.add(animal);
                EcosystemManager.logEvent("Животное " + animal.getName() + " побигло.");
                continue;  // Пропускаем дальнейшую обработку этого животного
            }

            // Проверяем, голодает ли животное
            if (animal.getHunger() < animal.getHungerThreshold()) {
                animal.starve();
            } else {
                if (animal.getFoodType().equals("Herbivore")) {
                    if (!plants.isEmpty()) {
                        Plants plant = plants.remove(0);  // Травоядное съедает растение
                        animal.eat();
                        EcosystemManager.logEvent("Животное " + animal.getName() + " съело " + plant.getName());
                    } else {
                        animal.starve();  // Если нет еды, животное голодает

                    }
                } else if (animal.getFoodType().equals("Carnivore")) {
                    Animal herbivore = findHerbivore();
                    if (herbivore != null && !toDelete.contains(herbivore)) {
                        toDelete.add(herbivore);  // Добавляем травоядное в список на удаление
                        animal.eat();
                        EcosystemManager.logEvent("Животное " + animal.getName() + " съело " + herbivore.getName());

                    }
                }
            }


        }


        animals.removeAll(toDelete);



    }



    private int countAnimalsOfName(String Name) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal.getName().equals(Name)) {
                count++;
            }
        }
        return count;
    }

    private int countAnimalsOfType(String foodType) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal.getFoodType().equals(foodType)) {
                count++;
            }
        }
        return count;
    }




    private Animal findHerbivore() {
        for (Animal animal : animals) {
            if (animal.getFoodType().equals("Herbivore")) {
                return animal;
            }
        }
        return null;
    }

    public boolean hasSufficientFood(String foodType) {
        int requiredAmount = foodType.equals("Herbivore") ? 1 : 6;

        if (foodType.equals("Herbivore")) {

            if (plants.size() >= requiredAmount) {
                return true;
            } else {

                return false;
            }
        } else if (foodType.equals("Carnivore")) {

            int herbivoreCount = 0;
            for (Animal animal : animals) {
                if (animal.getFoodType().equals("Herbivore")) {
                    herbivoreCount++;
                }
            }
            if (herbivoreCount >= requiredAmount) {
                return true;
            } else {

                return false;
            }
        } else {
            System.out.println("Неизвестный тип пищи: " + foodType);
            return false;
        }
    }

    private void applyRandomEvents() {
        int event = random.nextInt(6);
        switch (event) {
            case 0: {
                water += 200;
                break;
            }
            case 1: {
                addPlant("Новое растение", random.nextInt(5) + 1, 5);
                break;

            }
            case 2: {
                sunlight += 20;
                break;
            }
            case 3: {
                if (!animals.isEmpty()) {

                    Animal animal = animals.get(random.nextInt(animals.size()));


                    if (animal.getFoodType().equals("Herbivore")) {
                        if (countAnimalsOfName(animal.getName()) >= 2 && hasSufficientFood("Herbivore")) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(20), animal.getHungerThreshold());
                            animals.add(offspring);

                        }
                    } else if (animal.getFoodType().equals("Carnivore")) {
                        if (countAnimalsOfName(animal.getName()) >= 2 && hasSufficientFood("Carnivore")) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(20,30), animal.getHungerThreshold());
                            animals.add(offspring);

                        }
                    }
                }
                break;

            }
            case 4:{
                List<String> Herbivore = new ArrayList<>();
                Herbivore.add("Заяц");
                Herbivore.add("Овца");
                Herbivore.add("Корова");
                Herbivore.add("Лошадь");

                addAnimal(Herbivore.get( random.nextInt(Herbivore.size())), "Herbivore", random.nextInt(1,2), random.nextInt(1,8));
                break;
                }
            case 5: {
                List<String> Carnivore = new ArrayList<>();
                Carnivore.add("Волк");
                Carnivore.add("Медведь");
                if(countAnimalsOfType("Herbivore") > 2) {
                    addAnimal(Carnivore.get(random.nextInt(Carnivore.size())), "Carnivore", random.nextInt(1, 5), random.nextInt(15, 30));
                }
                break;
            }
        }
    }



    public void saveState(String ecosystemFilename) {



        File ecosystemFolder = new File(ecosystemFilename);
        if (!ecosystemFolder.exists()) {
            ecosystemFolder.mkdir();
        }
        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_plants.txt");
        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_animals.txt");
        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_resources.txt");

            for (Plants plant : plants) {
                EcosystemManager.savePlant(plant, ecosystemFilename+ "/"+ ecosystemFilename + "_plants.txt");
            }

            for (Animal animal : animals) {
                EcosystemManager.saveAnimal(animal, ecosystemFilename+ "/"+ ecosystemFilename + "_animals.txt");
            }

            EcosystemManager.saveResources(water, sunlight, ecosystemFilename + "/" + ecosystemFilename + "_resources.txt");


    }


    public void setResources(int water, int sunlight) {
        this.water = water;
        this.sunlight = sunlight;
    }

    public int getWater() {
        return water;
    }

    public int getSunlight() {
        return sunlight;
    }



}


