import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("=== Welcome to Gehtsoft Technical Assessment ===");

        while (true) {
            showMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    caesarEncrypt();
                    break;
                case 2:
                    caesarDecrypt();
                    break;
                case 3:
                    caesarFromFile();
                    break;
                case 4:
                    evaluateExpression();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("The wrong choice. Try again.");
            }

            if (!askToContinue()) {
                System.out.println("Goodbye!");
                break;
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n Please choose an option:");
        System.out.println("1.  Caesar Cipher Encryption");
        System.out.println("2.  Caesar Cipher Decryption ");
        System.out.println("3.  Caesar Cipher - File Input");
        System.out.println("4.  Arithmetic Expression Evaluation");
        System.out.println("5.  Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Неверный ввод
        }
    }

    private static void caesarEncrypt() {
        System.out.println("\n Encryption");
        System.out.print("Enter text to encrypt: ");
        String text = scanner.nextLine();

        System.out.print("Enter shift value (number): ");
        try {
            int shift = Integer.parseInt(scanner.nextLine());
            String result = CaesarCipher.encrypt(text, shift);
            try {
                byte[] bytes = result.getBytes("UTF-8");
                String utf8Result = new String(bytes, "UTF-8");
                System.out.println(" Result: " + utf8Result);
            } catch (Exception e) {
                System.out.println(" Result: " + result);
            }
        } catch (NumberFormatException e) {
            System.out.println(" The wrong value of the shift!");
        }
    }

    private static void caesarDecrypt() {
        System.out.println("\n Decryption");
        System.out.print("Enter text to decrypt: ");
        String text = scanner.nextLine();

        System.out.print("Enter shift value (number, or press Enter to try all shifts): ");
        String shiftInput = scanner.nextLine().trim();

        if (shiftInput.isEmpty()) {
            // Автоматическое определение сдвига
            System.out.println("\n AUTO-DECRYPTION");
            int bestShift = Analyzer.findBestShift(text);
            String bestResult = CaesarCipher.decrypt(text, bestShift);

            System.out.println("Best result (Shift " + bestShift + "): " + bestResult);

            System.out.print("\nShow all variants? (y/n): ");
            String showAll = scanner.nextLine().toLowerCase().trim();
            if (showAll.equals("y") || showAll.equals("yes")) {
                var results = Analyzer.getAllDecryptions(text);
                System.out.println("\n All variants:");
                for (int i = 0; i < Math.min(5, results.size()); i++) {
                    System.out.println((i + 1) + ". " + results.get(i));
                }
            }
        } else {
            try {
                int shift = Integer.parseInt(shiftInput);
                String result = CaesarCipher.decrypt(text, shift);
                try {
                    byte[] bytes = result.getBytes("UTF-8");
                    String utf8Result = new String(bytes, "UTF-8");
                    System.out.println(" Result: " + utf8Result);
                } catch (Exception e) {
                    System.out.println(" Result: " + result);
                }
            } catch (NumberFormatException e) {
                System.out.println(" The wrong value of the shift!");
            }
        }
    }

    private static void caesarFromFile() {
        System.out.println("\n FILE INPUT");
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine().trim();

        if (!FileHandler.fileExists(filePath)) {
            System.out.println(" File not found!");

            // Предложение создать тестовые файлы
            System.out.print("Create sample files for testing? (y/n): ");
            String response = scanner.nextLine().toLowerCase().trim();
            if (response.equals("y") || response.equals("yes")) {
                FileHandler.createSampleFiles();
                System.out.println("Use: test-files/english-sample.txt or test-files/russian-sample.txt");
            }
            return;
        }

        String fileContent = FileHandler.safeReadFile(filePath);
        if (fileContent.startsWith("Error:") || fileContent.startsWith("Undefined")) {
            System.out.println("Error " + fileContent);
            return;
        }

        System.out.println(" File content preview:");
        System.out.println(fileContent.length() > 200 ? fileContent.substring(0, 200) + "..." : fileContent);

        System.out.println("\nChoose operation:");
        System.out.println("1. Encrypt with specific shift");
        System.out.println("2. Decrypt with specific shift");
        System.out.println("3. Auto-decrypt (find best shift)");
        System.out.print("Your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    encryptFileContent(fileContent);
                    break;
                case 2:
                    decryptFileContent(fileContent);
                    break;
                case 3:
                    autoDecryptFileContent(fileContent);
                    break;
                default:
                    System.out.println(" Invalid choice");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input");
        }
    }

    private static void encryptFileContent(String content) {
        System.out.print("Enter shift value: ");
        try {
            int shift = Integer.parseInt(scanner.nextLine());
            String result = CaesarCipher.encrypt(content, shift);
            System.out.println("\n ENCRYPTED RESULT:");
            System.out.println("─".repeat(50));
            System.out.println(result);
            System.out.println("─".repeat(50));
        } catch (NumberFormatException e) {
            System.out.println(" Invalid shift value!");
        }
    }

    private static void decryptFileContent(String content) {
        System.out.print("Enter shift value: ");
        try {
            int shift = Integer.parseInt(scanner.nextLine());
            String result = CaesarCipher.decrypt(content, shift);
            System.out.println("\n DECRYPTED RESULT:");
            System.out.println("─".repeat(50));
            System.out.println(result);
            System.out.println("─".repeat(50));
        } catch (NumberFormatException e) {
            System.out.println(" Invalid shift value!");
        }
    }

    private static void autoDecryptFileContent(String content) {
        System.out.println("\n AUTO-DECRYPTION ANALYSIS");
        System.out.println("Analyzing text\n");

        // Найти лучший сдвиг
        int bestShift = Analyzer.findBestShift(content);
        String bestDecryption = CaesarCipher.decrypt(content, bestShift);

        System.out.println("BEST RESULT (Shift " + bestShift + "):");
        System.out.println("─".repeat(50));
        System.out.println(bestDecryption);
        System.out.println("─".repeat(50));

        // Показать топ-5 вариантов
        System.out.print("\n Show all variants? (y/n): ");
        String response = scanner.nextLine().toLowerCase().trim();
        if (response.equals("y") || response.equals("yes")) {
            System.out.println("\n ALL VARIANTS (sorted by probability):");
            var results = Analyzer.getAllDecryptions(content);
            for (int i = 0; i < Math.min(10, results.size()); i++) {
                var result = results.get(i);
                System.out.printf("%d. Shift %d (Score: %.3f):\n",
                        i + 1, result.shift, result.score);
                System.out.println(
                        "   " + (result.text.length() > 100 ? result.text.substring(0, 100) + "..." : result.text));
                System.out.println();
            }
        }
    }

    private static void evaluateExpression() {
        System.out.println("\n Calculator");
        System.out.print("Enter a mathematical expression: ");
        String expression = scanner.nextLine();

        try {
            double result = ExpressionEvaluator.evaluate(expression);
            System.out.println(" Result: " + result);
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static boolean askToContinue() {
        System.out.print("\n Continue? (y/n): ");
        String response = scanner.nextLine().toLowerCase().trim();
        return response.equals("y") || response.equals("yes") || response.equals("д") || response.equals("да");
    }
}