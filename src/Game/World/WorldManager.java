package Game.World;

import Game.Entities.Dynamic.Player;
import Game.Entities.Static.LillyPad;
import Game.Entities.Static.Log;
import Game.Entities.Static.StaticBase;
import Game.Entities.Static.Tree;
import Game.Entities.Static.Turtle;
import Main.Handler;
import UI.UIManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Literally the world. This class is very important to understand.
 * Here we spawn our hazards (StaticBase), and our tiles (BaseArea)
 * 
 * We move the screen, the player, and some hazards. 
 * 				How? Figure it out.
 */
public class WorldManager {
	boolean YLilly;											// boolean to prevent LillyPads from Spawning consecutively.
	private ArrayList<BaseArea> AreasAvailables;			// Lake, empty and grass area (NOTE: The empty tile is just the "sand" tile. Ik, weird name.)
	private ArrayList<StaticBase> StaticEntitiesAvailables;	// Has the hazards: LillyPad, Log, Tree, and Turtle.

	private ArrayList<BaseArea> SpawnedAreas;				// Areas currently on world
	private ArrayList<StaticBase> SpawnedHazards;			// Hazards currently on world.

	Long time;
	Boolean reset = true;


	Handler handler;


	private Player player;									// How do we find the frog coordinates? How do we find the Collisions? This bad boy.

	UIManager object = new UIManager(handler);
	UI.UIManager.Vector object2 = object.new Vector();


	private ID[][] grid;									
	private int gridWidth,gridHeight;						// Size of the grid. 
	private int movementSpeed;		// Movement of the tiles going downwards.
	private int pushB= 15;


