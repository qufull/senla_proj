import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ecosystem {


    private List<Plants> plants;
    private List<Animal> animals;
    private int water;
    private int sunlight;
    private final Random random;

    public Ecosystem() {
        this.plants = new ArrayList<>();
        this.animals = new ArrayList<>();
        this.water = 1000;
        this.sunlight = 500;
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
            EcosystemManager.logEvent("---------------------------------------------------------------------- ");
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
        List<Animal> animalsCopy = new ArrayList<>(animals);

        for (Animal animal : animalsCopy) {
            if (animal.getHunger() >= animal.getHungerThreshold() * 1.5) {
                toDelete.add(animal);
                EcosystemManager.logEvent("Животное " + animal.getName() + " сбежало.");
                continue;
            }


            if (animal.getHunger() < animal.getHungerThreshold()) {
                animal.starve();
                continue;
            }


            if (animal.getFoodType().equals("Herbivore")) {
                if (!plants.isEmpty()) {
                    Plants plant = plants.remove(0); // Убираем первое растение
                    animal.eat();
                    EcosystemManager.logEvent("Животное " + animal.getName() + " съело " + plant.getName());
                } else {
                    animal.starve(); // Если нет еды, животное голодает
                }
            } else if (animal.getFoodType().equals("Carnivore")) {
                Animal herbivore = findHerbivore(); // Ищем травоядное
                if (herbivore != null) {
                    toDelete.add(herbivore); // Добавляем травоядное в список на удаление
                    animal.eat();
                    EcosystemManager.logEvent("Животное " + animal.getName() + " съело " + herbivore.getName());
                } else {
                    animal.starve(); // Если нет травоядных, хищник голодает
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


        int herbivoreCount = countAnimalsOfType("Herbivore");
        int carnivoreCount = countAnimalsOfType("Carnivore");


        if (herbivoreCount > 0) {
            for (Animal animal : animals) {
                if (animal.getFoodType().equals("Herbivore")) {
                    String name = animal.getName();

                    // Условия для роста популяции травоядных
                    if (sufficientPlants && availableWater > 100 && availableSunlight > 50) {
                        if (herbivoreCount < plants.size() * 2) {
                            prediction.append(name).append(": популяция вероятно будет расти (достаточно ресурсов)\n");
                        } else {
                            prediction.append(name).append(": популяция вероятно останется стабильной (ресурсы ограничены)\n");
                        }
                    } else {
                        prediction.append(name).append(": популяция вероятно сократится из-за недостатка ресурсов\n");
                    }
                }
            }
        } else {
            prediction.append("Нет травоядных животных в экосистеме.\n");
        }


        if (carnivoreCount > 0) {
            for (Animal animal : animals) {
                if (animal.getFoodType().equals("Carnivore")) {
                    String name = animal.getName();


                    if (herbivoreCount % 4 >= 0) {
                        prediction.append(name).append(": популяция вероятно останется стабильной или немного вырастет (достаточно травоядных)\n");
                    } else {
                        prediction.append(name).append(": популяция вероятно сократится (недостаток травоядных)\n");
                    }
                }
            }
        } else {
            prediction.append("Нет хищных животных в экосистеме.\n");
        }


        if (availableWater < 50) {
            prediction.append("Общее состояние: недостаток воды может негативно сказаться на всех популяциях.\n");
        }
        if (availableSunlight < 50) {
            prediction.append("Общее состояние: недостаток солнечного света может негативно сказаться на растениях и травоядных.\n");
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
                water += 500;
                EcosystemManager.logEvent("Был дождливый день вода пополнилась на 500 единиц.");
                break;
            }
            case 1: {
                if(plants.size() < countAnimalsOfType("Herbivore")*2) {
                    int numberOfPlants = random.nextInt(countAnimalsOfType("Herbivore"),countAnimalsOfType("Herbivore")*2); // Случайное количество от 1 до 5
                    for (int i = 0; i < numberOfPlants; i++) {
                        addPlant("Растение", random.nextInt(1, 5), random.nextInt(1, 3));
                    }
                    EcosystemManager.logEvent("Появилось " + numberOfPlants + " новых растений.");
                }
                else {
                    break;
                }
                break;
            }
            case 2: {
                sunlight += 250;
                EcosystemManager.logEvent("Был солнечный день кол-во солнечного света пополнилось на 300 единиц.");
                break;
            }
            case 3: {
                if (!animals.isEmpty()) {
                    Animal animal = animals.get(random.nextInt(animals.size()));

                    int offspringCount = 0; // Счётчик для потомков

                    if (animal.getFoodType().equals("Herbivore")) {
                        while (countAnimalsOfName(animal.getName()) >= 2 && plants.size() >= countAnimalsOfType("Herbivore")) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(1, 3), random.nextInt(15, 20));
                            animals.add(offspring);
                            offspringCount++;


                            if (offspringCount >= random.nextInt(2, 5)) {
                                EcosystemManager.logEvent("Животное " + animal.getName() + " размножилось.");
                                break;
                            }
                        }
                    } else if (animal.getFoodType().equals("Carnivore")) {
                        while (countAnimalsOfName(animal.getName()) >= 2 && countAnimalsOfType("Herbivore") >= 4) {
                            Animal offspring = new Animal(animal.getName(), animal.getFoodType(), random.nextInt(3, 5), random.nextInt(20, 30));
                            animals.add(offspring);
                            offspringCount++;


                            if (offspringCount >= random.nextInt(1,2)) {
                                EcosystemManager.logEvent("Животное " + animal.getName() + " размножилось.");
                                break;
                            }
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
                    addAnimal(carnivores.get(random.nextInt(carnivores.size())), "Carnivore", random.nextInt(3, 5), random.nextInt(20, 30));
                    EcosystemManager.logEvent("Появилось новое хищное животное");
                }
                break;
            }

        }
    }



    public void saveState(String ecosystemFilename) {
        File ecosystemFolder = new File(ecosystemFilename);

        if (!ecosystemFolder.exists()) {
            boolean created = ecosystemFolder.mkdir();
            if (!created) {

                System.err.println("Не удалось создать директорию: " + ecosystemFolder.getAbsolutePath());
                return;
            }
        }


        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_plants.txt");
        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_animals.txt");
        EcosystemManager.clearFile(ecosystemFilename + "/" + ecosystemFilename + "_resources.txt");


        for (Plants plant : plants) {
            EcosystemManager.savePlant(plant, ecosystemFilename + "/" + ecosystemFilename + "_plants.txt");
        }


        for (Animal animal : animals) {
            EcosystemManager.saveAnimal(animal, ecosystemFilename + "/" + ecosystemFilename + "_animals.txt");
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


