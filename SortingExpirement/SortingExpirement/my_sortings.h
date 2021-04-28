#include <stdint.h>

#pragma once
class my_sortings
{
public:
	// ������ �������� ������
	const size_t size = 4100;
	// ���������� ������� ��� ����������
	const size_t aproxyCount = 25;
	int* data_0;
	int* data_1;
	int* data_2;
	int* data_3;
	// ��������� ��������� �� ������������ ������
	static bool is_sorted(const int* array, size_t arrSize);
	// ���������� ���������
	static void bubblesort(int*& array, int*& outputArray, int left, int right);
	// ���������� ��������� � �������� ��������� 1.
	static void bubblesort_A(int*& array, int*& outputArray, int left, int right);
	// ���������� ��������� ���������
	static void binaryinserts(int*& array, int*& outputArray, int left, int right);
	// ���������� ���������
	static void countingSort(int*& numbers, int*& outputArray, int left, int right);
	// �������� ���������� �� ��������� 256
	static void radixSort(int*& numbers, int*& outputArray, int left, int right);
	// ���������� ��������
	static void mergeSort(int*& array, int*& outputArray, int left, int right);
	// ������� ����������
	static void quicksort(int*& numbers, int*& outputArray, int left, int right);
	// ���������� �����
	static void heapSort(int*& numbers, int*& outputArray, int left, int right);
	// ��������� �����������
	long long execute(void (*sorting_func)(int*&, int*&, int, int), const int* data, size_t data_size);
	// �������� ����������� ��� ������� data_size
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
	// ������ �� ������� ����������
	void (*sorts[8])(int*&, int*&, int, int);
	// ������ �� �������� ������
	int* data[4];
	// ��������� ������ 0
	void gen_data_0();
	// ��������� ������ 1
	void gen_data_1();
	// ��������� ������ 2
	void gen_data_2();
	// ��������� ������ 3
	void gen_data_3();
	// ������� ���� � ����� ��� �������� ����������
	static int digitsCount(int num);
	// �������� ����� �� ������� m � 256-�� ������� ��������� ��� �������� ����������
	static unsigned char getDigit(int num, int m);
	// �������� �����
	static int binary_search(const int* vec, const int num, int left, int right);
	// �������� ������� �� ������� k
	static void insert_element(int* vec, int k, int left);
	// ����� ��� ������� ��� ���������� ��������
	static void merge(int* array, int* outputArray, int left, int mid, int right);
	// �������� ������� 
	static void move_indicies(const int* numbers, const int x, int& i, int& j);
	// ����������� ������� �������� ����
	static void heapify(int* heap, int heapSize, int i);
	// ������ ����
	static void build_heap(int* heap, int heapSize);

	// ����� � 256-�� ������� ���������
	union number256
	{
		// ����� � 10-�� ������� ���������
		int number;
		// ����� ����� � 256-�� ������� ���������
		unsigned char bytes[3];
	};
};

