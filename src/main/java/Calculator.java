import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/* TODO:
- Należy napisać parser pliku z kursami walut (dopuszczalne jest użycie bibliotek do parsowania XMLa) i klasę kalkulatora.
- Kalkulator powinien przyjmować kwotę w EUR i docelową walutę, zwracać kwotę w docelowej walucie.
 */
public class Calculator {
    private final static String FILE_NAME = "eurofxref-daily.xml";

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        try {
            Map<String, Double> currencies = readDataFromXml();
            getDataFromUser();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Double> readDataFromXml() throws ParserConfigurationException, SAXException, IOException {
        Map<String, Double> currencies = new HashMap<>();
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if (attributes.getLength() == 2 && attributes.getValue(0).length() == 3) {
                    currencies.put(attributes.getValue(0), Double.valueOf(attributes.getValue(1)));
                }
            }
        };

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File(FILE_NAME), handler);

        return currencies;
    }

    private static void getDataFromUser() {
        String currency;
        double amount;
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
