package engine.world.tile;

public class LightTile extends Tile {

	protected float luminosity;
	protected int radius;
	
	/**
	 * Constructs a tile that acts as a light-emitter
	 * @param id
	 * @param luminosity (intensity of light)
	 * @param radius (units are per tile, i.e. 8 normalized pixels)
	 */
	public LightTile(int id, float luminosity, int radius) {
		super(id);
		
		this.luminosity = luminosity;
		this.radius = radius;
	}
	
	public float getLuminosity() {
		return luminosity;
	}
	
	public int getRadius() {
		return radius;
	}

}
