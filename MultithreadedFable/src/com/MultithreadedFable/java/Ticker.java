package com.MultithreadedFable.java;

/**
 * Класс для счетчика (Существует как поток)
 */
public class Ticker implements Runnable {
    /**
     * Временной период в миллисекундах
     */
    private static final int COOL_DOWN = 2000;
    /**
     * Телега
     */
    private final Dray dray;

    /**
     * Конструктор счетчика
     * @param dray Телега
     */
    public Ticker(Dray dray)
    {
        this.dray = dray;
    }


    @Override
    public synchronized void run()
    {
        try
        {
            // Избегаю лишнюю проверку в while
            wait(COOL_DOWN);
            while (dray.isAllowedToInteract())
            {
                // Вывести позицию телеги
                printPos();
                wait(COOL_DOWN);
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Вывести текущую позицию телеги
     */
    private void printPos()
    {
        // Блокируем мьютекс (чтобы позиция не поменялась за время вывода)
        dray.setAbilityToPull(false);
        // Выводим текущую позицию
        System.out.printf("Dray position: (%.2f; %.2f)%n",
                dray.getPosition().getX(), dray.getPosition().getY());
        // Разблокируем мьютекс
        dray.setAbilityToPull(true);
    }
}
