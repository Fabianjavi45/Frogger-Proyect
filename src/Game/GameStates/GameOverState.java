package Game.GameStates;

import Main.Handler;
import Resources.Images;
import UI.UIImageButton;
import UI.UIManager;

import java.awt.*;

import Game.Entities.Dynamic.Player;

/**
 * Created by AlexVR on 7/1/2018.
 */
public class GameOverState extends State {

    private int count = 0;
    private UIManager uiManager;

    public GameOverState(Handler handler) {
        super(handler);
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);

        /*
         * Adds a button that by being pressed changes the State
         */
        uiManager.addObjects(new UIImageButton(60, handler.getGame().getHeight() - 150, 140, 100, Images.deathRetry, () -> {
            handler.getMouseManager().setUimanager(null);
            handler.getGame().reStart();
            handler.getPlayer().setScore(0);
            State.setState(handler.getGame().gameState);
         
        }));

      

        uiManager.addObjects(new UIImageButton(360,  handler.getGame().getHeight() - 140, 128, 75, Images.deathTitle, () -> {
            handler.getMouseManager().setUimanager(null);
            State.setState(handler.getGame().menuState);
        }));





    }

    @Override
    public void tick() {
        handler.getMouseManager().setUimanager(uiManager);
        uiManager.tick();
        count++;
        if( count>=30){
            count=30;
        }
        if(handler.getKeyManager().pbutt && count>=30){
            count=0;
            State.setState(handler.getGame().gameState);
        }

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Images.frog,0,0,handler.getGame().getWidth(),handler.getGame().getHeight(),null);
    	g.setColor(Color.BLACK);
    	g.setFont(new Font("Arial",Font.PLAIN,30));
    	g.drawString("Score: " + Player.getScore(), handler.getWidth()-365, handler.getHeight()-200);
    
        uiManager.Render(g);

    }
}