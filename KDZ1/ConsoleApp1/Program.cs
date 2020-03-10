using System;

namespace ConsoleApp1
{
    class Program
    {
        // Начальное число очков
        const int StartScore = 10;
        // Максимальное количество бойцов в команде
        const int FightersCount = 10;
        // Команда игрока
        private static SucideSquad PlayerSquad = new SucideSquad(FightersCount, true);
        // Команда противника
        private static SucideSquad EnemySquad = new SucideSquad(FightersCount, false);
        // Экземпляр класса Random для дальнейшего использования
        private static Random rnd = new Random();

        // Входная точка программы
        static void Main(string[] args)
        {
            // Цикл повтора решений
            do
            {
                Console.WriteLine("Введите rnd, чтобы случайным образом составить команду. " +
                    "Введите cst чтобы самостоятельно составить команду.\nВведите help чтобы прочитать правила.");
                InputCommand();
                PlayerSquad.Display();
                EnemySquad.GetRandomSquad();
                EnemySquad.Display();
                Battle();

                // Очистка команд
                ClearSquad(ref PlayerSquad);
                ClearSquad(ref EnemySquad);
            } while (Console.ReadKey(true).Key != ConsoleKey.Escape);
        }

        /// <summary>
        ///     Процесс битвы
        /// </summary>
        private static void Battle()
        {
            Console.WriteLine("Нажмите на Enter чтобы начать первый ход.");
            Console.ReadKey();

            try
            {
                // Ход
                int turn = 0;
                // Определение первого хода
                int whoFirst = rnd.Next(0, 2);
                // Массив из двух команд
                SucideSquad[] squads = new SucideSquad[] { PlayerSquad, EnemySquad };

                // Индексы для поочередного перебора
                int k1 = 0, k2 = 0;
                // Временная переменная
                int l = 0;
                // Цикл до окончания игры
                while (PlayerSquad.LastAlive() != 0 && EnemySquad.LastAlive() != 0)
                {
                    // Очистка консоли
                    Console.Clear();
                    if (l == 0)
                    {
                        l = 1;
                        Console.WriteLine(whoFirst == 0 ? "Вы ходите первым!" : "Противник ходит первым!");
                    }
                    turn++;
                    Console.WriteLine("Ход " + turn);

                    // Первая команда атакует
                    while (squads[whoFirst][k1] == null || !squads[whoFirst][k1].isAlive)
                    {
                        k1++;
                        k1 = k1 % squads[whoFirst].FindLastNull();
                    }
                    int targetIndex = 0;
                    FirstAttack(squads, whoFirst, k1, ref targetIndex);
                    // Исключение когда последний боец в противоположной команде умирает
                    if (PlayerSquad.LastAlive() == 0 || EnemySquad.LastAlive() == 0)
                    {
                        Console.Write(Environment.NewLine);
                        PlayerSquad.Display();
                        EnemySquad.Display();
                        Console.ReadKey();
                        break;
                    }
                    // Вторая команда атакует
                    while (squads[whoFirst == 0 ? 1 : 0][k2] == null || !squads[whoFirst == 0 ? 1 : 0][k2].isAlive)
                    {
                        k2++;
                        k2 = k2 % squads[whoFirst == 0 ? 1 : 0].FindLastNull();
                    }
                    SecondAttack(squads, whoFirst, k2, ref targetIndex);

                    // Окончание боя и вывод команд на экран
                    Console.Write(Environment.NewLine);
                    PlayerSquad.Display();
                    EnemySquad.Display();
                    k1++;
                    k1 = k1 % squads[whoFirst].FindLastNull();
                    k2++;
                    k2 = k2 % squads[whoFirst == 0 ? 1 : 0].FindLastNull();
                    Console.ReadKey();
                }

                // Проверки на исход битвы
                if (PlayerSquad.LastAlive() == 0 && EnemySquad.LastAlive() != 0)
                {
                    Console.Write("Вы ");
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.Write("проиграли");
                    Console.ResetColor();
                    Console.WriteLine("! Ваша команда полностью уничтожена!");
                    return;
                }
                if (EnemySquad.LastAlive() == 0 && PlayerSquad.LastAlive() != 0)
                {
                    Console.Write("Вы ");
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.Write("победили");
                    Console.ResetColor();
                    Console.WriteLine("! Команда противника полностью уничтожена!");
                    return;
                }
                else
                {
                    Console.WriteLine("Ничья! Каким-то чудесным образом они убили друг друга! \nВероятно последний самурай совершил харакири. Отличный получился бы фильм!");
                }
            }
            catch (Exception e) // Отлавливание ошибок
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.Source);
                Console.WriteLine(e.StackTrace);
            }
        }

        /// <summary>
        ///     Функция атаки первой команды. Существует для зацикливания.
        /// </summary>
        /// <param name="squads">Массив с двумя командами</param>
        /// <param name="whoFirst">Та самая переменная первичности хода</param>
        /// <param name="i">Текущий индекс для первой комнады</param>
        /// <param name="targetIndex">Индекс цели</param>
        private static void FirstAttack(SucideSquad[] squads, int whoFirst, int i, ref int targetIndex)
        {
            targetIndex = rnd.Next(0, squads[whoFirst == 0 ? 1 : 0].FindLastNull());
            if (PlayerSquad.LastAlive() != 0 && EnemySquad.LastAlive() != 0)
            {
                if (!squads[whoFirst == 0 ? 1 : 0][targetIndex].isAlive) FirstAttack(squads, whoFirst, i, ref targetIndex);
                else squads[whoFirst][i].Attack(squads[whoFirst == 0 ? 1 : 0][targetIndex]);
            }
        }

        /// <summary>
        ///     Функция атаки второй команды. Существует для зацикливания.
        /// </summary>
        /// <param name="squads">Массив с двумя командами</param>
        /// <param name="whoFirst">Та самая переменная первичности хода</param>
        /// <param name="i">Текущий индекс для второй комнады</param>
        /// <param name="targetIndex">Индекс цели</param>
        private static void SecondAttack(SucideSquad[] squads, int whoFirst, int i, ref int targetIndex)
        {
            targetIndex = rnd.Next(0, squads[whoFirst].FindLastNull());
            if (PlayerSquad.LastAlive() != 0 && EnemySquad.LastAlive() != 0)
            {
                if (!squads[whoFirst][targetIndex].isAlive) SecondAttack(squads, whoFirst, i, ref targetIndex);
                else squads[whoFirst == 0 ? 1 : 0][i].Attack(squads[whoFirst][targetIndex]);
            }
        }

        /// <summary>
        ///     Функция очистки команды
        /// </summary>
        /// <param name="squad"></param>
        public static void ClearSquad(ref SucideSquad squad)
        {
            try
            {
                squad = new SucideSquad(FightersCount, squad.isPlayerSquad);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        /// <summary>
        ///     Функция для ввода команд
        /// </summary>
        private static void InputCommand()
        {
            string input = Console.ReadLine().Trim().ToLower();

            try
            {
                switch (input)
                {
                    case "rnd":
                        PlayerSquad.GetRandomSquad();
                        break;
                    case "cst":
                        PlayerSquad.GetCustomSquad(StartScore);
                        break;
                    case "help":
                        Console.WriteLine("Так как в Assigments.pdf не полностью описаны правила игры, я поделюсь своей интерпритацией." +
                            "\nИгрок выбирает между случайной командой из 10 бойцов и самостоятельным выбором бойцов." +
                            "\nЗатем каждый ход бойцы по очереди бьют друг друга. Весь ход битвы выводится на экран." +
                            "\nПричем важно, что за ход в схватке учавствуют только 2 бойца из разных команд." +
                            "\nТаким образом исключается вероятность ничьи. Учитывайте, что количество ходов может достигать 120, а значит необязательно смотреть все ходы. " +
                            "\nМожно зажать кнопку Enter и пролистать до конца. Приятной игры!");
                        InputCommand();
                        break;
                    default:
                        Console.WriteLine("Команда введена неверно. Введите еще раз.");
                        InputCommand();
                        break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }

        /// <summary>
        ///     Функция для ввода целого числа. Встречается в конце.
        /// </summary>
        /// <param name="input">Строка из ввода</param>
        /// <param name="N">Вывод в целочисленную переменную</param>
        /// <returns></returns>
        public static bool CheckInput(string input, out int N)
        {
            if (int.TryParse(input, out N)) return true;
            else return false;
        }

        /// <summary>
        ///     Функция для нахождения шанса в долях
        /// </summary>
        /// <returns></returns>
        public static double RandomChance()
        {
            return rnd.Next(1, 101) / 100.0f;
        }
    }

    /// <summary>
    ///     Класс характеризующий человека
    /// </summary>
    class Human
    {
        // Ссылка на производный класс 
        public Fighter fighter;
        // Переменная обозначающая жив ли человек
        public bool isAlive = true;
        // Начальное здоровье. Нужно для цвета текста в выводе.
        public double startHealthy;
        // Переменная обозначающая отравлен ли человек
        protected bool poisoned;
        // Здоровье человека
        protected double healthy;

        // Свойство для poisoned
        public bool Poisoned
        {
            get
            {
                return poisoned;
            }
            set
            {
                poisoned = value;
            }
        }

        // Свойство для healthy
        public double Healthy
        {
            get
            {
                return healthy;
            }
            set
            {
                if (value > 0)
                {
                    if (healthy == 0) startHealthy = value;
                    healthy = value;
                }
                else
                {
                    healthy = 0;
                    isAlive = false;
                }
            }
        }
    }

    /// <summary>
    ///     Класс характеризующий бойца
    /// </summary>
    class Fighter : Human
    {
        // Ссылка на производные классы
        public Fighter postFighter;
        // Цена наема в очках
        public const float cost = 1.0f;
        // Индекс в команде
        public int indexInSquad;
        // Переменная характеризующая в какой комнде игрок
        protected bool isInPlayerSquad;
        // Экземпляр класса Random для дальнейшего использования
        public static Random rnd = new Random();

        // Урон
        protected int damage;
        // Защита
        protected int guard;
        // Уклонение
        protected double evade;

        // Свойство для урона
        public int Damage
        {
            get
            {
                return damage;
            }
            set
            {
                if (value > 0) damage = value;
            }
        }

        // Свойство для защиты
        public int Guard
        {
            get
            {
                return guard;
            }
            set
            {
                if (value > 0) guard = value;
                else guard = 0;
            }
        }

        // Свойство для улонения
        public double Evade
        {
            get
            {
                return evade;
            }
            set
            {
                if (value > 0) evade = value;
            }
        }

        /// <summary>
        ///     Функция атаки
        /// </summary>
        /// <param name="enemy">Цель</param>
        public virtual void Attack(Human enemy)
        {
            // Проверка жива ли цель
            if (enemy.isAlive)
            {
                // Проверка на отравление персонажа
                if (Poisoned)
                {
                    double poisonDamage = 0.07f * Healthy >= 5 ? 0.07f * Healthy : 5;
                    Healthy -= poisonDamage;
                    Console.WriteLine("Fighter получает урон от отравления: {0:0.0}", poisonDamage);
                    if (!isAlive) return;
                }
                // Проверка на уклонение противника
                double evadeChance = Program.RandomChance();
                if (evadeChance > enemy.fighter.Evade)
                {
                    enemy.Healthy -= Damage - enemy.fighter.Guard;
                    enemy.fighter.Guard--;
                    Console.Write("«Из команды {0} ({4}){1} атаковал ({5}){2} и нанес {3} урона» ",
                        (isInPlayerSquad ? "Игрока" : "Противника"),
                        "Fighter", enemy.GetType().Name, Damage - enemy.fighter.Guard, indexInSquad, enemy.fighter.indexInSquad);
                    BattleCry();
                }
                else
                {
                    Console.WriteLine("«Из команды {0} ({3}){1} увернулся от удара ({4}){2}» ",
                        (isInPlayerSquad ? "Противника" : "Игрока"),
                        enemy.GetType().Name, "Fighter", enemy.fighter.indexInSquad, indexInSquad);
                }
                // Проверка на ответный удар от самурая
                if (enemy.GetType() == typeof(Samurai) && enemy.isAlive)
                {
                    double counterAttackChance = Program.RandomChance();
                    Samurai samurai = (Samurai)enemy.fighter.postFighter;
                    if (counterAttackChance > samurai.Retaliation)
                    {
                        enemy.fighter.Attack(this);
                    }
                }
            }
            else throw new ArgumentException("Ошибка. Попытка атаковать мертвого Human.");
        }

        /// <summary>
        ///     Конструктор класса Fighter
        /// </summary>
        /// <param name="healthy">Здоровье</param>
        /// <param name="damage">Урон</param>
        /// <param name="guard">Защита</param>
        /// <param name="evade">Шанс уклонения</param>
        /// <param name="IsPlayerSquad">В комнде игрока?</param>
        /// <param name="indexInSquad">Номер в комнде</param>
        public Fighter(double healthy, int damage, int guard, double evade, bool IsPlayerSquad, int indexInSquad)
        {
            Healthy = healthy;
            Damage = damage;
            Guard = guard;
            Evade = evade;
            fighter = this;
            isInPlayerSquad = IsPlayerSquad;
            this.indexInSquad = indexInSquad;
        }

        /// <summary>
        ///     Функция формирования своей команды
        /// </summary>
        /// <param name="score">Оставшиеся очки</param>
        /// <param name="squad">Комнда</param>
        public static void AskForCount(ref float score, SucideSquad squad)
        {
            int Count;
            Console.Write("Введите количество файтеров: ");
            if (Program.CheckInput(Console.ReadLine(), out Count) && Count >= 0)
            {
                if (score - (Count * cost) >= 0 && squad.Length - squad.FindLastNull() - 1 >= 0)
                {
                    squad.AddFighters(SucideSquad.FighterType.Fighter, Count);
                    score -= Count * cost;
                }
                else if (squad.Length - squad.FindLastNull() - 1< 0)
                {
                    Console.WriteLine("В команде нету столько места.");
                    AskForCount(ref score, squad);
                }
                else if (score - (Count * cost) < 0)
                {
                    Console.WriteLine("Не хватает очков. Осталось: " + score);
                    AskForCount(ref score, squad);
                }
            }
            else
            {
                Console.WriteLine("Число должно быть целым неотритцательным.");
                AskForCount(ref score, squad);
            }
        }

        /// <summary>
        ///     Функция генерации нового солдата
        /// </summary>
        /// <param name="IsPlayerSquad">В команде игрока?</param>
        /// <param name="indexInSquad">Номер в команде</param>
        /// <returns></returns>
        public static Fighter GenerateNewFighter(bool IsPlayerSquad, int indexInSquad)
        {
            return new Fighter(rnd.Next(50, 71), rnd.Next(5, 11), rnd.Next(3, 7), 0, IsPlayerSquad, indexInSquad);
        }

        /// <summary>
        ///     Функция боевого оскала
        /// </summary>
        public virtual void BattleCry()
        {
            Console.WriteLine("Хыа");
        }
    }

    class Ninja : Fighter
    {
        // Цена наема в очках
        public new const float cost = 1.5f;
        // Шанс отравления
        protected double poison;
        //public const float cost = 1.5f;

        // Свойство отравления
        public double Poison
        {
            get
            {
                return poison;
            }
            set
            {
                if (value >= 0) poison = value;
            }
        }

        /// <summary>
        ///     Функция атаки
        /// </summary>
        /// <param name="enemy">Цель</param>
        public override void Attack(Human enemy)
        {
            if (enemy.isAlive)
            {
                // Проверка на отравление персонажа
                if (Poisoned)
                {
                    double poisonDamage = 0.07f * Healthy >= 5 ? 0.07f * Healthy : 5;
                    Healthy -= poisonDamage;
                    Console.WriteLine("Ninja получает урон от отравления: {0:0.0}", poisonDamage);
                    if (!isAlive) return;
                }
                // Проверка на уклонение противника
                double evadeChance = Program.RandomChance();
                if (evadeChance > enemy.fighter.Evade)
                {
                    enemy.Healthy -= Damage - enemy.fighter.Guard;
                    Console.Write("«Из команды {0} ({4}){1} атаковал ({5}){2} и нанес {3} урона» ",
                        (isInPlayerSquad ? "Игрока" : "Противника"),
                        "Ninja", enemy.GetType().Name, Damage - enemy.fighter.Guard, indexInSquad, enemy.fighter.indexInSquad);
                    BattleCry();
                    // Проверка на отравление противника
                    double poisonChance = Program.RandomChance();
                    if (poisonChance > Poison && !enemy.Poisoned)
                    {
                        enemy.Poisoned = true;
                        Console.WriteLine(enemy.GetType().Name + " был отравлен.");
                    }
                }
                else
                {
                    Console.WriteLine("«Из команды {0} ({3}){1} увернулся от удара ({4}){2}» ",
                        (isInPlayerSquad ? "Противника" : "Игрока"),
                        enemy.GetType().Name, "Ninja", enemy.fighter.indexInSquad, indexInSquad);
                }
                // Проверка на ответный удар от самурая
                if (enemy.GetType() == typeof(Samurai) && enemy.isAlive)
                {
                    double counterAttackChance = Program.RandomChance();
                    Samurai samurai = (Samurai)enemy.fighter.postFighter;
                    if (counterAttackChance > samurai.Retaliation)
                    {
                        enemy.fighter.Attack(this);
                    }
                }
            }
            else throw new ArgumentException("Ошибка. Попытка атаковать мертвого Human.");
        }

        /// <summary>
        ///     Конструктор класса Fighter
        /// </summary>
        /// <param name="healthy">Здоровье</param>
        /// <param name="damage">Урон</param>
        /// <param name="guard">Защита</param>
        /// <param name="evade">Шанс уклонения</param>
        /// <param name="IsPlayerSquad">В комнде игрока?</param>
        /// <param name="indexInSquad">Номер в комнде</param>
        public Ninja(double healthy, int damage, int guard, double evade, double poison, bool IsPlayerSquad, int indexInSquad) : base(healthy, damage, guard, evade, IsPlayerSquad, indexInSquad)
        {
            Healthy = healthy;
            Damage = damage;
            Guard = guard;
            Evade = evade;
            Poison = poison;
            postFighter = this;
            this.indexInSquad = indexInSquad;
        }

        /// <summary>
        ///     Функция формирования своей команды
        /// </summary>
        /// <param name="score">Оставшиеся очки</param>
        /// <param name="squad">Комнда</param>
        public new static void AskForCount(ref float score, SucideSquad squad)
        {
            int Count;
            Console.Write("Введите количество нинздя: ");
            if (Program.CheckInput(Console.ReadLine(), out Count) && Count >= 0)
            {
                if (score - (Count * cost) >= 0 && squad.Length - squad.FindLastNull() - 1 >= 0)
                {
                    squad.AddFighters(SucideSquad.FighterType.Ninja, Count);
                    score -= Count * cost;
                }
                else if (squad.Length - squad.FindLastNull() - 1 < 0)
                {
                    Console.WriteLine("В команде нету столько места.");
                    AskForCount(ref score, squad);
                }
                else if (score - (Count * cost) < 0)
                {
                    Console.WriteLine("Не хватает очков. Осталось: " + score);
                    AskForCount(ref score, squad);
                }
            }
            else
            {
                Console.WriteLine("Число должно быть целым неотритцательным.");
                AskForCount(ref score, squad);
            }
        }

        /// <summary>
        ///     Функция генерации нового солдата
        /// </summary>
        /// <param name="IsPlayerSquad">В команде игрока?</param>
        /// <param name="indexInSquad">Номер в команде</param>
        /// <returns></returns>
        public new static Ninja GenerateNewFighter(bool IsPlayerSquad, int indexInSquad)
        {
            return new Ninja(rnd.Next(50, 71), rnd.Next(5, 11), rnd.Next(3, 7), rnd.Next(40, 61) / 100.0f, rnd.Next(30, 61) / 100.0f, IsPlayerSquad, indexInSquad);
        }

        /// <summary>
        ///     Функция боевого оскала
        /// </summary>
        public override void BattleCry()
        {
            Console.WriteLine("Кия");
        }
    }

    /// <summary>
    ///     Класс характеризующий самурая
    /// </summary>
    class Samurai : Fighter
    {
        // Цена наема в очках
        public new const float cost = 2.0f;
        // Щанс контратаки
        protected double retaliation;

        // Свойство уклонения
        public double Retaliation
        {
            get
            {
                return retaliation;
            }
            set
            {
                if (value >= 0) retaliation = value;
            }
        }

        /// <summary>
        ///     Функция атаки
        /// </summary>
        /// <param name="enemy">Цель</param>
        public override void Attack(Human enemy)
        {
            if (enemy.isAlive)
            {
                // Проверка на отравление персонажа
                if (Poisoned)
                {
                    double poisonDamage = 0.07f * Healthy >= 5 ? 0.07f * Healthy : 5;
                    Healthy -= poisonDamage;
                    Console.WriteLine("Samurai получает урон от отравления: {0:0.0}", poisonDamage);
                    if (!isAlive) return;
                }
                // Проверка на уклонение противника
                double evadeChance = Program.RandomChance();
                if (evadeChance > enemy.fighter.Evade)
                {
                    enemy.Healthy -= Damage - enemy.fighter.Guard;
                    Console.Write("«Из команды {0} ({4}){1} атаковал ({5}){2} и нанес {3} урона» ",
                        (isInPlayerSquad ? "Игрока" : "Противника"),
                        "Samurai", enemy.GetType().Name, Damage - enemy.fighter.Guard, indexInSquad, enemy.fighter.indexInSquad);
                    BattleCry();
                }
                else
                {
                    Console.WriteLine("«Из команды {0} ({3}){1} увернулся от удара ({4}){2}» ",
                        isInPlayerSquad ? "Противника" : "Игрока",
                        enemy.GetType().Name, "Samurai", enemy.fighter.indexInSquad, indexInSquad);
                }
                // Проверка на ответный удар от самурая
                if (enemy.GetType() == typeof(Samurai) && enemy.isAlive)
                {
                    double counterAttackChance = Program.RandomChance();
                    Samurai samurai = (Samurai)enemy.fighter.postFighter;
                    if (counterAttackChance > samurai.Retaliation)
                    {
                        enemy.fighter.Attack(this);
                    }
                }
            }
            else throw new ArgumentException("Ошибка. Попытка атаковать мертвого Human.");
        }

        /// <summary>
        ///     Конструктор класса Fighter
        /// </summary>
        /// <param name="healthy">Здоровье</param>
        /// <param name="damage">Урон</param>
        /// <param name="guard">Защита</param>
        /// <param name="evade">Шанс уклонения</param>
        /// <param name="IsPlayerSquad">В комнде игрока?</param>
        /// <param name="indexInSquad">Номер в комнде</param>
        public Samurai(double healthy, int damage, int guard, double evade, double retaliation, bool IsPlayerSquad, int indexInSquad) : base(healthy, damage, guard, evade, IsPlayerSquad, indexInSquad)
        {
            // Переделать в свойства
            Healthy = healthy;
            Damage = damage;
            Guard = guard;
            Evade = evade;
            Retaliation = retaliation;
            postFighter = this;
            this.indexInSquad = indexInSquad;
        }

        /// <summary>
        ///     Функция формирования своей команды
        /// </summary>
        /// <param name="score">Оставшиеся очки</param>
        /// <param name="squad">Комнда</param>
        public new static void AskForCount(ref float score, SucideSquad squad)
        {
            int Count;
            Console.Write("Введите количество самураев: ");
            if (Program.CheckInput(Console.ReadLine(), out Count) && Count >= 0)
            {
                if (score - (Count * cost) >= 0 && squad.Length - squad.FindLastNull() - 1 >= 0)
                {
                    squad.AddFighters(SucideSquad.FighterType.Samurai, Count);
                    score -= Count * cost;
                }
                else if (squad.Length - squad.FindLastNull() - 1 < 0)
                {
                    Console.WriteLine("В команде нету столько места.");
                    AskForCount(ref score, squad);
                }
                else if (score - (Count * cost) < 0)
                {
                    Console.WriteLine("Не хватает очков. Осталось: " + score);
                    AskForCount(ref score, squad);
                }
            }
            else
            {
                Console.WriteLine("Число должно быть целым неотритцательным.");
                AskForCount(ref score, squad);
            }
        }

        /// <summary>
        ///     Функция генерации нового солдата
        /// </summary>
        /// <param name="IsPlayerSquad">В команде игрока?</param>
        /// <param name="indexInSquad">Номер в команде</param>
        /// <returns></returns>
        public new static Samurai GenerateNewFighter(bool IsPlayerSquad, int indexInSquad)
        {
            return new Samurai(rnd.Next(70, 86), rnd.Next(7, 13), rnd.Next(4, 7), rnd.Next(30, 51) / 100.0f, rnd.Next(30, 51) / 100.0f, IsPlayerSquad, indexInSquad);
        }

        /// <summary>
        ///     Функция боевого оскала
        /// </summary>
        public override void BattleCry()
        {
            Console.WriteLine("Чхуа");
        }
    }

    /// <summary>
    ///     Класс характеризующий команду/отряд
    /// </summary>
    class SucideSquad
    {
        // Команда игрока?
        public bool isPlayerSquad;
        // Количество слотов
        protected int length;
        // Массив для отряда
        Fighter[] squad;

        // Индексатор для отряда
        public Fighter this[int i]
        {
            get
            {
                return squad[i];
            }
            set
            {
                if (squad[squad.Length - 1] == null)
                    squad[i] = value;
                else
                    throw new ArgumentException("Ошибка. Команда полная.");
            }
        }

        // Конструктор для отряда
        public SucideSquad(int N, bool isPlayerSquad)
        {
            if (N > 0) squad = new Fighter[N];
            Length = N;
            this.isPlayerSquad = isPlayerSquad;
        }

        // Свойство для количества слотов
        public int Length
        {
            get
            {
                return length;
            }
            set
            {
                if (value > 0) length = value;
            }
        }

        /// <summary>
        ///     Функция для вывода отряда на экран
        /// </summary>
        public void Display()
        {
            int lastNull = FindLastNull();
            if (isPlayerSquad) Console.WriteLine("Ваш отряд({0}): ", lastNull);
            else Console.WriteLine("Отряд противника({0}): ", lastNull);
            for (int i = 0; i < lastNull; i++)
            {
                if (this[i] != null) {
                    Console.Write("(" + this[i].indexInSquad + ") " + this[i].GetType().Name + " [");
                    if (this[i].Healthy < this[i].startHealthy) Console.ForegroundColor = ConsoleColor.Yellow;
                    if (!this[i].isAlive) Console.ForegroundColor = ConsoleColor.Red;
                    Console.Write("{0:0.0}" + " hp", this[i].Healthy);
                    Console.ResetColor();
                    Console.WriteLine(", {0} damage, {1} guard]", this[i].Damage, this[i].Guard);
                }
            }
        }

        /// <summary>
        ///     Функция для рандомизации отряда
        /// </summary>
        public void GetRandomSquad()
        {
            Random rnd = new Random();
            for (int i = 0; i < Length; i++)
            {
                int randomChance = rnd.Next(1, 101);
                if (randomChance <= 45)
                    this[i] = Fighter.GenerateNewFighter(isPlayerSquad, i);
                if (randomChance > 45 && randomChance <= 75)
                    this[i] = Ninja.GenerateNewFighter(isPlayerSquad, i);
                if (randomChance > 75 && randomChance < 101)
                    this[i] = Samurai.GenerateNewFighter(isPlayerSquad, i);
            }
        }

        /// <summary>
        ///     Функция для самостоятельного набора
        /// </summary>
        /// <param name="score">Число очков</param>
        public void GetCustomSquad(float score)
        {
            Console.WriteLine("У вас имеется 10 очков. Стоимость распределения: \n\t{0}\n\t{1}\n\t{2}",
                "(1) Fighter: " + Fighter.cost, "(2) Ninja: " + Ninja.cost, "(3) Samurai: " + Samurai.cost);

            Samurai.AskForCount(ref score, this);
            Ninja.AskForCount(ref score, this);
            Fighter.AskForCount(ref score, this);
        }

        /// <summary>
        ///     Функция для добавления солдат в отряд
        /// </summary>
        /// <param name="fighterType">Тип бойца</param>
        /// <param name="count">Количество добавляемых бойцов</param>
        public void AddFighters(FighterType fighterType, int count)
        {
            int lastIndex = FindLastNull();
            switch (fighterType)
            {
                case FighterType.Samurai:
                    for (int i = 0; i < count; i++)
                    {
                        this[lastIndex + i] = Samurai.GenerateNewFighter(isPlayerSquad, lastIndex + i);
                    }
                    break;
                case FighterType.Ninja:
                    for (int i = 0; i < count; i++)
                    {
                        this[lastIndex + i] = Ninja.GenerateNewFighter(isPlayerSquad, lastIndex + i);
                    }
                    break;
                case FighterType.Fighter:
                    for (int i = 0; i < count; i++)
                    {
                        this[lastIndex + i] = Fighter.GenerateNewFighter(isPlayerSquad, lastIndex + i);
                    }
                    break;
            }
        }

        /// <summary>
        ///     Тип бойца
        /// </summary>
        public enum FighterType
        {
            Fighter, Ninja, Samurai
        }

        /// <summary>
        ///     Нахождение последней пустой ячейки
        /// </summary>
        /// <returns></returns>
        public int FindLastNull()
        {
            for (int i = 0; i < Length; i++)
                if (this[i] == null)
                    return i;
            
            return Length;
        }

        /// <summary>
        ///     Количество живых бойцов в отряде
        /// </summary>
        /// <returns></returns>
        public int LastAlive()
        {
            int k = 0;
            foreach (Fighter _fighter in squad)
                if (_fighter != null && _fighter.isAlive) k++;

            return k;
        }
    }
}