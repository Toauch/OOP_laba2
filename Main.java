import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВведите путь к файлу (или 'exit' для выхода):");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                long startTime = System.currentTimeMillis();
                List<Address> addresses = AddressParser.parseFile(input);

                // Поиск дубликатов
                Map<Address, Integer> duplicates = new HashMap<>();
                for (Address address : addresses) {
                    duplicates.merge(address, 1, Integer::sum);
                }

                System.out.println("\nДубликаты:");
                duplicates.entrySet().stream()
                        .filter(entry -> entry.getValue() > 1)
                        .forEach(entry -> System.out.printf("%s: %d раз%n",
                                entry.getKey(), entry.getValue()));

                // Статистика по этажности
                Map<String, Map<Integer, Integer>> floorStats = new HashMap<>();
                for (Address address : addresses) {
                    floorStats.computeIfAbsent(address.getCity(), k -> new HashMap<>())
                            .merge(address.getFloor(), 1, Integer::sum);
                }

                System.out.println("\nСтатистика по этажности:");
                floorStats.forEach((city, stats) -> {
                    System.out.println("\nГород: " + city);
                    for (int floor = 1; floor <= 5; floor++) {
                        int count = stats.getOrDefault(floor, 0);
                        System.out.printf("%d-этажных зданий: %d%n", floor, count);
                    }
                });

                long endTime = System.currentTimeMillis();
                System.out.printf("%nВремя обработки: %d мс%n", (endTime - startTime));

            } catch (Exception e) {
                System.out.println("Ошибка при обработке файла: " + e.getMessage());
            }
        }

        scanner.close();
    }
}