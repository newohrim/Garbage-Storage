package com.MultithreadedFable.java;

/**
 * Класс телеги
 */
public class Dray {
    /**
     * Время басни
     */
    private final static int DURATION = 25000;
    /**
     * Массив существ
     */
    private final Creature[] CREATURES;
    /**
     * Текущая позиция телеги
     */
    private Vector2 position;
    /**
     * Толкют ли телегу в данный момент
     */
    private boolean pulling = false;
    /**
     * Разрешено ли взаимодействие с телегой
     */
    private boolean allowedToInteract = false;
    /**
     * Можно ли толкать телегу (мьютекс или вроде того, используется для вывода)
     */
    private boolean allowedToPull = true;

    /**
     * Получить текущую позицию телеги (readonly)
     * @return Текущая позиция телеги
     */
    public Vector2 getPosition() { return new Vector2(position); }

    /**
     * Толкют ли телегу в данный момент
     * @return true - если толкают, false - иначе
     */
    public boolean isPulling() { return pulling; }

    /**
     * Разрешено ли взаимодействие с телегой
     * @return true - если разрешено, false - иначе
     */
    public boolean isAllowedToInteract() { return allowedToInteract; }

    /**
     * Разрешено ли толкать телегу
     * @return true - если разрешено, false - иначе
     */
    public boolean isAllowedToPull() { return allowedToPull; }

    /**
     * Задать возможность толкания телеги
     * @param value Значение
     * @return
     */
    public void setAbilityToPull(boolean value) { allowedToPull = value; }

    /**
     * Конструктор телеги
     * @param startPos Стартовая позиция телеги
     * @param creatures Коллекция существ
     * @throws NullPointerException
     */
    public Dray(final Vector2 startPos, Creature ... creatures) throws NullPointerException
    {
        if (creatures == null)
            throw new NullPointerException("Creatures collection was null.");
        for (Creature creature : creatures)
            if (creature == null)
                throw new NullPointerException("Null creature exposed.");
            else
                // Привязываем существо к телеге
                creature.linkTo(this);
        CREATURES = creatures;
        position = new Vector2(startPos);
    }

    /**
     * Запустить басню.
     */
    public void begin()
    {
        if (allowedToInteract)
            throw new RuntimeException("One dray can't be pulled by two functions begin().");
        // Массив потоков-существ
        Thread[] threads = new Thread[CREATURES.length];
        // Инициализация и запуск всех потоков
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(CREATURES[i]);
            threads[i].setDaemon(true);
            threads[i].start();
        }
        // Можно толкать телегу (снимаем барьер)
        allowedToInteract = true;
        // Инициализация и запуск потока счетчика, выводящего позицию телеги рад в 2 секунды
        Thread ticker = new Thread(new Ticker(this));
        ticker.setDaemon(true);
        ticker.start();
        try
        {
            synchronized (this)
            {
                // Телега ждет в течении DURATION
                wait(DURATION);
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        // Завершаем басню
        allowedToInteract = false;
        // Вывод конечной позиции
        System.out.println("Dray finished at " + position.toString());
    }

    /**
     * Толкнуть телегу
     * @param pullingCreature Толкающее существо
     */
    public synchronized void pull(final Creature pullingCreature)
    {
        // Мьютекс на вывод (в Ticker)
        while (!allowedToPull) { /* wait */ }
        pulling = true;
        Vector2 startPos = new Vector2(getPosition());
        position = pullingCreature.getNextPos();
        if (Main.isLogging())
            System.out.printf("%s pulled dray from %s to %s%n",
                    pullingCreature.getName(), startPos.toString(), getPosition().toString());
        pulling = false;
    }
}
