/*************************************************
character.java
Designed to generate, build, and manage 
characters for Dungeons and Dragons v3.5. 
Author: John M. Phillips

Content from the game is taken from d20srd.org
This content is Open Game Content, and is licensed for public use under the terms of the Open Game License v1.0a.
'd20 System' is a trademark of Wizards of the Coast Inc. and is used according to the terms of the d20 System License Version 6.0 (which can be found at wizards.com/d20)
*******************************************/
import java.util.*;


/* Class to store and manage the characters
*/
class character {

	private static HashMap<String, characterClass> classFeatures = new HashMap<String, characterClass>();
	
	private String name;
	private String race;
	private HashMap<String,Integer> classes = new HashMap<String,Integer>();
	private int hitPoints;

	private int strength;
	private int strMod;
	private int dexterity;
	private int dexMod;
	private int consitution;
	private int conMod;
	private int intelligence;
	private int intMod;
	private int wisdom;
	private int wisMod;
	private int charisma;
	private int chaMod;

	// private HashMap<String,int> fortSave;
	// private HashMap<String,int> refSave;
	// private HashMap<String,int> willSave;

	// private HashMap<String, int[]> skills;

	private ArrayList<String> specialList;


	character(String newName, String newRace, String newClass, int newClassLevel) {
		name = newName;
		race = newRace;
		classes.put(newClass, newClassLevel);
		strength = 10;
		strMod = 0;
		dexterity = 10;
		dexMod = 0;
		consitution = 10;
		conMod = 0;
		intelligence = 10;
		intMod = 0;
		wisdom = 10;
		wisMod = 0;
		charisma = 10;
		chaMod = 0;
		// fortSave = new HashMap<String,int>();
		// refSave = new HashMap<String,int>();
		// willSave = new HashMap<String,int>();
		// skills = new HashMap<String, int[]>();
		specialList = new ArrayList<String>();
	}



	// Function to add an additional class and levels to the character. 
	private void multiClass(String newClass, int newClassLevel) {
		classes.put(newClass, newClassLevel);
	}





	/* Function to initially get all class features of the character
   Will look at the character's classes hashmap in order to find out 
   what classes and how many levels of each of stuff to append to
   the character's specialList
   To add new class features to an existing character use the levelUp() method  */
   private void getClassFeatures() {
   	System.out.println("Getting class features...");
   	Set classSet = classes.keySet(); // a set containing all the classes the character has levels in
   	Iterator classIterator = classSet.iterator();
   	while (classIterator.hasNext()) { // loop through all the character's classes
   		String clas = (String)classIterator.next(); // string containing the name of the class
   		characterClass newClass = new characterClass(clas); // characterClass object of the new class; set it up here so we only set up the ones that we need
   		classFeatures.put(clas, newClass);
   		Integer level = classes.get(clas); // the number of the levels the character has in this clas
   		ArrayList<ArrayList<String>> classSpecial = classFeatures.get(clas).special;
   		for(int i=0; i < level; i++) {
   			ArrayList<String> newFeatures = classFeatures.get(clas).special.get(i);
   			specialList.addAll(newFeatures);
   		}
   		// still to be done: bab, saves, skills, hp
   	}
   }

   private void getRacialTraits() {
   	System.out.println("Getting racial traits...");
   	playerRace newRace = new playerRace(race);
   	specialList.addAll(newRace.special);
   	// i also need to adjust ability scores
   	// adjust skills
   	// and do languages
   }



	/* public static int calcMod(int score)
	   takes an int score as its only parameter and returns the 
	   corresponding modifier for that score */
	public static int calcMod(int score) {
		if (score % 2 == 0)
			return (score - 10) / 2;
		else
			return (score - 11) / 2;
	}


