public class CaesarCipher {
    
    // Английский алфавит
    private static final String ENGLISH_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String ENGLISH_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    // Русский алфавит
    private static final String RUSSIAN_LOWER = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
    private static final String RUSSIAN_UPPER = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    
    /**
     * Шифрование текста шифром Цезаря
     */
    public static String encrypt(String text, int shift) {
        return processText(text, shift);
    }
    
    /**
     * Дешифрование текста шифром Цезаря
     */
    public static String decrypt(String text, int shift) {
        return processText(text, -shift);
    }
    
    /**
     * Основная логика обработки текста
     */
    private static String processText(String text, int shift) {
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            char shiftedChar = shiftCharacter(c, shift);
            result.append(shiftedChar);
        }
        
        return result.toString();
    }
    
    /**
     * Сдвиг одного символа
     */
    private static char shiftCharacter(char c, int shift) {
        // Проверяем английские буквы (строчные)
        if (ENGLISH_LOWER.indexOf(c) != -1) {
            return shiftInAlphabet(c, shift, ENGLISH_LOWER);
        }
        
        // Проверяем английские буквы (заглавные)
        if (ENGLISH_UPPER.indexOf(c) != -1) {
            return shiftInAlphabet(c, shift, ENGLISH_UPPER);
        }
        
        // Проверяем русские буквы (строчные)
        if (RUSSIAN_LOWER.indexOf(c) != -1) {
            return shiftInAlphabet(c, shift, RUSSIAN_LOWER);
        }
        
        // Проверяем русские буквы (заглавные)
        if (RUSSIAN_UPPER.indexOf(c) != -1) {
            return shiftInAlphabet(c, shift, RUSSIAN_UPPER);
        }
        
        // Если символ не буква, возвращаем как есть
        return c;
    }
    
    /**
     * Сдвиг символа в пределах алфавита
     */
    private static char shiftInAlphabet(char c, int shift, String alphabet) {
        int currentIndex = alphabet.indexOf(c);
        int alphabetSize = alphabet.length();
        
        // Вычисляем новый индекс с учетом циклического сдвига
        int newIndex = (currentIndex + shift) % alphabetSize;
        
        // Обрабатываем отрицательные индексы
        if (newIndex < 0) {
            newIndex += alphabetSize;
        }
        
        return alphabet.charAt(newIndex);
    }
    
    /**
     * Демонстрация работы класса
     */
    public static void main(String[] args) {
        // Тесты для английского
        System.out.println("=== Tests for English ===");
        String testEn = "Hello World";
        int shift = 3;
        String encrypted = encrypt(testEn, shift);
        String decrypted = decrypt(encrypted, shift);
        
        System.out.println("Original: " + testEn);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Deciphered: " + decrypted);
        
        // Тесты для русского
        System.out.println("\n=== Тесты для русского ===");
        String testRu = "Привет Мир";
        shift = 5;
        encrypted = encrypt(testRu, shift);
        decrypted = decrypt(encrypted, shift);
        
        System.out.println("Оригинал: " + testRu);
        System.out.println("Зашифровано: " + encrypted);
        System.out.println("Расшифровано: " + decrypted);
    }
}