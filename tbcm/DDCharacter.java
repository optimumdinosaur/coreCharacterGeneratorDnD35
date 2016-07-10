package tbcm;

/*************************************************
DDCharacter.java
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
class DDCharacter { // D&D Character
	
	private String name; // character's name
	private PlayerRace race; // character's race
	// hashmap with characterClasses as the keys, and the character's number of levels in that class as the value
	HashMap<CharacterClass,Integer> classes = new HashMap<CharacterClass,Integer>(); 
	int hitPoints;
	private int size; // medium = 0, small = -1, large = +1, huge = +2, etc.
	// these int[] store info for the ability scores in order:
	// 0-Strength, 1-Dexterity, 2-Constitution, 3-Intelligence, 4-Wisdom, 5-Charisma
	private int[] abilityScores;
	private int[] abiMods;
	// each part of the total save bonus is stored separately
	// keys include: "Total", "Base", "Misc"
	private HashMap<String,Integer> fortSave;
	private HashMap<String,Integer> refSave;
	private HashMap<String,Integer> willSave;
	private int baseAttackBonus;
	private Random r;
	// skills is the master hashmap for all the character's skills
	// the skill name serves as a key, and returns a value that is another hashmap
	// this inner hashmap is structured similarly to the ones for saves
	HashMap<String, HashMap<String, Integer>> skills; 
	Set<String> classSkills; // a set containing the class skills of all the classes of the character
	Set<String> prioritySkills; // a set containing priority skills for the character
	private Set<String> specialList; // the character's special abilities, class features, racial traits, etc.

	/* Basic constructor
		Creates a level 1 character of the given race and class
		Stats are rolled using the rollStats() method, 4d6 drop lowest
	*/
	DDCharacter(String newName, String newRace, CharacterClass clas) {
		name = newName;
		race = new PlayerRace(newRace);

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

		baseAttackBonus = 0;

		r = new Random(); // Random object I'll need later

		setupCoreSkills();
		classSkills = new HashSet<String>(); // both this and prioritySkills are actually set up in getClassFeatures()
		prioritySkills = new HashSet<String>();
		specialList = new HashSet<String>();
		getRacialTraits();
		getClassFeatures(clas, 1);
		classes.put(clas, 1);
		calcSkillTotals();
	}

	/* Constructor that does not require a CharacterClass
		Useful for creating a character with an unknown class because building a CharacterClass with Menu requires a DDCharacter
		Has to later to be set up with the setupFirstClass() method */
	DDCharacter(String newName, String newRace) {
		name = newName;
		race = new PlayerRace(newRace);

		hitPoints = 0;
		abiMods = new int[] {0, 0, 0, 0, 0, 0};
		abilityScores = new int[] {10, 10, 10, 10, 10, 10};

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

		baseAttackBonus = 0;

		r = new Random(); // Random object I'll need later

		setupCoreSkills();
		classSkills = new HashSet<String>(); // both this and prioritySkills are actually set up in getClassFeatures()
		prioritySkills = new HashSet<String>();
		specialList = new HashSet<String>();
		getRacialTraits();
	}


	// setupCoreSkills
	// method to be called within the constructor to initialize the character's skill HashMap and the core skills within. 
	// it's a bunch of stuff so it's in a separate method to save space. 
	private void setupCoreSkills() {	
		skills = new HashMap<String, HashMap<String, Integer>>();
		// and a HashMap for each of the skills, then place it in the character's skills HashMap
		skills.put("APPRAISE", new HashMap<String, Integer>());
		skills.put("BALANCE", new HashMap<String, Integer>());
		skills.put("BLUFF", new HashMap<String, Integer>());
		skills.put("CLIMB", new HashMap<String, Integer>());
		skills.put("CONCENTRATION", new HashMap<String, Integer>());
		skills.put("CRAFT", new HashMap<String, Integer>());
		skills.put("DECIPHER SCRIPT", new HashMap<String, Integer>());
		skills.put("DIPLOMACY", new HashMap<String, Integer>());
		skills.put("DISABLE DEVICE", new HashMap<String, Integer>());
		skills.put("DISGUISE", new HashMap<String, Integer>());
		skills.put("ESCAPE ARTIST", new HashMap<String, Integer>());
		skills.put("FORGERY", new HashMap<String, Integer>());
		skills.put("GATHER INFORMATION", new HashMap<String, Integer>());
		skills.put("HANDLE ANIMAL", new HashMap<String, Integer>());
		skills.put("HEAL", new HashMap<String, Integer>());
		skills.put("HIDE", new HashMap<String, Integer>());
		skills.put("INTIMIDATE", new HashMap<String, Integer>());
		skills.put("JUMP", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(ARCANA)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(ARCHITECTURE & ENGINEERING)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(DUNGEONEERING)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(GEOGRAPHY)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(HISTORY)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(LOCAL)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(NATURE)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(NOBILITY & ROYALTY)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(RELIGION)", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(THE PLANES)", new HashMap<String, Integer>());
		skills.put("LISTEN", new HashMap<String, Integer>());
		skills.put("MOVE SILENTLY", new HashMap<String, Integer>());
		skills.put("OPEN LOCK", new HashMap<String, Integer>());
		skills.put("PERFORM", new HashMap<String, Integer>());
		skills.put("PROFESSION", new HashMap<String, Integer>());
		skills.put("RIDE", new HashMap<String, Integer>());
		skills.put("SEARCH", new HashMap<String, Integer>());
		skills.put("SENSE MOTIVE", new HashMap<String, Integer>());
		skills.put("SLEIGHT OF HAND", new HashMap<String, Integer>());
		skills.put("SPELLCRAFT", new HashMap<String, Integer>());
		skills.put("SPOT", new HashMap<String, Integer>());
		skills.put("SURVIVAL", new HashMap<String, Integer>());
		skills.put("SWIM", new HashMap<String, Integer>());
		skills.put("TUMBLE", new HashMap<String, Integer>());
		skills.put("USE MAGIC DEVICE", new HashMap<String, Integer>());
		skills.put("USE ROPE", new HashMap<String, Integer>());
		skills.put("AUTOHYPNOSIS", new HashMap<String, Integer>());
		skills.put("KNOWLEDGE(PSIONICS)", new HashMap<String, Integer>());
		skills.put("PSICRAFT", new HashMap<String, Integer>());
		skills.put("USE PSIONIC DEVICE", new HashMap<String, Integer>());
		// these lists are for setting up the appropriate ability score modifier to each skill
		String[] strSkills = {"CLIMB", "JUMP", "SWIM"};
		String[] dexSkills = {"BALANCE", "ESCAPE ARTIST", "HIDE", "MOVE SILENTLY", "OPEN LOCK", "RIDE", "SLEIGHT OF HAND", "TUMBLE", "USE ROPE"};
		String[] conSkills = {"CONCENTRATION"};
		String[] intSkills = {"APPRAISE", "CRAFT", "DECIPHER SCRIPT", "DISABLE DEVICE", "FORGERY", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "KNOWLEDGE(PSIONICS)", "SEARCH", "SPELLCRAFT", "PSICRAFT"};
		String[] wisSkills = {"AUTOHYPNOSIS", "HEAL", "LISTEN", "PROFESSION", "SENSE MOTIVE", "SPOT", "SURVIVAL"};
		String[] chaSkills = {"BLUFF", "DIPLOMACY", "DISGUISE", "GATHER INFORMATION", "HANDLE ANIMAL", "INTIMIDATE", "PERFORM", "USE MAGIC DEVICE", "USE PSIONIC DEVICE"};
		String[][] skillsByMod = {strSkills, dexSkills, conSkills, intSkills, wisSkills, chaSkills}; // with this we can use nested loops to do all the ability scores
		for (int i=0; i < 6; i++) { // for each ability score
			for (int j=0; j < skillsByMod[i].length; j++) { // for each skill in the String[]
				skills.get(skillsByMod[i][j]).put("AbiMod", i);
			}
			skills.get(skillsByMod[i]);
		}
		// next i need to initialize Ranks, Misc, and Total all to 0 for each skill. 
		for (String skl : skills.keySet()) {
			HashMap<String, Integer> sklMap = skills.get(skl);
			sklMap.put("Total", 0);
			sklMap.put("Ranks", 0);
			sklMap.put("Misc", 0);
		}
	}

	/* setupFirstClass
		Method for DDCharacters that have been created with the constructor that does not require a CharacterClass
		This method completes the process of setting up the character */
	public void setupFirstClass(CharacterClass newClass) {
		classes.put(newClass, 0);
		assignRolls(rollStats());
		getClassFeatures(newClass, 1);
		classes.put(newClass, 1);
		calcSkillTotals();
	}


	/* addSpecial
		method to add a new special feature to the character */
	public void addSpecial(String abilityName) {
		specialList.add(abilityName);
		System.out.println("Special ability added: " + abilityName);
	}


	/* setAbilityScores
		method to reset the character's ability scores to the first 6 values in any given int[] */
	public void setAbilityScores(int[] newScores) {
		this.abilityScores = newScores;
		for (int i=0; i < 6; i++)
			this.abiMods[i] = this.calcMod(this.abilityScores[i]);
		System.out.println("Ability scores reassigned.");
	}

	// Function to add levels to a new or already existing class
	// @param: cName - the name of the class the character is gaining levels in
	// @param: levels - the number of levels
	public void levelUp(String cName, int levels) {
		// first we check if the given class is already in classes
		boolean nnew = true; // whether or not the class represented by cName is a new one or not
		Iterator<CharacterClass> cIterator = classes.keySet().iterator();
		while (cIterator.hasNext()) { // check each class the character has
			CharacterClass currClass = cIterator.next();
			if (currClass.className.equals(cName)) { // if we find a match
				getClassFeatures(currClass, levels); // do the thing
				classes.put(currClass, classes.get(currClass) + levels);
				nnew = false; // this class is not new
				break; // we're done with this while loop
			}
		}
		if (nnew) { // if it is a new class
			CharacterClass newClass = new CharacterClass(cName); // create the CharacterClass
			classes.put(newClass, 0); // put it into classes at level 0 so getClassFeatures() works properly
			getClassFeatures(newClass, levels); // get the character the classFeatures from 1 to levels
			classes.put(newClass, levels); // update classes with the actual level. 
		}
		calcSkillTotals();
	}

	/* getClassFeatures
		Function to add get class features from the given class
	    according to the number of given levels in the class*/
   public void getClassFeatures(CharacterClass clas, int levels) {
	   	System.out.format("Getting class features for %s...\n", clas.className);
	   	System.out.println("Current level: " + classes.get(clas));
	   	System.out.format("Leveling up %1$d times, getting to Level %2$d.\n", levels, classes.get(clas)+levels);

	   	int sumOfLevels = 0; // the number of levels the character has in classes that are not clas
	   	for (int lv : classes.values()) // this value is needed for calculating maximum skill rank
	   		sumOfLevels += lv;
	   	sumOfLevels -= classes.get(clas);

	   	for(int i=0; i < levels; i++) {
	   		int currentLevel = i + classes.get(clas);
	   		System.out.format("**Going from level %1$d to %2$d...\n", currentLevel, currentLevel + 1);
	   		ArrayList<String> newFeatures = clas.special.get(currentLevel);
	   		System.out.println("newFeatures of Lv" + (currentLevel+1) + ": " + newFeatures);
	   		for(int j=0; j < newFeatures.size(); j++) {
	   			specialList.add(newFeatures.get(j));
	   		}

	   		int sppl = clas.skillPointsPerLevel + abiMods[3]; // skill points per level
	   		if (race.raceName.equals("HUMAN")) 
	   			sppl++; // if human, get one more skill point per level
	   		if (sppl < 1)
	   			sppl = 1; // everybody gets at least one skill point per level
	   		
			if (classes.get(clas) == 0 && classes.size() == 1) { // if this is the character's very first level
	   			sppl = sppl * 4; // characters get quadruple hit points on their first ever level
	   			hitPoints = hitPoints + clas.hitDie + abiMods[2]; // full hp rolls at level 1
	   		}
	   		else
	   			hitPoints = hitPoints + r.nextInt(clas.hitDie) + abiMods[2] + 1;
	   		System.out.println("hitPoints increasesd to " + hitPoints);
	   		

	   		if ((currentLevel+1) == 1) { // if this is the character's first level in this class
	   			System.out.println("**1st level of a class.**");
	   			classSkills.addAll(clas.classSkills); 
	   			prioritySkills.addAll(clas.prioritySkills);
	   			if (clas.className.equals("BARD")){
	   				System.out.println("**Class found to be Bard!*");
	   				for (String ps : clas.prioritySkills) {
	   					if (ps.startsWith("PERFORM")) { // this should be the bard's priority perform
	   						System.out.println("Found priority perform: " + ps);
	   						skills.put(ps, new HashMap<String, Integer>());
	   						skills.get(ps).put("Total", 0);
	   						skills.get(ps).put("Ranks", 0);
	   						skills.get(ps).put("Misc", 0);
	   						skills.get(ps).put("AbiMod", 5);
	   					}
	   				}
	   			}

	   			if (clas.spellsPerDayProgression != null) {
	   				System.out.println("FOUND FIRST LEVEL OF A SPELLCASTING CLASS");
	   				System.out.println("Initializing clas.spellsPerDay...");
	   				System.out.println("clas.spellsPerDayProgression[0][0] : " + clas.spellsPerDayProgression[0][0]);
	   				clas.spellsPerDay = new ArrayList<Integer>();
	   				for(int j=0; j < clas.spellsPerDayProgression[0].length; j++) {
	   					System.out.println("About to set clas.spellPerDay equal to clas.spellsPerDayProgression[0]["+j+"]...");
	   					System.out.println("Value equal to : " + clas.spellsPerDayProgression[0][j]);
	   					clas.spellsPerDay.add(j, clas.spellsPerDayProgression[0][j]);
	   					System.out.println("Spells per day Level " + j + " set to : " + clas.spellsPerDay.get(j));
	   				}	   			
	   			}

	   			if (clas.goodFort)
	   				fortSave.put("plusTwo", 2);
	   			if (clas.goodRef)
	   				refSave.put("plusTwo", 2);
	   			if (clas.goodWill)
	   				willSave.put("plusTwo", 2);
	   		}
	   		if (((currentLevel+1) % 2) == 0) { // if the level is even, when a goodSave progression increments
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

	   		System.out.println("classSkills: " + classSkills);
	   		System.out.println("Initializing cSkill...");
	   		ArrayList<String> cSkill = new ArrayList<String>();
	   		System.out.println("cSkill successfully initialized!");
	   		cSkill.addAll(classSkills);
	   		System.out.println("cSkill size: " + cSkill.size());
	   		System.out.println("Skill points this level: " + sppl);
	   		int maxRank = currentLevel + sumOfLevels + 4; // level + 3; for now we're calculating all skills as if they were class skills
	   		for (String pSkill : prioritySkills) {
	   			System.out.println("Adjust priority skill " + pSkill +"...");
	   			System.out.println(skills.get(pSkill));
	   			System.out.println("Current maxRank: " + maxRank);
	   			while (skills.get(pSkill).get("Ranks") < maxRank && sppl > 0) {
	   				sppl--; // decrement sppl, increment Ranks for pSkill
	   				skills.get(pSkill).put("Ranks", (skills.get(pSkill).get("Ranks")+1));
	   				System.out.println(pSkill + " : " + skills.get(pSkill).get("Ranks"));
	   				System.out.println("Remaining skill points: " + sppl);
	   			}
	   		}

	   		for (int j=0; j < sppl; j++) { // for the remaining skill points
	   			String skillToRankUp = cSkill.get(r.nextInt(cSkill.size()));
	   			System.out.println("Current statistics of " + skillToRankUp);
	   			System.out.println(skills.get(skillToRankUp));
	   			if (skills.get(skillToRankUp).get("Ranks") < maxRank) {
	   				System.out.println("Adding ranks to skill: " + skillToRankUp);
	   				skills.get(skillToRankUp).put("Ranks", (skills.get(skillToRankUp).get("Ranks")+1));
	   			}
	   			else {
	   				System.out.println("Skill already at maximum rank!");
	   				j--; // decrement j so this doesn't affect the loop
	   			}	
	   		}

	   		
	   		System.out.println("*************************");
	   	} // end for (i=0; i < levels; i++)
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
   		// still to be done: spells

   	   	if(clas.spellList != null) {
			System.out.println("**#@@ SPELLCASTER FOUND @@#**");



	   		if(clas.spellsKnownProgression != null)
	   			System.out.println("***$$SPONTANEOUS CATSTER HERE $$***");
	   		else 
	   			System.out.println("*** PPPREPARED CASTER OVER HERE ***");
	   	}




	   	calcSkillTotals();
   	} // END PRIVATE VOID GETCLASSFEATURES


   private void getRacialTraits() {
   	System.out.println("Getting racial traits...");
   	size = race.size;
   	skills.get("HIDE").put("Misc", (skills.get("HIDE").get("Misc") + (-4 * size)));
   	// Ability Scores
   	System.out.println("Adjusting ability scores...");
   	for (int i=0; i<6; i++) {
   		abilityScores[i] += race.abiScoreAdjustments[i];
   		abiMods[i] = calcMod(abilityScores[i]);
   	}
   	System.out.println("Ability scores adjusted.");
   	System.out.println("Adding special racial abilities...");
   	// Special abilities
   	for(int i=0; i<race.special.size(); i++) {
   		specialList.add(race.special.get(i));
   	}
   	System.out.println("Special racial abilities added.");
   	System.out.println("Adjusting skills...");
   	// Skills
   	for (String key : race.skillAdjust.keySet()) {
   		System.out.println("Adjusting skill: " + key);
   		if (skills.containsKey(key))
   			skills.get(key).put("Misc", race.skillAdjust.get(key));
   		else { // we'll have to put it there
   			skills.put(key, new HashMap<String, Integer>());
   			skills.get(key).put("Misc", race.skillAdjust.get(key));
   			skills.get(key).put("Ranks", 0);
   			skills.get(key).put("Total", 0);
   			// now we have to look at what the skill is and figure out whta it's ability score modifier is going to be
   			// the only skills that wouldn't already be in the skills map are special Crafts, Performs, etc. 
   			if (key.startsWith("CRAFT"))
   				skills.get(key).put("AbiMod", 3);
   			else if (key.startsWith("PERFORM"))
   				skills.get(key).put("AbiMod", 5);
   			else if (key.startsWith("PROFESSION"))
   				skills.get(key).put("AbiMod", 4);
   			else {
   				System.out.println("Not sure what this skill is. Assuming it's int based");
   				skills.get(key).put("AbiMod", 3);
   			}
   		}
   	}
   	System.out.println("Skills adjusted.");

   	fortSave.put("Misc", (fortSave.get("Misc")+race.saveAdjust[0]));
   	refSave.put("Misc", (refSave.get("Misc")+race.saveAdjust[0]));
   	willSave.put("Misc", (willSave.get("Misc")+race.saveAdjust[0]));

   	// and do languages
   }

   public void calcSkillTotals() {
   		for (String skl : skills.keySet()) {
   			HashMap<String, Integer> sklMap = skills.get(skl);
   			sklMap.put("Total", (sklMap.get("Ranks") + sklMap.get("Misc") + abiMods[sklMap.get("AbiMod")]));
   		}
   		baseAttackBonus = 0; // make sure it's 0
   		for (CharacterClass clas : classes.keySet()) {
   			baseAttackBonus += (clas.baseAttackBonus * classes.get(clas));
   		}
   		for (int i=0; i < 6; i++) {
   			abiMods[i] = calcMod(abilityScores[i]);
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
		for (int i=0; i<6; i++)
			System.out.println(finalRolls[i]);
		return finalRolls;
	}

	/* private void assignRolls(int[] rolls) 
	Function to assign rolls from rollStats just in order
	also calculates and assigns the mod for each stat
	@param: rolls, must be an integer array of length 6, presumably one generated from rollStats() */
	private void assignRolls(int[] rolls) {
		// at this point the keySet to the classes would only contain one element
		// that element, being the only class the character has
		CharacterClass[] clasArray = classes.keySet().toArray(new CharacterClass[1]);
		CharacterClass firstClass = clasArray[0]; // the first and only element, or should be
		String cName = firstClass.className;
		System.out.println("Assigning rolls...");
		System.out.println("Class found to be " + cName);
		Arrays.sort(rolls);
		System.out.format("Sorted rolls: [%1$d, %2$d, %3$d, %4$d, %5$d, %6$d]\n", rolls[0], rolls[1], rolls[2], rolls[3], rolls[4], rolls[5]);
		// so by default they're sorted in increasing order
		Random rand = new Random();
		abilityScores = new int[6];
		if (cName.equals("BARBARIAN")) { // barbarian prioritizes: Strength, then Con, Dex, Cha, Wis, and finally Int
			abilityScores[0] = rolls[5];
			abilityScores[2] = rolls[4];
			abilityScores[1] = rolls[3];
			abilityScores[5] = rolls[2];
			abilityScores[4] = rolls[1];
			abilityScores[3] = rolls[0];
		}
		else if (cName.equals("BARD")) {
			abilityScores[5] = rolls[5]; // charisma
			abilityScores[3] = rolls[4]; // intelligence
			abilityScores[1] = rolls[3]; // dexterity
			abilityScores[2] = rolls[2];
			abilityScores[0] = rolls[1];
			abilityScores[4] = rolls[0];
		}
		else if (cName.equals("CLERIC")) {
			abilityScores[4] = rolls[5]; // wis
			abilityScores[5] = rolls[4]; // cha
			abilityScores[2] = rolls[3]; // con
			abilityScores[0] = rolls[2]; // str
			abilityScores[3] = rolls[1]; // int
			abilityScores[1] = rolls[0]; // dex
		}
		else if (cName.equals("DRUID")) {
			abilityScores[4] = rolls[5];
			abilityScores[2] = rolls[4];
			abilityScores[5] = rolls[3];
			abilityScores[1] = rolls[2];
			abilityScores[3] = rolls[1];
			abilityScores[0] = rolls[0];
		}
		else if (cName.equals("FIGHTER")) {
			abilityScores[2] = rolls[5];
			abilityScores[0] = rolls[4];
			abilityScores[1] = rolls[3];
			abilityScores[3] = rolls[2];
			abilityScores[4] = rolls[1];
			abilityScores[5] = rolls[0];
		}
		else if (cName.equals("MONK")) {
			abilityScores[4] = rolls[5];
			abilityScores[1] = rolls[4];
			abilityScores[0] = rolls[3];
			abilityScores[2] = rolls[2];
			abilityScores[3] = rolls[1];
			abilityScores[5] = rolls[0];
		}
		else if (cName.equals("PALADIN")) {
			abilityScores[0] = rolls[5];
			abilityScores[5] = rolls[4];
			abilityScores[2] = rolls[3];
			abilityScores[4] = rolls[2];
			abilityScores[1] = rolls[1];
			abilityScores[3] = rolls[0];
		}
		else if (cName.equals("PSION")) {
			abilityScores[3] = rolls[5];
			abilityScores[4] = rolls[4];
			abilityScores[1] = rolls[3];
			abilityScores[2] = rolls[2];
			abilityScores[5] = rolls[1];
			abilityScores[0] = rolls[0];
		}
		else if (cName.equals("PSYCHIC WARRIOR")) {
			abilityScores[0] = rolls[5];
			abilityScores[4] = rolls[4];
			abilityScores[2] = rolls[3];
			abilityScores[1] = rolls[2];
			abilityScores[3] = rolls[1];
			abilityScores[5] = rolls[0];
		}
		else if (cName.equals("RANGER")) {
			abilityScores[0] = rolls[5];
			abilityScores[1] = rolls[4];
			abilityScores[4] = rolls[3];
			abilityScores[2] = rolls[2];
			abilityScores[3] = rolls[1];
			abilityScores[5] = rolls[0];
		}
		else if (cName.equals("ROGUE")) {
			abilityScores[1] = rolls[5];
			abilityScores[3] = rolls[4];
			abilityScores[2] = rolls[3];
			abilityScores[5] = rolls[2];
			abilityScores[0] = rolls[1];
			abilityScores[4] = rolls[0];
		}
		else if (cName.equals("SORCERER")) {
			abilityScores[5] = rolls[5];
			abilityScores[2] = rolls[4];
			abilityScores[1] = rolls[3];
			abilityScores[3] = rolls[2];
			abilityScores[4] = rolls[1];
			abilityScores[0] = rolls[0];
		}
		else if (cName.equals("SOULKNIFE")) {
			abilityScores[1] = rolls[5];
			abilityScores[0] = rolls[4];
			abilityScores[4] = rolls[3];
			abilityScores[2] = rolls[2];
			abilityScores[3] = rolls[1];
			abilityScores[5] = rolls[0];
		}
		else if (cName.equals("WILDER")) {
			abilityScores[5] = rolls[5];
			abilityScores[1] = rolls[4];
			abilityScores[2] = rolls[3];
			abilityScores[3] = rolls[2];
			abilityScores[0] = rolls[1];
			abilityScores[4] = rolls[0];
		}
		else if (cName.equals("WIZARD")) {
			abilityScores[3] = rolls[5];
			abilityScores[2] = rolls[4];
			abilityScores[1] = rolls[3];
			abilityScores[4] = rolls[2];
			abilityScores[5] = rolls[1];
			abilityScores[0] = rolls[0];
		}
		else {
			abilityScores[0] = rolls[0];
			abilityScores[1] = rolls[1];
			abilityScores[2] = rolls[2];
			abilityScores[3] = rolls[3];
			abilityScores[4] = rolls[4];
			abilityScores[5] = rolls[5];
		}

	}


	public String getClassName() {
		Set<CharacterClass> classSet = classes.keySet();
		CharacterClass clas = classSet.iterator().next();
		return clas.className;
	}

	public HashMap<String, Integer> getSkill(String skillName) {
		return skills.get(skillName);
	}


	public void printCharacter() {
		System.out.println("Name: " + name);
		System.out.println("Race: " + race.raceName);
		System.out.println("Size: " + size);
		System.out.print("{");
		for (CharacterClass item : classes.keySet()) {
			System.out.print("["+item.className + " : " + classes.get(item)+"]");
		}
		System.out.print("}\n");
		System.out.println("HP " + hitPoints);
		System.out.format("Str %d (%d)\n", abilityScores[0], abiMods[0]);
		System.out.format("Dex %d (%d)\n", abilityScores[1], abiMods[1]);
		System.out.format("Con %d (%d)\n", abilityScores[2], abiMods[2]);
		System.out.format("Int %d (%d)\n", abilityScores[3], abiMods[3]);
		System.out.format("Wis %d (%d)\n", abilityScores[4], abiMods[4]);
		System.out.format("Cha %d (%d)\n", abilityScores[5], abiMods[5]);
		System.out.println("BAB " + baseAttackBonus);
		System.out.println("Fort " + fortSave);
		System.out.println("Ref " + refSave);
		System.out.println("Will " + willSave);

		for (CharacterClass item : classes.keySet()) {
			if (item.spellList != null) {
				System.out.println("Spells per day for " + item.className);
				for (int i=0; i < item.spellsPerDay.size(); i++) {
					System.out.println("Lvl " + i + " :: " + item.spellsPerDay.get(i) + "/day");
				}
			}
		}


		System.out.println("Special: " + specialList);
		System.out.println("Class Skills: " +classSkills);
		System.out.println("Priority Skills: " + prioritySkills);
		for (String item : prioritySkills) {
			System.out.println(item + " : " + skills.get(item));
		}
		//System.out.println("Character Skill w/ Bonuses: " + skills);
	}

	public static void main(String[] args) {
		DDCharacter c = new DDCharacter("Jim", "Half-Giant", new CharacterClass("ROGUE"));
		c.levelUp("ROGUE", 19);
		c.calcSkillTotals();
		c.printCharacter();

	}
}




/*
Class to store all the traits of a race or template
*/
class PlayerRace {

	String raceName;
	int[] abiScoreAdjustments;
	int movement;
	int size;
	ArrayList<String> special;
	HashMap<String, Integer> skillAdjust;
	int[] saveAdjust;
	ArrayList<String> autoLanguages;
	ArrayList<String> bonusLanguages;

	PlayerRace(String name) {
		raceName = name;
		abiScoreAdjustments = new int[] {0, 0, 0, 0, 0, 0};
		movement = 30;
		size = 0;
		special = new ArrayList<String>();
		skillAdjust = new HashMap<String, Integer>();
		saveAdjust = new int[] {0, 0, 0};
		autoLanguages = new ArrayList<String>();
		autoLanguages.add("Common");
		bonusLanguages = new ArrayList<String>();
		setSpecial(raceName);
	}

	private void setSpecial(String name) {
		if (name.equals("DROMITE")) {
			abiScoreAdjustments[5] = 2;
			abiScoreAdjustments[0] = -2;
			abiScoreAdjustments[4] = -2;
			size = -1;
			movement = 20;
			special.addAll(Arrays.asList("Chitin", "Naturally Psionic", "Energy Ray 1/day", "Scent", "Blind-Fight"));
			skillAdjust.put("SPOT", 2);
			bonusLanguages.addAll(Arrays.asList("Dwarven", "Gnome", "Goblin", "Terran"));
		}
		else if (name.equals("DUERGAR")) {
			abiScoreAdjustments[2] = 2;
			abiScoreAdjustments[5] = -4;
			movement = 20;
			special.addAll(Arrays.asList("Dwarven Movement", "Darkvision 120ft", "Immunity to Paralysis", "Immunity to Phantasms", "Immunity to Poison", "Duergar Spell Resistance", "Stability", "Stonecunning", "Expansion 1/day", "Invisibility 1/day", "Naturally Psionic", "Dwarven Orc & Goblinoid Tactics", "Dwarven Giant-Fighting Tactics", "Light Sensitivity"));
			skillAdjust.put("MOVE SILENTLY", 4);
			skillAdjust.put("LISTEN", 1);
			skillAdjust.put("SPOT", 1);
		}
		else if (name.equals("DWARF")) {
			abiScoreAdjustments[2] = 2;
			abiScoreAdjustments[5] = -2;
			movement = 20;
			special.addAll(Arrays.asList("Dwarven Movement", "Darkvision 60ft", "Stonecunning", "Dwarven Weapon Familiarity", "Stability", "Dwarven Poison Resistance", "Dwarven Spell Resistance", "Dwarven Orc & Goblinoid Tactics", "Dwarven Giant-Fighting Tactics"));
			autoLanguages.add("Dwarven");
			bonusLanguages.addAll(Arrays.asList("Giant", "Gnome", "Goblin", "Orc", "Terran", "Undercommon"));
		}
		else if (name.equals("ELAN")) {
			abiScoreAdjustments[5] = -2;
			special.addAll(Arrays.asList("Aberration", "Naturally Psionic", "Resistance", "Resilience", "Repletion"));
			bonusLanguages.addAll(Arrays.asList("Abyssal", "Aquan", "Auran", "Celestial", "Draconic", "Dwarven", "Giant", "Gnome", "Goblin", "Gnoll", "Halfling", "Ignan", "Infernal", "Orc", "Sylvan", "Terran", "Undercommon"));
		}	

		else if (name.equals("ELF")) {
			abiScoreAdjustments[1] = 2;
			abiScoreAdjustments[2] = -2;
			special.addAll(Arrays.asList("Immunity to Sleep", "Low-Light Vision", "Elven Weapon Proficiencies"));
			skillAdjust.put("LISTEN", 2);
			skillAdjust.put("SEARCH", 2);
			skillAdjust.put("SPOT", 2);
			autoLanguages.add("Elven");
			bonusLanguages.addAll(Arrays.asList("Draconic", "Gnoll", "Gnome", "Goblin", "Orc", "Sylvan"));		
		}
		else if (name.equals("GNOME")) {
			abiScoreAdjustments[0] = -2;
			abiScoreAdjustments[2] = 2;
			size = -1;
			movement = 20;
			special.addAll(Arrays.asList("Low-Light Vision", "Gnomish Weapon Familiarity", "Gnomish Illusion Mastery", "Gnomish Kobold & Goblin Tactics", "Gnomish Giant-Fighting Tactics", "Gnomish Spell-Like Abilities"));
			skillAdjust.put("LISTEN", 2);
			skillAdjust.put("CRAFT(Alchemy)", 2);
			autoLanguages.add("Gnome");
			bonusLanguages.addAll(Arrays.asList("Draconic", "Dwarven", "Elven", "Giant", "Goblin", "Orc"));
		}
		else if (name.equals("HALF-ELF")) {
			special.addAll(Arrays.asList("Immunity to Sleep", "Enchantment Resistance", "Low-Light Vision", "Elven Blood"));
			skillAdjust.put("LISTEN", 1);
			skillAdjust.put("SEARCH", 1);
			skillAdjust.put("SPOT", 1);
			skillAdjust.put("DIPLOMACY", 2);
			skillAdjust.put("GATHER INFORMATION", 2);
			autoLanguages.add("Elven");
			bonusLanguages.addAll(Arrays.asList("Abyssal", "Aquan", "Auran", "Celestial", "Draconic", "Dwarven", "Giant", "Gnome", "Goblin", "Gnoll", "Halfling", "Ignan", "Infernal", "Orc", "Sylvan", "Terran", "Undercommon"));
		}
		else if (name.equals("HALF-GIANT")) {
			abiScoreAdjustments[0] = 2;
			abiScoreAdjustments[2] = 2;
			abiScoreAdjustments[1] = -2;
			special.addAll(Arrays.asList("Giant", "Low-Light Vision", "Fire Acclimiated", "Powerful Build", "Naturally Psionic", "Stomp 1/day"));
			bonusLanguages.addAll(Arrays.asList("Draconic", "Giant", "Gnoll", "Ignan"));
		}
		else if (name.equals("HALF-ORC")) {
			abiScoreAdjustments[0] = 2;
			abiScoreAdjustments[3] = -2;
			abiScoreAdjustments[5] = -2;
			special.addAll(Arrays.asList("Darkvision 60ft", "Orc Blood"));
			autoLanguages.add("Orc");
			bonusLanguages.addAll(Arrays.asList("Draconic", "Giant", "Gnoll", "Goblin", "Abyssal"));
		}
		else if (name.equals("HALFLING")) {
			abiScoreAdjustments[1] = 2;
			abiScoreAdjustments[0] = -2;
			size = -1;
			movement = 20;
			skillAdjust.put("CLIMB", 2);
			skillAdjust.put("JUMP", 2);
			skillAdjust.put("LISTEN", 2);
			skillAdjust.put("MOVE SILENTLY", 2);
			special.addAll(Arrays.asList("Halfling Fearlessness", "Halfling Thrown Weapon Mastery"));
			for(int i=0; i<3; i++) 
				saveAdjust[i] =1;
			autoLanguages.add("Halfling");
			bonusLanguages.addAll(Arrays.asList("Dwarven", "Elven", "Gnome", "Goblin", "Orc"));
		}
		else if (name.equals("HUMAN")) {
			special.addAll(Arrays.asList("Human Bonus Feat", "Human Skill"));
			bonusLanguages.addAll(Arrays.asList("Abyssal", "Aquan", "Auran", "Celestial", "Draconic", "Dwarven", "Giant", "Gnome", "Goblin", "Gnoll", "Halfling", "Ignan", "Infernal", "Orc", "Sylvan", "Terran", "Undercommon"));
		}
		else if (name.equals("MAENAD")) {
			special.addAll(Arrays.asList("Naturally Psionic", "Energy Ray 1/day", "Maenad Outburst"));
			autoLanguages.add("Maenad");
			bonusLanguages.addAll(Arrays.asList("Aquan", "Draconic", "Dwarven", "Elven", "Goblin"));			
		}
		else if (name.equals("XEPH")) {
			abiScoreAdjustments[1] = 2;
			abiScoreAdjustments[0] = -2;
			special.addAll(Arrays.asList("Darkvision 60ft", "Power & Spell Resistance", "Xeph Burst 3/day"));
			autoLanguages.add("Xeph");
			bonusLanguages.addAll(Arrays.asList("Draconic", "Elven", "Gnoll", "Goblin", "Halfling", "Sylvan"));
		}
	}

}


/*
Class to store all the mechanics of a D&D 3.5 character class. 
*/
class CharacterClass {
	String className; // the name of the class
	int hitDie; // the maximum value of the class's hit die
	float baseAttackBonus; // the class's BAB progression, 1.0, 0.75, or 0.5
	boolean goodFort; // whether the class has a good save or not
	boolean goodRef;
	boolean goodWill;
	ArrayList<String> classSkills; // an array list containing names of all of the class's class skills
	ArrayList<String> prioritySkills; // an array list containing skills that are vital to the class's functionality
	HashMap<String, Integer> skillAdjust;
	int skillPointsPerLevel; // the number of skill points a member of this class gains at each level, not counting their int bonus
	int numOfLevels; //for now I'll keep this commented out and default to 20, but if prestige classes are going to be involved i'll have to deal with it
	ArrayList<ArrayList<String>> special; // an array list of array lists to store the class's special features
	Random rand = new Random();

	// spellList, spellsPerDayProgression, and spellsKnownProgression are all Arrays that store raw data about the class itself, not the character
	String[][] spellList;
	int[][] spellsPerDayProgression;
	int[][] spellsKnownProgression;

	ArrayList<Integer> spellsPerDay; // the character's number of spells per day of each level for this CharacterClass
	ArrayList<String> spellsAvailable; // the character's spells known or spells prepared for this class

	// Default constructor
	CharacterClass(String name) {
		className = name;
		numOfLevels = 20;
		special = new ArrayList<ArrayList<String>>(numOfLevels);
		for (int i=0; i<numOfLevels; i++)
			special.add(new ArrayList<String>());
		classSkills = new ArrayList<String>();
		prioritySkills = new ArrayList<String>();
		skillAdjust = new HashMap<String, Integer>();
		setSpecial(className);
		System.out.println("Character Class Created: " + className);
	}


	// Constructor for a user input class
	CharacterClass(String name, int hd, float bab, boolean fort, boolean ref, boolean will, ArrayList<String> cskills, ArrayList<String> pSkills, int sppl) {
		System.out.println("Creating character class...");
		className = name;
		hitDie = hd;
		baseAttackBonus = bab;
		goodFort = fort;
		goodRef = ref;
		goodWill = will;
		classSkills = cskills;
		prioritySkills = pSkills;
		skillPointsPerLevel = sppl;
		numOfLevels = 20;
		// i figure i should initialize all the levels and stuff to be empty, that way I can just add stuff in
		special = new ArrayList<ArrayList<String>>(numOfLevels);
		skillAdjust = new HashMap<String, Integer>();
		for(int i=0;i<numOfLevels;i++)
			special.add(new ArrayList<String>());
		// and now all the special lists for each level are empty
		setSpecial(className);
	}


	// Method to set up the base features of a class after it has been constructed
	public void setChassis(int hd, float bab, boolean fort, boolean ref, boolean will, ArrayList<String> cskills, ArrayList<String> pSkills, int sppl) {
		System.out.println("Setting up " + className + "'s chassis...");
		hitDie = hd;
		baseAttackBonus = bab;
		goodFort = fort;
		goodRef = ref;
		goodWill = will;
		classSkills = cskills;
		prioritySkills = pSkills;
		skillPointsPerLevel = sppl;
	}


	/* private void setSpecial(String clas)
	Function to set up the CharacterClass object's ArrayList of 
	special features. 
	Also sets up the chassis of the class */
	private void setSpecial(String clas) {
		if (clas.equals("BARBARIAN")) {
			hitDie = 12;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CLIMB", "CRAFT", "HANDLE ANIMAL", "INTIMIDATE", "JUMP", "LISTEN", "RIDE", "SURVIVAL", "SWIM"));
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
		else if (clas.equals("BARD")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = true;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("APPRAISE", "BALANCE", "BLUFF", "CLIMB", "CONCENTRATION", "CRAFT", "DECIPHER SCRIPT", "DIPLOMACY", "DISGUISE", "ESCAPE ARTIST", "GATHER INFORMATION", "HIDE", "JUMP", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "LISTEN", "MOVE SILENTLY", "PERFORM", "PROFESSION", "SENSE MOTIVE", "SLEIGHT OF HAND", "SPELLCRAFT", "SWIM", "TUMBLE", "USE MAGIC DEVICE"));
			String[] performChoices = new String[] {"ACT", "DANCE", "DANCE", "KEYBOARD", "ORATORY", "PERCUSSION", "STRINGS", "WIND", "SING"};
			Random r = new Random();
			int randomIndex = r.nextInt(performChoices.length);
			String priorityPerform = "PERFORM(" + performChoices[randomIndex]+")";
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
			String[] spellsLv0 = new String[] {"Dancing Lights", "Daze", "Detect Magic", "Flare", "Ghost Sound", "Know Direction", "Light", "Lullaby", "Mage Hand", "Mending", "Open/Close", "Prestidigitation", "Read Magic", "Resistance", "Summon Instrument"};
			String[] spellsLv1 = new String[] {"Alarm", "Animate Rope", "Cause Fear", "Charm Person", "Comprehend Languages", "Lesser Confusion", "Cure Light Wounds", "Detect Secret Doors", "DISGUISE Self", "Erase", "Expeditious Retreat", "Feather Fall", "Grease", "Hideous Laughter", "Hypnotism", "Identify", "Magic Mouth", "Magic Aura", "Obscure Object", "Remove Fear", "Silent Image", "Sleep", "Summon Monster I", "Undetectable Alignment", "Unseen Servant", "Ventriloquism"};
			String[] spellsLv2 = new String[] {"Alter Self", "Animal Messenger", "Animal Trance", "Blindness/Deafness", "Blur", "Calm Emotions", "Cat's Grace", "Cure Moderate Wounds", "Darkness", "Daze Monster", "Delay Poison", "Detect Thoughts", "Eagle's Splendor", "Enthrall", "Fox's Cunning", "Glitterdust", "Heroism", "Hold Person", "Hypnotic Pattern", "Invisibility", "Locate Object", "Minor Image", "Mirror Image", "Misdirection", "Pyrotechnics", "Rage", "Scare", "Shatter", "Silence", "Sound Burst", "Suggestion", "Summon Monster II", "Summon Swarm", "Tongues", "Whispering Wind"};
			String[] spellsLv3 = new String[] {"Blink", "Charm Monster", "Clairaudience/Clairvoyance", "Confusion", "Crushing Despair", "Cure Serious Wounds", "Daylight", "Deep Slumber", "Dispel Magic", "Displacement", "Fear", "Gaseous Form", "Lesser Geas", "Glibness", "Good Hope", "Haste", "Illusory Script", "Invisibility Sphere", "Major Image", "Phantom Steed", "Remove Curse", "Scrying", "Sculpt Sound", "Secret Page", "See Invisibility", "Sepia Snake Sigil", "Slow", "Speak with Animals", "Tiny Hut"};
			String[] spellsLv4 = new String[] {"Break Enchantment", "Cure Critical Wounds", "Detect Scrying", "Dimension Door", "Dominate Person", "Freedom of Movement", "Hallucinatory Terrain", "Hold Monster", "Greater Invisibility", "Legend Lore", "Locate Creature", "Modify Memory", "Neutralize Poison", "Rainbow Pattern", "Repel Vermin", "Secure Shelter", "Shadow Conjuration", "Shout", "Speak with Plants", "Summon Monster IV", "Zone of Silence"};
			String[] spellsLv5 = new String[] {"Mass Cure Light Wounds", "Greater Dispel Magic", "Dream", "False Vision", "Greater Heroism", "Mind Fog", "Mirage ARCANA", "Mislead", "Nightmare", "Persistent Image", "Seeming", "Shadow Evocation", "Shadow Walk", "Song of Discord", "Mass Suggestion", "Summon Monster V"};
			String[] spellsLv6 = new String[] {"Analyze Dweomer", "Animate Objects", "Mass Cat's Grace", "Mass Charm Monster", "Mass Cure Moderate Wounds", "Mass Eagle's Splendor", "Eyebite", "Find the Path", "Mass Fox's Cunning", "Geas/Quest", "Heroes' Feast", "Irresistable Dance", "Permanent Image", "Programmed Image", "Project Image", "Greater Scrying", "Greater Shout", "Summon Monster VI", "Sympathetic Vibration", "Veil"};
			spellList = new String[][] {spellsLv0, spellsLv1, spellsLv2, spellsLv3, spellsLv4, spellsLv5, spellsLv6};

			spellsPerDayProgression = new int[][] {{2}, {3, 0}, {3, 1}, {3, 2, 0}, {3, 3, 1}, {3, 3, 2}, {3, 3, 2, 0}, {3, 3, 3, 1}, {3, 3, 3, 2}, {3, 3, 3, 2, 0}, {3, 3, 3, 3, 1}, {3, 3, 3, 3, 2}, {3, 3, 3, 3, 2, 0}, {4, 3, 3, 3, 3, 1}, {4, 4, 3, 3, 3, 2}, {4, 4, 4, 3, 3, 2, 0}, {4, 4, 4, 4, 3, 3, 1}, {4, 4, 4, 4, 4, 3, 2}, {4, 4, 4, 4, 4, 4, 3}, {4, 4, 4, 4, 4, 4, 4}};
			spellsKnownProgression = new int[][] {{4}, {5, 2}, {6, 3}, {6, 3, 2}, {6, 4, 3}, {6, 4, 3}, {6, 4, 4, 2}, {6, 4, 4, 3}, {6, 4, 4, 3}, {6, 4, 4, 4, 2}, {6, 4, 4, 4, 3}, {6, 4, 4, 4, 3}, {6, 4, 4, 4, 4, 2}, {6, 4, 4, 4, 4, 3}, {6, 4, 4, 4, 4, 3}, {6, 5, 4, 4, 4, 4, 2}, {6, 5, 5, 4, 4, 4, 3}, {6, 5, 5, 5, 4, 4, 3}, {6, 5, 5, 5, 5, 4, 4}, {6, 5, 5, 5, 5, 5, 4}};
		
		}
		else if (clas.equals("CLERIC")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "DIPLOMACY", "HEAL", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "PROFESSION", "SPELLCRAFT"));
			prioritySkills.add("KNOWLEDGE(RELIGION)");
			skillPointsPerLevel = 2;
			special.get(0).add("Turn or Rebuke Undead");
			String[] spellsLv0 = new String[] {"Create Water", "Cure Minor Wounds", "Detect Magic", "Detect Poison", "Guidance", "Inflict Minor Wounds", "Light", "Mending", "Purify Food and Drink", "Read Magic", "Resistance", "Virtue"};
			String[] spellsLv1 = new String[] {"Bane", "Bless", "Bless Water", "Cause Fear", "Command", "Comprehend Languages", "Cure Light Wounds", "Curse Water", "Deathwatch", "Detect Chaos/Evil/Good/Law", "Detect Undead", "Divine Favor", "Doom", "Endure Elements", "Entropic Shield", "HIDE from Undead", "Inflict Light Wounds", "Magic Stone", "Magic Weapon", "Obscuring Mist", "Protection from Chaos/Evil/Good/Law", "Remove Fear", "Sanctuary", "Shield of Faith", "Summon Monster I"};
			String[] spellsLv2 = new String[] {"Aid", "Align Weapon", "Augury", "Bear's Endurance", "Bull's Strength", "Calm Emotions", "Consecrate", "Cure Moderate Wounds", "Darkness", "Death Knell", "Delay Poison", "Desecrate", "Eagle's Splendor", "Enthrall", "Find Traps", "Gentle Repose", "Hold Person", "Inflict Moderate Wounds", "Make Whole", "Owl's Wisdom", "Remove Paralysis", "Resist Energy", "Lesser Restoration", "Shatter", "Shield Other", "Silence", "Sound Burst", "Spiritual Weapon", "Status", "Summon Monster II", "Undetectable Alignment", "Zone of Truth"};
			String[] spellsLv3 = new String[] {"Animate Dead", "Bestow Curse", "Blindness/Deafness", "Contagion", "Continual Flame", "Create Food and Water", "Cure Serious Wounds", "Daylight", "Deeper Darkness", "Dispel Magic", "Glyph of Warding", "Helping Hand", "Inflict Serious Wounds", "Invisibility Purge", "Locate Object", "Magic Circle Against Chaos/Evil/Good/Law", "Magic Vestment", "Meld into Stone", "Obscure Object", "Prayer", "Protection from Energy", "Remove Blindness/Deafness", "Remove Curse", "Remove Disease", "Searing Light", "Speak with Dead", "Stone Shape", "Summon Monster III", "Water Breathing", "Water Walk", "Wind Wall"};
			String[] spellsLv4 = new String[] {"Air Walk", "Control Water", "Cure Critical Wounds", "Death Ward", "Dimensional Anchor", "Discern Lies", "Dismissal", "Divination", "Divine Power", "Freedom of Movement", "Giant Vermin", "Imbue with Spell Ability", "Inflict Critical Wounds", "Greater Magic Weapon", "Neutralize Poison", "Lesser Planar Ally", "Poison", "Repel Vermin", "Restoration", "Sending", "Spell Immunity", "Summon Monster IV", "Tongues"};
			String[] spellsLv5 = new String[] {"Atonement", "Break Enchantment", "Greater Command", "Commune", "Mass Cure Light Wounds", "Dispel Chaos/Evil/Good/Law", "Disrupting Weapon", "Flame Strike", "Hallow", "Mass Inflict Light Wounds", "Insect Plague", "Mark of Justice", "Plane Shift", "Raise Dead", "Righteous Might", "Scrying", "Slay Living", "Spell Resistance", "Summon Monster V", "Symbol of Pain", "Symbol of Sleep", "True Seeing", "Unhallow", "Wall of Stone"};
			String[] spellsLv6 = new String[] {"Animate Objects", "Antilife Shell", "Banishment", "Mass Bear's Endurance", "Blade Barrier", "Mass Bull's Strength", "Create Undead", "Mass Cure Moderate Wounds", "Greater Dispel Magic", "Mass Eagle's Splendor", "Find the Path", "Forbiddance", "Geas/Quest", "Greater Glyph of Warding", "Harm", "HEAL", "Heroes' Feast", "Mass Inflict Moderate Wounds", "Mass Owl's Wisdom", "Planar Ally", "Summon Monster VI", "Symbol of Fear", "Symbol of Persuasion", "Undeath to Death", "Wind Walk", "Word of Recall"};
			String[] spellsLv7 = new String[] {"Blasphemy", "Control Weather", "Mass Cure Serious Wounds", "Destruction", "Dictum", "Ethereal Jaunt", "Holy Word", "Mass Inflict Serious Wounds", "Refuge", "Regenerate", "Repulsion", "Greater Restoration", "Resurrection", "Greater Scrying", "Summon Monster VII", "Symbol of Stunning", "Symbol of Weaknes", "Word of Chaos"};
			String[] spellsLv8 = new String[] {"Antimagic Field", "Cloak of Chaos", "Create Greater Undead", "Mass Cure Critical Wounds", "Dimensional Lock", "Discern Location", "Earthquake", "Fire Storm", "Holy Aura", "Mass Inflict Critical Wounds", "Greater Planar Ally", "Shield of Law", "Greater Spell Immunity", "Summon Monster VIII", "Symbol of Death", "Symbol of Insanity", "Unholy Aura"};
			String[] spellsLv9 = new String[] {"Astral Projection", "Energy Drain", "Etherealness", "Gate", "Mass HEAL", "Implosion", "Miracle", "Soul Bind", "Storm of Vengeance", "Summon Monster IX", "True Resurrection"};
			spellList = new String[][] {spellsLv0, spellsLv1, spellsLv2, spellsLv3, spellsLv4, spellsLv5, spellsLv6, spellsLv7, spellsLv8, spellsLv9};


		}
		else if (clas.equals("DRUID")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "DIPLOMACY", "HANDLE ANIMAL", "HEAL", "KNOWLEDGE(NATURE)", "LISTEN", "PROFESSION", "RIDE", "SPELLCRAFT", "SPOT", "SURVIVAL", "SWIM"));
			prioritySkills.add("KNOWLEDGE(NATURE)");
			skillPointsPerLevel = 4;
			special.get(0).add("Animal Companion");
			special.get(0).add("NATURE Sense");
			special.get(0).add("Wild Empathy");
			special.get(1).add("Woodland Stride");
			special.get(2).add("Trackless Step");
			special.get(3).add("Resist NATURE's Lure");
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
		else if (clas.equals("FIGHTER")) {
			hitDie = 10;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CLIMB", "CRAFT", "HANDLE ANIMAL", "INTIMIDATE", "JUMP", "RIDE", "SWIM"));
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
		else if (clas.equals("MONK")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = true;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("BALANCE", "CLIMB", "CONCENTRATION", "CRAFT", "DIPLOMACY", "ESCAPE ARTIST", "HIDE", "JUMP", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(RELIGION)", "LISTEN", "MOVE SILENTLY", "PERFORM", "PROFESSION", "SENSE MOTIVE", "SPOT", "SWIM", "TUMBLE"));
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
		else if (clas.equals("PALADIN")) {
			hitDie = 10;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "DIPLOMACY", "HANDLE ANIMAL", "HEAL", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "PROFESSION", "RIDE", "SENSE MOTIVE"));
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
		else if (clas.equals("PSION")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "PROFESSION", "PSICRAFT"));
			skillPointsPerLevel = 2;
			System.out.println("Initializing discChoices...");
			String[] discChoices = new String[] {"Clairsentience", "Metacreativiy", "Psychokinesis", "Psychometabolism", "Psychoportation", "Telepathy"};
			System.out.println("discChoices initialized! : " + discChoices);
			System.out.println("Choosing discipline...");
			int randomIndex = rand.nextInt(6);
			System.out.println("Random index chosen: " + randomIndex);

			String discChoice = discChoices[randomIndex];
			System.out.println("Discipline chosen: " + discChoice);

			special.get(0).add("Discipline: "+discChoice);
			special.get(0).add("Bonus Psion Feat");
			special.get(4).add("Bonus Psion Feat");
			special.get(9).add("Bonus Psion Feat");
			special.get(14).add("Bonus Psion Feat");
			special.get(19).add("Bonus Psion Feat");
		}
		else if (clas.equals("PSYCHIC WARRIOR")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("AUTOHYPNOSIS", "CLIMB", "CONCENTRATION", "CRAFT", "JUMP", "KNOWLEDGE(PSIONICS)", "PROFESSION", "RIDE", "SEARCH", "SWIM"));
			skillPointsPerLevel = 2;
			special.get(0).add("Bonus Psychic Warrior Feat");
			special.get(1).add("Bonus Psychic Warrior Feat");
			special.get(4).add("Bonus Psychic Warrior Feat");
			special.get(7).add("Bonus Psychic Warrior Feat");
			special.get(10).add("Bonus Psychic Warrior Feat");
			special.get(13).add("Bonus Psychic Warrior Feat");
			special.get(16).add("Bonus Psychic Warrior Feat");
			special.get(19).add("Bonus Psychic Warrior Feat");
		}
		else if (clas.equals("RANGER")) {
			hitDie = 8;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = true;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CLIMB", "CONCENTRATION", "CRAFT", "HANDLE ANIMAL", "HEAL", "HIDE", "JUMP", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(NATURE)", "LISTEN", "MOVE SILENTLY", "PROFESSION", "RIDE", "SEARCH", "SPOT", "SURVIVAL", "SWIM", "USE ROPE"));
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
			special.get(16).add("HIDE in Plain Sight");
			special.get(19).add("5th Favored Enemy");
		}
		else if (clas.equals("ROGUE")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = true;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("APPRAISE", "BALANCE", "BLUFF", "CLIMB", "CRAFT", "DECIPHER SCRIPT", "DIPLOMACY", "DISABLE DEVICE", "DISGUISE", "ESCAPE ARTIST", "FORGERY", "GATHER INFORMATION", "HIDE", "INTIMIDATE", "JUMP", "KNOWLEDGE(LOCAL)", "LISTEN", "MOVE SILENTLY", "OPEN LOCK", "PERFORM", "PROFESSION", "SEARCH", "SENSE MOTIVE", "SLEIGHT OF HAND", "SPOT", "SWIM", "TUMBLE", "USE MAGIC DEVICE", "USE ROPE"));
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
			ArrayList<String> specialAbilities = new ArrayList<String>(Arrays.asList("Crippling Strike", "Defensive Roll", "Improved Evasion", "Opportunist", "Skill Mastery", "Slippery Mind"));

			//special.get(9).add(specialAbilities.get(rand.nextInt(specialAbilities.size())));
			special.get(10).add("Sneak Attack +6d6");
			special.get(11).add("Trap Sense +4");
			special.get(12).add("Sneak Attack +7d6");

			//special.get(12).add(specialAbilities.get(rand.nextInt(specialAbilities.size())));
			special.get(14).add("Sneak Attack +8d6");
			special.get(14).add("Trap Sense +5");

			//special.get(15).add(specialAbilities.get(rand.nextInt(specialAbilities.size())));
			special.get(16).add("Sneak Attack +9d6");
			special.get(17).add("Trap Sense +6");
			special.get(18).add("Sneak Attack +10d6");
			
			//special.get(18).add(specialAbilities.get(rand.nextInt(specialAbilities.size())));
		}
		else if (clas.equals("SORCERER")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("BLUFF", "CONCENTRATION", "CRAFT", "KNOWLEDGE(ARCANA)", "PROFESSION", "SPELLCRAFT"));
			skillPointsPerLevel = 2;
			special.get(0).add("Summon Familiar");
		}
		else if (clas.equals("SOULKNIFE)")) {
			hitDie = 10;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = true;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("AUTOHYPNOSIS", "CLIMB", "CONCENTRATION", "CRAFT", "HIDE", "JUMP", "KNOWLEDGE(PSIONICS)", "LISTEN", "MOVE SILENTLY", "PROFESSION", "TUMBLE"));
			skillPointsPerLevel = 4;
			special.get(0).addAll(Arrays.asList("Mind Blade", "Weapon Focus(Mind Blade)", "Wild Talent"));
			special.get(1).add("Throw Mind Blade");
			special.get(2).add("Psychic Strike +1d8");
			special.get(3).add("+1 Mind Blade");
			special.get(4).addAll(Arrays.asList("Free Draw", "Shape Mind Blade"));
			special.get(5).addAll(Arrays.asList("Mind Blade Enhancement +1", "Speed of Thought"));
			special.get(6).add("Psychic Strike +2d8");
			special.get(7).add("+2 Mind Blade");
			special.get(8).addAll(Arrays.asList("Bladewind", "Greater Weapon Focus"));
			special.get(9).add("Mind Blade Enhancement +2");
			special.get(10).add("Psychic Strike +3d8");
			special.get(11).add("+3 Mind Blade");
			special.get(12).add("Knife to the Soul");
			special.get(13).add("Mind Blade Enhancement +3");
			special.get(14).add("Psychic Strike +4d8");
			special.get(15).add("+4 Mind Blade");
			special.get(16).add("Multiple Throw");
			special.get(17).add("Mind Blade Enhancement +4");
			special.get(18).add("Psychic Strike +5d8");
			special.get(19).add("+5 Mind Blade");
		}
		else if (clas.equals("WILDER")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("AUTOHYPNOSIS", "BALANCE", "BLUFF", "Cimb", "CONCENTRATION", "CRAFT", "ESCAPE ARTIST", "INTIMIDATE", "JUMP", "KNOWLEDGE(PSIONICS)", "LISTEN", "PROFESSION", "PSICRAFT", "SENSE MOTIVE", "SPOT", "SWIM", "TUMBLE"));
			skillPointsPerLevel = 4;
			special.get(0).addAll(Arrays.asList("Wild Surge +1", "Psychic Enervation"));
			special.get(1).add("Elude Touch");
			special.get(2).add("Wild Surge +2");
			special.get(3).add("Surging Euphoria +1");
			special.get(4).add("Volatile Mind (1 PP)");
			special.get(6).add("Wild Surge +3");
			special.get(8).add("Volatile Mind (2 PP)");
			special.get(10).add("Wild Surge +4");
			special.get(11).add("Surging Euphoria +2");
			special.get(12).add("Volatile Mind (3 PP)");
			special.get(14).add("Wild Surge +5");
			special.get(16).add("Volatile Mind (4 PP)");
			special.get(18).add("Wild Surge +6");
			special.get(19).add("Surging Euphoria +3");
		}
		else if (clas.equals("WIZARD")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "DECIPHER SCRIPT", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "PROFESSION", "SPELLCRAFT"));
			prioritySkills.add("KNOWLEDGE(ARCANA)");
			skillPointsPerLevel = 2;
			special.get(0).add("Summon Familiar");
			special.get(0).add("Scribe Scroll");
			special.get(4).add("Bonus Wizard Feat");
			special.get(9).add("Bonus Wizard Feat");
			special.get(14).add("Bonus Wizard Feat");
			special.get(19).add("Bonus Wizard Feat");
		}
		else if (clas.equals("ADEPT")) {
			hitDie = 6;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("CONCENTRATION", "CRAFT", "HANDLE ANIMAL", "HEAL", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "PROFESSION", "SPELLCRAFT", "SURVIVAL"));
			skillPointsPerLevel = 2;
			special.get(1).add("Summon Familiar");
		}
		else if (clas.equals("ARISTOCRAT")) {
			hitDie = 8;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = false;
			goodWill = true;
			classSkills = new ArrayList<String>(Arrays.asList("APPRAISE", "BLUFF", "DIPLOMACY", "DISGUISE", "FORGERY", "GATHER INFORMATION", "HANDLE ANIMAL", "INTIMIDATE", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "LISTEN", "PERFORM", "RIDE", "SENSE MOTIVE", "SPOT", "SWIM", "SURVIVAL"));
			skillPointsPerLevel = 4;
		}
		else if (clas.equals("COMMONER")) {
			hitDie = 4;
			baseAttackBonus = 0.5f;
			goodFort = false;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CLIMB", "CRAFT", "HANDLE ANIMAL", "JUMP", "LISTEN", "PROFESSION", "RIDE", "SPOT", "SWIM", "USE ROPE"));
			skillPointsPerLevel = 2;
		}
		else if (clas.equals("EXPERT")) {
			hitDie = 6;
			baseAttackBonus = 0.75f;
			goodFort = false;
			goodRef = false;
			goodWill = false;
			// have to choose 10 skills out of all of them. at random, i guess. so first i'll need an array with each of them. 
			String[] allTheSkills = {"APPRAISE", "AUTOHYPNOSIS", "BALANCE", "BLUFF", "CLIMB", "CONCENTRATION", "CRAFT", "DECIPHER SCRIPT", "DIPLOMACY", "DISABLE DEVICE", "DISGUISE", "ESCAPE ARTIST", "FORGERY", "GATHER INFORMATION", "HANDLE ANIMAL", "HEAL", "HIDE", "INTIMIDATE", "KNOWLEDGE(ARCANA)", "KNOWLEDGE(ARCHITECTURE & ENGINEERING)", "KNOWLEDGE(DUNGEONEERING)", "KNOWLEDGE(GEOGRAPHY)", "KNOWLEDGE(HISTORY)", "KNOWLEDGE(LOCAL)", "KNOWLEDGE(NATURE)", "KNOWLEDGE(NOBILITY & ROYALTY)", "KNOWLEDGE(RELIGION)", "KNOWLEDGE(THE PLANES)", "KNOWLEDGE(PSIONICS)", "LISTEN", "MOVE SILENTLY", "OPEN LOCK", "PERFORM", "PROFESSION", "PSICRAFT", "RIDE", "SEARCH", "SENSE MOTIVE", "SLEIGHT OF HAND", "SPELLCRAFT", "SPOT", "SURVIVAL", "SWIM", "TUMBLE", "USE MAGIC DEVICE", "USE PSIONIC DEVICE", "USE ROPE"};
			classSkills = new ArrayList<String>();
			while (classSkills.size() < 10) { // and as long as we have fewer than 10 elements we will keep searching
				String skillToAdd = allTheSkills[rand.nextInt(allTheSkills.length)];
				if (!classSkills.contains(skillToAdd)) { // check to make sure the skill is not already in the array list
					classSkills.add(skillToAdd);
				}
			}
			skillPointsPerLevel = 6;
		}
		else if (clas.equals("WARRIOR")) {
			hitDie = 8;
			baseAttackBonus = 1.0f;
			goodFort = true;
			goodRef = false;
			goodWill = false;
			classSkills = new ArrayList<String>(Arrays.asList("CLIMB", "HANDLE ANIMAL", "INTIMIDATE", "JUMP", "RIDE", "SWIM"));
			skillPointsPerLevel = 2;
		}
	}

	public String getRandom(String[] array) {
		return array[rand.nextInt(array.length)];
	}

	public void printClass() {
		System.out.println("Character Class Name: " + className);
		System.out.println("Special:");
		System.out.println(special);
	}
}

