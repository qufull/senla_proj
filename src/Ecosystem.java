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

            EcosystemManager.logEvent(" ");
            EcosystemManager.logEvent(predictPopulationChanges());
            EcosystemManager.logEvent(" ");

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
        water -= 50;
        sunlight -= 20;
        water = Math.max(0, water);
        sunlight = Math.max(0, sunlight);
    }

    private void growPlants() {
        if (water > 0 && sunlight > 0) {
            for (Plants plant : plants) {
                water -= plant.getWaterRate();
                sunlight -= plant.getSunlightRate();
            }

        }
    }

    private void animalsEat() {
        List<Animal> toDelete = new ArrayList<>();


        var iterator = animals.iterator();

        while (iterator.hasNext()) {
            Animal animal = iterator.next();


            if (animal.getHunger() >= animal.getHungerThreshold() * 1.5) {
                toDelete.add(animal);
                EcosystemManager.logEvent("Животное " + animal.getName() + " побигло.");
                continue;
            }


            if (animal.getHunger() < animal.getHungerThreshold()) {
                animal.starve();
            } else {
                if (animal.getFoodType().equals("Herbivore")) {
                    if (!plants.isEmpty()) {
                        Plants plant = plants.remove(0);
                        animal.eat();
                        EcosystemManager.logEvent("Животное " + animal.getName() + " съело " + plant.getName());
                    } else {
                        animal.starve();

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


    public String predictPopulationChanges() {
        StringBuilder prediction = new StringBuilder("Прогноз изменения популяций:\n");

        int availableWater = getWater();
        int availableSunlight = getSunlight();
        boolean sufficientPlants = !plants.isEmpty();

        // Считаем количество травоядных и хищников
        int herbivoreCount = countAnimalsOfType("Herbivore");
        int carnivoreCount = countAnimalsOfType("Carnivore");

        for (Animal animal : animals) {
            String name = animal.getName();
            String foodType = animal.getFoodType();

            if (foodType.equals("Herbivore")) {
                // Прогноз для травоядных на основе ресурсов
                if (sufficientPlants && availableWater > 100 && availableSunlight > 50) {
                    if (herbivoreCount < plants.size() * 2 && herbivoreCount >= 2) {
                        prediction.append(name).append(": популяция вероятно будет расти\n");
                    } else {
                        prediction.append(name).append(": популяция вероятно останется стабильной\n");
                    }
                } else {
                    prediction.append(name).append(": популяция вероятно сократится из-за недостатка ресурсов\n");
                }
            } else if (foodType.equals("Carnivore")) {
                // Прогноз для хищников на основе количества травоядных
                if (herbivoreCount >= 6 && carnivoreCount <= herbivoreCount / 2) {
                    prediction.append(name).append(": популяция вероятно останется стабильной или немного вырастет\n");
                } else {
                    prediction.append(name).append(": популяция вероятно сократится\n");
                }
            }
        }

        return prediction.toString();
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
                        if (countAnimalsOfName(animal.getName()) >= 2 && plants.size() == countAnimalsOfName("Herbivore")) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(1,3), random.nextInt(15,20));
                            animals.add(offspring);

                        }
                    } else if (animal.getFoodType().equals("Carnivore")) {
                        if (countAnimalsOfName(animal.getName()) >= 2  && countAnimalsOfType("Herbivore") >= 6) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(3,5),random.nextInt(20,30) );
                            animals.add(offspring);

                        }
                    }
                }
                break;

            }
            case 4:{
                List<String> herbivores = List.of("Заяц", "Овца", "Корова", "Лошадь");
                addAnimal(herbivores.get( random.nextInt(herbivores.size())), "Herbivore", random.nextInt(1,2), random.nextInt(8,10));
                EcosystemManager.logEvent("Появилось новое травоядное животное");
                break;
                }
            case 5: {
                List<String> carnivores = List.of("Волк", "Медведь");
                if(countAnimalsOfType("Herbivore") > 6) {
                    addAnimal(carnivores.get(random.nextInt(carnivores.size())), "Carnivore", random.nextInt(3, 5), random.nextInt(15, 20));
                    EcosystemManager.logEvent("Появилось новое хищное животное");
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


