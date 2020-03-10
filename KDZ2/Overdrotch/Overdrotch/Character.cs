using System;
using System.Xml.Linq;

namespace Overdrotch
{
    public class Character
    {
        // Шаблон делегата для обработчиков попаданий 
        public delegate void HitDelegate(double damage);
        // Ивент попадания обычной атакой
        public event HitDelegate SimpleHit;
        // Ивент попадания точной атакой
        public event HitDelegate AccurateHit;
        // Ивент попадания в голову
        public event HitDelegate HeadshotHit;
        // Ивент промаха
        public event Action MissHit;

        // Первая линия в CSV файле
        public const string ZeroLine = "Heroes;Damage per second;Headshot;Single shot;Life;Reload";

        private static Random rnd = new Random();
        private double damage;
        private double headshot;
        private double singleShot;
        private double health;
        private double reload;
        private double currentHealth;

        public string Name { get; set; }

        public double Damage 
        {
            get { return damage; }
            set 
            {
                if (value >= 0) damage = value;
                else 
                {
                    damage = 0;
                    throw new WrongCharacterInfoException("Неверные данные для урона. Урон должен быть не меньше нуля.");
                }
            }
        }

        public double Headshot
        {
            get { return headshot; }
            set
            {
                if (value >= 0) headshot = value;
                else
                {
                    headshot = 0;
                    throw new WrongCharacterInfoException("Неверные данные для урона в голову. Урон должен быть не меньше нуля.");
                }
            }
        }

        public double SingleShot
        {
            get { return singleShot; }
            set
            {
                if (value >= 0) singleShot = value;
                else
                {
                    singleShot = 0;
                    throw new WrongCharacterInfoException("Неверные данные для одиночного выстрела. Одиночный выстрел должен быть не меньше нуля.");
                }
            }
        }

        public double Health
        {
            get { return health; }
            set
            {
                if (value >= 0) health = value;
                else
                {
                    health = 0;
                    throw new WrongCharacterInfoException("Неверные данные для здоровья персонажа. Здоровье персонажа должно быть не меньше нуля.");
                }
            }
        }

        public double Reload
        {
            get { return reload; }
            set
            {
                if (value >= 0) reload = value;
                else
                {
                    reload = 0;
                    throw new WrongCharacterInfoException("Неверные данные для перезарядки. Перезарядка должна быть не меньше нуля.");
                }
            }
        }
        public double CurrentHealth 
        {
            get { return currentHealth; }
            set 
            {
                if (value >= 0) currentHealth = value;
                else currentHealth = 0;
            }
        }

        public bool IsAlive => CurrentHealth > 0;

        public Character(double Damage, double Headshot, double SingleShot, double Health, double Reload) 
        {
            this.Damage = Damage;
            this.Headshot = Headshot;
            this.SingleShot = SingleShot;
            this.Health = Health;
            this.Reload = Reload;
        }

        public Character() { }

        /// <summary>
        ///     Метод атаки
        /// </summary>
        /// <param name="Enemy">Объект цели атаки</param>
        /// <param name="attackType">Тип атаки</param>
        public void Attack(Character Enemy, AttackType attackType) 
        {
            if (attackType == AttackType.SimpleAttack) 
            {
                for (int i = 0; i < 5; i++) 
                {
                    if (GetChance(70.0d))
                    {
                        Enemy.GetDamage(0.1d * Damage);
                        SimpleHit(0.1d * Damage);
                    }
                    else
                        MissHit();
                }
            }
            if (attackType == AttackType.AccurateShot)
            {
                for (int i = 0; i < 3; i++)
                {
                    if (GetChance(30.0d))
                    {
                        if (GetChance(20.0d))
                        {
                            Enemy.GetDamage(Headshot);
                            HeadshotHit(Headshot);
                        }
                        else 
                        { 
                            Enemy.GetDamage(0.4d * Damage);
                            AccurateHit(0.4d * Damage);
                        }
                    }
                    else
                        MissHit();
                }
            }
        }

        /// <summary>
        ///     Получение урона
        /// </summary>
        /// <param name="damage">Урон</param>
        public void GetDamage(double damage) => currentHealth -= damage;

        /// <summary>
        ///     Получение шанса (0 или 1)
        /// </summary>
        /// <param name="percent">Шанс в процентах</param>
        /// <returns></returns>
        public static bool GetChance(double percent) => rnd.Next(1, 101) <= percent;

        /// <summary>
        ///     Метод конвертирующий объект игрока в XmlElement
        /// </summary>
        /// <param name="CharacterType">Тип игрока (Player или Computer)</param>
        /// <returns></returns>
        public XElement ConvertToXmlElement(string CharacterType) 
        {
            return new XElement("hero",
                    new XAttribute("type", CharacterType),
                    new XElement("name", Name),
                    new XElement("damage", Damage),
                    new XElement("headshot", Headshot),
                    new XElement("singleShot", SingleShot),
                    new XElement("health", Health),
                    new XElement("reload", Reload),
                    new XElement("currentHealth", CurrentHealth));
        }

        public override string ToString()
        {
            return String.Format("{0};{1};{2};{3};{4};{5}", Name, Damage, Headshot, SingleShot, Health, Reload);
        }

        public void Display() => Console.WriteLine(Name + " " + currentHealth + " " + Health + " " + Damage);

        // Тип атаки
        public enum AttackType 
        { 
            SimpleAttack,
            AccurateShot
        }
    }
}
