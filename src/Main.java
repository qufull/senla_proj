import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Ecosystem ecosystem = new Ecosystem();

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Создать новую экосистему");
            System.out.println("2. Использовать имеющуюся экосистему");
            System.out.println("0. Выход");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        createNewEcosystem();
                        break;

                    case 2:
                        useExistingEcosystem();
                        break;

                    case 0:
                        System.out.println("Выход из программы.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите число.");
                scanner.nextLine();
            }
        }
    }

    private static void createNewEcosystem() {
        System.out.print("Введите название экосистемы: ");
        String fileName = scanner.nextLine();

        File file = new File(fileName);


        if (file.exists()) {
            System.out.println("Файл с таким именем уже существует. Хотите перезаписать его? (да/нет)");
            String response = scanner.nextLine().trim().toLowerCase();

            if (!response.equals("да")) {
                System.out.println("Создание новой экосистемы отменено. Пожалуйста, введите другое имя для экосистемы.");
                return;
            }
        }

        EcosystemManager.createFile(fileName + "/" + fileName + "_log.txt");
        EcosystemManager.createFile(fileName + "/" + fileName + "_plants.txt");
        EcosystemManager.createFile(fileName + "/" + fileName + "_animals.txt");
        EcosystemManager.createFile(fileName + "/" + fileName + "_resources.txt");

        EcosystemManager.clearFile(fileName + "/" + fileName + "_log.txt");
        ecosystem.log(fileName);


        while (true) {
            System.out.println("1. Добавить растение");
            System.out.println("2. Добавить животное");
            System.out.println("3. Задать кол-во воды");
            System.out.println("4. Задать кол-во солнечного света");
            System.out.println("5. Запустить симуляцию");
            System.out.println("0. Сохранить и выйти");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        addPlant();
                        break;

                    case 2:
                        addAnimal();
                        break;
                    case 3:
                        setWater();
                        break;

                    case 4:
                        setSunlight();
                        break;

                    case 5:
                        startSimulation();
                        break;

                    case 0:
                        ecosystem.saveState(fileName);
                        System.out.println("Экосистема сохранена.");
                        return;

                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите число.");
                scanner.nextLine();
            }
        }
    }

    private static void useExistingEcosystem() {
        System.out.print("Введите название экосистемы для загрузки: ");
        String fileName = scanner.nextLine();

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Файл с именем " + fileName + " не найден. Пожалуйста, проверьте имя файла.");
            return;
        }


        EcosystemManager.clearFile(fileName + "/" + fileName + "_log.txt");
        ecosystem.log(fileName);

        ecosystem.loadPlants(fileName);
        ecosystem.loadAnimals(fileName);
        ecosystem.loadResources(fileName);
        System.out.println("Экосистема загружена.");




        while (true) {
            System.out.println("1. Добавить растение");
            System.out.println("2. Добавить животное");
            System.out.println("3. Задать кол-во воды");
            System.out.println("4. Задать кол-во солнечного света");
            System.out.println("5. Запустить симуляцию");
            System.out.println("0. Сохранить и выйти");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        addPlant();
                        break;

                    case 2:
                        addAnimal();
                        break;

                    case 3:
                        setWater();
                        break;

                    case 4:
                        setSunlight();
                        break;

                    case 5:
                        startSimulation();
                        break;

                    case 0:
                        ecosystem.saveState(fileName);
                        System.out.println("Экосистема сохранена.");
                        return;

                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите число.");
                scanner.nextLine();
            }

        }
    }

    private static void addPlant() {
        System.out.print("Введите имя растения: ");
        String plantName = scanner.nextLine();
        int sunlightRate = -1;
        while (sunlightRate < 1 || sunlightRate > 5) {
            System.out.print("Введите скорость солнечного света (1-5): ");
            sunlightRate = scanner.nextInt();
            if (sunlightRate < 1 || sunlightRate > 5) {
                System.out.println("Пожалуйста, введите число от 1 до 5.");
            }
        }

        int waterRate = -1;
        while (waterRate < 1 || waterRate > 10) {
            System.out.print("Введите скорость воды (1-10): ");
            waterRate = scanner.nextInt();
            if (waterRate < 1 || waterRate > 10) {
                System.out.println("Пожалуйста, введите число от 1 до 10.");
            }
        }
        scanner.nextLine(); // Очистка буфера
        ecosystem.addPlant(plantName, sunlightRate, waterRate);
    }

    private static void addAnimal() {
        System.out.print("Введите имя животного: ");
        String animalName = scanner.nextLine();
        System.out.print("Введите тип пищи (Herbivore/Carnivore): ");
        String foodType = scanner.nextLine();

        int foodConsumption = -1;
        while (foodConsumption < 0) {
            System.out.print("Введите потребление пищи: ");
            foodConsumption = scanner.nextInt();
            if (foodConsumption < 0) {
                System.out.println("Пожалуйста, введите неотрицательное число.");
            }
        }

        int hungerThreshold = -1;
        while (hungerThreshold < 0) {
            System.out.print("Введите порог голода: ");
            hungerThreshold = scanner.nextInt();
            if (hungerThreshold < 0) {
                System.out.println("Пожалуйста, введите неотрицательное число.");
            }
        }
        scanner.nextLine(); // Очистка буфера
        ecosystem.addAnimal(animalName, foodType, foodConsumption, hungerThreshold);
    }

    private static void startSimulation() {
        System.out.print("Введите количество дней для симуляции: ");
        int days = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        ecosystem.simulate(days);
    }

    private static void setWater() {
        int water = -1;
        while (water < 0) {
            System.out.print("Введите количество воды: ");
            water = scanner.nextInt();
            if (water < 0) {
                System.out.println("Пожалуйста, введите неотрицательное число.");
            }
        }
        scanner.nextLine();
        ecosystem.setResources(water, ecosystem.getSunlight());
        System.out.println("Количество воды обновлено.");
    }

    private static void setSunlight() {
        int sunlight = -1;
        while (sunlight < 0) {
            System.out.print("Введите количество солнечного света: ");
            sunlight = scanner.nextInt();
            if (sunlight < 0) {
                System.out.println("Пожалуйста, введите неотрицательное число.");
            }
        }
        scanner.nextLine();
        ecosystem.setResources(ecosystem.getWater(), sunlight);
        System.out.println("Количество солнечного света обновлено.");
    }

}