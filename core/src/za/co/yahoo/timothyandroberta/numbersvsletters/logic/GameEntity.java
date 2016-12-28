package za.co.yahoo.timothyandroberta.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class GameEntity {
	
	public World world;
	public String name;
	public Vector2 position;
	
	public GameEntity(World world, String name, Vector2 position) {
		this.world = world;
		this.name = name;
		this.position = position;
	}
	
	public void setUpBox2D() {
		
	}
}