	/* private static int[] rollStats() 
	Function to roll for ability scores in the standard way
	Each stat becomes equal to 4d6 drop lowest
	return an int[] of size 6 containing the final rolls. */
	private static int[] rollStats() {
		int[] rolls = {0, 0, 0, 0};
		int[] finalRolls = {0, 0, 0, 0, 0, 0};
		Random r = new Random();
		for (int i = 0; i<6;i++) {
			while (finalRolls[i] < 6) {
				for (int j=0;j<4;j++) {
					rolls[j] = r.nextInt(6) + 1;
					finalRolls[i] += rolls[j];
				}
				Arrays.sort(rolls);
				finalRolls[i] = finalRolls[i] - rolls[0];
			}
		}
		return finalRolls;
	}

	/* private void assignRolls(int[] rolls) 
	Function to assign rolls from rollStats just in order
	also calculates and assigns the mod for each stat
	@param: rolls, must be an integer array of length 6, presumably one generated from rollStats() */
	private void assignRolls(int[] rolls) {
		strength = rolls[0];
		strMod = calcMod(strength);
		dexterity = rolls[1];
		dexMod = calcMod(dexterity);
		consitution = rolls[2];
		conMod = calcMod(consitution);
		intelligence = rolls[3];
		intMod = calcMod(intelligence);
		wisdom = rolls[4];
		wisMod = calcMod(wisdom);
		charisma = rolls[5];
		chaMod = calcMod(charisma);
	}

	public void printCharacter() {
		System.out.println("Name: " + name);
		System.out.println("Race: " + race);
		System.out.println(classes);

		System.out.format("Str %d (%d)\n", strength, strMod);
		System.out.format("Dex %d (%d)\n", dexterity, dexMod);
		System.out.format("Con %d (%d)\n", consitution, conMod);
		System.out.format("Int %d (%d)\n", intelligence, intMod);
		System.out.format("Wis %d (%d)\n", wisdom, wisMod);
		System.out.format("Cha %d (%d)\n", charisma, chaMod);

		System.out.println(specialList);
	}

	public static void main(String[] args) {
		character c = new character("Jim", "Dwarf", "Wizard", 1);
		c.assignRolls(c.rollStats());
		c.getRacialTraits();
		c.getClassFeatures();
		c.printCharacter();

	}
}



class playerRace {

	String raceName;
	int[] abiScoreAdjustments;
	int movement;
	int size;
	ArrayList<String> special;
	HashMap<String, Integer> skillAdjust;
	ArrayList<String> autoLanguages;
	ArrayList<String> bonusLanguages;

	playerRace(String name) {
		raceName = name;
		abiScoreAdjustments = new int[] {0, 0, 0, 0, 0, 0};
		movement = 30;
		size = 0;
		special = new ArrayList<String>();
		skillAdjust = new HashMap<String, Integer>();
		autoLanguages = new ArrayList<String>();
		bonusLanguages = new ArrayList<String>();
		setSpecial(raceName);
	}

