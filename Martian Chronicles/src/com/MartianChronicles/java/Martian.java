package com.MartianChronicles.java;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Класс Марсианина
 * @param <T> Тип хранимого значения
 */
public abstract class Martian<T> {
    /**
     * Значение, хранимое марсианином
     */
    protected T value;
    /**
     * Родитель марсианина
     */
    protected Martian<T> parent;
    /**
     * Дети марсианина
     */
    protected ArrayList<Martian<T>> children;

    /**
     * Конструктор марсианина
     * @param value Значение
     */
    protected Martian(final T value)
    {
        this.value = value;
        children = new ArrayList<Martian<T>>();
    }

    /**
     * Получить хранимое значение
     * @return Хранимое значение
     */
    public final T getValue() { return value; }

    /**
     * Получить родителя
     * @return Родитель
     */
    public Martian<T> getParent()
    {
        if (this instanceof ConservatorMartian)
            return ((ConservatorMartian<T>) this).parent;
        return parent;
    }

    /**
     * Получить детей
     * @return Коллекция всех детей
     */
    public final Collection<Martian<T>> getChildren()
    {
        if (this instanceof ConservatorMartian)
            return ((ConservatorMartian<T>) this).children;
        return children;
    }

    /**
     * Получить всех потомков
     * @return Коллекция потомков
     */
    public final Collection<Martian<T>> getDescadants()
    {
        if (getChildren().isEmpty())
            return new ArrayList<Martian<T>>(0);
        ArrayList<Martian<T>> descadants = new ArrayList<Martian<T>>(getChildren());
        for (Martian<T> martian : getChildren())
            descadants.addAll(martian.getDescadants());
        return descadants;
    }

    /**
     * Есть ли ребенок со значением value
     * @param value Значение
     * @return true - если есть и false - если нету
     */
    public final boolean hasChildWithValue(T value)
    {
        for (Martian<T> martian : children)
            if (martian.getValue().equals(value))
                return true;
        return false;
    }

    /**
     * Есть ли потомок со значением value
     * @param value Значение
     * @return true - если есть и false - если нету
     */
    public final boolean hasDescadantWithValue(T value)
    {
        if (this.value.equals(value))
            return true;
        if (getChildren().isEmpty())
            return false;
        var descadants = getDescadants();
        for (var martian : descadants)
            if (martian.getValue().equals(value))
                return true;
        return false;
    }

    /**
     * Получить корневой элемент
     * @param node Начальный узел для поиска
     * @return Корень
     */
    protected Martian<T> findTop(final Martian<T> node)
    {
        Martian<T> ancestor = node;
        while (ancestor.getParent() != null)
            ancestor = ancestor.getParent();
        return ancestor;
    }

    /**
     * Строчное представление марсианина
     * @return Строчное предстваление
     */
    @Override
    public String toString()
    {
        return String.format("%s(%s:%s)",
                getClass().getSimpleName(),
                value.getClass().getSimpleName(),
                value.toString()
        );
    }
}
