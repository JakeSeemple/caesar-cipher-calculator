import java.util.*;

public class Analyzer {
    
    // Частоты букв в английском языке (в процентах)
    private static final Map<Character, Double> ENGLISH_FREQUENCIES = createEnglishFrequencies();
    
    // Частоты букв в русском языке (в процентах)
    private static final Map<Character, Double> RUSSIAN_FREQUENCIES = createRussianFrequencies();
    
    // Наиболее частые биграммы (пары букв)
    private static final Set<String> ENGLISH_BIGRAMS = Set.of(
        "th", "he", "in", "er", "an", "re", "ed", "nd", "on", "en"
    );
    
    private static final Set<String> RUSSIAN_BIGRAMS = Set.of(
        "ст", "но", "то", "на", "ен", "ра", "во", "ко", "ро", "ер"
    );
    
    // Наиболее частые триграммы (тройки букв)
    private static final Set<String> ENGLISH_TRIGRAMS = Set.of(
        "the", "and", "ing", "her", "hat", "his", "tha", "ere", "for", "ent"
    );
    
    private static final Set<String> RUSSIAN_TRIGRAMS = Set.of(
        "что", "это", "как", "все", "для", "его", "она", "они", "при", "или"
    );
    
    private static Map<Character, Double> createEnglishFrequencies() {
        Map<Character, Double> frequencies = new HashMap<>();
        frequencies.put('a', 8.12);
        frequencies.put('b', 1.49);
        frequencies.put('c', 2.78);
        frequencies.put('d', 4.25);
        frequencies.put('e', 12.02);
        frequencies.put('f', 2.23);
        frequencies.put('g', 2.02);
        frequencies.put('h', 6.09);
        frequencies.put('i', 6.97);
        frequencies.put('j', 0.15);
        frequencies.put('k', 0.77);
        frequencies.put('l', 4.03);
        frequencies.put('m', 2.41);
        frequencies.put('n', 6.75);
        frequencies.put('o', 7.51);
        frequencies.put('p', 1.93);
        frequencies.put('q', 0.10);
        frequencies.put('r', 5.99);
        frequencies.put('s', 6.33);
        frequencies.put('t', 9.06);
        frequencies.put('u', 2.76);
        frequencies.put('v', 0.98);
        frequencies.put('w', 2.36);
        frequencies.put('x', 0.15);
        frequencies.put('y', 1.97);
        frequencies.put('z', 0.07);
        return Collections.unmodifiableMap(frequencies);
    }
    
    private static Map<Character, Double> createRussianFrequencies() {
        Map<Character, Double> frequencies = new HashMap<>();
        frequencies.put('а', 8.01);
        frequencies.put('б', 1.59);
        frequencies.put('в', 4.54);
        frequencies.put('г', 1.70);
        frequencies.put('д', 2.98);
        frequencies.put('е', 8.45);
        frequencies.put('ё', 0.04);
        frequencies.put('ж', 0.94);
        frequencies.put('з', 1.65);
        frequencies.put('и', 7.35);
        frequencies.put('й', 1.21);
        frequencies.put('к', 3.49);
        frequencies.put('л', 4.40);
        frequencies.put('м', 3.21);
        frequencies.put('н', 6.70);
        frequencies.put('о', 10.97);
        frequencies.put('п', 2.81);
        frequencies.put('р', 4.73);
        frequencies.put('с', 5.47);
        frequencies.put('т', 6.26);
        frequencies.put('у', 2.62);
        frequencies.put('ф', 0.26);
        frequencies.put('х', 0.97);
        frequencies.put('ц', 0.48);
        frequencies.put('ч', 1.44);
        frequencies.put('ш', 0.73);
        frequencies.put('щ', 0.36);
        frequencies.put('ъ', 0.04);
        frequencies.put('ы', 1.90);
        frequencies.put('ь', 1.74);
        frequencies.put('э', 0.32);
        frequencies.put('ю', 0.64);
        frequencies.put('я', 2.01);
        return Collections.unmodifiableMap(frequencies);
    }
    
    /**
     * Определяет наиболее вероятный сдвиг для дешифрования
     */
    public static int findBestShift(String ciphertext) {
        String language = detectLanguage(ciphertext);
        
        double bestScore = Double.MIN_VALUE;
        int bestShift = 0;
        
        for (int shift = 1; shift <= 25; shift++) {
            if (language.equals("russian") && shift > 32) break; // Русский алфавит 33 буквы
            
            String decrypted = CaesarCipher.decrypt(ciphertext, shift);
            double score = calculateScore(decrypted, language);
            
            if (score > bestScore) {
                bestScore = score;
                bestShift = shift;
            }
        }
        
        return bestShift;
    }
    
    /**
     * Определяет язык текста
     */
    private static String detectLanguage(String text) {
        int russianChars = 0;
        int englishChars = 0;
        
        for (char c : text.toLowerCase().toCharArray()) {
            if (c >= 'а' && c <= 'я' || c == 'ё') {
                russianChars++;
            } else if (c >= 'a' && c <= 'z') {
                englishChars++;
            }
        }
        
        return russianChars > englishChars ? "russian" : "english";
    }
    
