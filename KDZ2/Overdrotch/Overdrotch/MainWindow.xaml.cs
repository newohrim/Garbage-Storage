using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;

namespace Overdrotch
{
    /// <summary>
    /// Логика взаимодействия для MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public static Frame MainFrame;
        public static List<Character> characterList;

        public MainWindow()
        {
            InitializeComponent();
            MainFrame = frame;
            characterList = new List<Character>();
            GoToStartPage();
        }

        public static void ShowMessage(string text) => MessageBox.Show(text);
        //public static void OpenFightPage() => MainFrame.Navigate(new FightPage());

        public static void GoToStartPage() 
        {
            MainFrame.Navigate(new StartPage(characterList));
        }
    }
}
