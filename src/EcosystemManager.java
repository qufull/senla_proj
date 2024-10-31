import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EcosystemManager {
    private static final Logger logger = Logger.getLogger(EcosystemManager.class.getName());
    private static BufferedWriter logWriter;

    static {
        logger.setLevel(Level.WARNING); // Установите уровень логирования
    }

    public static void savePlant(Plants plant, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(plant.toString());
            writer.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при сохранении растения в файл: " + filename, e);
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
            logger.log(Level.SEVERE, "Ошибка при загрузке растений из файла: " + filename, e);
        }
        return plants;
    }

    public static void saveAnimal(Animal animal, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(animal.toString());
            writer.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при сохранении животного в файл: " + filename, e);
        }
    }

    public static List<Animal> loadAnimals(String filename) {
        List<Animal> animals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 5) {
                    logger.warning("Ошибка: неполная запись в файле " + filename + ": " + line);
                    continue;
                }
                try {
                    animals.add(new Animal(data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])));
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Ошибка форматирования данных в строке: " + line, e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке животных из файла: " + filename, e);
        }
        return animals;
    }

    public static void saveResources(int water, int sunlight, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write("Water:" + water);
            writer.newLine();
            writer.write("Sunlight:" + sunlight);
            writer.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при сохранении ресурсов в файл: " + filename, e);
        }
    }

    public static int[] loadResources(String filename) {
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
            logger.log(Level.SEVERE, "Ошибка при загрузке ресурсов из файла: " + filename, e);
        }
        return resources;
    }

    public static void clearFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                new FileWriter(file, false).close(); // Открываем файл для записи и сразу закрываем, чтобы очистить его
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при очистке файла: " + e.getMessage(), e);
        }
    }

    public static void initializeLog(String filename) {
        try {
            logWriter = new BufferedWriter(new FileWriter(filename, true));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при инициализации лог-файла: " + e.getMessage(), e);
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
            logger.log(Level.SEVERE, "Ошибка при записи в лог-файл: " + e.getMessage(), e);
        }
    }

    public static void createFile(String filename) {
        File file = new File(filename);
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    logger.warning("Не удалось создать директорию: " + parentDir.getAbsolutePath());
                    return;
                }
            }

            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    logger.info("Файл создан: " + filename);
                } else {
                    logger.warning("Не удалось создать файл: " + filename);
                }
            } else {
                logger.info("Файл уже существует: " + filename);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при создании файла: " + e.getMessage(), e);
        }
    }
}
