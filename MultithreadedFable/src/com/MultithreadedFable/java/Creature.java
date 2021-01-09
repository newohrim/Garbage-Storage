package com.MultithreadedFable.java;

import java.util.Random;

/**
 * Класс существа (существует как Runnable для потока)
 */
public class Creature implements Runnable {
    /**
     * Минимальная сила
     */
    private final static int MIN_FORCE = 1;
    /**
     * Максимальная сила
     */
    private final static int MAX_FORCE = 10;
    /**
     * Минимальное время сна
     */
    private final static int MIN_SLEEP_TIME = 1000;
    /**
     * Максимальное время сна
     */
    private final static int MAX_SLEEP_TIME = 5000;
    /**
     * Значение косинуса угла
     */
    private final double X_COS;
    /**
     * Значение синуса угла
     */
    private final double Y_SIN;
    /**
     * Значение силы существа
     */
    private final double FORCE;
    /**
     * Значение имени существа
     */
    private final String NAME;
    /**
     * Телега, к которой
     */
    private Dray dray;

    /**
     * Получить силу существа
     * @return Сила существа
     */
    public double getForce() { return FORCE; }

    /**
     * Получить позицию существа (телеги)
     * @return Позиция существа
     */
    public Vector2 getPosition() throws NullPointerException { return dray.getPosition(); }

    /**
     * Полчуть имя существа
     * @return Имя существа
     */
    public String getName() { return NAME; }

    /**
     * Привязать существо к телеге
     * @param dray Телега
     */
    public void linkTo(Dray dray) { this.dray = dray; }

    /**
     * Конструктор существа
     * @param angle Угол движения
     * @param name Имя существа
     */
    public Creature(final double angle, final String name)
    {
        X_COS = Math.cos(Math.toRadians(angle));
        Y_SIN = Math.sin(Math.toRadians(angle));
        FORCE = calculateForce();
        NAME = name;
    }

    /**
     * Вычислить силу существа
     * @return Сила существа
     */
    private double calculateForce()
    {
        Random rnd = new Random();
        return rnd.nextInt(MAX_FORCE - MIN_FORCE) + MIN_FORCE + rnd.nextDouble();
    }

    /**
     * Основной процесс басни
     * @throws NullPointerException
     */
    @Override
    public void run() throws NullPointerException
    {
        try
        {
            // Барьер
            while (!dray.isAllowedToInteract()) { /* wait until it changes */ }
            while (dray.isAllowedToInteract())
            {
                synchronized (this)
                {
                    dray.pull(this);
                    goSleep();
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Получить новую позицию
     * @return Новая позиция
     */
    public Vector2 getNextPos()
    {
        return new Vector2(getNextX(), getNextY());
    }

    /**
     * Получить новое значение для X
     * @return Новое значение для X
     */
    private double getNextX()
    {
        return getPosition().getX() + FORCE * X_COS;
    }

    /**
     * Получить новое значение Y
     * @return Новое значение Y
     */
    private double getNextY()
    {
        return getPosition().getY() + FORCE * Y_SIN;
    }

    /**
     * Уйти в сон.
     * @throws InterruptedException
     */
    private synchronized void goSleep() throws InterruptedException
    {
        if (Main.isLogging())
            System.out.printf("%s goes to sleep%n", NAME);
        wait(getSleepTime());
        if (Main.isLogging())
            System.out.printf("%s wakes up%n", NAME);
    }

    /**
     * Вычислить время сна существа.
     * @return Время сна
     */
    private int getSleepTime()
    {
        return new Random().nextInt(MAX_SLEEP_TIME - MIN_SLEEP_TIME) + MIN_SLEEP_TIME;
    }
}