	public WorldManager(Handler handler) {
		this.handler = handler;

		AreasAvailables = new ArrayList<>();				// Here we add the Tiles to be utilized.
		StaticEntitiesAvailables = new ArrayList<>();		// Here we add the Hazards to be utilized.

		AreasAvailables.add(new GrassArea(handler, 0));		
		AreasAvailables.add(new WaterArea(handler, 0));
		AreasAvailables.add(new EmptyArea(handler, 0));

		StaticEntitiesAvailables.add(new LillyPad(handler, 0, 0));
		StaticEntitiesAvailables.add(new Log(handler, 0, 0));
		StaticEntitiesAvailables.add(new Tree(handler,0,0));
		StaticEntitiesAvailables.add(new Turtle(handler, 0, 0));

		SpawnedAreas = new ArrayList<>();
		SpawnedHazards = new ArrayList<>();

		player = new Player(handler);       

		gridWidth = handler.getWidth()/64;
		gridHeight = handler.getHeight()/64;
		movementSpeed = 1;

		//movementSpeed = 20; //I dare you.

		/* 
		 * 	Spawn Areas in Map (2 extra areas spawned off screen)
		 *  To understand this, go down to randomArea(int yPosition) 
		 */
		int Grass=0;									//"for loop" and "if" statement to prevent Frogger to spawn on water.
		for(int i=0; i<gridHeight+2; i++) {
			if(Grass<11) { 									
				SpawnedAreas.add(randomArea((-2+i)*64));
				Grass++;
			}
			else {
				SpawnedAreas.add(new GrassArea(handler,(-2+i)*64));
			}
		}


		player.setX((gridWidth/2)*64);
		player.setY((gridHeight-2)*64);



		// Not used at the time.
		grid = new ID[gridWidth][gridHeight];
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				grid[x][y]=ID.EMPTY;
			}
		}
	}


	public void tick() {

		//grid range here
		if(player.getX()-1<=0) {
			player.setX(player.getX()+10);
		}
		if(player.getX()+1>=576) {
			player.setX(player.getX()-10);
		}
		if(player.getY()-1<=10) {
			player.setY(player.getY()+25);
		}
		if(player.getY()+1>=768) {      // this is added while the game over state is being created-here
			player.setY(player.getY()-10);}








		if(this.player.getY()> this.handler.getHeight()) {
			movementSpeed=0;
		}
		if(this.player.getY()<= this.handler.getHeight()) {
			movementSpeed=1;
		}
		if(this.player.getX()> this.handler.getWidth()) {
			this.player.setX(this.player.getX()-40);
		}
		if(this.player.getX()<0) {
			this.player.setX(this.player.getX()+20);
		}



		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[2])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[1];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[0])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[2];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[1])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[0];
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[3])) {
			this.object2.addVectors();
		}
		if(this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[4]) && this.object2.isUIInstance) {
			this.object2.scalarProduct(handler);
		}

		if(this.reset) {
			time = System.currentTimeMillis();
			this.reset = false;
		}

		if(this.object2.isSorted) {

			if(System.currentTimeMillis() - this.time >= 2000) {		
				this.object2.setOnScreen(true);	
				this.reset = true;
			}

		}

		for (BaseArea area : SpawnedAreas) {
			area.tick();
		}
		for (StaticBase hazard : SpawnedHazards) {
			hazard.tick();
		}



		for (int i = 0; i < SpawnedAreas.size(); i++) {
			SpawnedAreas.get(i).setYPosition(SpawnedAreas.get(i).getYPosition() + movementSpeed);

			// Check if Area (thus a hazard as well) passed the screen.
			if (SpawnedAreas.get(i).getYPosition() > handler.getHeight()) {
				// Replace with a new random area and position it on top
				SpawnedAreas.set(i, randomArea(-2 * 64));
			}
			//Make sure players position is synchronized with area's movement
			if (SpawnedAreas.get(i).getYPosition() < player.getY()
					&& player.getY() - SpawnedAreas.get(i).getYPosition() < 3) {
				player.setY(SpawnedAreas.get(i).getYPosition());
			}
		}

		HazardMovement();

		player.tick();
		//make player move the same as the areas
		player.setY(player.getY()+movementSpeed); 

		object2.tick();

	}

	private void HazardMovement() {

		for (int i = 0; i < SpawnedHazards.size(); i++) {

			// Moves hazard down
			SpawnedHazards.get(i).setY(SpawnedHazards.get(i).getY() + movementSpeed);

			if(SpawnedHazards.get(i) instanceof Tree)	{

				if(SpawnedHazards.get(i).GetCollision() != null
						&&  player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {

					String facing= player.getFacing();

					switch(facing) {
					case "UP": player.setY(player.getY()+15);
					break;
					case "DOWN": player.setY(player.getY()-15);
					break;
					case "LEFT": player.setX(player.getX()+15);
					break;
					case "RIGHT": player.setX(player.getX()-15);
					break;
					default: break;	
					}

				}
			}
			//Log left to right
			if (SpawnedHazards.get(i) instanceof Log) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() +1);
				// Verifies the hazards Rectangles aren't null and
				// If the player Rectangle intersects with the Log or Turtle Rectangle, then
				// move player to the right.
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {
					player.setX(player.getX() +1);
				}
			}

			// Moves Turtle to the left
			if (SpawnedHazards.get(i) instanceof Turtle) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() -1);

				// Verifies the hazards Rectangles aren't null and
				// If the player Rectangle intersects with the Log or Turtle Rectangle, then
				// move player to the right.
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {
					player.setX(player.getX() - 1);
				}
			}

			// if hazard has passed the screen height, then remove this hazard.
			if (SpawnedHazards.get(i).getY() > handler.getHeight()) {
				SpawnedHazards.remove(i);
			}


		}
	}


	public void render(Graphics g){

		for(BaseArea area : SpawnedAreas) {
			area.render(g);
		}

		for (StaticBase hazards : SpawnedHazards) {
			hazards.render(g);

		}

		player.render(g);       
		this.object2.render(g);      

	}

	/*
	 * Given a yPosition, this method will return a random Area out of the Available ones.)
	 * It is also in charge of spawning hazards at a specific condition.
	 */
	private BaseArea randomArea(int yPosition) {
		Random rand = new Random();
		int treeloop= rand.nextInt(4)+1;			//Enables the spawn of multiple trees in the same yPosition
		// From the AreasAvailable, get me any random one.
		BaseArea randomArea = AreasAvailables.get(rand.nextInt(AreasAvailables.size())); 

		if(randomArea instanceof GrassArea) {
			randomArea = new GrassArea(handler, yPosition);
			for(int i=0;i<treeloop;i++) {
				SpawnedHazards.add(new Tree(handler,64 *rand.nextInt(9), yPosition));
			}


		}
		else if(randomArea instanceof WaterArea) {
			randomArea = new WaterArea(handler, yPosition);
			SpawnHazard(yPosition);
		}
		else {
			randomArea = new EmptyArea(handler, yPosition);
			//SpawnedHazards.add(new Tree(handler,64 *rand.nextInt(9), yPosition));
		}
		return randomArea;
	}

	/*
	 * Given a yPositionm this method will add a new hazard to the SpawnedHazards ArrayList
	 */
	private void SpawnHazard(int yPosition) {
		Random rand = new Random();
		int randInt;
		int choice = rand.nextInt(7);
		int lillyLoop= rand.nextInt(8) + 1;		//Variable to spawn more than one LillyPad per YPosition
		int logLoop= rand.nextInt(4) + 1;
		int turtLoop= rand.nextInt(4)+1;
		int turtOrLog= rand.nextInt(1);			//Variable to choose if to spawn Log or Turtle
		int notOverlap=0;						//Prevents overlapping of SpawnedHazards on same level
		int xLevel=0;
		
		if (choice <=2) {
			//Spawns more than 1 Log in the same YPosition
			for(int i=0;i<logLoop;i++) {
				SpawnedHazards.add(new Log(handler, notOverlap, yPosition));
				notOverlap+=128;
			}
			YLilly=false;
		}
		else if(choice>=5){ 
			for(int i=0;i<turtLoop;i++) {
				randInt = 576 - notOverlap;
				SpawnedHazards.add(new Turtle(handler, randInt, yPosition));
				notOverlap+=80;

			}
			YLilly=false;
		}
		else {
			if(YLilly) {
				if(turtOrLog==1) {
					for(int i=0;i<logLoop;i++) {
						SpawnedHazards.add(new Log(handler, notOverlap, yPosition));
						notOverlap+=128;
					}
					YLilly=false;
				}
				else {
					for(int i=0;i<turtLoop;i++) {
						randInt = 576 - notOverlap;
						SpawnedHazards.add(new Turtle(handler, randInt, yPosition));
						notOverlap+=80;

					}
					YLilly=false;
				}

			}
			else {
				//Spawns more than 1 LillyPad in the same YPosition
				for(int i=0;i<=lillyLoop;i++) {
					
					randInt = 64 * rand.nextInt(10);
					if(randInt==xLevel) {					//Prevents Overlapping of LillyPads
						randInt = 64 * rand.nextInt(10);
					}
					else {
					SpawnedHazards.add(new LillyPad(handler, randInt, yPosition));
					xLevel=randInt;
					}
				}
				YLilly=true;
			}
		}
	}
}





