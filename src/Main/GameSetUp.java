package Main;

import Display.DisplayScreen;
import Game.GameStates.GameState;
import Game.GameStates.MenuState;
import Game.GameStates.PauseState;
import Game.GameStates.State;
import Input.KeyManager;
import Input.MouseManager;
import Resources.Images;
import Resources.MusicHandler;
import UI.UIObject;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * Created by AlexVR on 7/1/2018.
 */


/*
 * Try to understand the LOGIC behind this first. (aka don't overthink this, just try to understand the concept)
 */
public class GameSetUp implements Runnable {
    private DisplayScreen display;
    private int width, height;
    private String title;
    public static String str;

    private boolean running = false;
    private Thread thread;

    //Input
    private KeyManager keyManager;
    private MouseManager mouseManager;
    

    //Handler
    private Handler handler;

    //States
    public State gameState;
    public State menuState;
    public State pauseState;

    //Res.music
    public MusicHandler musicHandler;

    private BufferedImage loading;

    GameSetUp(String title, int width, int height){

        this.width = width;
        this.height = height;
        this.title = title;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        musicHandler = new MusicHandler();
        str = UIObject.getLtr();

    }

    private void init(){
        display = new DisplayScreen(title, width, height);
        display.getFrame().addKeyListener(keyManager);
        display.getFrame().addMouseListener(mouseManager);
        display.getFrame().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);

        Images img = new Images();

        handler = new Handler(this);
        musicHandler.setHandler(handler);

        gameState = new GameState(handler);
        menuState = new MenuState(handler);
        pauseState = new PauseState(handler);

        State.setState(menuState);

        musicHandler.set_changeMusic("res/music/Pokemon RubySapphireEmerald- Surf.mp3");//was UTheme.mp3
        musicHandler.play();
        musicHandler.setLoop(true);
        musicHandler.setVolume(0.25);
    }

    public void reStart(){
        gameState = new GameState(handler);
    }

    public synchronized void start(){
        if(running)
            return;
        running = true;
        //this runs the run method in this  class
        thread = new Thread(this);
        thread.start();
    }

    public void run(){

        //initiallizes everything in order to run without breaking
        init();

        int fps = 60;
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        while(running){
            //makes sure the games runs smoothly at 60 FPS
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            timer += now - lastTime;
            lastTime = now;

            if(delta >= 1){
                //re-renders and ticks the game around 60 times per second
                tick();
                render();
                ticks++;
                delta--;
            }

            if(timer >= 1000000000){
                ticks = 0;
                timer = 0;
            }
        }

        stop();

    }

    /*
     * Many classes will have this badboy (and the render.)
     * With the tick you can check stuff every "tick" which is called like a lot of times per second (like 60).
     * And with it you can move an object every tick, or check for conditions every tick, etc.
     */
    private void tick(){
        //checks for key types and manages them
        keyManager.tick();

        //game states are the menus
        if(State.getState() != null)
            State.getState().tick();
    }

    /*
     * As stated before, many classes have a render method. Because it is like a brush, where you can draw stuff on it
     * And since it is called many times, then you can modify what you draw every time it is called!
     */
    private void render(){
        BufferStrategy bs = display.getCanvas().getBufferStrategy();
        if(bs == null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0, 0, width, height);


        g.drawImage(loading ,0,0,width,height,null);
        if(State.getState() != null)
            State.getState().render(g);


        //End Drawing!
        bs.show();
        g.dispose();
    }

    public synchronized void stop(){
        if(!running)
            return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    KeyManager getKeyManager(){
        return keyManager;
    }

    MusicHandler getMusicHandler() {
        return musicHandler;
    }

    MouseManager getMouseManager(){
        return mouseManager;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
}

