
import java.util.Timer;
import java.util.TimerTask;

class PeriodicTask extends TimerTask {
    private int count = 1;

    @Override
    public void run() {
        System.out.println("[Периодический таймер] Срабатывание №" + count++);
    }
}


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

        Timer periodicTimer = new Timer();
        Timer delayedTimer = new Timer();
        Timer stopTimer = new Timer();

        periodicTimer.scheduleAtFixedRate(new PeriodicTask(), 0, 1500);

        delayedTimer.schedule(new OneTimeTask("Прошло 3 секунды с момента старта"), 3000);

        stopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("[Управляющий таймер] 7 секунд истекли. Останавливаем периодический таймер.");

                periodicTimer.cancel();
                delayedTimer.cancel();
                stopTimer.cancel();
                System.out.println("Работа приложения завершена.");
            }
        }, 7000);
    }
}
