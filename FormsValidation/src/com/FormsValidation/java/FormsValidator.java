package com.FormsValidation.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class FormsValidator implements Validator {

    /**
     * Проверить форму на правильность заполнения.
     * @param object Заполненная форма.
     * @return Множество ошибок валидации.
     * @throws NullPointerException
     */
    @Override
    public Set<ValidationError> validate(Object object) throws NullPointerException, RuntimeException {
        // Проверка формы на null
        if (object == null)
            throw new NullPointerException("Form was null.");
        // Множество ошибок заполнения формы
        Set<ValidationError> validationErrors = new HashSet<>();
        // Если форму необходимо проверять
        if (object.getClass().isAnnotationPresent(Constrained.class))
        {
            try
            {
                // Все поля класса формы
                Field[] formFields = object.getClass().getDeclaredFields();
                for (Field field : formFields)
                {
                    // Пропускаем статические поля
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                        continue;
                    // Путь к полю
                    StringBuilder pathToValue = new StringBuilder();
                    pathToValue.append(field.getName());
                    // Осуществляем валидацию
                    validationErrors.addAll(fieldInspection(object, field, pathToValue));
                }
            }
            catch (IllegalAccessException e)
            {
                System.out.println(e.getMessage());
            }
        }

        // Убираем все null
        validationErrors.removeIf(Objects::isNull);
        return validationErrors;
    }

    /**
     * Валидация поля.
     * @param validObject Проверяемая форма
     * @param field Проверяемое поле
     * @param pathToValue Путь к полю
     * @return Множество ошибок валидации
     * @throws IllegalAccessException
     */
    private Set<ValidationError> fieldInspection(final Object validObject, final Field field, final StringBuilder pathToValue) throws IllegalAccessException, RuntimeException
    {
        // Открываем доступ к приватному полю
        field.setAccessible(true);
        // Получаем значение поля
        Object fieldValue = field.get(validObject);
        // Получаем аннотации поля
        Annotation[] fieldAnnotations = field.getAnnotatedType().getAnnotations();
        Set<ValidationError> foundErrors = new HashSet<>();
        // Проходимся по аннотациям поля
        for (Annotation annotation : fieldAnnotations)
        {
            StringBuilder path = new StringBuilder(pathToValue);
            // Валидация поля для конкретной аннотации
            foundErrors.addAll(proceedAnnotation(fieldValue, field.getName(), path, annotation));
        }
        // Если поле является коллекцией
        if (fieldValue instanceof Collection)
        {
            StringBuilder path = new StringBuilder(pathToValue);
            // Параметризованный тип поля
            AnnotatedParameterizedType listType =
                    (AnnotatedParameterizedType) field.getAnnotatedType();
            // Валидация аннотированных generic типов поля
            foundErrors.addAll(genericTypesValidation((Collection)fieldValue, field.getName(), listType, path));
        }
        // Если поля типа, который также подтвержается проверке
        if (fieldValue != null && fieldValue.getClass().isAnnotationPresent(Constrained.class))
        {
            // Проходимся по всем полям объекта
            for (Field innerField : fieldValue.getClass().getDeclaredFields())
            {
                StringBuilder path = new StringBuilder(pathToValue);
                path.append('.');
                path.append(innerField.getName());
                // Валидация каждого поля отдельно
                foundErrors.addAll(fieldInspection(fieldValue, innerField, path));
            }
        }
        // Закрываем доступ к приватному полю
        field.setAccessible(false);
        return foundErrors;
    }

    /**
     * Распознает тип аннотации и запускает валидацию.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля (простое)
     * @param pathToValue Путь к полю
     * @param annotation Аннотация
     * @return Множество ошибок валидации.
     * @throws IllegalAccessException
     * @throws RuntimeException
     */
    private Set<ValidationError> proceedAnnotation(final Object fieldValue, final String fieldName, StringBuilder pathToValue, Annotation annotation) throws IllegalAccessException, RuntimeException
    {
        Set<ValidationError> validationErrors = new HashSet<>();
        // Определение типа аннотации и запуск соответствующей проверки
        if (annotation instanceof NotNull)
        {
            validationErrors.add(notNull(fieldValue, fieldName, pathToValue));
        }
        else if (annotation instanceof Positive)
        {
            validationErrors.add(positive(fieldValue, fieldName, pathToValue));
        }
        else if (annotation instanceof Negative)
        {
            validationErrors.add(negative(fieldValue, fieldName, pathToValue));
        }
        else if (annotation instanceof NotBlank)
        {
            validationErrors.add(notBlank(fieldValue, fieldName, pathToValue));
        }
        else if (annotation instanceof NotEmpty)
        {
            validationErrors.add(notEmpty(fieldValue, fieldName, pathToValue));
        }
        else if (annotation instanceof Size)
        {
            validationErrors.add(size(fieldValue, fieldName, annotation, pathToValue));
        }
        else if (annotation instanceof InRange)
        {
            validationErrors.add(inRange(fieldValue, fieldName, annotation, pathToValue));
        }
        else if (annotation instanceof AnyOf)
        {
            validationErrors.add(anyOf(fieldValue, fieldName, annotation, pathToValue));
        }
        return validationErrors;
    }

    /**
     * Валидация поля с аннотацией @NotNull.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     */
    private ValidationError notNull(final Object fieldValue, final String fieldName, StringBuilder pathToValue)
    {
        if (fieldValue == null)
        {
            return new FormValidationError(
                    String.format("Form's field %s is empty.", fieldName),
                    pathToValue,
                    null
            );
        }
        return null;
    }

    /**
     * Валидация поля с аннотацией @Positive.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к byte, short, int, long и их оберткам.
     */
    private ValidationError positive(final Object fieldValue, final String fieldName, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null && !checkPrimitiveTypes(fieldValue))
        {
            throw new RuntimeException("Annotation type mismatch. Positive annotation can be applied only to byte, short, int, long.");
        }
        if (fieldValue != null)
        {
            long value = getLong(fieldValue);
            if (value <= 0)
            {
                return new FormValidationError(
                        String.format("Field %s must be positive!", fieldName),
                        pathToValue,
                        fieldValue
                );
            }
        }
        return null;
    }

    /**
     * Валидация поля с аннотацией @Negative.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к byte, short, int, long и их оберткам.
     */
    private ValidationError negative(final Object fieldValue, final String fieldName, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null && !checkPrimitiveTypes(fieldValue))
        {
            throw new RuntimeException("Annotation type mismatch. Negative annotation can be applied only to byte, short, int, long.");
        }
        if (fieldValue != null)
        {
            long value = getLong(fieldValue);
            if (value >= 0)
            {
                return new FormValidationError(
                        String.format("Field %s must be negative!", fieldName),
                        pathToValue,
                        fieldValue
                );
            }
        }
        return null;
    }

    /**
     * Валидация поля с аннотацией @NotBlank.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к String.
     */
    private ValidationError notBlank(final Object fieldValue, final String fieldName, StringBuilder pathToValue) throws RuntimeException
    {
        //Object fieldValue = field.get(validObject);
        if (fieldValue == null)
            return null;
        if (fieldValue instanceof String) {
            String value = (String)fieldValue;
            if (value.isBlank())
            {
                return new FormValidationError(
                    String.format("Field %s must be filled!", fieldName),
                    pathToValue,
                    fieldValue
                );
            }
            return null;
        }
        else
            throw new RuntimeException("NotBlank annotation can be applied only to String type field.");
    }

    /**
     * Валидация поля с аннотацией @NotEmpty.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к List, Set, Map, String.
     */
    private ValidationError notEmpty(final Object fieldValue, final String fieldName, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null && !checkCollectionTypes(fieldValue))
            throw new RuntimeException("Annotation type mismatch. NotEmpty annotation can be applied only to List, Set, Map, String.");
        if (fieldValue != null)
        {
            if (fieldValue instanceof String)
            {
                if (((String)fieldValue).isEmpty()) {
                    return new FormValidationError(
                            String.format("Field %s must not be empty!", fieldName),
                            pathToValue,
                            fieldValue
                    );
                }
            }
            else //if (fieldValue instanceof Collection)
            {
                boolean isEmpty = true;
                if (fieldValue instanceof Collection )
                    isEmpty = ((Collection)fieldValue).isEmpty();
                else if (fieldValue instanceof Map)
                    isEmpty = ((Map)fieldValue).isEmpty();
                if (isEmpty) {
                    return new FormValidationError(
                            String.format("Collection %s must not be empty!", fieldName),
                            pathToValue,
                            fieldValue
                    );
                }
            }
        }
        return null;
    }

    /**
     * Валидация поля с аннотацией @Size.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param annotation Аннотация поля (или параметра)
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к List, Set, Map, String.
     */
    private ValidationError size(final Object fieldValue, final String fieldName, final Annotation annotation, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null && !checkCollectionTypes(fieldValue))
            throw new RuntimeException("Annotation type mismatch. Size annotation can be applied only to List, Set, Map, String.");
        if (fieldValue != null)
        {
            int length = 0;
            if (fieldValue instanceof String)
                length = ((String)fieldValue).length();
            else if (fieldValue instanceof Collection)
                length = ((Collection)fieldValue).size();
            else if (fieldValue instanceof Map)
                length = ((Map)fieldValue).size();
            Size sizeAnnotation = (Size)annotation;
            int min = sizeAnnotation.min();
            int max = sizeAnnotation.max();
            if (max < min)
                throw new RuntimeException("Max value must be more or equal then min value in Size annotation.");
            if (length < min || length > max)
                return new FormValidationError(
                    String.format("Field's or collection's %s size must be at least %d and at max %d!", fieldName, min, max),
                    pathToValue,
                    fieldValue
                );
        }
        return null;
    }

    /**
     * Валидация аннотированных параметров списка
     * @param fieldValue Значение поля (список)
     * @param fieldName Имя поля
     * @param genericType Аннотированный параметр
     * @param pathToValue Путь к полю
     * @return Множество ошибок валидации.
     * @throws IllegalAccessException Ошибка доступа к значению поля.
     * @throws RuntimeException Ошибка во время валидаци аннотированных параметров.
     */
    private Set<ValidationError> genericTypesValidation(final Object fieldValue, final String fieldName, AnnotatedParameterizedType genericType, StringBuilder pathToValue) throws IllegalAccessException, RuntimeException
    {
        Set<ValidationError> valErrors = new HashSet<>();
        // Первый аннотированный параметр.
        AnnotatedType annType = (AnnotatedType)
                genericType.getAnnotatedActualTypeArguments()[0];
        if (annType.getAnnotations() == null || annType.getAnnotations().length == 0)
            return valErrors;
        // Индекс элемента в массиве
        int k = 0;
        if (fieldValue instanceof Collection)
        {
            // Проход по всем элементам
            for (Object element : (Collection)fieldValue)
            {
                String fieldIndexedName = String.format("[%d]", k);
                StringBuilder genericsPath = new StringBuilder(pathToValue);
                //genericsPath.append('.');
                genericsPath.append(fieldIndexedName);
                // Проход по всем аннотациям параметра
                for (Annotation genericsAnnotation : annType.getAnnotations())
                {
                    // Валидация конкретного элемента как поля
                    valErrors.addAll(proceedAnnotation(element, fieldIndexedName, genericsPath, genericsAnnotation));
                }
                // Если элемент нуждается в глубокой проверке
                if (element != null && element.getClass().isAnnotationPresent(Constrained.class))
                {
                    // Проходимся по всем полям
                    for (Field innerField : element.getClass().getDeclaredFields())
                    {
                        StringBuilder path = new StringBuilder(pathToValue);
                        if (path.length() > 0) {
                            path.append(fieldIndexedName);
                            path.append('.');
                        }
                        path.append(innerField.getName());
                        // Запускаем отдельную проверку
                        valErrors.addAll(fieldInspection(element, innerField, path));
                    }
                }
                // Проверка вложенных аннотированных параметров
                if (element instanceof Collection)
                {
                    StringBuilder elementPath = new StringBuilder(pathToValue);
                    elementPath.append(fieldIndexedName);
                    AnnotatedParameterizedType nextGenericType = (AnnotatedParameterizedType) genericType.getAnnotatedActualTypeArguments()[0];
                    valErrors.addAll(genericTypesValidation((Collection) element, fieldIndexedName, nextGenericType, elementPath));
                }
                k++;
            }
        }

        return valErrors;
    }

    /**
     * Валидация поля с аннотацией @InRange.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param annotation Аннотация поля (или параметра)
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к byte, short, int, long и их оберткам.
     */
    private ValidationError inRange(final Object fieldValue, final String fieldName, final Annotation annotation, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null && !checkPrimitiveTypes(fieldValue))
            throw new RuntimeException("Annotation type mismatch. InRange annotation can be applied only to byte, short, int, long.");
        //Object fieldValue = field.get(validObject);
        if (fieldValue != null)
        {
            long value = getLong(fieldValue);
            InRange inRangeAnnotation = (InRange) annotation;
            long min = inRangeAnnotation.min();
            long max = inRangeAnnotation.max();
            if (max < min)
                throw new RuntimeException("Max value must be more or equal then min value in InRange annotation.");
            if (value < min || value > max)
            {
                //pathToValue.append('.');
                //pathToValue.append(field.getName());
                return new FormValidationError(
                        String.format("Field %s value must be between %d and %d.", fieldName, min, max),
                        pathToValue,
                        fieldValue
                );
            }
        }
        return null;
    }

    /**
     * Валидация поля с аннотацией @AnyOf.
     * @param fieldValue Значение поля
     * @param fieldName Имя поля
     * @param annotation Аннотация поля (или параметра)
     * @param pathToValue Путь к полю
     * @return Ошибка валидации.
     * @throws RuntimeException Если аннотация применена не к String.
     */
    private ValidationError anyOf(final Object fieldValue, final String fieldName, final Annotation annotation, StringBuilder pathToValue) throws RuntimeException
    {
        if (fieldValue != null)
        {
            if (fieldValue instanceof String)
            {
                String value = (String)fieldValue;
                String[] annotationValues = ((AnyOf)annotation).value();
                if (annotationValues == null || annotationValues.length == 0)
                {
                    return null;
                }
                if (Arrays.stream(annotationValues).anyMatch(Objects::isNull))
                    throw new RuntimeException("AnyOf annotation's elements in collection must not be null.");
                if (Arrays.stream(annotationValues).noneMatch((String str) -> str.equals(value)))
                {
                    return new FormValidationError(
                            String.format("Field %s value must be one of the following: %s.", fieldName, String.join(", ", annotationValues)),
                            pathToValue,
                            fieldValue
                    );
                }
            }
            else
                throw new RuntimeException("Annotation type mismatch. AnyOf annotation can be applied only to String.");
        }
        return null;
    }

    /**
     * Проверка на byte, short, int, long или их обертки.
     * @param fieldValue Значенеие поля
     * @return Если значение относится к перечисленным типам, то true. Иначе false.
     */
    private boolean checkPrimitiveTypes(Object fieldValue)
    {
        String typeName = fieldValue.getClass().getSimpleName();
        return typeName.equals("byte") || typeName.equals("Byte")
                || typeName.equals("short") || typeName.equals("Short")
                || typeName.equals("int") || typeName.equals("Integer")
                || typeName.equals("long") || typeName.equals("Long");
    }

    /**
     * Проверка на List, Set, Map, String.
     * @param fieldValue Значение поля
     * @return Если значение относится к перечисленным типам, то true. Иначе false.
     */
    private boolean checkCollectionTypes(Object fieldValue)
    {
        return fieldValue instanceof List
                || fieldValue instanceof Set
                || fieldValue instanceof Map
                || fieldValue instanceof String;
    }

    /**
     * Получить значение типа long из int, byte, short, long.
     * @param num Число.
     * @return Значение типа long.
     */
    private long getLong(Object num)
    {
        String typeName = num.getClass().getSimpleName();
        if (typeName.equals("Byte"))
            return (long)((byte)num);
        if (typeName.equals("Short"))
            return (long)((short)num);
        if (typeName.equals("Integer"))
            return (long)((int)num);
        return (long)num;
    }
}
