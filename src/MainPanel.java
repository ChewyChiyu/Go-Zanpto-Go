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
import javax.swing.JOptionPane;
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
	final int PUSHBACK = 75;
	final int BUFFER = 5;
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

		for(int i = 0; i < 5; i++){
			blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		}

		//random cloud lol
		blocks.add(new Tiles(ObjectType.CLOUD,xBuffer,yBuffer-250,200,100,blocksSprites[4]));

		xBuffer+=200;
		yBuffer+=100;
		
		for(int i = 0; i < 12; i++){
			blocks.add(new Tiles(ObjectType.GRASS,xBuffer+=100,yBuffer,100,100,blocksSprites[0]));
		}
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
				Thread.sleep(15);
			}catch(Exception e){

			}
		}
	}
	public void updatePanel(){
		updatePlayerLocation();
		updateTileLocation();
		checkIfFall();
		repaint();
	}
	public void checkIfFall(){
		if(player.getY()>HEIGHT){
			int reply = JOptionPane.showConfirmDialog(null, "You Fell!" + "\n Play Again?" , "Lose", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				player =  new Zanpto(0,(int)(HEIGHT*.3));
				blocks.clear();
				setUpMap();
				return;
			}
			else {
				System.exit(0);
			}
		}
	}
	public void updateTileLocation(){
		for(int index = 0 ; index < blocks.size(); index++){
			Tiles t = blocks.get(index);
			t.changeX(t.getXVelocity()); //Ground Speed
			t.changeY(t.getYVelocity()); //Ground Speed
			if(t.getX()<-100){
				blocks.remove(t);
				blocks.add(new Tiles(ObjectType.GRASS,WIDTH,currentWorldY,100,100,blocksSprites[0]));
				if((int)(Math.random()*5)==2)// 1/5 chance
					currentWorldY = ((int)(Math.random()*2)==1)?(currentWorldY+=50):(currentWorldY-=100);
				if((int)(Math.random()*5)==2)// 1/5 chance
					blocks.add(new Tiles(ObjectType.CLOUD,WIDTH,(int)(Math.random()*500),100,100,blocksSprites[4]));
			
				if(currentWorldY<0||currentWorldY>HEIGHT)
					currentWorldY = (int) (HEIGHT*.9);
			
			}
		}
	}
	public void updatePlayerLocation(){
		boolean onGround = false;
		for(int index = 0 ; index < blocks.size(); index++){
			if(onGround)
				continue;
			Tiles t = blocks.get(index);
			if(t.getType().equals(ObjectType.CLOUD)){
				continue;
			}
			int tileX1 = t.getX()-BUFFER; //5 pixel buffer
			int tileX2 = t.getX()+t.getWidth()+BUFFER; //5 pixel buffer
			int tileY1 = t.getY();
			

			if(player.getY()+player.getHeight()>t.getY()&&player.getY()+player.getHeight()<t.getY()+t.getHeight()&&player.getX()+player.getWidth()+BUFFER>t.getX()&&player.getX()+player.getWidth()-BUFFER<t.getX()){//5 pixel buffer
				player.changeX(-PUSHBACK); // 25 block push back
			}
			
			
			
			if(player.getX()>tileX1&&player.getX()<tileX2&&((player.getY()+player.getHeight())>tileY1&&player.getY()+player.getHeight()<t.getY()+20)){ // 20 pixel buffer
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

		//Physics buffer ~
				for(int index = 0; index < blocks.size(); index++){
					Tiles t = blocks.get(index);
					
					if(!player.inAir()&&player.getY()+player.getHeight()-BUFFER>t.getY()&&player.getX()>t.getX()&&player.getX()<t.getX()+t.getWidth()){ 
						player.changeYVelocity(-1); 
					}
					if(player.inAir()&&player.getY()+player.getHeight()!=t.getY()&&player.getX()>t.getX()&&player.getX()<t.getX()+t.getWidth()){
						player.changeXVelocity(-1);
					}
					
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
