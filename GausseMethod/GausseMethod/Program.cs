using System;

namespace GausseMethod
{
    class Program
    {
        // Входная точка программы
        static void Main(string[] args)
        {
            do
            {
                Matrix matrix = InputMatrix();
                matrix.Display();
                Console.ReadLine();
                matrix.GaussMethod();
                Console.WriteLine("Матрица, приведенная у каноническому виду:");
                matrix.Display();
                matrix.SolveGauss();
            } while (Console.ReadKey(true).Key != ConsoleKey.Escape);
        }
        // Метод для ввода матрицы
        static Matrix InputMatrix() 
        {
            int i, j;
            Console.Write("Введите количество строк: ");
            if (!int.TryParse(Console.ReadLine(), out i) || i < 2) 
            {
                Console.WriteLine("Неверный ввод. " +
                    "Количество строк это целое положительное число больше 2.");
                return InputMatrix();
            }
            Console.Write("Введите количество столбцов: ");
            if (!int.TryParse(Console.ReadLine(), out j) || j < 2)
            {
                Console.WriteLine("Неверный ввод. " +
                    "Количество столбцов это целое положительное число больше 2.");
                return InputMatrix();
            }

            Matrix matrix = new Matrix(i, j);
            Console.WriteLine("Введите элементы строк через проблел.");
            for (int y = 0; y < i; y++) 
            {
                string[] inp = Console.ReadLine().Trim().Split(' ');
                if (inp.Length != j) 
                {
                    Console.WriteLine("Количество элементов в строке не соответствует введенному раннее числу.");
                    return InputMatrix();
                }
                try
                {
                    for (int x = 0; x < j; x++)
                    {
                        matrix[y, x] = double.Parse(inp[x]);
                    }
                }
                catch (Exception e) 
                {
                    Console.WriteLine("Формат ввода был неверный. Используйте запятую для разделения целой и дробной части числа или " + e.Message);
                    return InputMatrix();
                }
            }
            return matrix;
        }
    }
    // Класс, характеризующий матрицу
    class Matrix 
    {
        // Двухмерный массив для хранения матрицы
        double[,] data;
        public double this [int i, int j] 
        {
            get { return data[i, j]; }
            set { data[i, j] = value; }
        }
        // Свойство, которое считает ранг матрицы
        public int Rank
        {
            get
            {
                int count = 0;
                for (int i = 0; i < data.GetLength(0); i++) 
                {
                    bool flag = false;
                    for (int j = 0; j < data.GetLength(1); j++) 
                    {
                        if (data[i, j] != 0)
                            flag = true;
                    }
                    if (flag) count++;
                }
                return count;
            }
        }
        // Свойство, которое считает количество переменных
        public int VariablesCount 
        {
            get 
            {
                return data.GetLength(1) - 1;
            }
        }
        public Matrix(int i, int j) 
        {
            data = new double[i, j];
        }
        // Матод Гаусса для приведения матрицы к каноническому виду
        public void GaussMethod() 
        {
            try
            {
                for (int i = 0; i < data.GetLength(0); i++)
                {
                    // Индексы главного элемента
                    int mainElementX = 0, mainElementY = 0;
                    bool isFound = false;
                    for (int x = 0; x < data.GetLength(1); x++)
                    {
                        for (int y = i; y < data.GetLength(0); y++)
                        {
                            if (data[y, x] != 0)
                            {
                                mainElementX = x;
                                mainElementY = y;
                                isFound = true;
                                break;
                            }
                        }
                        if (isFound) break;
                    }

                    double MainElement = data[mainElementY, mainElementX];
                    for (int j = 0; j < data.GetLength(1); j++)
                    {
                        data[i, j] = data[i, j] / MainElement;
                    }

                    // Если главный элемент ниже рассматриваемой строки, то они меняются местами в матрице
                    if (mainElementY > i) SwapLines(i, mainElementY);
                    mainElementY = i;

                    for (int y = 0; y < data.GetLength(0); y++)
                    {
                        if (y == mainElementY)
                            continue;
                        double tempValue = data[y, mainElementX];
                        for (int x = 0; x < data.GetLength(1); x++)
                        {
                            data[y, x] += data[mainElementY, x] * -tempValue;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.Source);
                Console.WriteLine(e.StackTrace);
            }
        }
        // Метод решающий систему, если это возможно
        public void SolveGauss() 
        {
            if (isZeroMatrix()) 
            {
                Console.WriteLine("Ввдена нулевая матрица, которая не образует СЛАУ.");
                return;
            }
            int rank = Rank;
            Console.WriteLine("Решение системы: ");
            if (rank == VariablesCount)
                DisplayParticularSolution();
            if (rank < VariablesCount) 
            {
                Console.WriteLine("Частное решение: ");
                DisplayParticularSolution();
                Console.WriteLine("Набор ФСР: ");
                DisplayFSS(rank);
            }
            if (rank > VariablesCount)
                Console.WriteLine("Система несовместна.");
        }
        /// <summary>
        ///     Метод для вывода частного решения (решения системы, когда ранг равен количеству переменных)
        /// </summary>
        private void DisplayParticularSolution() 
        {
            Console.Write("(");
            for (int i = 0; i < data.GetLength(0); i++)
            {
                Console.Write("{0:0.000}, ", data[i, data.GetLength(1) - 1]);
            }
            Console.WriteLine(")");
        }
        /// <summary>
        ///     Метод для вывода ФСР
        /// </summary>
        /// <param name="rank">Ранг матрицы</param>
        private void DisplayFSS(int rank) 
        {
            Console.WriteLine("Так как нашлись свободные переменные, решение будет выражаться через них.");
        }
        // Метод, который меняет строки местами
        public void SwapLines(int i1, int i2) 
        {
            for (int j = 0; j < data.GetLength(1); j++) 
            {
                double temp = data[i1, j];
                data[i1, j] = data[i2, j];
                data[i2, j] = temp;
            }
        }
        // Метод, проверяющий нулевая ли матрица
        public bool isZeroMatrix() 
        {
            bool flag = true;
            for (int i = 0; i < data.GetLength(0); i++) 
            {
                for (int j = 0; j < data.GetLength(1); j++)
                {
                    if (data[i, j] != 0) flag = false;
                }
            }
            return flag;
        }
        // Метод выводящий матрицу на экран
        public void Display() 
        {
            for (int i = 0; i < data.GetLength(0); i++) 
            {
                Console.Write("(");
                for (int j = 0; j < data.GetLength(1); j++)
                {
                    Console.Write("{0:0.000}, ", data[i, j]);
                }
                Console.Write(")\n");
            }
            Console.Write(Environment.NewLine);
        }
    }
}
