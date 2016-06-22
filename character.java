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
	
	private String name; // character's name
	private playerRace race; // character's race

	// hashmap with characterClasses as the keys, and the character's number of levels in that class as the value
	private HashMap<characterClass,Integer> classes = new HashMap<characterClass,Integer>(); 
	
	private int hitPoints;

	// these int[] store info for the ability scores in order:
	// 0-Strength, 1-Dexterity, 2-Constitution, 3-Intelligence, 4-Wisdom, 5-Charisma
	private int[] abilityScores;
	private int[] abiMods;

	// each part of the total save bonus is stored separately
	// keys include: "Total", "Base", "Misc"
	private HashMap<String,Integer> fortSave;
	private HashMap<String,Integer> refSave;
	private HashMap<String,Integer> willSave;

	// skills is the master hashmap for all the character's skills
	// the skill name serves as a key, and returns a value that is another hashmap
	// this inner hashmap is structured similarly to the ones for saves
	private HashMap<String, HashMap<String, Integer>> skills;
	private Set<String> classSkills; // a set containing the class skills of all the classes of the character
	private Set<String> prioritySkills; // a set containing priority skills for the character

	private Set<String> specialList; // the character's special abilities, class features, racial traits, etc.


	character(String newName, String newRace, String newClass) {
		name = newName;
		//playerRace nRace = new playerRace(newRace);
		race = new playerRace(newRace);

		characterClass clas = new characterClass(newClass);
		classes.put(clas, 0);

		hitPoints = 0;
		abiMods = new int[] {0, 0, 0, 0, 0, 0};
		assignRolls(rollStats());

		fortSave = new HashMap<String,Integer>();
		fortSave.put("Total", 0);
		fortSave.put("Base", 0);
		fortSave.put("Misc", 0);
		refSave = new HashMap<String,Integer>();
		refSave.put("Total", 0);
		refSave.put("Base", 0);
		refSave.put("Misc", 0);
		willSave = new HashMap<String,Integer>();
		willSave.put("Total", 0);
		willSave.put("Base", 0);
		willSave.put("Misc", 0);
		
		skills = new HashMap<String, HashMap<String, Integer>>();
		classSkills = new HashSet<String>();
		prioritySkills = new HashSet<String>();

		specialList = new HashSet<String>();
		getRacialTraits();
		getClassFeatures(clas, 1);
		classes.put(clas, 1);
	}

	// Function to add levels to a new or already existing class
	// @param: cName - the name of the class the character is gaining levels in
	// @param: levels - the number of levels
	private void levelUp(String cName, int levels) {
		// first we check if the given class is already in classes
		boolean nnew = true; // whether or not the class represented by cName is a new one or not
		Set<characterClass> cSet = classes.keySet();
		Iterator<characterClass> cIterator = cSet.iterator();
		while (cIterator.hasNext()) { // check each class the character has
			characterClass currClass = cIterator.next();
			if (currClass.className.equals(cName)) { // if we find a match
				getClassFeatures(currClass, levels); // do the thing
				classes.put(currClass, classes.get(currClass) + levels);
				nnew = false; // this class is not new
				return; // we're done here
			}
		}
		if (nnew) { // if it is a new class
			characterClass newClass = new characterClass(cName); // create the characterClass
			classes.put(newClass, 0); // put it into classes at level 0 so getClassFeatures() works properly
			getClassFeatures(newClass, levels); // will the class's features from level 1 to levels
			classes.put(newClass, levels); // update classes with the actual level. 
		}

	}


	/* Function to add get class features from the given class
	   according to the number of given levels in the class*/
   private void getClassFeatures(characterClass clas, int levels) {
	   	System.out.format("Getting class features for %s...\n", clas.className);
	   	System.out.println("Current level: " + classes.get(clas));
	   	System.out.format("Leveling up %1$d times, getting to Level %2$d.\n", levels, classes.get(clas)+levels);


	   	Random r = new Random(); // Random object I'll need later to roll for hit points
	   	for(int i=0; i < levels; i++) {
	   		int currentLevel = i + classes.get(clas);
	   		System.out.format("**Going from level %1$d to %2$d...\n", currentLevel, currentLevel + 1);
	   		ArrayList<String> newFeatures = clas.special.get(currentLevel);
	   		System.out.println("newFeatures of Lv" + (currentLevel+1) + ": " + newFeatures);
	   		for(int j=0; j < newFeatures.size(); j++) {
	   			specialList.add(newFeatures.get(j));
	   		}

	   		hitPoints = hitPoints + r.nextInt(clas.hitDie) + abiMods[2] + 1;
	   		System.out.println("hitPoints increasesd to " + hitPoints);

	   		if ((currentLevel+1) == 1) {
	   			System.out.println("1st level of a class.");

	   			// let's set up some skills here, basically just import the class and priority skills of the class and add them to the corresponding lists of the character
	   			// clas.classSkills isnt an arraylist, its just an array so ill have to loop through it
	   			classSkills.addAll(clas.classSkills);
	   			prioritySkills.addAll(clas.prioritySkills);

	   			
	   			if (clas.goodFort)
	   				fortSave.put("plusTwo", 2);
	   			if (clas.goodRef)
	   				refSave.put("plusTwo", 2);
	   			if (clas.goodWill)
	   				willSave.put("plusTwo", 2);
	   		}
	   		if (((currentLevel+1) % 2) == 0) { // if the level is even, when a goodSave progression increments
	   			System.out.println("Even level. Good saves going up!");
	   			if (clas.goodFort) 
	   				fortSave.put("Base", fortSave.get("Base")+1);
	   			if (clas.goodRef)
	   				refSave.put("Base", refSave.get("Base")+1);
	   			if (clas.goodWill)
	   				willSave.put("Base", willSave.get("Base")+1);
	   		}
	   		if (((currentLevel+1) % 3) == 0) { // checks for every 3rd level in a class, when a poor save progression is incremented
	   			if (!clas.goodFort) 
	   				fortSave.put("Base", fortSave.get("Base")+1);
	   			if (!clas.goodRef)
	   				refSave.put("Base", refSave.get("Base")+1);
	   			if (!clas.goodWill)
	   				willSave.put("Base", willSave.get("Base")+1);
	   		}
	   		System.out.println("*************************");
	   	}
	   	// here i will calculate the character's total save bonus
	   	int fortTotal = (fortSave.get("Total") * -1) + abiMods[2]; // initialize it to this so when we add them all together, the preexisting total does not throw it off
	   	for (int i : fortSave.values())
	   		fortTotal += i;
	   	fortSave.put("Total", fortTotal);

	   	int refTotal = (refSave.get("Total") * -1) + abiMods[1]; // initialize it to this so when we add them all together, the preexisting total does not throw it off
	   	for (int i : refSave.values())
	   		refTotal += i;
	   	refSave.put("Total", refTotal);

	   	int willTotal = (willSave.get("Total") * -1) + abiMods[4]; // initialize it to this so when we add them all together, the preexisting total does not throw it off
	   	for (int i : willSave.values())
	   		willTotal += i;
	   	willSave.put("Total", willTotal);
   		// still to be done: bab, skills, spells
   	}

   private void getRacialTraits() {
   	System.out.println("Getting racial traits...");
   	for(int i=0; i<race.special.size(); i++) {
   		specialList.add(race.special.get(i));
   	}

   	// i also need to adjust ability scores
   	for (int i=0; i<6; i++) {
   		abilityScores[i] += race.abiScoreAdjustments[0];
   		abiMods[i] = calcMod(abilityScores[i]);
   	}


   	fortSave.put("Misc", (fortSave.get("Misc")+race.saveAdjust[0]));
   	refSave.put("Misc", (refSave.get("Misc")+race.saveAdjust[0]));
   	willSave.put("Misc", (willSave.get("Misc")+race.saveAdjust[0]));

   	// i now need to calculate ability score mods
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
		for (int i=0; i<6; i++)
			System.out.println(finalRolls[i]);
		return finalRolls;
	}

	/* private void assignRolls(int[] rolls) 
	Function to assign rolls from rollStats just in order
	also calculates and assigns the mod for each stat
	@param: rolls, must be an integer array of length 6, presumably one generated from rollStats() */
	private void assignRolls(int[] rolls) {
		abilityScores = rolls;
		for (int i=0; i<6; i++) 
			abiMods[i] = calcMod(abilityScores[i]);
	}

	public void printCharacter() {
		System.out.println("Name: " + name);
		System.out.println("Race: " + race);
		System.out.println(classes);
		System.out.println("HP " + hitPoints);
		System.out.format("Str %d (%d)\n", abilityScores[0], abiMods[0]);
		System.out.format("Dex %d (%d)\n", abilityScores[1], abiMods[1]);
		System.out.format("Con %d (%d)\n", abilityScores[2], abiMods[2]);
		System.out.format("Int %d (%d)\n", abilityScores[3], abiMods[3]);
		System.out.format("Wis %d (%d)\n", abilityScores[4], abiMods[4]);
		System.out.format("Cha %d (%d)\n", abilityScores[5], abiMods[5]);
		System.out.println("Fort " + fortSave);
		System.out.println("Ref " + refSave);
		System.out.println("Will " + willSave);

		System.out.println("Special: " + specialList);
		System.out.println("Class Skills: " +classSkills);
		System.out.println("Priority Skills: " + prioritySkills);
	}

	public static void main(String[] args) {
		character c = new character("Jim", "Halfling", "Bard");
		c.levelUp("Bard", 4);

		
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
	int[] saveAdjust;
	ArrayList<String> autoLanguages;
	ArrayList<String> bonusLanguages;

	playerRace(String name) {
		raceName = name;
		abiScoreAdjustments = new int[] {0, 0, 0, 0, 0, 0};
		movement = 30;
		size = 0;
		special = new ArrayList<String>();
		skillAdjust = new HashMap<String, Integer>();
		saveAdjust = new int[] {0, 0, 0};
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
			special.addAll(Arrays.asList("Halfling Fearlessness", "Halfling Thrown Weapon Mastery"));
			// fortSave.put("Misc", fortSave.get("Misc")+1);
			// refSave.put("Misc", refSave.get("Misc")+1);
			// willSave.put("Misc", willSave.get("Misc")+1);
			saveAdjust[0] = 1;
			saveAdjust[1] = 1;
			saveAdjust[2] = 1;
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
	ArrayList<String> classSkills; // an array list containing names of all of the class's class skills
	ArrayList<String> prioritySkills; // an array list containing skills that are vital to the class's functionality
	int skillPointsPerLevel; // the number of skill points a member of this class gains at each level, not counting their int bonus
	int numOfLevels; //for now I'll keep this commented out and default to 20, but if prestige classes are going to be involved i'll have to deal with it
	ArrayList<ArrayList<String>> special; // an array list of array lists to store the class's special features


	characterClass(String name) {
		className = name;

		numOfLevels = 20;
		special = new ArrayList<ArrayList<String>>(numOfLevels);
		for (int i=0; i<numOfLevels; i++)
			special.add(new ArrayList<String>());
		prioritySkills = new ArrayList<String>();
		setSpecial(className);
		System.out.println("Character Class Created: " + className);

	}

	characterClass(String name, int hd, float bab, boolean fort, boolean ref, boolean will, ArrayList<String> cskills, int sppl) {
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
			classSkills = new ArrayList<String>(Arrays.asList("Climb", "Craft", "Handle Animal", "Intimidate", "Jump", "Listen", "Ride", "Survival", "Swim"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Appraise", "Balance", "Bluff", "Climb", "Concentration", "Craft", "Decipher Script", "Diplomacy", "Disguise", "Escape Artist", "Gather Information", "Hide", "Jump", "Knowledge(Arcana)", "Knowledge(Architecture & Engineering)", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(History)", "Knowledge(Local)", "Knowledge(Nature)", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Knowledge(The Planes)", "Listen", "Move Silently", "Perform", "Profession", "Sense Motive", "Sleight of Hand", "Speak Language", "Spellcraft", "Swim", "Tumble", "Use Magic Device"));
			String[] performChoices = {"Act", "Comedy", "Dance", "Keyboard", "Oratory", "Percussion", "Strings", "Wind", "Sing"};
			Random r = new Random();
			int randomIndex = r.nextInt(performChoices.length);
			String priorityPerform = "Perform(" + performChoices[randomIndex]+")";
			prioritySkills.add(priorityPerform);
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
			classSkills = new ArrayList<String>(Arrays.asList("Concentration", "Craft", "Diplomacy", "Heal", "Knowledge(Arcana)", "Knowledge(History)", "Knowledge(Religion)", "Knowledge(The Planes)", "Profession", "Spellcraft"));
			skillPointsPerLevel = 2;
			special.get(0).add("Turn or Rebuke Undead");
		}
		else if (clas.equals("Druid")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("Concentration", "Craft", "Diplomacy", "Handle Animal", "Heal", "Knowledge(Nature)", "Listen", "Profession", "Ride", "Spellcraft", "Spot", "Survival", "Swim"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Climb", "Craft", "Handle Animal", "Intimidate", "Jump", "Ride", "Swim"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Balance", "Climb", "Concentration", "Craft", "Diplomacy", "Escape Artist", "Hide", "Jump", "Knowledge(Arcana)", "Knowledge(Religion)", "Listen", "Move Silently", "Perform", "Profession", "Sense Motive", "Spot", "Swim", "Tumble"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Concentration", "Craft", "Diplomacy", "Handle Animal", "Heal", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Profession", "Ride", "Sense Motive"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Climb", "Concentration", "Craft", "Handle Animal", "Heal", "Hide", "Jump", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(Nature)", "Listen", "Move Silently", "Profession", "Ride", "Search", "Spot", "Survival", "Swim", "Use Rope"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Appraise", "Balance", "Bluff", "Climb", "Craft", "Decipher Script", "Diplomacy", "Disable Device", "Disguise", "Escape Artist", "Forgery", "Gather Information", "Hide", "Intimidate", "Jump", "Knowledge(Local)", "Listen", "Move Silently", "Open Lock", "Perform", "Profession", "Search", "Sense Motive", "Sleight of Hand", "Spot", "Swim", "Tumble", "Use Magic Device", "Use Rope"));
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
			classSkills = new ArrayList<String>(Arrays.asList("Bluff", "Concentration", "Craft", "Knowledge(Arcana)", "Profession", "Spellcraft"));
			skillPointsPerLevel = 2;
			special.get(0).add("Summon Familiar");
		}
		else if (clas.equals("Wizard")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("Concentration", "Craft", "Decipher Script", "Knowledge(Arcana)", "Knowledge(Architecture & Engineering)", "Knowledge(Dungeoneering)", "Knowledge(Geography)", "Knowledge(History)", "Knowledge(Local)", "Knowledge(Nature)", "Knowledge(Nobility & Royalty)", "Knowledge(Religion)", "Knowledge(The Planes)", "Profession", "Spellcraft"));
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

