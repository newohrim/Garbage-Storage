#include "my_sortings.h"
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include <stdexcept>
#include <chrono>

using namespace std;

my_sortings::my_sortings()
{
	sorts[0] = bubblesort;
	sorts[1] = bubblesort_A;
	sorts[2] = binaryinserts;
	sorts[3] = countingSort;
	sorts[4] = radixSort;
	sorts[5] = mergeSort;
	sorts[6] = quicksort;
	sorts[7] = heapSort;
	data_0 = new int[size];
	data_1 = new int[size];
	data_2 = new int[size];
	data_3 = new int[size];
	gen_data_0();
	gen_data_1();
	gen_data_2();
	gen_data_3();
	data[0] = data_0;
	data[1] = data_1;
	data[2] = data_2;
	data[3] = data_3;
}

void my_sortings::gen_data_0()
{
	srand(time(NULL));
	for (size_t i = 0; i < size; ++i)
		data_0[i] = rand() % 6;
}

void my_sortings::gen_data_1()
{
	srand(time(NULL));
	for (size_t i = 0; i < size; ++i)
		data_1[i] = rand() % 4001;
}

void my_sortings::gen_data_2()
{
	srand(time(NULL));
	for (size_t i = 0; i < size; ++i)
		data_2[i] = rand() % 4001;
	int* temp_output = new int[size];
	quicksort(data_2, temp_output, 0, size - 1);
	for (int i = 0; i < 4000; i += 1000)
		for (int j = i; j < 1000; j += 100)
			swap(data_2[j], data_2[j + 50]);
}

void my_sortings::gen_data_3()
{
	srand(time(NULL));
	for (size_t i = 0; i < size; ++i)
		data_3[i] = rand() % 4001;
	int* temp_output = new int[size];
	quicksort(data_3, temp_output, 0, size - 1);
	for (size_t i = 0; i < size / 2; ++i)
		swap(data_3[i], data_3[size - i - 1]);
}

int my_sortings::digitsCount(int num)
{
	num = abs(num);
	int count = 0;
	while (num > 0)
	{
		num /= 256;
		count++;
	}
	return count;
}

unsigned char my_sortings::getDigit(int num, int m)
{
	number256 temp_num;
	temp_num.number = num;
	return temp_num.bytes[m];
}

int my_sortings::binary_search(const int* vec, const int num, int left, int right)
{
	int mid = left + (right - left) / 2;
	if (left >= right)
		return left;
	if (num < vec[mid])
		return binary_search(vec, num, left, mid);
	else
		return binary_search(vec, num, mid + 1, right);
}

void my_sortings::insert_element(int* vec, int k, int left)
{
	int insert_num = vec[k];
	while (k > left)
	{
		vec[k] = vec[k - 1];
		k--;
	}
	vec[left] = insert_num;
}

void my_sortings::binaryinserts(int*& array, int*& outputArray, int left, int right)
{
	for (int i = 1; i <= right; ++i)
	{
		if (array[i] < array[i - 1])
		{
			int insert_num = array[i];
			int l = binary_search(array, insert_num, 0, i - 1);
			int k = i;
			int p = insert_num;
			insert_element(array, i, l);
		}
	}
	delete[] outputArray;
	outputArray = array;
}

void my_sortings::countingSort(int*& numbers, int*& outputArray, int left, int right)
{
	// Минимальное число в массиве
	int min = INT32_MAX;
	// Максимальное число в массиве
	int max = INT32_MIN;
	// Поиск максимального и минимального чисел в массиве
	for (int i = 0; i <= right; ++i)
	{
		if (numbers[i] < min)
			min = numbers[i];
		if (numbers[i] > max)
			max = numbers[i];
	}
	// Размер массива вхождений чисел
	int k = max - min + 1;
	// Массив вхождений чисел
	int* counts = new int[k];
	for (int i = 0; i < k; ++i)
		counts[i] = 0;
	// Подсчет вхождений для каждого числа
	for (int i = 0; i <= right; ++i)
		counts[numbers[i] - min]++;
	// Подсчет сколько чисел небольше данного
	for (int i = 1; i < k; ++i)
		counts[i] += counts[i - 1];
	// Отсортированный массив
	for (int i = right; i >= 0; --i)
	{
		// Ставим число на его место в отсортированном массиве
		outputArray[counts[numbers[i] - min] - 1] = numbers[i];
		// Отнимаем кол-во вхождений для данного числа
		counts[numbers[i] - min]--;
	}
	// Копирование
	//for (int i = 0; i < array_size; ++i)
	//	numbers[i] = sortedArray[i];
	delete[] counts;
	delete[] numbers;
	numbers = outputArray;
}

