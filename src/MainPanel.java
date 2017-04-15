import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
@SuppressWarnings("serial")
public class MainPanel extends JPanel implements Runnable{
	Thread game;
	BufferedImage spriteSheet;
	BufferedImage backDropIsland;
	ArrayList<Tiles> blocks = new ArrayList<Tiles>();
	BufferedImage[] blocksSprites = new BufferedImage[10];
	int currentWorldY;
	final int GRAVITY = 3;
	final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public Zanpto player = new Zanpto(0,(int)(HEIGHT*.3));
	boolean isRunning;
	public static void main(String[] args){
		new MainPanel();
	}
	public MainPanel(){
		setUpPanel();
		openImg();
		setUpMap();
		start();
		repaint();
	}
	public void setUpMap(){
		//Blocks hard coded due to not interfering with screen size
		int xBuffer=0;
		int yBuffer = (int)(HEIGHT*.62);



		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		//random cloud lol
		blocks.add(new Tiles(ObjectType.CLOUD,xBuffer,yBuffer-250,200,100,blocksSprites[4]));

		xBuffer+=200;
		yBuffer+=100;
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));

		currentWorldY = yBuffer;

	}
	public void openImg(){
		URL imageUrl = getClass().getResource("/imgs/Tiles.png");
		try {
			spriteSheet = ImageIO.read(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		URL imageUrl2 = getClass().getResource("/imgs/backDropIsland.png");
		try {
			backDropIsland = ImageIO.read(imageUrl2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		blocksSprites[0] = spriteSheet.getSubimage(0, 0, 50 , 50); //Grass
		blocksSprites[1] = spriteSheet.getSubimage(200, 0, 50 , 50); //Dirt
		blocksSprites[2] = spriteSheet.getSubimage(385, 0, 62 , 65); //Crate
		blocksSprites[3] = spriteSheet.getSubimage(450, 130, 62 , 50); //Roots
		blocksSprites[4] = spriteSheet.getSubimage(320, 260, 125 , 60); //Cloud
	}
	public void setUpPanel(){
		JFrame frame = new JFrame("Run Zanpto Run!");
		frame.add(this);
		frame.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "jump");
		this.getActionMap().put("jump", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!player.inAir()){
					player.jump();
				}
			}

		});;

	}

	public synchronized void start(){
		isRunning = true;
		game = new Thread(this);
		game.start();
	}
	public synchronized void stop(){
		try{
			isRunning = false;
			game.join();
		}catch(Exception e){

		}
	}
	public void run(){
		while(isRunning){
			updatePanel();
			try{
				Thread.sleep(10);
			}catch(Exception e){

			}
		}
	}
	public void updatePanel(){
		updatePlayerLocation();
		updateTileLocation();
		repaint();
	}
	public void updateTileLocation(){
		for(int index = 0 ; index < blocks.size(); index++){
			Tiles t = blocks.get(index);
			t.changeX(t.getXVelocity()); //Ground Speed
			t.changeY(t.getYVelocity()); //Ground Speed
			if(t.getX()<-100){
				blocks.remove(t);
				blocks.add(new Tiles(ObjectType.GRASS,WIDTH,currentWorldY,100,100,blocksSprites[0]));
				if((int)(Math.random()*20)==10)// 1/20 chance
					currentWorldY = ((int)(Math.random()*2)==1)?(currentWorldY+=100):(currentWorldY-=100);
				if((int)(Math.random()*20)==10)// 1/20 chance
					blocks.add(new Tiles(ObjectType.CLOUD,WIDTH,(int)(Math.random()*500),100,100,blocksSprites[4]));
			}
		}
	}
	public void updatePlayerLocation(){
		boolean onGround = false;
		for(int index = 0 ; index < blocks.size(); index++){
			if(onGround)
				continue;
			Tiles t = blocks.get(index);
			int tileX1 = t.getX();
			int tileX2 = t.getX()+t.getWidth();
			int tileY1 = t.getY();

			if(player.getX()>tileX1&&player.getX()<tileX2&&(player.getY()+player.getHeight())>tileY1){
				onGround = true;
			}	
		}
		if(onGround){
			player.changeYVelocity(0);
			player.onGround();
		}else{
			player.changeYVelocity(GRAVITY);
			player.toAir();
		}

		if(player.getX()<(int)(WIDTH*.4)){
			player.changeXVelocity(player.getSpeed());
		}else{
			player.changeXVelocity(0);
		}
		player.changeY(player.getYVelocity());
		player.changeX(player.getXVelocity());
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		drawBackGround(g);
		drawCharacters(g);
	}
	public void drawBackGround(Graphics g){
		g.drawImage(backDropIsland, 0, 0,WIDTH,HEIGHT, null);
		for(int index = 0 ; index < blocks.size(); index++){
			Tiles t = blocks.get(index);
			t.draw(g, t.getX(), t.getY());
		}
	}
	public void drawCharacters(Graphics g){
		player.draw(g, player.getX(), player.getY());
	}
}
