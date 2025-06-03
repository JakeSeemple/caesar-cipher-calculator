import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileHandler {
    
    /**
     * Чтение текста из файла с поддержкой UTF-8
     */
    public static String readTextFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            throw new IOException("Файл недоступен для чтения: " + filePath);
        }
        
        // Попытка определить кодировку и чтение файла
        byte[] bytes = Files.readAllBytes(path);
        
        // Пробуем разные кодировки
        String[] encodings = {"UTF-8", "UTF-16", "Windows-1251", "CP1252"};
        
        for (String encoding : encodings) {
            try {
                String content = new String(bytes, encoding);
                // Проверяем, содержит ли текст читаемые символы
                if (isReadableText(content)) {
                    return content;
                }
            } catch (Exception e) {
                // Пробуем следующую кодировку
            }
        }
        
        // Если ничего не подошло, используем UTF-8 по умолчанию
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Проверка, является ли текст читаемым
     */
    private static boolean isReadableText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        // Подсчитываем количество печатных символов
        int printableChars = 0;
        int totalChars = 0;
        
        for (char c : text.toCharArray()) {
            totalChars++;
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || 
                ".,!?;:-()[]{}\"'".indexOf(c) >= 0) {
                printableChars++;
            }
        }
        
        // Если более 80% символов печатные, считаем текст читаемым
        return totalChars > 0 && (double) printableChars / totalChars > 0.8;
    }
    
    /**
     * Получение списка поддерживаемых расширений файлов
     */
    public static boolean isSupportedTextFile(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".txt") || 
               lowerPath.endsWith(".text") ||
               lowerPath.endsWith(".rtf") ||
               lowerPath.endsWith(".log") ||
               !lowerPath.contains(".");
    }
    
    /**
     * Безопасное чтение файла с обработкой ошибок
     */
    public static String safeReadFile(String filePath) {
        try {
            return readTextFromFile(filePath);
        } catch (FileNotFoundException e) {
            return "Ошибка: Файл не найден - " + filePath;
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        } catch (Exception e) {
            return "Неожиданная ошибка: " + e.getMessage();
        }
    }
    
    /**
     * Проверка существования файла
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Получение размера файла в байтах
     */
    public static long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }
    
    /**
     * Создание тестового файла для демонстрации
     */
    public static void createSampleFiles() {
        try {
            // Создаем папку для тестовых файлов
            Files.createDirectories(Paths.get("test-files"));
            
            // Английский тестовый файл
            String englishContent = "Hello World! This is a test message for Caesar cipher encryption and decryption. " +
                                   "The quick brown fox jumps over the lazy dog. " +
                                   "This text contains common English words and phrases.";
            Files.write(Paths.get("test-files/english-sample.txt"), 
                       englishContent.getBytes(StandardCharsets.UTF_8));
            
            // Русский тестовый файл
            String russianContent = "Привет мир! Это тестовое сообщение для шифрования и дешифрования шифром Цезаря. " +
                                   "Быстрая коричневая лиса прыгает через ленивую собаку. " +
                                   "Этот текст содержит обычные русские слова и фразы.";
            Files.write(Paths.get("test-files/russian-sample.txt"), 
                       russianContent.getBytes(StandardCharsets.UTF_8));
            
            // Смешанный файл
            String mixedContent = "Mixed text: Hello Привет! English and Russian together. " +
                                 "Английский и русский вместе. This is a test of multilingual support.";
            Files.write(Paths.get("test-files/mixed-sample.txt"), 
                       mixedContent.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("Тестовые файлы созданы в папке test-files/");
            
        } catch (IOException e) {
            System.err.println("Ошибка создания тестовых файлов: " + e.getMessage());
        }
    }
}