
import java.util.Timer;
import java.util.TimerTask;

// 1. Класс для циклически повторяющегося действия
class PeriodicTask extends TimerTask {
    private int count = 1;

    @Override
    public void run() {
        System.out.println("[Периодический таймер] Срабатывание №" + count++);
    }
}

// 2. Класс для однократного уведомления
class OneTimeTask extends TimerTask {
    private final String message;

    public OneTimeTask(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println("[Однократный таймер] " + message);
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Приложение запущено...");

        // Инициализируем объекты таймеров
        Timer periodicTimer = new Timer();
        Timer delayedTimer = new Timer();
        Timer stopTimer = new Timer();

        // РЕЖИМ 1: Работа с указанным периодом (каждые 1.5 секунды без задержки)
        periodicTimer.scheduleAtFixedRate(new PeriodicTask(), 0, 1500);

        // РЕЖИМ 2: Срабатывание через определённый промежуток времени (через 3 секунды)
        delayedTimer.schedule(new OneTimeTask("Прошло 3 секунды с момента старта"), 3000);

        // РЕЖИМ 3: Ограничение работы первого таймера (остановка через 7 секунд)
        stopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("[Управляющий таймер] 7 секунд истекли. Останавливаем периодический таймер.");
                
                // Принудительно останавливаем периодический таймер
                periodicTimer.cancel(); 
                
                // Завершаем работу всех потоков
                delayedTimer.cancel();
                stopTimer.cancel();
                System.out.println("Работа приложения завершена.");
            }
        }, 7000);
    }
}
