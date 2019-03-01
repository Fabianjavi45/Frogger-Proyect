package Game.Entities.Dynamic;

import Game.Entities.EntityBase;
import Game.World.WorldManager;
import Main.Handler;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/*
 * The Frog.
 */
public class Player extends EntityBase {
	private Handler handler;

	private Rectangle player;
	private String facing = "UP";
	private Boolean moving = false;
	private int moveCoolDown=0;
	private static  int score=0;
	private Rectangle lastLocation1;
	private boolean spawned= false;

	private int index =0;

	public Player(Handler handler) {
		super(handler);
		this.handler = handler;
		this.handler.getEntityManager().getEntityList().add(this);

		player = new Rectangle(); 	// see UpdatePlayerRectangle(Graphics g) for its usage.
	}

	public void tick(){

		if(moving) {
			animateMovement();
		}

		if(!moving){
			move();
		}
		//maxScore--;
		if(spawned==true){
			lastLocation1=new Rectangle((int) lastLocation1.getX(), (int)lastLocation1.getY()+handler.getWorld().getMovementSpeed(), (int)lastLocation1.getWidth(),(int)lastLocation1.getHeight());
		}
	}

	private void reGrid() {
		if(facing.equals("UP")) {
			if(this.getX() % 64 >= 64 / 2 ) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			}
			else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY()-64);
		}
	}

	private void move(){
		if(moveCoolDown< 25){
			moveCoolDown++;
		}
		index=0;

		/////////////////MOVE UP///////////////
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && facing.equals("UP")){
			moving=true;
			//lastLocation1=new Rectangle(300, this.getY()-200, getWidth(), getHeight());
			updateScore();
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && !facing.equals("UP")){
			if(facing.equals("DOWN")) {
				if(this.getX() % 64 >= 64 / 2 ) {

					this.setX(this.getX() + (64 - this.getX() % 64));
				}
				else {
					this.setX(this.getX() - this.getX() % 64);
				}
				setY(getY() + 64);
			}
			if(facing.equals("LEFT")) {
				setY(getY() + 64);
			}
			if(facing.equals("RIGHT")) {
				setX(getX()-64);
				setY(getY()+64);
			}
			facing = "UP";
		}

		/////////////////MOVE LEFT///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving && facing.equals("LEFT")){
			moving=true;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving&& !facing.equals("LEFT")){
			if(facing.equals("RIGHT")) {
				setX(getX()-64);
			}
			reGrid();
			facing = "LEFT";
		}

		/////////////////MOVE DOWN///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && facing.equals("DOWN")){
			moving=true;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && !facing.equals("DOWN")){
			reGrid();
			if(facing.equals("RIGHT")){
				setX(getX()-64);
			}
			facing = "DOWN";
		}

		/////////////////MOVE RIGHT///////////////
		else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving && facing.equals("RIGHT")){
			moving=true;
		}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving&& !facing.equals("RIGHT")){
			if(facing.equals("LEFT")) {
				setX(getX()+64);
			}
			if(facing.equals("UP")) {
				setX(getX()+64);
				setY(getY()-64);
			}
			if(facing.equals("DOWN")) {
				if(this.getX() % 64 >= 64 / 2 ) {
					this.setX(this.getX() + (64 - this.getX() % 64));
				}
				else {
					this.setX(this.getX() - this.getX() % 64);
				}
				setX(getX()+64);
			}
			facing = "RIGHT";
		}
	}

	private void animateMovement(){
		if(index==8) {
			moving = false;
			index = 0;
		}
		moveCoolDown = 0;
		index++;
		switch (facing) {
		case "UP":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() - (8));
			break;

		case "LEFT":
			setX(getX() - (8));
			break;

		case "DOWN":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() + (8));
			break;

		case "RIGHT":
			setX(getX() + (8));
			break;

		}
	}
	
	
	
	public void updateScore() {
		
		
		
		if(spawned==false) {
			spawned= true;
			lastLocation1= player;
		}
		//lastLocation1=new Rectangle(300, (int)player.getY(), getWidth(), getHeight());
		if(this.getY()<= lastLocation1.getY()) {
			//lastLocation1=new Rectangle(300, (int)player.getY(), getWidth(), getHeight());
			lastLocation1=player;
			score+=100;
		}
		return;
		
	}

	public void render(Graphics g){

		String fullscore=Integer.toString(score);

		g.setColor(Color.black);
		
		g.drawRect(30, 8, 100, 35);
		g.fillRect(30, 8, 100, 35);
		g.setColor(Color.yellow);
		g.drawString(" Score: "+fullscore, 30, 30);

		if(index>=8){
			index=0;
			moving = false;
		}

		switch (facing) {
		case "UP":
			g.drawImage(Images.Player[index], getX(), getY(), getWidth(), -1 * getHeight(), null);
			break;
		case "DOWN":
			g.drawImage(Images.Player[index], getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "LEFT":
			g.drawImage(rotateClockwise90(Images.Player[index]), getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "RIGHT":
			g.drawImage(rotateClockwise90(Images.Player[index]), getX(), getY(), -1 * getWidth(), getHeight(), null);
			break;
		}


		UpdatePlayerRectangle(g);

	}



	// Rectangles are what is used as "collisions." 
	// The hazards have Rectangles of their own.
	// This is the Rectangle of the Player.
	// Both come in play inside the WorldManager.
	private void UpdatePlayerRectangle(Graphics g) {

		player = new Rectangle(this.getX(), this.getY(), getWidth(), getHeight());

		if (facing.equals("UP")){
			player = new Rectangle(this.getX(), this.getY() - 64, getWidth(), getHeight());
		}
		else if (facing.equals("RIGHT")) {
			player = new Rectangle(this.getX() - 64, this.getY(), getWidth(), getHeight());
		}
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private static BufferedImage rotateClockwise90(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage dest = new BufferedImage(height, width, src.getType());

		Graphics2D graphics2D = dest.createGraphics();
		graphics2D.translate((height - width) / 2, (height - width) / 2);
		graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
		graphics2D.drawRenderedImage(src, null);

		return dest;
	}

	public Rectangle getPlayerCollision() {
		return player;
	}
	public String getFacing(){   //added

		return facing;
	}
	public static int getScore() {
		return score;
	}
	public static void setScore(int score1){
		score=score1;
		
	}
}
