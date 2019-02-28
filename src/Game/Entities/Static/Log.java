package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class Log extends StaticBase {

    private Rectangle log;
	
    public Log(Handler handler,int xPosition, int yPosition) {
        super(handler);
        // Sets original position to be this one.
        this.setX(xPosition); 
        this.setY(yPosition);
    }
    
    @Override
    public void render(Graphics g) {
    	
    	g.drawImage(Images.log, this.getX(), this.getY(), 128, 60, null);
    	log = new Rectangle(this.getX()+40, this.getY(), 50, 60);
    }
    
    @Override
    public Rectangle GetCollision() {
    	return log;
    }
}
