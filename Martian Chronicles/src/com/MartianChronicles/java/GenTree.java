package com.MartianChronicles.java;

import java.util.Scanner;

/**
 * Генетическое дерево марсиан
 * @param <T> Тип хранимого семьей генетического кода
 */
public class GenTree<T> {
    /**
     * Корень
     */
    private Martian<T> root;

    /**
     * Получить корень
     * @return Корень
     */
    public Martian<T> getRoot() { return root; }

    /**
     * Конструктор генетического дерева
     * @param root Корневой элемент
     */
    public GenTree(final Martian<T> root) {
        this.root = root;
    }

    /**
     * Конструктор генетического дерева из строки
     * @param genTree Строка
     */
    public GenTree(String genTree)
    {
        if (genTree.trim().isEmpty())
            throw new RuntimeException("Empty string exception");
        boolean isConservator = false;
        Scanner scanner = new Scanner(genTree);
        InnovatorMartian<T> currentNode = null;
        InnovatorMartian<T> lastAdded = null;
        // Если консерватор найден, то он тут
        InnovatorMartian<T> potentialConservator = null;
        int currentDeep = 0;
        try {
            while (scanner.hasNext())
            {
                int deep = 0;
                String[] parts = scanner.nextLine().split("\\(|:|\\)");
                for (int i = 0; i < parts[0].length(); i++)
                    if (parts[0].charAt(i) == ' ')
                        deep++;
                String value = parts[2];
                if (root == null) {
                    if (deep > 0)
                        throw new RuntimeException("Root input exception");
                    root = makeNode(parts[1], value);
                    currentNode = (InnovatorMartian<T>) root;
                    lastAdded = currentNode;
                    currentDeep = 0;
                }
                else
                {
                    // Ниже описываются все возможные случаи нахождения смежных строк
                    InnovatorMartian<T> node = makeNode(parts[1], value);
                    if (deep / 4 - 1 > currentDeep) {
                        lastAdded.addChild(node);
                        currentNode = lastAdded;
                        lastAdded = node;
                        currentDeep++;
                    }
                    else if (deep / 4 > currentDeep)
                    {
                        currentNode.addChild(node);
                        lastAdded = node;
                    }
                    else if (deep / 4 == currentDeep)
                    {
                        currentNode = (InnovatorMartian<T>) currentNode.getParent();
                        currentNode.addChild(node);
                        lastAdded = node;
                        currentDeep--;
                    }
                    else
                    {
                        throw new RuntimeException("Spusk error");
                    }
                }
                if (parts[0].trim().equals("ConservatorMartian")) {
                    isConservator = true;
                    potentialConservator = lastAdded;
                }
                if (!parts[0].trim().equals("ConservatorMartian") && !parts[0].trim().equals("InnovatorMartian"))
                    throw new RuntimeException("Unknown Martian Type Exception");
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        if (isConservator)
            MakeMartianConservative(potentialConservator);
    }

    /**
     * Создать узел
     * @param valueType Тип хранимого значения
     * @param value Хранимое значение
     * @return Новый узел
     */
    private InnovatorMartian makeNode(String valueType, String value)
    {
        switch (valueType.trim()) {
            case "String":
                if (value.length() > 256)
                    throw new RuntimeException("Max string length is 256 symbols.");
                return new InnovatorMartian<String>(value);
            case "Integer":
                return new InnovatorMartian<Integer>(Integer.parseInt(value.trim()));
            case "Double":
                return new InnovatorMartian<Double>(Double.parseDouble(value.trim()));
            default:
                throw new RuntimeException("Unknown value type exception.");
        }
    }

    /**
     * Законсервировать семью
     * @param martian Член семьи
     */
    public void MakeMartianConservative(Martian<T> martian)
    {
        if (martian instanceof InnovatorMartian)
        {
            Martian<T> mainRoot = root.findTop(root);
            root = ConservatorMartian.convertFamilyToConservators(mainRoot);
        }
    }

    /**
     * Строчное предстваление семьи
     * @return Строчное представление
     */
    @Override
    public String toString()
    {
        return recursiveToString(0, root);
    }

    /**
     * Создать строку из узла
     * @param deep Глубина узла
     * @param node Узел
     * @return Строка
     */
    private String recursiveToString(int deep, final Martian<T> node)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSpaces(deep * 4));
        stringBuilder.append(node.toString());
        stringBuilder.append("\n");
        if (!node.getChildren().isEmpty())
        {
            deep++;
            for (Martian<T> child : node.getChildren())
                stringBuilder.append(recursiveToString(deep, child));
        }
        return stringBuilder.toString();
    }

    /**
     * Получить строку из пробелов
     * @param count Количество пробелов
     * @return Строка из пробелов
     */
    private String getSpaces(final int count)
    {
        StringBuilder spaces = new StringBuilder(count);
        for (int i = 0; i < count; i++)
            spaces.append(' ');
        return spaces.toString();
    }
}
