using System;

namespace Overdrotch
{
    [Serializable]
    public class WrongCharacterInfoException : Exception
    {
        public WrongCharacterInfoException() { }
        public WrongCharacterInfoException(string message) : base(message) 
        {
            //MainWindow.ShowMessage(message);
        }
        public WrongCharacterInfoException(string message, Exception inner) : base(message, inner) 
        {
            //MainWindow.ShowMessage(message);
        }
        protected WrongCharacterInfoException(
          System.Runtime.Serialization.SerializationInfo info,
          System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
    }
}
