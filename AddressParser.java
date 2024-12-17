import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AddressParser {

    private static List<Address> parseCsv(String filePath) throws Exception {
        List<Address> addresses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Пропускаем заголовок
            br.readLine();
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(";");
                    if (values.length == 4) {
                        String city = values[0].trim();
                        String street = values[1].trim();
                        int house = Integer.parseInt(values[2].trim());
                        int floor = Integer.parseInt(values[3].trim());

                        addresses.add(new Address(city, street, house, floor));
                    } else {
                        System.err.println("Пропущена н��корректная строка: " + line);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка при парсинге чисел в строке: " + line);
                }
            }
        } catch (IOException e) {
            throw new Exception("Ошибка при чтении CSV файла: " + e.getMessage(), e);
        }
        return addresses;
    }

    private static List<Address> parseXml(String filePath) throws Exception {
        try {
            List<Address> addresses = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            NodeList items = document.getElementsByTagName("item");
            if (items.getLength() == 0) {
                throw new Exception("XML файл не содержит элементов 'item'");
            }

            for (int i = 0; i < items.getLength(); i++) {
                try {
                    Element item = (Element) items.item(i);
                    // Получаем значения из атрибутов
                    String city = item.getAttribute("city");
                    String street = item.getAttribute("street");
                    String houseStr = item.getAttribute("house");
                    String floorStr = item.getAttribute("floor");

                    if (city.isEmpty() || street.isEmpty() || houseStr.isEmpty() || floorStr.isEmpty()) {
                        throw new Exception("Отсутствуют обязательные атрибуты");
                    }

                    int house = Integer.parseInt(houseStr);
                    int floor = Integer.parseInt(floorStr);

                    addresses.add(new Address(city, street, house, floor));
                } catch (Exception e) {
                    System.err.println("Ошибка при парсинге элемента " + (i + 1) + ": " + e.getMessage());
                }
            }
            return addresses;
        } catch (Exception e) {
            throw new Exception("Ошибка при парсинге XML файла: " + e.getMessage(), e);
        }
    }

    public static List<Address> parseFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }

        if (filePath.toLowerCase().endsWith(".xml")) {
            return parseXml(filePath);
        } else if (filePath.toLowerCase().endsWith(".csv")) {
            return parseCsv(filePath);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла. Поддерживаются только XML и CSV");
        }
    }

    public static void main(String[] args) {
        try {
            List<Address> addresses = parseCsv("path_to_your_csv_file.csv");
            for (Address address : addresses) {
                System.out.println(address);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге CSV файла: " + e.getMessage());
        }
    }
}