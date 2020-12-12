package com.MartianChronicles.java;

import java.util.ArrayList;

/**
 * Класс марсианина консерватора
 * @param <T> Тип хрпнимого значения
 */
public class ConservatorMartian<T> extends Martian<T> {
    /**
     * Константное значение
     */
    protected final T value;
    /**
     * Родитель марсианина
     */
    protected final Martian<T> parent;
    /**
     * Коллекция детей
     */
    protected final ArrayList<Martian<T>> children;

    /**
     * Конструктор консерватора
     * @param novator Новатор origin
     */
    public ConservatorMartian(final InnovatorMartian<T> novator)
    {
        super((T)novator.getValue());
        this.value = null;
        parent = novator.getParent();
        children = (ArrayList<Martian<T>>) novator.getChildren();
    }

    /**
     * Конструктор консерватора
     * @param conservator  Консерватор origin
     * @param newParent Новый родитель
     */
    public ConservatorMartian(final ConservatorMartian<T> conservator, final ConservatorMartian<T> newParent)
    {
        super((T)conservator.getValue());
        value = conservator.value;
        parent = newParent;
        children = conservator.children;
    }

    /**
     * Конвертировать семью в консерваторов
     * @param node Узел
     * @return Узел (Консерватор)
     */
    public static ConservatorMartian convertFamilyToConservators(final Martian node)
    {
        // Если лист, то возвращаем консерватора без детей
        if (node.getChildren() == null || node.getChildren().isEmpty())
            return new ConservatorMartian((InnovatorMartian) node);
        // Коллекция всех детей узла
        ArrayList<ConservatorMartian> nodeKids = new ArrayList(node.getChildren().size());
        for (int i = 0; i < node.getChildren().size(); i++)
        {
            // Рекурсивно конвертировать всех детей
            nodeKids.add(i, convertFamilyToConservators(((ArrayList<Martian>) node.getChildren()).get(i)));
        }
        // Узел-копия текущего узла
        InnovatorMartian outputNode = new InnovatorMartian(node.getValue());
        for (Martian kid : nodeKids)
            outputNode.addChild(kid);
        ConservatorMartian conservatorMartian = new ConservatorMartian(outputNode);
        // Добавляем детей новому консерватору
        for (int i = 0; i < nodeKids.size(); i++)
            nodeKids.set(i, new ConservatorMartian(nodeKids.get(i), conservatorMartian));
        conservatorMartian.children.clear();
        for (Martian kid : nodeKids)
            conservatorMartian.children.add(kid);
        return conservatorMartian;
    }
}
