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
	   	}
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
		String strengthStr = String.format("Str %1$d (%2$d)", strength, strMod);
		String dexterityStr = String.format("Dex %1$d (%2$d)", dexterity, dexMod);
		String consitutionStr = String.format("Con %1$d (%2$d)", consitution, conMod);
		String intelligenceStr = String.format("Int %1$d (%2$d)", intelligence, intMod);
		String wisdomStr = String.format("Wis %1$d (%2$d)", wisdom, wisMod);
		String charismaStr = String.format("Cha %1$d (%2$d)", charisma, chaMod);
		System.out.println(strengthStr);
		System.out.println(dexterityStr);
		System.out.println(consitutionStr);
		System.out.println(intelligenceStr);
		System.out.println(wisdomStr);
		System.out.println(charismaStr);
		System.out.println(specialList);
	}

	public static void main(String[] args) {
		character c = new character("Jim", "Human", "Barbarian", 1);
		c.multiClass("Bard", 2);
		c.multiClass("Fighter", 2);
		c.assignRolls(c.rollStats());
		c.getClassFeatures();
		c.printCharacter();

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
	public void setSpecial(String clas) {
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
			special.get(0).add("Bonus Feat");
			special.get(1).add("Bonus Feat");
			special.get(3).add("Bonus Feat");
			special.get(5).add("Bonus Feat");
			special.get(7).add("Bonus Feat");
			special.get(9).add("Bonus Feat");
			special.get(11).add("Bonus Feat");
			special.get(13).add("Bonus Feat");
			special.get(15).add("Bonus Feat");
			special.get(17).add("Bonus Feat");
			special.get(19).add("Bonus Feat");
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
	}

	public void printClass() {
		System.out.println("Character Class Name: " + className);
		System.out.println("Special:");
		System.out.println(special);
	}
}