void my_sortings::radixSort(int*& numbers, int*& outputArray, int left, int right)
{
	// Кол-во разрядов
	int m = 1;
	for (int i = 0; i <= right; ++i)
	{
		int m_temp = digitsCount(numbers[i]);
		if (m_temp > m)
			m = m_temp;
	}
	for (int i = 0; i < m; ++i)
	{
		// Массив из 256 цифр (256 системы счисления)
		int counts[256];
		for (int j = 0; j < 256; ++j)
			counts[j] = 0;
		// Подсчет вхождений цифр
		for (int j = 0; j <= right; ++j)
			counts[getDigit(numbers[j], i)]++;
		// Подсчет чисел больше данного
		int count = 0;
		for (int j = 0; j < 256; ++j)
		{
			int c = counts[j];
			counts[j] = count;
			count += c;
		}
		// Отсортированный массив
		for (int j = 0; j <= right; ++j)
		{
			// i-ый разряд числа
			unsigned char d = getDigit(numbers[j], i);
			// Вставляем в отсортированный по i-ому разряду массив
			outputArray[counts[d]] = numbers[j];
			counts[d]++;
		}
		// Копируем
		for (int j = 0; j <= right; ++j)
			numbers[j] = outputArray[j];
	}
	delete[] numbers;
	numbers = outputArray;
}

void my_sortings::bubblesort_A(int*& array, int*& outputArray, int left, int right)
{
	int i, j, c;
	int k = 0;
	for (i = right + 1; i > 1; i--)
	{
		k = 0;
		for (j = 1; j < i; j++)
			if (array[j] < array[j - 1])
			{
				c = array[j];
				array[j] = array[j - 1];
				array[j - 1] = c;
				k = 1;
			}
		if (k == 0) break;
	};
	delete[] outputArray;
	outputArray = array;
}

bool my_sortings::is_sorted(const int* array, size_t arrSize)
{
	for (size_t i = 1; i < arrSize; ++i)
		if (array[i] < array[i - 1])
			return false;
	return true;
}

void my_sortings::bubblesort(int*& array, int*& outputArray, int left, int right)
{
	for (int i = 0; i < right; ++i)
	{
		for (int j = i + 1; j <= right; ++j)
		{
			if (array[i] > array[j])
			{
				swap(array[i], array[j]);
			}
		}
	}
	delete[] outputArray;
	outputArray = array;
}

void my_sortings::mergeSort(int*& array, int*& outputArray, int left, int right)
{
	if (left >= right)
		return;
	int mid = left + (right - left) / 2;
	mergeSort(array, outputArray, left, mid);
	mergeSort(array, outputArray, mid + 1, right);
	merge(array, outputArray, left, mid, right);
	// delete[] array?
}

void my_sortings::merge(int* array, int* outputArray, int left, int mid, int right)
{
	int n1 = mid - left + 1;
	int n2 = right - mid;
	int* left_part = new int[n1];
	int* right_part = new int[n2];
	for (int i = 0; i < n1; ++i)
		left_part[i] = array[left + i];
	for (int i = 0; i < n2; ++i)
		right_part[i] = array[mid + 1 + i];
	int k1 = 0;
	int k2 = 0;
	int k = left;
	while (k1 < n1 && k2 < n2)
	{
		if (left_part[k1] <= right_part[k2])
			array[k] = left_part[k1++];
		else
			array[k] = right_part[k2++];
		k++;
	}
	while (k1 < n1)
	{
		array[k] = left_part[k1++];
		k++;
	}
	while (k2 < n2)
	{
		array[k] = right_part[k2++];
		k++;
	}
	delete[] left_part;
	delete[] right_part;
}

// Функция, сдвигающая индексы для quicksort
void my_sortings::move_indicies(const int* numbers, const int x, int& i, int& j)
{
	while (numbers[i] < x)
		i++;
	while (numbers[j] > x)
		j--;
}

