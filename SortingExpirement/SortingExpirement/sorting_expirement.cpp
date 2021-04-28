// АиСД-2, 2021, задание 5
// Синенко Арсений Александрович БПИ197
// Visual Studio 2019
// Все условия задачи выполнены. 
// В файлах sorting_report_1.csv и sorting_report_2.csv находятся отчеты замеров времени.
// В файлах sorting_report_1.xlsx и отчет об эксперименте.pdf находятся таблицы, графики, выводы.

#include <fstream>
#include <algorithm>
#include "my_sortings.h"

using namespace std;

// Эксперимент по размеру size 
void sort_for_size(my_sortings& sortings, size_t size, ofstream& out)
{
    int64_t* expirement_report = sortings.proceed_sized(size);
    for (int i = 0; i < 32; ++i)
        out << expirement_report[i] << ';';
    out << endl;
    delete[] expirement_report;
}

// Выводит исходные данные
void extract_data(my_sortings& sortings) 
{
    ofstream out("input.txt");
    out << "rand(0-5)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << sortings.data_0[i] << endl;
    out << "rand(0-4000)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << sortings.data_1[i] << endl;
    out << "almost sort(0-4000)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << sortings.data_2[i] << endl;
    out << "reverse sort(4001-1)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << sortings.data_3[i] << endl;
    out.close();
}

// Копирует массив размера size
int* copy_array(const int* arr, size_t size) 
{
    int* new_arr = new int[size];
    for (int i = 0; i < size; ++i)
        new_arr[i] = arr[i];
    return new_arr;
}

// Выводит отсортированные исходные данные
void extract_sorted_data(my_sortings& sortings)
{
    ofstream out("output.txt");
    int* data_0 = copy_array(sortings.data_0, sortings.size);
    sort(data_0, data_0 + sortings.size);
    out << "rand(0-5)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << data_0[i] << endl;
    delete[] data_0;
    int* data_1 = copy_array(sortings.data_1, sortings.size);
    sort(data_1, data_1 + sortings.size);
    out << "rand(0-4000)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << data_1[i] << endl;
    delete[] data_1;
    int* data_2 = copy_array(sortings.data_2, sortings.size);
    sort(data_2, data_2 + sortings.size);
    out << "almost sort(0-4000)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << data_2[i] << endl;
    delete[] data_2;
    int* data_3 = copy_array(sortings.data_3, sortings.size);
    sort(data_3, data_3 + sortings.size);
    out << "reverse sort(4001-1)" << endl;
    for (int i = 0; i < sortings.size; ++i)
        out << data_3[i] << endl;
    delete[] data_3;
}

// Входная точка программы
int main()
{
    // Отчет о массивах от 50 до 300
    ofstream out1("sorting_report_1.csv");
    // Отчет о массивах от 100 до 4100
    ofstream out2("sorting_report_2.csv");
    my_sortings sortings;
    // Вывод исходных данных
    extract_data(sortings);
    extract_sorted_data(sortings);
    // Сортировки и замер времени по размерам массивов
    sort_for_size(sortings, 50, out1);
    sort_for_size(sortings, 100, out1);
    sort_for_size(sortings, 150, out1);
    sort_for_size(sortings, 200, out1);
    sort_for_size(sortings, 250, out1);
    sort_for_size(sortings, 300, out1);
    sort_for_size(sortings, 100, out2);
    sort_for_size(sortings, 1100, out2);
    sort_for_size(sortings, 2100, out2);
    sort_for_size(sortings, 3100, out2);
    sort_for_size(sortings, 4100, out2);
    out1.close();
    out2.close();
    return 0;
}