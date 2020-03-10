using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Animation;
using System.Xml;
using System.IO;
using System.Xml.Linq;

namespace Overdrotch
{
    /// <summary>
    /// Логика взаимодействия для FightPage.xaml
    /// </summary>
    public partial class FightPage : Page
    {
        public Character Player, Computer;
        private Battle battle;

        public FightPage(Character Player, Character Computer)
        {
            InitializeComponent();
            this.Player = Player;
            this.Computer = Computer;
            // Создание битвы
            battle = new Battle(this, ref Player, ref Computer);
            // Инициализация ивентов
            battle.CharacterInfoChanged += UpdateCharactersHealthBar;
            battle.WinAction += WinAction;
            InitializeCharacterEvents();
            // Обновление интерфейсов игроков
            UpdateCharactersHealthBar();
            LogToConsole("Битва началась!");
        }

        /// <summary>
        ///     Метод инициализицрующий ивенты
        /// </summary>
        private void InitializeCharacterEvents() 
        {
            Player.SimpleHit += (double damage) => LogToConsole("Игрок " + Player.Name + " попал в противника и нанес " + damage + " урона.");
            Player.AccurateHit += (double damage) => LogToConsole("Игрок " + Player.Name + " попал в противника и нанес " + damage + " урона.");
            Player.HeadshotHit += (double damage) => LogToConsole("Игрок " + Player.Name + " попал в голову противника и нанес " + damage + " урона.");
            Player.MissHit += () => LogToConsole("Игрок " + Player.Name + " промахнулся!");

            Computer.SimpleHit += (double damage) => LogToConsole("Компьютер " + Computer.Name + " попал в противника и нанес " + damage + " урона.");
            Computer.AccurateHit += (double damage) => LogToConsole("Компютер " + Computer.Name + " попал в противника и нанес " + damage + " урона.");
            Computer.HeadshotHit += (double damage) => LogToConsole("Компютер " + Computer.Name + " попал в голову противника и нанес " + damage + " урона.");
            Computer.MissHit += () => LogToConsole("Компютер " + Player.Name + " промахнулся!");
        }

        /// <summary>
        ///     Метод обновляющий интерфейс
        /// </summary>
        private void UpdateCharactersHealthBar() 
        {
            if (Player.Health != 0)
                PlayerHealthBar.Value = Math.Ceiling((Player.CurrentHealth / Player.Health) * 100);
            if (Computer.Health != 0)
                ComputerHealthBar.Value = Math.Ceiling((Computer.CurrentHealth / Computer.Health) * 100);

            PlayerHealthStatus.Text = Math.Ceiling(Player.CurrentHealth).ToString();
            ComputerHealthStatus.Text = Math.Ceiling(Computer.CurrentHealth).ToString();
            PlayerCharacterName.Content = Player.Name;
            ComputerCharacterName.Content = Computer.Name;
        }

        private void BattleStartButton(object sender, RoutedEventArgs e)
        {
            battleStartButton.Visibility = Visibility.Collapsed;
            battleStartButton.IsEnabled = false;
            AnimateRectangle(0.5d);
        }

        /// <summary>
        ///     Анимация fadeout
        /// </summary>
        /// <param name="duration"></param>
        private void AnimateRectangle(double duration) 
        {
            DoubleAnimation alpha = new DoubleAnimation();
            alpha.From = rectangle.Opacity;
            alpha.To = 0.0d;
            alpha.Duration = TimeSpan.FromSeconds(duration);
            alpha.Completed += RectangleAnimationCompletedHandler;
            rectangle.BeginAnimation(OpacityProperty, alpha);
        }

        private void RectangleAnimationCompletedHandler(object sender, EventArgs e)
        {
            rectangle.Visibility = Visibility.Collapsed;
            rectangle.IsEnabled = false;
        }

        private void PlayerSimpleAttackButton_Click(object sender, RoutedEventArgs e)
        {
            battle.OneRoundAction(Character.AttackType.SimpleAttack);
        }

        private void PlayerAccurateAttackButton_Click(object sender, RoutedEventArgs e)
        {
            battle.OneRoundAction(Character.AttackType.AccurateShot);
        }

        private void SaveButton_Click(object sender, RoutedEventArgs e)
        {
            SaveProgress("save.xml");
        }

