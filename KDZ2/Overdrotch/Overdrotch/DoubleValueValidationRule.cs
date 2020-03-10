using System;
using System.Windows.Controls;

namespace Overdrotch
{
    public class DoubleValueValidationRule : ValidationRule
    {
        /// <summary>
        ///     Метод и класс, определяющие правила заполнения таблицы
        /// </summary>
        /// <param name="value"></param>
        /// <param name="cultureInfo"></param>
        /// <returns></returns>
        public override ValidationResult Validate(object value,
            System.Globalization.CultureInfo cultureInfo)
        {
            double myVal = 0;

            try
            {
                if (value.ToString().Contains("."))
                    value = value.ToString().Replace(".", ",");
                if ((string)value == String.Empty)
                    return new ValidationResult(true, null);
                if (((string)value).Length > 0)
                    myVal = double.Parse((String)value);
            }
            catch (Exception e)
            {
                return new ValidationResult(false, "Illegal characters or " + e.Message);
            }

            if (myVal == Double.PositiveInfinity)
                return new ValidationResult(true, null);
            if (myVal < 0)
            {
                return new ValidationResult(false,
                  "Неверный ввод. Введите неотритцательное значение.");
            }
            else
            {
                return new ValidationResult(true, null);
            }
        }
    }
}
