import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/* TODO:
- Należy napisać parser pliku z kursami walut (dopuszczalne jest użycie bibliotek do parsowania XMLa) i klasę kalkulatora.
- Kalkulator powinien przyjmować kwotę w EUR i docelową walutę, zwracać kwotę w docelowej walucie.
 */
public class Calculator {
    public static void main(String[] args) {
        start();
    }

    private static void start() {
       getDataFromUser();
    }

    private static void getDataFromUser() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Enter target currency (USD | PLN etc.): ");
            String currency = reader.readLine().toUpperCase(Locale.ROOT).trim();
            System.out.println("Enter amount to convert: ");
            double amount = Double.parseDouble(reader.readLine().trim());
            System.out.println(currency+" amount: "+amount);
        }catch (IOException e){
            System.err.println("Error while reading from the file");
            e.printStackTrace();
        }
    }
}
