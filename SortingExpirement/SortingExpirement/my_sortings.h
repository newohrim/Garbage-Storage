#include <stdint.h>

#pragma once
class my_sortings
{
public:
	// Размер исходных данных
	const size_t size = 4100;
	// Количество замеров для усреднения
	const size_t aproxyCount = 25;
	int* data_0;
	int* data_1;
	int* data_2;
	int* data_3;
	// Проверяет корректно ли отсортирован массив
	static bool is_sorted(const int* array, size_t arrSize);
	// Сортировка пузырьком
	static void bubblesort(int*& array, int*& outputArray, int left, int right);
	// Сортировка пузырьком с условием Айверсона 1.
	static void bubblesort_A(int*& array, int*& outputArray, int left, int right);
	// Сортировка бинарными вставками
	static void binaryinserts(int*& array, int*& outputArray, int left, int right);
	// Сортировка подсчетом
	static void countingSort(int*& numbers, int*& outputArray, int left, int right);
	// Цифровая сортировка по основанию 256
	static void radixSort(int*& numbers, int*& outputArray, int left, int right);
	// Сортировка слиянием
	static void mergeSort(int*& array, int*& outputArray, int left, int right);
	// Быстрая сортировка
	static void quicksort(int*& numbers, int*& outputArray, int left, int right);
	// Сортировка кучей
	static void heapSort(int*& numbers, int*& outputArray, int left, int right);
	// Запускает эксперимент
	long long execute(void (*sorting_func)(int*&, int*&, int, int), const int* data, size_t data_size);
	// Провести эксперимент для размера data_size
	int64_t* proceed_sized(size_t data_size);
	my_sortings();
	~my_sortings()
	{
		delete[] data_0;
		delete[] data_1;
		delete[] data_2;
		delete[] data_3;
	}

private:
	// Ссылки на функции сортировки
	void (*sorts[8])(int*&, int*&, int, int);
	// Ссылки на исходные данные
	int* data[4];
	// Генерация данных 0
	void gen_data_0();
	// Генерация данных 1
	void gen_data_1();
	// Генерация данных 2
	void gen_data_2();
	// Генерация данных 3
	void gen_data_3();
	// Подсчет цифр в числе для цифровой сортировки
	static int digitsCount(int num);
	// Получает цифру на разряде m в 256-ой системе счисления для цифровой сортировки
	static unsigned char getDigit(int num, int m);
	// Бинарный поиск
	static int binary_search(const int* vec, const int num, int left, int right);
	// Вставить элемент по индексу k
	static void insert_element(int* vec, int k, int left);
	// Слить два массива для сортировки слиянием
	static void merge(int* array, int* outputArray, int left, int mid, int right);
	// Сдвинуть индексы 
	static void move_indicies(const int* numbers, const int x, int& i, int& j);
	// Гарантирует главное свойство кучи
	static void heapify(int* heap, int heapSize, int i);
	// Строит кучу
	static void build_heap(int* heap, int heapSize);

	// Число в 256-ой степени счисления
	union number256
	{
		// Число в 10-ой системе счисления
		int number;
		// Цифры числа в 256-ой степени счисления
		unsigned char bytes[3];
	};
};