        /// <summary>
        ///     Метод, сохраняющий прогресс битвы
        /// </summary>
        /// <param name="fileName"></param>
        private void SaveProgress(string fileName) 
        {
            try
            {
                XDocument xDoc = new XDocument(
                    new XElement("heroes",
                    Player.ConvertToXmlElement("Player"),
                    Computer.ConvertToXmlElement("Computer")
                )); 
                xDoc.Save(fileName);
            }
            catch (Exception e) { CSVParser.ReportException(e); }
        }

        /// <summary>
        ///     Метод загружающий прогресс битвы
        /// </summary>
        /// <param name="fileName">Путь к файлу</param>
        private void LoadProgress(string fileName)
        {
            Character NewPlayer = new Character();
            Character NewComputer = new Character();

            try
            {
                if (!File.Exists(Directory.GetCurrentDirectory() + "/" + fileName))
                {
                    MessageBox.Show("Не найден файл с сохранением.");
                    return;
                }

                XmlDocument xDoc = new XmlDocument();
                xDoc.Load(Directory.GetCurrentDirectory() + "/" + fileName);
                XmlElement xRoot = xDoc.DocumentElement;
                foreach (XmlNode xNode in xRoot)
                {
                    if (xNode.Attributes.GetNamedItem("type").Value == "Player")
                        NewPlayer = ReadCharacterInfo(xNode);
                    if (xNode.Attributes.GetNamedItem("type").Value == "Computer")
                        NewComputer = ReadCharacterInfo(xNode);
                }
                if (NewPlayer == null || NewComputer == null)
                    throw new WrongCharacterInfoException("Не удалось прочитать файл сохранения.");
            }
            catch (WrongCharacterInfoException e) { MessageBox.Show(e.Message); }
            catch (Exception e) { CSVParser.ReportException(e); }

            Player = NewPlayer;
            Computer = NewComputer;
            UpdateCharactersHealthBar();
            InitializeCharacterEvents();
        }

        /// <summary>
        ///     Метод, считывающий инфу о персонаже
        /// </summary>
        /// <param name="xNode"></param>
        /// <returns></returns>
        private Character ReadCharacterInfo(XmlNode xNode) 
        {
            Character NewCharacter = new Character();

            foreach (XmlNode childNode in xNode)
            {
                switch (childNode.Name)
                {
                    case "name":
                        NewCharacter.Name = childNode.InnerText;
                        break;
                    case "damage":
                        NewCharacter.Damage = ConvertStringToDouble(childNode.InnerText);
                        break;
                    case "headshot":
                        NewCharacter.Headshot = ConvertStringToDouble(childNode.InnerText);
                        break;
                    case "singleShot":
                        NewCharacter.SingleShot = ConvertStringToDouble(childNode.InnerText);
                        break;
                    case "health":
                        NewCharacter.Health = ConvertStringToDouble(childNode.InnerText);
                        break;
                    case "reload":
                        NewCharacter.Reload = ConvertStringToDouble(childNode.InnerText);
                        break;
                    case "currentHealth":
                        NewCharacter.CurrentHealth = ConvertStringToDouble(childNode.InnerText);
                        break;
                }
            }

            return NewCharacter;
        }

        private double ConvertStringToDouble(string value) 
        {
            if (value == "INF") return Double.PositiveInfinity;
            return double.Parse(value.Replace(".", ","));
        }

        private void LoadButton_Click(object sender, RoutedEventArgs e)
        {
            if (MessageBox.Show("Загрузить сохранение? Весь несохраненный прогресс будет утерян.", "Загрузка", MessageBoxButton.OKCancel) == MessageBoxResult.OK)
                LoadProgress("save.xml");
        }

        private void WinAction(Character winner) 
        {
            Console.WriteLine(winner.Name + " победил!");
            Dispatcher.Invoke(new Action(() => {
                if (MessageBox.Show(winner.Name + " одержал победу!", "Конец битвы", MessageBoxButton.OK) == MessageBoxResult.OK)
                    MainWindow.GoToStartPage();
            }));
        }

        /// <summary>
        ///     Метод, выводящий информацию в консоль
        /// </summary>
        /// <param name="text"></param>
        public void LogToConsole(string text) 
        {
            ConsoleBlock.Text += "\n  " + text;
            ScrollBar.ScrollToBottom();
            Console.WriteLine(text);
        }
    }
}
