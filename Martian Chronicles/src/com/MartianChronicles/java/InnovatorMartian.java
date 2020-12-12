package com.MartianChronicles.java;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Класс марианина инноватора
 * @param <T> Тип хранимого значения
 */
public class InnovatorMartian<T> extends Martian {
    /**
     * Конструктор инноватора
     * @param value Хранимое значение
     */
    public InnovatorMartian(final T value) {
        super(value);
    }

    /**
     * Присваивает значение value
     * @param value Значение
     */
    public void setValue(final T value)
    {
        this.value = value;
    }

    /**
     * Назначить нового родителя
     * @param newParent Новый родитель
     */
    public void setParent(final Martian<T> newParent)
    {
        if (newParent == null)
            return;
        if(newParent instanceof ConservatorMartian)
            return;
        InnovatorMartian<T> novatorParent = (InnovatorMartian<T>)newParent;
        if (getParent() != null)
            ((InnovatorMartian<T>)getParent()).deleteChild(this);
        if (!newParent.getChildren().contains(this))
            novatorParent.addChild(this);
        parent = newParent;
    }

    /**
     * Назначить новых детей
     * @param newChildren Коллекция новых детей
     */
    public void setChildren(final Collection<Martian<T>> newChildren)
    {
        if (newChildren == null || newChildren.isEmpty())
            children = new ArrayList<Martian<T>>(0);
        else
        {
            for (Martian<T> child : newChildren)
            {
                if (child == this)
                    throw new RuntimeException("Exception: " + toString() + " is an instance of child.");
                if (this.isDescadantOf(child))
                    throw new RuntimeException("Exception: " + toString() + " has an ancestor role in child.");
            }
            for (var child : children)
                ((InnovatorMartian<T>)child).parent = null;
            children = new ArrayList<Martian<T>>(newChildren);
            for (Object child : children)
            {
                InnovatorMartian<T> currentChild = (InnovatorMartian<T>)child;
                //if (currentChild.getParent() != null)
                //    ((InnovatorMartian<T>)currentChild.getParent()).deleteChild(currentChild);
                currentChild.setParent(this);
            }
        }
    }

    /**
     * Назначить нового ребенка
     * @param child Ребенок
     * @return true - если возможно, false - если нет
     */
    public boolean addChild(final Martian<T> child)
    {
        if(child instanceof ConservatorMartian)
        {
            children.add(child);
            return true;
        }
        if (child == this)
            return false;
        if (isDescadantOf(child))
            return false;
        if (children.contains(child))
            return false;
        InnovatorMartian<T> novatorChild = (InnovatorMartian<T>)child;
        children.add(novatorChild);
        novatorChild.setParent(this);
        return true;
    }

    /**
     * Удалить ребенка
     * @param child Ребенок
     * @return true - если возможно, false - если нет
     */
    public boolean deleteChild(final Martian<T> child)
    {
        if (child instanceof ConservatorMartian)
            return false;
        for (var martian : children)
        {
            if (martian == child)
            {
                child.parent = null;
                children.remove(child);
                return true;
            }
        }
        return false;
    }

    /**
     * Является ли марсианин потомком потенциального предка
     * @param potentialAncestor Потенциальный предок
     * @return true - если является, false
     */
    private boolean isDescadantOf(final Martian<T> potentialAncestor)
    {
        Martian<T> ancestor = getParent();
        while (ancestor != null)
        {
            if (ancestor == potentialAncestor)
                return true;
            ancestor = ancestor.getParent();
        }
        return false;
    }
}
