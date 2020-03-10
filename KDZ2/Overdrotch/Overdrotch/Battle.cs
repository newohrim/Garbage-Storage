using System;

namespace Overdrotch
{
    class Battle
    {
        // Шаблон делегата для победного ивента
        public delegate void OnWinAction(Character winner);
        // Ивент после изменения характеристик героя
        public event Action CharacterInfoChanged;
        // Ивент после выигрыша одного из игроков
        public event OnWinAction WinAction; 

        private Character Player, Computer;
        private FightPage UI;

        public Battle (FightPage UI, ref Character Player, ref Character Computer) 
        {
            this.Player = Player;
            this.Computer = Computer;
            this.UI = UI;
        }

        // Сценарий раунда
        public void OneRoundAction(Character.AttackType attackType)
        {
            Player = UI.Player;
            Computer = UI.Computer;
            Console.WriteLine(Player.CurrentHealth + " " + Computer.CurrentHealth);
            if (!IsEverybodyAlive())
                EndBattle();

            Player.Attack(Computer, attackType);
            if (Computer.IsAlive) ComputerAttack();
            CharacterInfoChanged();
        }

        // Конец раунда
        private void EndBattle()
        {
            if (Player.IsAlive && !Computer.IsAlive) { WinAction(Player); return; }
            if (!Player.IsAlive && Computer.IsAlive) { WinAction(Computer); return; }

            throw new ArgumentException("Ничья невозможна.");
        }

        // Метод атаки компьютера
        private void ComputerAttack()
        {
            if (Character.GetChance(50) && Computer.IsAlive)
                Computer.Attack(Player, Character.AttackType.SimpleAttack);
            else
                Computer.Attack(Player, Character.AttackType.AccurateShot);
        }

        /// <summary>
        ///     Все ли игроки живы?
        /// </summary>
        /// <returns></returns>
        private bool IsEverybodyAlive() => Player.IsAlive & Computer.IsAlive;
    }
}
