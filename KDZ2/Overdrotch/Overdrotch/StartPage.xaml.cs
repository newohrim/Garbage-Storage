using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;

namespace Overdrotch
{
    /// <summary>
    /// Логика взаимодействия для StartPage.xaml
    /// </summary>
    public partial class StartPage : Page
    {
        Random rnd = new Random();
        CSVParser Parser = new CSVParser();
        bool isFileOpen = false;
        List<Character> characterList;

        public StartPage(List<Character> CharacterList)
        {
            InitializeComponent();
            characterList = CharacterList;
            if (characterList != null && CharacterList != null && charactersGrid != null)
            {
                charactersGrid.ItemsSource = characterList;
                if (characterList.Count > 1) isFileOpen = true;
            }
        }

        /// <summary>
        ///     Метод генерирующий таблицу
        /// </summary>
        /// <param name="path"></param>
        public void GenerateDataGrid(string path)
        {
            characterList = Parser.Read(path);
            charactersGrid.ItemsSource = characterList;
            isFileOpen = true;
        }

        /// <summary>
        ///     Метод, открывающий проводник
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ChooseCsvFile(object sender, RoutedEventArgs e)
        {
            OpenFileDialog fileDialog = new OpenFileDialog();
            fileDialog.Filter = "Таблица (*.CSV)|*.csv";
            fileDialog.CheckFileExists = true;
            fileDialog.Multiselect = false;
            if (fileDialog.ShowDialog() == true)
            {
                fileStatus.Content = fileDialog.FileName;
                GenerateDataGrid(fileDialog.FileName);
            }
        }

        /// <summary>
        ///     Метод, открывающий проводник
        /// </summary>
        /// <returns></returns>
        private string OpenSaveFile()
        {
            SaveFileDialog fileDialog = new SaveFileDialog();
            fileDialog.FileName = "Overwatch";
            fileDialog.DefaultExt = ".csv";
            fileDialog.Filter = "Таблица (*.CSV)|*.csv";
            if (fileDialog.ShowDialog() == true)
            {
                return fileDialog.FileName;
            }
            return "cancel";
        }

        private void FightButtonClick(object sender, RoutedEventArgs e)
        {
            if (isFileOpen) 
            {
                if (!IsValidate) 
                {
                    MessageBox.Show("Исправьте все ошибки перед началом игры.");
                    return;
                }
                if (charactersGrid.SelectedItem == null)
                {
                    MainWindow.ShowMessage("Выберите персонажа.");
                    return;
                }

                Character Player = (Character)charactersGrid.SelectedItem;
                Character Computer = characterList[rnd.Next(0, characterList.Count)];
                Player.CurrentHealth = Player.Health;
                Computer.CurrentHealth = Computer.Health;
                Player.Display();
                Computer.Display();
                if (Computer == null) 
                    throw new ArgumentException("Ошибка выбора персонажей.");
                MainWindow.characterList = characterList;
                MainWindow.MainFrame.Navigate(new FightPage(Player, Computer)); 
            }
            else MainWindow.ShowMessage("Не был открыт файл с персонажами."); 
        }

        /// <summary>
        ///     Метод, сохраняющий таблицу в файл
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveGridButtonClick(object sender, RoutedEventArgs e)
        {
            // Сохранение измененной таблицы
            if (!isFileOpen) return;
            string path = OpenSaveFile();
            if (path == "cancel") return;
            Parser.Save(path, characterList);
        }

        /// <summary>
        ///     Метод, открывающий ReadMe файл
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ReadMeClick(object sender, RoutedEventArgs e)
        {
            try
            {
                System.Diagnostics.Process txt = new System.Diagnostics.Process();
                txt.StartInfo.FileName = "notepad.exe";
                txt.StartInfo.Arguments = $"../../README.txt";
                txt.Start();
            }
            catch (Exception ex) { CSVParser.ReportException(ex); }
        }

        /// <summary>
        ///     Нет ли ошабок в таблице
        /// </summary>
        private bool IsValidate { get => !Validation.GetHasError(charactersGrid); }

        private void DataGridTextColumn_Error(object sender, ValidationErrorEventArgs e)
        {
            // Попытка обойти идиотский баг после закрытия MessageBox
            Dispatcher.BeginInvoke(new Action(() => MessageBox.Show(e.Error.ErrorContent.ToString())));
        }

        private void charactersGrid_Sorting(object sender, DataGridSortingEventArgs e)
        {
            
        }
    }
}