	private void setSpecial(String name) {
		if (name.equals("Dwarf")) {
			abiScoreAdjustments[2] = 2;
			abiScoreAdjustments[5] = -2;
			movement = 20;
			special.addAll(Arrays.asList("Dwarven Movement", "Darkvisin 60ft", "Stonecunning", "Dwarven Weapon Familiarity", "Stability", "Dwarven Poison Resistance", "Dwarven Spell Resistance", "Dwarven Orc & Goblinoid Tactics", "Dwarven Giant-Fighting Tactics"));
			autoLanguages.addAll(Arrays.asList("Common", "Dwarven"));
			bonusLanguages.addAll(Arrays.asList("Giant", "Gnome", "Goblin", "Orc", "Terran", "Undercommon"));
		}
		else if (name.equals("Elf")) {
			abiScoreAdjustments[1] = 2;
			abiScoreAdjustments[2] = -2;
			special.addAll(Arrays.asList("Immunity to Sleep", "Low-Light Vision", "Elven Weapon Proficiencies"));
			skillAdjust.put("Listen", 2);
			skillAdjust.put("Search", 2);
			skillAdjust.put("Spot", 2);
			autoLanguages.addAll(Arrays.asList("Common", "Elven"));
			bonusLanguages.addAll(Arrays.asList("Draconic", "Gnoll", "Gnome", "Goblin", "Orc", "Sylvan"));		
		}
		else if (name.equals("Gnome")) {
			abiScoreAdjustments[0] = -2;
			abiScoreAdjustments[2] = 2;
			size = -1;
			movement = 20;
			special.addAll(Arrays.asList("Low-Light Vision", "Gnomish Weapon Familiarity", "Gnomish Illusion Mastery", "Gnomish Kobold & Goblin Tactics", "Gnomish Giant-Fighting Tactics", "Gnomish Spell-Like Abilities"));
			skillAdjust.put("Listen", 2);
			skillAdjust.put("Craft(Alchemy)", 2);
			autoLanguages.addAll(Arrays.asList("Common", "Gnome"));
			bonusLanguages.addAll(Arrays.asList("Draconic", "Dwarven", "Elven", "Giant", "Goblin", "Orc"));
		}
		else if (name.equals("Half-Elf")) {
			special.addAll(Arrays.asList("Immunity to Sleep", "Enchantment Resistance", "Low-Light Vision", "Elven Blood"));
			skillAdjust.put("Listen", 1);
			skillAdjust.put("Search", 1);
			skillAdjust.put("Spot", 1);
			skillAdjust.put("Diplomacy", 2);
			skillAdjust.put("Gather Information", 2);
			autoLanguages.addAll(Arrays.asList("Common", "Elven"));
			bonusLanguages.addAll(Arrays.asList("Abyssal", "Aquan", "Auran", "Celestial", "Draconic", "Dwarven", "Giant", "Gnome", "Goblin", "Gnoll", "Halfling", "Ignan", "Infernal", "Orc", "Sylvan", "Terran", "Undercommon"));
		}
		else if (name.equals("Half-Orc")) {
			abiScoreAdjustments[0] = 2;
			abiScoreAdjustments[3] = -2;
			abiScoreAdjustments[5] = -2;
			special.addAll(Arrays.asList("Darkvision 60ft", "Orc Blood"));
			autoLanguages.addAll(Arrays.asList("Common", "Orc"));
			bonusLanguages.addAll(Arrays.asList("Draconic", "Giant", "Gnoll", "Goblin", "Abyssal"));
		}
		else if (name.equals("Halfling")) {
			abiScoreAdjustments[1] = 2;
			abiScoreAdjustments[0] = -2;
			size = -1;
			movement = 20;
			skillAdjust.put("Climb", 2);
			skillAdjust.put("Jump", 2);
			skillAdjust.put("Listen", 2);
			skillAdjust.put("Move Silently", 2);
			special.addAll(Arrays.asList("Halfling Fearlessness", "Halfling Thrown Weapon Mastery", "Halfling Luck"));
			autoLanguages.addAll(Arrays.asList("Common", "Halfling"));
			bonusLanguages.addAll(Arrays.asList("Dwarven", "Elven", "Gnome", "Goblin", "Orc"));
		}
		else if (name.equals("Human")) {
			special.addAll(Arrays.asList("Human Bonus Feat", "Human Skill"));
			autoLanguages.add("Common");
			bonusLanguages.addAll(Arrays.asList("Abyssal", "Aquan", "Auran", "Celestial", "Draconic", "Dwarven", "Giant", "Gnome", "Goblin", "Gnoll", "Halfling", "Ignan", "Infernal", "Orc", "Sylvan", "Terran", "Undercommon"));


		}
	}

}




