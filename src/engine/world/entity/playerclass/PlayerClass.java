package engine.world.entity.playerclass;

import engine.world.entity.action.Spellbook;

public class PlayerClass {
	
	public static final int MAX_LEVEL = 60;
	public static final int MIN_BASELINE_DAMAGE = 10;
	public static final int MIN_EXP = 0;
	public static final int MIN_LEVEL = 1;
	
	//Class ID
	public static final int ID_CLASS_WARRIOR = 1;
	public static final int ID_CLASS_NATURALIST = 2;
	public static final int ID_CLASS_WIZARD = 3;
	
	protected int id;
	
	//progress attributes
	protected int experience = MIN_EXP;
	protected int currentLevel = MIN_LEVEL;
	
	protected int baselineDamage = MIN_BASELINE_DAMAGE;
	
	//damage attributes (power of atk, efficiency (speed), crit (critical hit chance))
	protected float power, efficiency, crit;
	
	//spellbook
	protected Spellbook spellbook = new Spellbook();
	
	//defensive attributes
	protected float meleeResistance, rangedResistance, magicResistance;
	
	public PlayerClass(float power, float efficiency, float crit, float meleeResistance, float rangedResistance, float magicResistance) {
		this.power = power;
		this.efficiency = efficiency;
		this.crit = crit;
		
		this.meleeResistance = meleeResistance;
		this.rangedResistance = rangedResistance;
		this.magicResistance = magicResistance;
		
		calculateBaselineDamage();
	}
	
	public void calculateBaselineDamage() {
		baselineDamage = (int) (Math.pow(baselineDamage, 0.7 * Math.pow(currentLevel, 0.1)) * currentLevel);
	}
	
	public void setPower(float power) {
		this.power = power;
	}
	
	public void setEfficiency(float efficiency) {
		this.efficiency = efficiency;
	}
	
	public void setCrit(float crit) {
		this.crit = crit;
	}
	
	public void setMeleeResistance(float meleeResistance) {
		this.meleeResistance = meleeResistance;
	}
	
	public void setRangedResistance(float rangedResistance) {
		this.rangedResistance = rangedResistance;
	}
	
	public void setMagicResistance(float magicResistance) {
		this.magicResistance = magicResistance;
	}
	
	public void addExperience(int expAmount) {
		experience += expAmount;
	}
	
}
