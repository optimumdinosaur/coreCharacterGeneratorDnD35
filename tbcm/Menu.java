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
						System.out.println("Creating character class...");

						System.out.println("Creating DDCharacter...");
						tbcm.DDCharacter c = new tbcm.DDCharacter(inName, inRace);
						System.out.println("DDCharacter object created!");
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

						for(int i=0; i < csLineVector.length; i++) { // for each skill
							csLineVector[i].trim(); // trim off any whitespace
							if(c.skills.get(csLineVector[i]) == null) { // if the character's skillmap does not include this skill
								c.skills.put(csLineVector[i], new HashMap<String, Integer>()); // set it up with its own hashmap
								c.skills.get(csLineVector[i]).put("Total", 0);
								c.skills.get(csLineVector[i]).put("Ranks", 0);
								c.skills.get(csLineVector[i]).put("Misc", 0);
								if (csLineVector[i].startsWith("CRAFT") || csLineVector[i].startsWith("KNOWLEDGE")) {
									System.out.println("New intelligence based skill: " + csLineVector[i]);
									c.skills.get(csLineVector[i]).put("AbiMod", 3);
								}
								else if (csLineVector[i].startsWith("PERFORM")) {
									System.out.println("New Perform skill: " + csLineVector[i]);
									c.skills.get(csLineVector[i]).put("AbiMod", 5);
								}
								else if (csLineVector[i].startsWith("PROFESSION")) {
									System.out.println("New Profession skill: " + csLineVector[i]);
									c.skills.get(csLineVector[i]).put("AbiMod", 4);
								}
								else {
									System.out.println("Unrecognized skill: " + csLineVector[i]);
									System.out.print("Which ability score is this based on?\n" +
										"Use the corresponding integer to indicate which one.\n" +
										"0-Str, 1-Dex, 2-Con, 3-Int, 4-Wis, 5-Cha :: ");
									int whichAbi = input.nextInt();
									c.skills.get(csLineVector[i]).put("AbiMod", whichAbi);
								}
							}
						}
						System.out.println("Class skills added!");
						input.nextLine();
						ArrayList<String> cs = new ArrayList<String>(Arrays.asList(csLineVector));
						System.out.println("Any priority skills?");
						csLine = input.nextLine().toUpperCase();
						System.out.println("pSkills read as: " + csLine);
						csLineVector = csLine.split(",");
						ArrayList<String> ps = new ArrayList<String>(Arrays.asList(csLineVector));
						for (String item : ps)
							addPrioritySkill(c, item);
						System.out.print("And how many skill points each level? ");
						int inSPPL = input.nextInt();
						System.out.println("Creating CharacterClass...");
						tbcm.CharacterClass clas = new tbcm.CharacterClass(inClass, inHD, inBAB, inFort, inRef, inWill, cs, ps, inSPPL);
						System.out.println("CharacterClass created!");
						System.out.println("Setting up first class for DDCharacter...");
						c.setupFirstClass(clas);
						System.out.println("First class setup successful!");
						System.out.println("Character created!");
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
				addPrioritySkill(ddc, inStr);
			}
			else if (inStr.startsWith("ADDPRIORITYSKILL")) {
				String skill = inStr.substring(inStr.indexOf(' ')+1);
				System.out.format("Prioritizing %s...\n", skill);
				addPrioritySkill(ddc, skill);
			}
			else if (inStr.equals("LEVELUP")) {
				System.out.print("Level up in which class? ");
				inStr = input.nextLine().toUpperCase();
				this.levelUp(ddc, inStr);
			}
			else if (inStr.startsWith("LEVELUP")) {
				String clas = inStr.substring(inStr.indexOf(' ')+1);
				System.out.println("clas read as: "+ clas);
				this.levelUp(ddc, clas);
			}
			else if (inStr.equals("ADDSPECIAL")) {
				System.out.print("Add special ability: ");
				inStr = input.nextLine().toUpperCase();
				ddc.addSpecial(inStr);
			}
			else if (inStr.startsWith("ADDSPECIAL")) {
				String inSpecial = inStr.substring(inStr.indexOf(' ')+1);
				System.out.println("inSpecial read as: " + inSpecial);
				ddc.addSpecial(inSpecial);
			}
			else if (inStr.equals("HELP"))
				System.out.println("Usable commands: addpriorityskill, addspecial, exit, help, levelup, print, printskill, reassignstats");
			else if (inStr.equals("REASSIGNSTATS"))
				this.reassignCharStats(ddc);
			else if (inStr.equals("EXIT"))
				this.exit();
			else
				System.out.println("Unrecognized command\n" +
					"Usable commands: addpriorityskill, addspecial, exit, help, levelup, print, printskill, reassignstats");
		}
	}


	/* levelUp
		Method to level up a DDCharacter in the given class
		Checks the class to see if it is supported and if not, asks the user to create it
	*/
	private void levelUp(tbcm.DDCharacter ddc, String className) {
		if (!(Arrays.asList(supportedClasses).contains(className))) { // the class is not one of the Core OGL classes
			boolean nnew = true;
			Iterator<CharacterClass> cIterator = ddc.classes.keySet().iterator();
			while(cIterator.hasNext()) {
				CharacterClass currClass = cIterator.next();
				if (currClass.className.equals(className)) { // we've found a match in here though wat do; getClassFeatures still advances the class's chassis features
					ddc.getClassFeatures(currClass, 1);
					ddc.classes.put(currClass, ddc.classes.get(currClass) + 1);
					nnew = false;
					ddc.calcSkillTotals();
					return;
				}
			}
			if (nnew) {
				tbcm.CharacterClass newClass = this.buildCharacterClass(ddc, className);
				ddc.classes.put(newClass, 0);
				ddc.getClassFeatures(newClass, 1);
				ddc.classes.put(newClass, 1);
				ddc.calcSkillTotals();
				return;
			}
		}
	}	


	/* buildCharacterClass
		method to create a new CharacterClass object for a DDCharacter 
		does not add the CharacterClass to the DDCharacter's classes HashMap
		takes user input to define the class's base characteristics and 
		sets the DDCharacter up for any new things the class may have
	*/
	private tbcm.CharacterClass buildCharacterClass(tbcm.DDCharacter c, String className) {
		System.out.println("Implementing new base class: " + className);
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
		for(int i=0; i < csLineVector.length; i++) { // for each skill
			csLineVector[i].trim(); // trim off any whitespace
			if(c.skills.get(csLineVector[i]) == null) { // if the character's skillmap does not include this skill
				c.skills.put(csLineVector[i], new HashMap<String, Integer>()); // set it up with its own hashmap
				c.skills.get(csLineVector[i]).put("Total", 0);
				c.skills.get(csLineVector[i]).put("Ranks", 0);
				c.skills.get(csLineVector[i]).put("Misc", 0);
				if (csLineVector[i].startsWith("CRAFT") || csLineVector[i].startsWith("KNOWLEDGE")) {
					System.out.println("New intelligence based skill: " + csLineVector[i]);
					c.skills.get(csLineVector[i]).put("AbiMod", 3);
				}
				else if (csLineVector[i].startsWith("PERFORM")) {
					System.out.println("New Perform skill: " + csLineVector[i]);
					c.skills.get(csLineVector[i]).put("AbiMod", 5);
				}
				else if (csLineVector[i].startsWith("PROFESSION")) {
					System.out.println("New Profession skill: " + csLineVector[i]);
					c.skills.get(csLineVector[i]).put("AbiMod", 4);
				}
				else {
					System.out.println("Unrecognized skill: " + csLineVector[i]);
					System.out.print("Which ability score is this based on?\n" +
						"Use the corresponding integer to indicate which one.\n" +
						"0-Str, 1-Dex, 2-Con, 3-Int, 4-Wis, 5-Cha :: ");
					int whichAbi = input.nextInt();
					c.skills.get(csLineVector[i]).put("AbiMod", whichAbi);
				}
			}
		}
		System.out.println("Class skills added!");
		input.nextLine();
		ArrayList<String> cs = new ArrayList<String>(Arrays.asList(csLineVector));
		System.out.println("Any priority skills?");
		csLine = input.nextLine().toUpperCase();
		System.out.println("pSkills read as: " + csLine);
		csLineVector = csLine.split(",");
		ArrayList<String> ps = new ArrayList<String>(Arrays.asList(csLineVector));
		for (String item : ps)
			addPrioritySkill(c, item);
		System.out.print("And how many skill points each level? ");
		int inSPPL = input.nextInt();
		System.out.println("Creating CharacterClass...");
		tbcm.CharacterClass clas = new tbcm.CharacterClass(className, inHD, inBAB, inFort, inRef, inWill, cs, ps, inSPPL);
		System.out.println("CharacterClass created!");
		return clas;
	}


	private void addPrioritySkill(tbcm.DDCharacter ddc, String pSkill) {
		if (ddc.skills.get(pSkill) == null) {
			ddc.skills.put(pSkill, new HashMap<String, Integer>());
			ddc.skills.get(pSkill).put("Total", 0);
			ddc.skills.get(pSkill).put("Ranks", 0);
			ddc.skills.get(pSkill).put("Misc", 0);
			if (pSkill.startsWith("CRAFT") || pSkill.startsWith("KNOWLEDGE")) {
				ddc.skills.get(pSkill).put("AbiMod", 3);
			}
			else if (pSkill.startsWith("PERFORM")) {
				ddc.skills.get(pSkill).put("AbiMod", 5);
			}
			else if (pSkill.startsWith("PROFESSION")) {
				ddc.skills.get(pSkill).put("AbiMod", 4);
			}
			else {
				System.out.println("Unrecognized skill: " + pSkill);
				System.out.print("Which ability score is this based on?\n" +
					"Use the corresponding integer to indicate which one.\n" +
					"0-Str, 1-Dex, 2-Con, 3-Int, 4-Wis, 5-Cha :: ");
				int whichAbi = input.nextInt();
				ddc.skills.get(pSkill).put("AbiMod", whichAbi);
			}
		}
		ddc.prioritySkills.add(pSkill);
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