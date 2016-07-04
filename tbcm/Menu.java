package tbcm; // text based character manager

import java.util.*;

class Menu {

	private Scanner input = new Scanner(System.in);
	private String[] supportedRaces = new String[] {"DROMITE", "DUERGAR", "DWARF", "ELAN", "ELF", "GNOME", "HALF-ELF", "HALF-GIANT", "HALF-ORC", "HALFLING", "HUMAN", "MAENAD", "XEPH"}; 
	private String[] supportedClasses = new String[] {"BARBARIAN", "BARD", "CLERIC", "DRUID", "FIGHTER", "MONK", "PALADIN", "PSION", "PSYCHIC WARRIOR", "RANGER", "ROGUE", "SORCERER", "SOULKNIFE", "WILDER", "WIZARD", "ADEPT", "ARISTOCRAT", "COMMONER", "EXPERT", "WARRIOR"};

	public void display() {
		System.out.println("--D&D v3.5 Character Manager--");
		System.out.println("-----------Main Menu----------");
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
			System.out.println("---Build Menu---");
			System.out.println("What's your character's name?");
			String inName = input.nextLine();
			System.out.println("Great name! Now your character's race?");
			String inRace = input.nextLine().toUpperCase();
			// here i just want to check if the race is one we have saved, let's just accept those for now
			// we can becomre more tolerant later, like the good ol' us of a
			if (Arrays.asList(supportedRaces).contains(inRace)) {
				// the race object will be created with the character so let's just accept that the race we got is good and move on
				System.out.println("And your character's first class?");
				String inClass = input.nextLine().toUpperCase();
				if (Arrays.asList(supportedClasses).contains(inClass)) {
					// now we can create the character
					System.out.println("Creating class...");
					tbcm.CharacterClass clas = new tbcm.CharacterClass(inClass);
					System.out.println("Creating character...");
					tbcm.DDCharacter c = new tbcm.DDCharacter(inName, inRace, clas);
					System.out.println("Characer successfully created!");
					this.characterMenu(c);
					break;
				}
				else { // the given character class is not recognized
					System.out.println("Unrecognized character class. Input new character class? (Y/N)");
					String inStr = input.nextLine().toUpperCase();
					if (inStr.equals("Y") || inStr.equals("YES")) {
						System.out.print("Class's Hit die? (just the integer)");
						int inHD = input.nextInt();
						System.out.print("Base attack bonus? (0.5, 0.75, or 1.0)");
						float inBAB = input.nextFloat();
						System.out.print("Good Fortitude save? (true/false)" );
						boolean inFort = input.nextBoolean();
						System.out.print("How about Reflex save? ");
						boolean inRef = input.nextBoolean();
						System.out.print("Will save? ");
						boolean inWill = input.nextBoolean();
						System.out.println("What are the class's class skills? Separate them with commas, please.");
						String csLine = input.nextLine();
						//System.out.println("First initialization of csLine: " + csLine);
						csLine = input.nextLine().toUpperCase(); // class skill line
						System.out.println("Second setting of csLine: " + csLine);
						String[] csLineVector = csLine.split(",");
						ArrayList<String> cs = new ArrayList<String>(Arrays.asList(csLineVector));
						System.out.println("Any priority skills?");
						csLine = input.nextLine().toUpperCase();
						System.out.println("pSkills read as: " + csLine);
						csLineVector = csLine.split(",");
						ArrayList<String> ps = new ArrayList<String>(Arrays.asList(csLineVector));
						System.out.print("And how many skill points each level? ");
						int inSPPL = input.nextInt();
						tbcm.CharacterClass clas = new tbcm.CharacterClass(inClass, inHD, inBAB, inFort, inRef, inWill, cs, ps, inSPPL);
						System.out.println("CharacterClass successfully created!");
						System.out.println("Creating character...");
						tbcm.DDCharacter c = new tbcm.DDCharacter(inName, inRace, clas);
						this.characterMenu(c);
						break;
					}
				}
			}
			else {
				System.out.println("Unrecognized race.");
			}
		}
	}


	public void characterMenu(tbcm.DDCharacter ddc) {
		while(true) {
			System.out.println("-----Character Menu-----");
			System.out.println("Enter a command, or 'help' for a list of commands: ");
			String inStr = input.nextLine().toUpperCase();
			System.out.println("Input String: " + inStr);
			if (inStr.equals("PRINT"))
				ddc.printCharacter();
			else if (inStr.equals("PRINTSKILL")) {
				System.out.print("Print out which skill? ");
				inStr = input.nextLine().toUpperCase();
				System.out.println(ddc.getSkill(inStr));
			}
			else if (inStr.startsWith("PRINTSKILL")) {
				String skill = inStr.substring(inStr.indexOf(' ')+1);
				System.out.println("skill read as: " + skill);
				System.out.println(ddc.getSkill(skill));
			}
			else if (inStr.equals("ADDPRIORITYSKILL")) {
				System.out.println("Prioritize which skill?");
				inStr = input.nextLine().toUpperCase();
				ddc.addPrioritySkill(inStr);
			}
			else if (inStr.startsWith("ADDPRIORITYSKILL")) {
				String skill = inStr.substring(inStr.indexOf(' ')+1);
				System.out.format("Prioritizing %s...\n", skill);
				ddc.addPrioritySkill(skill);
			}
			else if (inStr.equals("LEVELUP")) {
				System.out.print("Level up in which class? ");
				inStr = input.nextLine().toUpperCase();
				ddc.levelUp(inStr, 1);
			}
			else if (inStr.startsWith("LEVELUP")) {
				String clas = inStr.substring(inStr.indexOf(' ')+1);
				System.out.println("clas read as: "+ clas);
				ddc.levelUp(clas, 1);
			}
			else if (inStr.equals("HELP"))
				System.out.println("Usable commands: addpriorityskill, exit, help, levelup, print, printskill, reassignstats");
			else if (inStr.equals("REASSIGNSTATS"))
				this.reassignCharStats(ddc);
			else if (inStr.equals("EXIT"))
				this.exit();
			else
				System.out.println("Unrecognized command\n" +
					"Usable commands: addpriorityskill, exit, help, levelup, print, printskill, reassignstats");
		}
	}



	private void reassignCharStats(tbcm.DDCharacter ddc) {
		System.out.println("Enter your character's new ability scores");
		int[] newStats = new int[] {0, 0, 0, 0, 0, 0};
		System.out.print("Strength: ");
		newStats[0] = input.nextInt();
		System.out.print("Dexterity: ");
		newStats[1] = input.nextInt();
		System.out.print("Constitution: ");
		newStats[2] = input.nextInt();
		System.out.print("Intelligence: ");
		newStats[3] = input.nextInt();
		System.out.print("Wisdom: ");
		newStats[4] = input.nextInt();
		System.out.print("Charisma: ");
		newStats[5] = input.nextInt();
		ddc.setAbilityScores(newStats);
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