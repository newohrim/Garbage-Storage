using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace Overdrotch
{
    public class CSVParser
    {
        /// <summary>
        ///     Метод читающий Csv файл
        /// </summary>
        /// <param name="path">Путь к файлу</param>
        /// <returns>Возвращает лист с персонажами</returns>
        public List<Character> Read(string path) 
        {
            List<Character> charactersList = new List<Character>();
            string[] lines = null;

            try
            {
                lines = File.ReadAllLines(path, Encoding.UTF8);
            }
            catch (Exception e) 
            {
                ReportException(e);
                return null;
            }

            foreach (string line in lines) 
            {
                if (line.Contains("Heroes")) continue;
                Console.WriteLine(line);

                string[] characterInfo = line.Split(';');
                foreach (string info in characterInfo) 
                {
                    Console.WriteLine(info);
                }

                try
                {
                    charactersList.Add(new Character()
                    {
                        Name = characterInfo[0].Trim(),
                        Damage = DataParse(characterInfo[1]),
                        Headshot = DataParse(characterInfo[2]),
                        SingleShot = DataParse(characterInfo[3]),
                        Health = DataParse(characterInfo[4]),
                        Reload = DataParse(characterInfo[5]),
                    }); ;
                }
                catch (WrongCharacterInfoException e)
                {
                    ReportException(e);
                    break;
                }
                catch (Exception e) 
                {
                    ReportException(e);
                    break;
                }
            }

            return charactersList;
        }

        /// <summary>
        ///     Метод, сохраняющий лист игроков
        /// </summary>
        /// <param name="path">Путь к файлу</param>
        /// <param name="charactersList">Лист персонажей</param>
        public void Save(string path, List<Character> charactersList) 
        {
            try
            {
                File.WriteAllLines(path, ConvertCharacterToLines(charactersList));
            }
            catch (Exception e) { ReportException(e); }
        }
        
        /// <summary>
        ///     Метод, конвертирующий лист персонажей в строки
        /// </summary>
        /// <param name="charactersList">Лист с персонажами</param>
        /// <returns></returns>
        private List<string> ConvertCharacterToLines(List<Character> charactersList) 
        {
            List<string> lines = new List<string>();
            lines.Add(Character.ZeroLine);
            foreach (Character character in charactersList) 
            {
                lines.Add(character.ToString());
            }
            return lines;
        }

        double DataParse(string data) 
        {
            data = data.Trim();
            if (data == String.Empty) return 0;
            if (data == "infinity") return Double.PositiveInfinity;
            if (data.Contains("/")) return CalculateTheDivideData(data);
            return double.Parse(data.Replace(".", ","));
        }

        /// <summary>
        ///     Метод, который считает инфу типа (100/200/300)
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        double CalculateTheDivideData(string data) 
        {
            string[] subData = data.Split('/');
            double result = double.Parse(subData[0]);

            for (int i = 1; i < subData.Length; i++) 
            {
                result /= double.Parse(subData[i]);
            }

            return result;
        }

        public static void ReportException(Exception e) 
        {
            Console.WriteLine(e.Message);
            Console.WriteLine(e.Source);
            Console.WriteLine(e.StackTrace);
        }
    }
}