// class to store all the features of a D&D 3.5 Character Class
class characterClass {
	String className; // the name of the class
	int hitDie; // the maximum value of the class's hit die
	float baseAttackBonus; // the class's BAB progression, 1.0, 0.75, or 0.5
	boolean goodFort; // whether the class has a good save or not
	boolean goodRef;
	boolean goodWill;
	String[] classSkills; // an array containing names of all of the class's class skills
	int skillPointsPerLevel; // the number of skill points a member of this class gains at each level, not counting their int bonus
	int numOfLevels; //for now I'll keep this commented out and default to 20, but if prestige classes are going to be involved i'll have to deal with it
	ArrayList<ArrayList<String>> special; // an array of array lists to store the class's special features


	characterClass(String name) {
		className = name;

		numOfLevels = 20;
		special = new ArrayList<ArrayList<String>>(numOfLevels);
		for (int i=0; i<numOfLevels; i++)
			special.add(new ArrayList<String>());
		setSpecial(className);
	}

	characterClass(String name, int hd, float bab, boolean fort, boolean ref, boolean will, String[] cskills, int sppl) {
		className = name;
		hitDie = hd;
		baseAttackBonus = bab;
		goodFort = fort;
		goodRef = ref;
		goodWill = will;
		classSkills = cskills;
		skillPointsPerLevel = sppl;
		numOfLevels = 20;
		// i figure i should initialize all the levels and stuff to be empty, that way I can just add stuff in
		special = new ArrayList<ArrayList<String>>(numOfLevels);
		for(int i=0;i<numOfLevels;i++)
			special.add(new ArrayList<String>());
		// and now all the special lists for each level are empty
		setSpecial(className);
	}

