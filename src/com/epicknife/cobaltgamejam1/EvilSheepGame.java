package com.epicknife.cobaltgamejam1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.python.util.PythonInterpreter;

public class EvilSheepGame extends BasicGame
{
    
    private Image healthbar;
    private Image healthrect;
    private final PythonInterpreter interpreter;
    
    public static HashMap<String, Image> images;
    public static ArrayList<Tile> world;
    // There are allowed to occupy the same space.
    public static ArrayList<AI> entities;
    public static Controllable player;
    
    public static void main(String[] args) throws Exception
    {
        EvilSheepGame esg = new EvilSheepGame("Evil Sheep - Samuel \"MrOverkill\" Meyers");
        EvilSheepGame.images = new HashMap<>();
        EvilSheepGame.world = new ArrayList<>();
        EvilSheepGame.entities = new ArrayList<>();
        AppGameContainer container = new AppGameContainer(esg);
        container.setDisplayMode(800, 600, false);
        container.start();
    }
    
    // This is a very expensive function for large maps.
    public static void addTile(Tile tile)
    {
        world.add(tile);
        for(int i = 0; i < entities.size(); i++)
        {
            entities.get(i).onTerrainChange();
        }
    }
    
    public static void setPlayer(Controllable c)
    {
        player = c;
    }
    
    public static void addEntity(AI entity)
    {
        entities.add(entity);
        for(int i = 0; i < entities.size(); i++)
        {
            entities.get(i).onEntityChange();
        }
    }
    
    public static void setImage(String name, String location)
    {
        try
        {
            images.put(name, new Image(location));
        }
        catch(SlickException e)
        {
            e.printStackTrace();
        }
    }
    
    public static Image getImage(String name)
    {
        return images.get(name);
    }
    
    public EvilSheepGame(String title)
    {
        super(title);
        interpreter = new PythonInterpreter();
    }

    @Override
    public void init(GameContainer gc) throws SlickException
    {
        gc.setShowFPS(false);
        player = new Controllable(new Point2i(), 1);
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("map.py"));
            String script = "";
            String line;
            while((line = br.readLine()) != null)
            {
                script += line + "\n";
            }
            br.close();
            interpreter.exec(script);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void update(GameContainer gc, int dt) throws SlickException
    {
        player.onUpdate();
        
        if(interpreter.get("onUpdate") != null)
        {
            interpreter.get("onUpdate").__call__();
        }
        
        for(int i = 0; i < entities.size(); i++)
        {
            entities.get(i).onThink(dt);
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        if(interpreter.get("onDraw") != null)
        {
            interpreter.get("onDraw").__call__();
        }
        
        for(int i = 0; i < world.size(); i++)
        {
            world.get(i).onDraw(player.pos);
        }
        
        for(int i = 0; i < entities.size(); i++)
        {
            entities.get(i).onDraw(player.pos);
        }
        
        player.onDraw();
        
        // Different scenes could need different UI
        if(interpreter.get("onGUI") != null)
        {
            interpreter.get("onGUI").__call__();
        }
    }
    
}