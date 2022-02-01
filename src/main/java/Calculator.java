import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/* TODO:
- Należy napisać parser pliku z kursami walut (dopuszczalne jest użycie bibliotek do parsowania XMLa) i klasę kalkulatora.
- Kalkulator powinien przyjmować kwotę w EUR i docelową walutę, zwracać kwotę w docelowej walucie.
 */
public class Calculator {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
        Map<String, Double> dataFromUser = getDataFromUser();
    }

    private static Map<String, Double> getDataFromUser() {
        String currency = null;
        double amount = 0;
        boolean flag = true;
        Map<String, Double> data = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            while (flag) {
                currency = readCurrency(reader);
                printLine("Enter amount to convert: ");
                amount = readAmount(reader);
                printLine(currency + " amount: " + amount);
                flag = false;
            }
        } catch (IOException | NumberFormatException e) { // handle NumberFormatException message.
            System.out.println(e.getMessage());
            getDataFromUser();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // check value assignment
        if ((currency != null && !currency.isEmpty()) && amount > 0) {
            data.put(currency, amount);
        }
        return data;
    }

    private static double readAmount(BufferedReader reader) throws IOException, NumberFormatException {
        double amount = Double.parseDouble(reader.readLine().trim());
        if (amount > 0) {
            return amount;
        } else {
            throw new IOException("Wrong amount");
        }
    }

    private static String readCurrency(BufferedReader reader) throws IOException {
        printLine("Enter target currency (in format: USD|PLN|IDR etc.): ");
        String currency = reader.readLine().toUpperCase(Locale.ROOT).trim();
        if (currency.length() == 3 && Pattern.matches("[a-zA-Z]+", currency)) {
            return currency;
        } else {
            throw new IOException("Wrong currency code");
        }
    }

    private static void printLine(String line) {
        System.out.println("\n" + line);
    }
}