	/* public void setSpecial(String clas)
	Function to set up the characterClass object's ArrayList of 
	special features. 
	Also sets up the chassis of the class */
	private void setSpecial(String clas) {
		if (clas.equals("Barbarian")) {
			hitDie = 12;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			String[] classSkills = {"Climb", "Craft", "Handle Animal", "Intimidate", "Jump", "Listen", "Ride", "Survival", "Swim"};
			skillPointsPerLevel = 4;
			special.get(0).add("Fast Movement");
			special.get(0).add("Illiteracy");
			special.get(0).add("Rage 1/day");
			special.get(1).add("Uncanny Dodge");
			special.get(2).add("Trap Sense +1");
			special.get(3).add("Rage 2/day");
			special.get(4).add("Improved Uncanny Dodge");
			special.get(5).add("Trap Sense +2");
			special.get(6).add("Damage Reduction 1/-");
			special.get(7).add("Rage 3/day");
			special.get(8).add("Trap Sense +3");
			special.get(9).add("Damage Reduction 2/-");
			special.get(10).add("Greater Rage");
			special.get(11).add("Rage 4/day");
			special.get(11).add("Trap Sense +4");
			special.get(12).add("Damage Reduction 3/-");
			special.get(13).add("Indomitable Will");
			special.get(14).add("Trap Sense +5");
			special.get(15).add("Damage Reduction 4/-");
			special.get(15).add("Rage 5/day");
			special.get(16).add("Tireless Rage");
			special.get(17).add("Trap Sense +6");
			special.get(18).add("Damage Reduction 5/-");
			special.get(19).add("Mighty Rage");
			special.get(19).add("Rage 6/day");
		}
		else if (clas.equals("Bard")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = true;
			goodWill = true;
			String[] classSkills = {"Appraise", "Balance", "Bluff", "Climb", "Concentration", "Craft", "Decipher Script", "Diplomacy", "Disguise", "Escape Artist", "Gather Information", "Hide", "Jump", "Knowledge(Arcana)", "Knowledge(Architecture & Engineering)", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(History)", "Knowledge(Local)", "Knowledge(Nature)", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Knowledge(The Planes)", "Listen", "Move Silently", "Perform", "Profession", "Sense Motive", "Sleight of Hand", "Speak Language", "Spellcraft", "Swim", "Tumble", "Use Magic Device"};
			skillPointsPerLevel = 6;
			special.get(0).add("Bardic Music");
			special.get(0).add("Bardic Knowledge");
			special.get(0).add("Countersong");
			special.get(0).add("Fascinate");
			special.get(0).add("Inspire Courage +1");
			special.get(2).add("Inspire Competence");
			special.get(5).add("Suggestion");
			special.get(7).add("Inspire Courage +2");
			special.get(8).add("Inspire Greatness");
			special.get(11).add("Song of Freedom");
			special.get(13).add("Inspire Courage +3");
			special.get(14).add("Inspire Heroics");
			special.get(17).add("Mass Suggestion");
			special.get(19).add("Inspire Courage +4");
			// HAVE TO DO SPELLS
		}
		else if (clas.equals("Cleric")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = true;
			String[] classSkills = {"Concentration", "Craft", "Diplomacy", "Heal", "Knowledge(Arcana)", "Knowledge(History)", "Knowledge(Religion)", "Knowledge(The Planes)", "Profession", "Spellcraft"};
			skillPointsPerLevel = 2;
			special.get(0).add("Turn or Rebuke Undead");
		}
		else if (clas.equals("Druid")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = true;
			String[] classSkills = {"Concentration", "Craft", "Diplomacy", "Handle Animal", "Heal", "Knowledge(Nature)", "Listen", "Profession", "Ride", "Spellcraft", "Spot", "Survival", "Swim"};
			skillPointsPerLevel = 4;
			special.get(0).add("Animal Companion");
			special.get(0).add("Nature Sense");
			special.get(0).add("Wild Empathy");
			special.get(1).add("Woodland Stride");
			special.get(2).add("Trackless Step");
			special.get(3).add("Resist Nature's Lure");
			special.get(4).add("Wild Shape 1/day");
			special.get(5).add("Wild Shape 2/day");
			special.get(6).add("Wild Shape 3/day");
			special.get(7).add("Wild Shape(Large)");
			special.get(8).add("Venom Immunity");
			special.get(9).add("Wild Shape 4/day");
			special.get(10).add("Wild Shape(Tiny)");
			special.get(11).add("Wild Shape(Plant)");
			special.get(12).add("A Thousand Faces");
			special.get(13).add("Wild Shape 5/day");
			special.get(14).add("Timeless Body");
			special.get(14).add("Wild Shape(Huge)");
			special.get(15).add("Wild Shape(Elemental 1/day");
			special.get(17).add("Wild Shape 6/day");
			special.get(17).add("Wild Shape(Elemental 2/day");
			special.get(19).add("Wild Shape(Elemental 3/day");
			special.get(19).add("Wild Shape(Huge Elemental");
		}
		else if (clas.equals("Fighter")) {
			hitDie = 10;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			String[] classSkills = {"Climb", "Craft", "Handle Animal", "Intimidate", "Jump", "Ride", "Swim"};
			skillPointsPerLevel = 2;
			special.get(0).add("Bonus Fighter Feat");
			special.get(1).add("Bonus Fighter Feat");
			special.get(3).add("Bonus Fighter Feat");
			special.get(5).add("Bonus Fighter Feat");
			special.get(7).add("Bonus Fighter Feat");
			special.get(9).add("Bonus Fighter Feat");
			special.get(11).add("Bonus Fighter Feat");
			special.get(13).add("Bonus Fighter Feat");
			special.get(15).add("Bonus Fighter Feat");
			special.get(17).add("Bonus Fighter Feat");
			special.get(19).add("Bonus Fighter Feat");
		}
		else if (clas.equals("Monk")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = true;
			goodWill = true;
			String[] classSkills = {"Balance", "Climb", "Concentration", "Craft", "Diplomacy", "Escape Artist", "Hide", "Jump", "Knowledge(Arcana)", "Knowledge(Religion)", "Listen", "Move Silently", "Perform", "Profession", "Sense Motive", "Spot", "Swim", "Tumble"};
			skillPointsPerLevel = 4;
			special.get(0).add("Monk AC Bonus");
			special.get(0).add("Flurry of Blows");
			special.get(0).add("Monk Unarmed Strike");
			// I could get hte character's size here and then create a list of unarmed damages
			// I'm going to take this opportunity to go work on setting up races some
			String[] featChoices = {"Improved Grapple", "Stunning Fist"};
			special.get(0).add(getRandom(featChoices));
			featChoices[0] = "Combat Reflexes";
			featChoices[1] = "Deflect Arrows";
			special.get(1).add(getRandom(featChoices));
			special.get(1).add("Evasion");
			special.get(2).add("Still Mind");
			special.get(2).add("Unarmored Speed Bonus 10ft");
			special.get(3).add("Ki Strike(Magic)");
			special.get(3).add("Slow Fall 20ft");
			special.get(3).add("Unarmed Damage 1d8");
			special.get(4).add("Purity of Body");
			featChoices[0] = "Improved Disarm";
			featChoices[1] = "Improved Trip";
			special.get(5).add(getRandom(featChoices));
			special.get(5).add("Slow Fall 30ft");
			special.get(6).add("Wholeness of Body");
			special.get(7).add("Slow Fall 40ft");
			special.get(7).add("Unarmed Damage 1d10");
			special.get(8).add("Improved Evasion");
			special.get(9).add("Ki Strike(Lawful)");
			special.get(9).add("Slow Fall 50ft");
			special.get(10).add("Diamond Body");
			special.get(10).add("Greater Flurry");
			special.get(11).add("Abundant Step");
			special.get(11).add("Slow Fall 60ft");
			special.get(11).add("Unarmed Damage 2d6");
			special.get(12).add("Diamond Soul");
			special.get(13).add("Slow Fall 70ft");
			special.get(14).add("Quivering Palm");
			special.get(15).add("Ki Strike(Adamantine)");
			special.get(15).add("Slow Fall 80ft");
			special.get(15).add("Unarmed Damage 2d8");
			special.get(16).add("Timeless Body");
			special.get(16).add("Tongue of the Sun and Moon");
			special.get(17).add("Slow Fall 90ft");
			special.get(18).add("Empty Body");
			special.get(19).add("Perfect Self");
			special.get(19).add("Slow Fall Any Distance");
			special.get(19).add("Unarmed Damage 2d10");
		}
		else if (clas.equals("Paladin")) {
			hitDie = 10;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			String[] classSkills = {"Concentration", "Craft", "Diplomacy", "Handle Animal", "Heal", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Profession", "Ride", "Sense Motive"};
			skillPointsPerLevel = 2;
			special.get(0).add("Aura of Good");
			special.get(0).add("Detect Evil");
			special.get(0).add("Smite Evil 1/day");
			special.get(1).add("Divine Grace");
			special.get(1).add("Lay on Hands");
			special.get(2).add("Aura of Courage");
			special.get(2).add("Divine Health");
			special.get(3).add("Turn Undead");
			special.get(4).add("Smite Evil 2/day");
			special.get(4).add("Special Mount");
			special.get(5).add("Remove Disease 1/week");
			special.get(8).add("Remove Disease 2/week");
			special.get(9).add("Smite Evil 3/day");
			special.get(11).add("Remove Disease 3/week");
			special.get(14).add("Remove Disease 4/week");
			special.get(14).add("Smite Evil 4/day");
			special.get(17).add("Remove Disease 5/week");
			special.get(19).add("Smite Evil 5/day");
		}
		else if (clas.equals("Ranger")) {
			hitDie = 8;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = true;
			goodWill = false;
			String[] classSkills = {"Climb", "Concentration", "Craft", "Handle Animal", "Heal", "Hide", "Jump", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(Nature)", "Listen", "Move Silently", "Profession", "Ride", "Search", "Spot", "Survival", "Swim", "Use Rope"};
			skillPointsPerLevel = 6;
			special.get(0).add("1st Favored Enemy");
			special.get(0).add("Track");
			special.get(0).add("Wild Empathy");
			special.get(1).add("Combat Style");
			special.get(2).add("Endurance");
			special.get(3).add("Animal Companion");
			special.get(4).add("2nd Favored Enemy");
			special.get(5).add("Improved Combat Style");
			special.get(6).add("Woodland Stride");
			special.get(7).add("Swift Tracker");
			special.get(8).add("Evasion");
			special.get(9).add("3rd Favored Enemy");
			special.get(10).add("Combat Style Mastery");
			special.get(12).add("Camouflage");
			special.get(14).add("4th Favored Enemy");
			special.get(16).add("Hide in Plain Sight");
			special.get(19).add("5th Favored Enemy");
		}
		else if (clas.equals("Rogue")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = true;
			goodWill = false;
			String[] classSkills = {"Appraise", "Balance", "Bluff", "Climb", "Craft", "Decipher Script", "Diplomacy", "Disable Device", "Disguise", "Escape Artist", "Forgery", "Gather Information", "Hide", "Intimidate", "Jump", "Knowledge(Local)", "Listen", "Move Silently", "Open Lock", "Perform", "Profession", "Search", "Sense Motive", "Sleight of Hand", "Spot", "Swim", "Tumble", "Use Magic Device", "Use Rope"};
			skillPointsPerLevel = 8;
			special.get(0).add("Sneak Attack +1d6");
			special.get(0).add("Trapfinding");
			special.get(1).add("Evasion");
			special.get(2).add("Sneak Attack +2d6");
			special.get(2).add("Trap Sense +1");
			special.get(3).add("Uncanny Dodge");
			special.get(4).add("Sneak Attack +3d6");
			special.get(5).add("Trap Sense +2");
			special.get(6).add("Sneak Attack +4d6");
			special.get(7).add("Improved Uncanny Dodge");
			special.get(8).add("Sneak Attack +5d6");
			special.get(8).add("Trap Sense +3");
			special.get(9).add("Special Ability"); // HAVE TO DO SOMETHING WITH THESE
			special.get(10).add("Sneak Attack +6d6");
			special.get(11).add("Trap Sense +4");
			special.get(12).add("Sneak Attack +7d6");
			special.get(12).add("Special Ability"); // AND THIS ONE
			special.get(14).add("Sneak Attack +8d6");
			special.get(14).add("Trap Sense +5");
			special.get(15).add("Special Ability"); // HERE TOO
			special.get(16).add("Sneak Attack +9d6");
			special.get(17).add("Trap Sense +6");
			special.get(18).add("Sneak Attack +10d6");
			special.get(18).add("Special Ability"); // AND THIS
		}
		else if (clas.equals("Sorcerer")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			String[] classSkills = {"Bluff", "Concentration", "Craft", "Knowledge(Arcana)", "Profession", "Spellcraft"};
			skillPointsPerLevel = 2;
			special.get(0).add("Summon Familiar");
		}
		else if (clas.equals("Wizard")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			String[] classSkills = {"Concentration", "Craft", "Decipher Script", "Knowledge(Arcana)", "Knowledge(Architecture & Engineering)", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(History)", "Knowledge(Local)", "Knowledge(Nature)", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Knowledge(The Planes)", "Profession", "Spellcraft"};
			skillPointsPerLevel = 2;
			special.get(0).add("Summon Familiar");
			special.get(0).add("Scribe Scroll");
			special.get(4).add("Bonus Wizard Feat");
			special.get(9).add("Bonus Wizard Feat");
			special.get(14).add("Bonus Wizard Feat");
			special.get(19).add("Bonus Wizard Feat");
		}
	}

	public static String getRandom(String[] array) {
		int r = new Random().nextInt(array.length);
		return array[r];
	}

	public void printClass() {
		System.out.println("Character Class Name: " + className);
		System.out.println("Special:");
		System.out.println(special);
	}
}

