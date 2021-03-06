import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/* TODO:
- Należy napisać parser pliku z kursami walut (dopuszczalne jest użycie bibliotek do parsowania XMLa) i klasę kalkulatora.
- Kalkulator powinien przyjmować kwotę w EUR i docelową walutę, zwracać kwotę w docelowej walucie.
 */
public class Calculator {
    private final static Path currentWorkingDir = Paths.get("").toAbsolutePath();
    private final static String FILE_NAME = currentWorkingDir.normalize() + "\\eurofxref-daily.xml";

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        Map<String, Double> currencies = readDataFromXml();
        getDataFromUser(currencies);
    }

    // Reads data from XML file with SAX
    private static Map<String, Double> readDataFromXml() {
        Map<String, Double> currencies = new HashMap<>();
        try {
            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    // checks if element has 2 attributes and if "currency" length is in international code https://taxsummaries.pwc.com/glossary/currency-codes
                    if (attributes.getLength() == 2 && attributes.getValue(0).length() == 3) {
                        currencies.put(attributes.getValue(0), Double.valueOf(attributes.getValue(1)));
                    }
                }
            };
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(FILE_NAME), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error during reading from the file: " + FILE_NAME);
        }
        return currencies;
    }

    // Gets data from user
    private static void getDataFromUser(Map<String, Double> currencies) {
        boolean flag = true;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            while (flag) { // loop in case errors
                String currency = readCurrency(reader, currencies); // get & check currency with XML
                double amount = readAmount(reader); // get and check amount
                calculate(amount, currency, currencies);
                flag = false;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
            getDataFromUser(currencies);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // final calculations
    private static void calculate(double amount, String currency, Map<String, Double> currencies) {
        double targetAmount = currencies.get(currency);
        double result = targetAmount * amount;
        printLine(String.format("%.2f %s is equal to %.2f %s", amount, "EUR", result, currency));
    }

    private static double readAmount(BufferedReader reader) throws IOException, NumberFormatException {
        printLine("Enter target amount: ");
        double amount = Double.parseDouble(reader.readLine().trim());
        if (amount > 0) { // target amount always > 0
            return amount;
        } else {
            throw new IOException("Wrong amount");
        }
    }

    private static String readCurrency(BufferedReader reader, Map<String, Double> currencies) throws IOException {
        printLine("Target currency (exmpl.: USD|PLN|IDR etc.): ");
        String currency = reader.readLine().toUpperCase(Locale.ROOT).trim();
        if ((currency.length() == 3 && Pattern.matches("[a-zA-Z]+", currency))
                && currencies.containsKey(currency)) { // checks user input + with XML
            return currency;
        } else {
            throw new IOException("Wrong currency code");
        }
    }

    private static void printLine(String line) {
        System.out.println("\n" + line);
    }
}
