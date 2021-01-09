/* Синенко Арсений Александрович БПИ-197 (2 курс)
На вход программе подаются две координаты x и y.
Если необходимо задать значение только для y,
необходимо подать в аргументы 0 и значение y.
Доп. функционал: Телегу могут толкать больше 3 существ + логгирование (3 параметр log)
В тестах можно случайно определить свои политические координаты.
 */

package com.MultithreadedFable.java;

public class Main {

    private static final String LOG_PARAMETER_NAME = "log";
    private static boolean logging = false;
    public static boolean isLogging() { return logging; }

    // Входная точка программы
    public static void main(String[] args)
    {
        // Кооредината x телеги
        double x = 0.0d;
        // Координата y телеги
        double y = 0.0d;
        try
        {
            // Парсинг дополнительных координат
            if (args.length > 0)
                x = Double.parseDouble(args[0]);
            if (args.length > 1)
                y = Double.parseDouble(args[1]);
            if (args.length > 2 && args[2].toLowerCase().trim().equals(LOG_PARAMETER_NAME))
                logging = true;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Number format was incorrect. Try again with different params...");
            return;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return;
        }
        // Создание существ
        Creature Lebed = new Creature(60, "Lebed");
        Creature Rak = new Creature(180, "Rak");
        Creature Shuka = new Creature(300, "Shuka");
        // Создание телеги
        Dray dray = new Dray(new Vector2(x, y), Lebed, Rak, Shuka);
        // Запуск басни
        dray.begin();
    }
}
