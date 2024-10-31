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
        System.out.print("Введите скорость солнечного света (1-5): ");
        int sunlightRate = scanner.nextInt();
        System.out.print("Введите скорость воды (1-10): ");
        int waterRate = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        ecosystem.addPlant(plantName, sunlightRate, waterRate);
    }

    private static void addAnimal() {
        System.out.print("Введите имя животного: ");
        String animalName = scanner.nextLine();
        System.out.print("Введите тип пищи (Herbivore/Carnivore): ");
        String foodType = scanner.nextLine();
        System.out.print("Введите потребление пищи: ");
        int foodConsumption = scanner.nextInt();
        System.out.print("Введите порог голода: ");
        int hungerThreshold = scanner.nextInt();
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
        System.out.print("Введите количество воды: ");
        int water = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        ecosystem.setResources(water, ecosystem.getSunlight()); // Устанавливаем только воду
        System.out.println("Количество воды обновлено.");
    }

    private static void setSunlight() {
        System.out.print("Введите количество солнечного света: ");
        int sunlight = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        ecosystem.setResources(ecosystem.getWater(), sunlight); // Устанавливаем только солнечный свет
        System.out.println("Количество солнечного света обновлено.");
    }

}