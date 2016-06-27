package tbcm; // text based character manager


import java.util.Scanner;
import java.util.Arrays;

class Menu {

	private Scanner input = new Scanner(System.in);

	public void display() {
		System.out.println("--D&D v3.5 Character Manager--");
		System.out.println("-------Main Menu-------");
		System.out.println(
			"Select an option: \n" +
			"	1) Build\n" +
			"	2) Generate\n" +
			"	3) Load\n" +
			"	4) Exit\n"
		);

		int selection = input.nextInt();
		input.nextLine();

		switch (selection) {
			case 1:
				this.buildMenu();
				break;
			case 2:
				System.out.println("Under construction");
				break;
			case 3:
				System.out.println("Under construction");
				break;
			case 4:
				this.exit();
				break;
			default:
				System.out.println("Invalid selection");
				break;
		}
	}


	public void buildMenu() {
		bmenu: while(true) {
			System.out.println("--Build Menu--");
			System.out.println("What's your character's name?");
			String inName = input.nextLine();
			System.out.println("Great name! Now your character's race?");
			String inRace = input.nextLine();
			// here i just want to check if the race is one we have saved, let's just accept those for now
			// we can becomre more tolerant later, like the good ol' us of a
			String[] supportedRaces = new String[] {"Dromite", "Duergar", "Dwarf", "Elan", "Elf", "Gnome", "Half-Elf", "Half-Giant", "Half-Orc", "Halfling", "Human", "Maenad", "Xeph"};
			if (Arrays.asList(supportedRaces).contains(inRace)) {
				// the race object will be created with the character so let's just accept that the race we got is good and move on
				System.out.println("And your character's first class?");
				String inClass = input.nextLine();
				String[] supportedClasses = new String[] {"Barbarian", "Bard", "Cleric", "Druid", "Fighter", "Monk", "Paladin", "Ranger", "Rogue", "Sorcerer", "Wizard"};
				if (Arrays.asList(supportedClasses).contains(inClass)) {
					// now we can create the character
					System.out.println("Creating character...");
					tbcm.DDCharacter c = new tbcm.DDCharacter(inName, inRace, inClass);
					System.out.println("Characer successfully created!");
					this.characterMenu(c);
					break;
				}
			}
		}
	}


	public void characterMenu(tbcm.DDCharacter ddc) {
		while(true) {
			System.out.println("---Character Menu---");
			System.out.println("Enter a command, or 'help' for a list of commands: ");
			String inStr = input.nextLine();
			switch (inStr) {
				case "print":
					ddc.printCharacter();
				case "levelup":
					ddc.levelUp(ddc.getClassName(), 1);

			}
		}
	}



	private void exit() {
		System.out.println("Exiting...");
		System.exit(1);
	}


	public static void main(String[] args) {
		Menu menu = new Menu();
		while (true)
			menu.display();
	}

}