void my_sortings::heapify(int* heap, int heapSize, int i)
{
	// Индексы потомков
	int left = 0, right = 0;
	if (i > 0)
	{
		left = i * 2 + 1;
		right = i * 2 + 2;
	}
	else
	{
		left = 1;
		right = 2;
	}
	// Наибольший среди тройки
	int largest = i;
	if (left < heapSize && heap[left] > heap[i])
		largest = left;
	if (right < heapSize && heap[right] > heap[largest])
		largest = right;
	if (largest != i)
	{
		// Меняем местами, если не выполняется свойство
		std::swap(heap[i], heap[largest]);
		if (largest > heapSize / 2 - 1)
			return;
		heapify(heap, heapSize, largest);
	}
}

void my_sortings::build_heap(int* heap, int heapSize)
{
	for (int i = heapSize / 2 - 1; i >= 0; --i)
	{
		heapify(heap, heapSize, i);
	}
}

// Функция быстрой сортировки
void my_sortings::quicksort(int*& numbers, int*& outputArray, int left, int right)
{
	int i = left, j = right;
	int mid = i + (j - i) / 2;
	// Опорный элемент
	int x = numbers[mid];
	while (i <= j)
	{
		// Сдвигаем индексы
		move_indicies(numbers, x, i, j);
		if (i > j)
			break;
		// Меняем элементы местами
		swap(numbers[i], numbers[j]);
		i++;
		j--;
	}
	if (left < j)
	{
		quicksort(numbers, outputArray, left, j);
	}
	if (right > i)
	{
		quicksort(numbers, outputArray, i, right);
	}
}

void my_sortings::heapSort(int*& numbers, int*& outputArray, int left, int right)
{
	int array_size = right + 1;
	build_heap(numbers, array_size);
	int heapSize = array_size;
	while (array_size > 2)
	{
		// Меняем местами корневой элемент (наибольший в куче) с последним
		swap(numbers[0], numbers[array_size - 1]);
		// Уменьшаем размер кучи на 1
		array_size--;
		// Восстанавливаем свойство кучи
		heapify(numbers, array_size, 0);
	}
	// Меняем местами первый и второй элементы
	if (heapSize > 1 && numbers[0] > numbers[1])
		swap(numbers[0], numbers[1]);
	delete[] outputArray;
	outputArray = numbers;
}

long long my_sortings::execute(void(*sorting_func)(int*&, int*&, int, int), const int* data, size_t data_size)
{
	if (data_size > size)
		throw runtime_error("Data size was more then max size.");
	
	long long mcsecs = -1;
	for (int i = 0; i < 3; ++i) 
	{
		if (i < 2) 
		{
			int* sized_data = new int[data_size];
			int* output_data = new int[data_size];
			for (size_t i = 0; i < data_size; ++i)
				sized_data[i] = data[i];
			sorting_func(sized_data, output_data, 0, data_size - 1);
			if (!is_sorted(output_data, data_size))
				throw runtime_error("Array wasn't sorted after execution.");
			if (sorting_func == mergeSort || sorting_func == quicksort)
				delete[] output_data;
			delete[] sized_data;
			continue;
		}
		int* sized_data = new int[data_size];
		int* output_data = new int[data_size];
		for (size_t i = 0; i < data_size; ++i)
			sized_data[i] = data[i];
		auto start = std::chrono::high_resolution_clock::now();
		sorting_func(sized_data, output_data, 0, data_size - 1);
		auto elapsed = std::chrono::high_resolution_clock::now() - start;
		mcsecs = std::chrono::duration_cast<std::chrono::microseconds>(elapsed).count();
		if (!is_sorted(output_data, data_size))\
			throw runtime_error("Array wasn't sorted after execution.");
		if (sorting_func == mergeSort || sorting_func == quicksort)
			delete[] output_data;
		delete[] sized_data;
	}
	return mcsecs;
}

int64_t* my_sortings::proceed_sized(size_t data_size)
{
	int k = 0;
	int64_t* times = new int64_t[32];
	for (int i = 0; i < 8; ++i) 
	{
		for (int j = 0; j < 4; ++j) 
		{
			uint64_t sum = 0;
			for (size_t a = 0; a < aproxyCount; ++a) 
			{
				//cout << i << " " << j << " " << a << sum << endl;
				sum += execute(sorts[i], data[j], data_size);
			}
			times[k++] = sum / aproxyCount;
		}
	}
	return times;
}
