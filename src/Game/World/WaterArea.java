package Game.World;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

public class WaterArea extends BaseArea {

    private Animation anim;
    private Rectangle rect;

    WaterArea(Handler handler, int yPosition) {
        super(handler, yPosition);
        // Instantiate the animation of this Water, and it starts it at a random frame.
        anim=new Animation(384,Images.Water,new Random().nextInt(3));
        rect= new Rectangle(9*64,yPosition ,64,66);

    }

    @Override
    public void tick() {
        anim.tick();
        rect= new Rectangle(64,yPosition ,768,1);// Animation frame movement.

    }

    @Override
    public void render(Graphics g) {
        for (int i = 0; i < 9; i++) {
            g.drawImage(anim.getCurrentFrame(), i*64, yPosition,64,66, null);
            
        }
    }
    
    
    public Rectangle getWaterCollision() {
    	return rect;
    }
}