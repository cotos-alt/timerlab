import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void printTopProcesses(int limit) {
        System.out.println("\n--- TOP-" + limit + " пользовательских процессов ---");
        System.out.printf("%-10s %-35s %-15s\n", "PID", "Команда / Имя", "Пользователь");
        System.out.println("------------------------------------------------------------");

        List<ProcessHandle> processes = ProcessHandle.allProcesses()
                .filter(ProcessHandle::isAlive)
                .filter(ph -> {
                    String user = ph.info().user().orElse("");
                    boolean hasCommand = ph.info().command().isPresent();
                    return hasCommand && !user.isEmpty() && !user.equals("root");
                })
                .sorted(Comparator.comparingLong(ProcessHandle::pid))
                .limit(limit)
                .collect(Collectors.toList());

        if (processes.isEmpty()) {
            System.out.println("Нет доступных процессов пользователя.");
            System.out.println("------------------------------------------------------------");
            return;
        }

        for (ProcessHandle ph : processes) {
            String command = ph.info().command()
                    .map(cmd -> cmd.contains("/") ? cmd.substring(cmd.lastIndexOf('/') + 1) : cmd)
                    .orElse("[Скрытый процесс]");

            String user = ph.info().user().orElse("N/A");

            System.out.printf("%-10d %-35s %-15s\n", ph.pid(), truncate(command, 34), user);
        }
        System.out.println("------------------------------------------------------------");
    }

    // Завершение процесса по PID
    public static boolean killProcess(long pid) {
        return ProcessHandle.of(pid)
                .map(ProcessHandle::destroy)
                .orElse(false);
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public static void main(String[] args) {
        System.out.println("=== Системный Таск-Менеджер и Планировщик (Java) ===");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n[Фоновый таймер]: Автоматический мониторинг на " + LocalTime.now());
            printTopProcesses(5);
            System.out.print("> ");
        }, 15, 15, TimeUnit.SECONDS);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nКоманды: [1] Показать топ процессов | [2] Завершить процесс по PID | [3] Выход");
            System.out.print("> ");

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                printTopProcesses(10);
            } else if ("2".equals(input)) {
                System.out.print("Введите PID процесса для завершения: ");
                try {
                    if (!scanner.hasNextLine()) break;
                    long pid = Long.parseLong(scanner.nextLine().trim());
                    if (killProcess(pid)) {
                        System.out.println("[Успех] Сигнал завершения отправлен процессу PID " + pid);
                    } else {
                        System.out.println("[Ошибка] Не удалось найти или завершить процесс PID " + pid);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат PID.");
                }
            } else if ("3".equals(input)) {
                System.out.println("Завершение работы таск-менеджера...");
                scheduler.shutdown();
                break;
            }
        }
        scanner.close();
    }
}
