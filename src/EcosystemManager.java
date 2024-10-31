import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EcosystemManager {
    private static BufferedWriter logWriter;

    public static void savePlant(Plants plant, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true))) {
            writer.write(plant.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Plants> loadPlants(String filename) {
        List<Plants> plants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                plants.add(new Plants(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plants;
    }

    public static void saveAnimal(Animal animal, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true))) {
            writer.write(animal.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Animal> loadAnimals(String filename) {
        List<Animal> animals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                animals.add(new Animal(data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animals;
    }
    public static void saveResources(int water, int sunlight, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename,true))) {
            writer.write("Water:" + water);
            writer.newLine();
            writer.write("Sunlight:" + sunlight);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] loadResources (String filename) {
        int[] resources = new int[2]; // 0: water, 1: sunlight
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Water:")) {
                    resources[0] = Integer.parseInt(line.split(":")[1]);
                } else if (line.startsWith("Sunlight:")) {
                    resources[1] = Integer.parseInt(line.split(":")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }

    static public void clearFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                new FileWriter(file, false).close(); // Открываем файл для записи и сразу закрываем, чтобы очистить его
            }
        } catch (IOException e) {
            System.out.println("Ошибка при очистке файла: " + e.getMessage());
        }
    }

    public static void initializeLog(String filename) {
        try {
            logWriter = new BufferedWriter(new FileWriter(filename, true));
        } catch (IOException e) {
            System.out.println("Ошибка при инициализации лог-файла: " + e.getMessage());
        }
    }
    public static void logEvent(String message) {
        try {
            if (logWriter != null) {
                logWriter.write(message);
                logWriter.newLine();
                logWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в лог-файл: " + e.getMessage());
        }
    }





}