    /**
     * Вычисляет общий рейтинг текста
     */
    private static double calculateScore(String text, String language) {
        double frequencyScore = calculateFrequencyScore(text, language);
        double bigramScore = calculateBigramScore(text, language);
        double trigramScore = calculateTrigramScore(text, language);
        double coincidenceScore = calculateCoincidenceIndex(text);
        
        // Взвешенная сумма всех оценок
        return frequencyScore * 0.4 + bigramScore * 0.3 + trigramScore * 0.2 + coincidenceScore * 0.1;
    }
    
    /**
     * Частотный анализ букв
     */
    private static double calculateFrequencyScore(String text, String language) {
        Map<Character, Integer> letterCounts = new HashMap<>();
        int totalLetters = 0;
        
        // Подсчет букв
        for (char c : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
                totalLetters++;
            }
        }
        
        if (totalLetters == 0) return 0;
        
        // Выбор эталонных частот
        Map<Character, Double> expectedFreqs = language.equals("russian") ? 
            RUSSIAN_FREQUENCIES : ENGLISH_FREQUENCIES;
        
        // Вычисление chi-squared статистики
        double chiSquared = 0;
        for (Map.Entry<Character, Double> entry : expectedFreqs.entrySet()) {
            char letter = entry.getKey();
            double expected = (entry.getValue() / 100.0) * totalLetters;
            double observed = letterCounts.getOrDefault(letter, 0);
            
            if (expected > 0) {
                chiSquared += Math.pow(observed - expected, 2) / expected;
            }
        }
        
        // Чем меньше chi-squared, тем лучше совпадение
        return 1.0 / (1.0 + chiSquared / 100.0);
    }
    
    /**
     * Анализ биграмм
     */
    private static double calculateBigramScore(String text, String language) {
        Set<String> commonBigrams = language.equals("russian") ? 
            RUSSIAN_BIGRAMS : ENGLISH_BIGRAMS;
        
        int bigramCount = 0;
        int totalBigrams = 0;
        
        String cleanText = text.toLowerCase().replaceAll("[^а-яёa-z]", "");
        
        for (int i = 0; i < cleanText.length() - 1; i++) {
            String bigram = cleanText.substring(i, i + 2);
            totalBigrams++;
            if (commonBigrams.contains(bigram)) {
                bigramCount++;
            }
        }
        
        return totalBigrams > 0 ? (double) bigramCount / totalBigrams : 0;
    }
    
    /**
     * Анализ триграмм
     */
    private static double calculateTrigramScore(String text, String language) {
        Set<String> commonTrigrams = language.equals("russian") ? 
            RUSSIAN_TRIGRAMS : ENGLISH_TRIGRAMS;
        
        int trigramCount = 0;
        int totalTrigrams = 0;
        
        String cleanText = text.toLowerCase().replaceAll("[^а-яёa-z]", "");
        
        for (int i = 0; i < cleanText.length() - 2; i++) {
            String trigram = cleanText.substring(i, i + 3);
            totalTrigrams++;
            if (commonTrigrams.contains(trigram)) {
                trigramCount++;
            }
        }
        
        return totalTrigrams > 0 ? (double) trigramCount / totalTrigrams : 0;
    }
    
    /**
     * Индекс совпадений (Index of Coincidence)
     */
    private static double calculateCoincidenceIndex(String text) {
        Map<Character, Integer> letterCounts = new HashMap<>();
        int totalLetters = 0;
        
        for (char c : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
                totalLetters++;
            }
        }
        
        if (totalLetters <= 1) return 0;
        
        double ic = 0;
        for (int count : letterCounts.values()) {
            ic += count * (count - 1);  
        }
        
        ic /= totalLetters * (totalLetters - 1);
        return ic;
    }
    
    /**
     * Получение всех возможных дешифровок с оценками
     */
    public static List<DecryptionResult> getAllDecryptions(String ciphertext) {
        String language = detectLanguage(ciphertext);
        List<DecryptionResult> results = new ArrayList<>();
        
        int maxShift = language.equals("russian") ? 33 : 26;
        
        for (int shift = 1; shift < maxShift; shift++) {
            String decrypted = CaesarCipher.decrypt(ciphertext, shift);
            double score = calculateScore(decrypted, language);
            results.add(new DecryptionResult(shift, decrypted, score));
        }
        
        // Сортировка по убыванию рейтинга
        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results;
    }
    
    /**
     * Класс для хранения результата дешифрования
     */
    public static class DecryptionResult {
        public final int shift;
        public final String text;
        public final double score;
        
        public DecryptionResult(int shift, String text, double score) {
            this.shift = shift;
            this.text = text;
            this.score = score;
        }
        
        @Override
        public String toString() {
            return String.format("Shift %d (Score: %.3f): %s", shift, score, text);
        }
    }
}