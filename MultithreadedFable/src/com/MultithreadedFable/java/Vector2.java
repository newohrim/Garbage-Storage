package com.MultithreadedFable.java;

/**
 * Класс двухмерного вектора (immutable)
 */
public final class Vector2 {
    /**
     * Начало координат в двухмерной декартовой системе координат
     */
    public static final Vector2 ZERO = new Vector2(0.0d, 0.0d);
    /**
     * Координата на оси x
     */
    private final double x;
    /**
     * Координата на оси y
     */
    private final double y;

    /**
     * Получить координату на оси X
     * @return Координата на оси X
     */
    public final double getX() { return x; }

    /**
     * Получить координату на оси Y
     * @return Координата на оси Y
     */
    public final double getY() { return y; }

    /**
     * Конструктор вектора
     * @param x Координата на оси X
     * @param y Координата на оси Y
     */
    public Vector2(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Конструктор вектора
     * @param vec Другой вектор
     */
    public Vector2(final Vector2 vec)
    {
        this.x = vec.getX();
        this.y = vec.getY();
    }

    /**
     * Строковое представление вектора
     * @return
     */
    @Override
    public String toString()
    {
        return String.format("(%f; %f)", x, y);
    }

    @Override
    public boolean equals(final Object other) throws IllegalArgumentException
    {
        if (other instanceof Vector2)
            return getX() == ((Vector2) other).getX() && getY() == ((Vector2) other).getY();
        throw new IllegalArgumentException("Argument's class is not an instance of Vector2.");
    }
}
