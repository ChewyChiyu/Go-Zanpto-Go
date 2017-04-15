import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Characters{
	private boolean inAir = false;
	private ObjectType o;
	private int motionRotation = 0;
	private Timer movement;
	private int x;
	private int y;
	private int xVelocity;
	private int yVelocity;
	private int height;
	private int width;
	private int speed;
	public final int jumpSpeed = -10; //more than gravity
	private BufferedImage[] walk = new BufferedImage[4];
	private BufferedImage[] jump = new BufferedImage[4];
	public BufferedImage spriteSheet;
	public Characters(ObjectType o, int x, int y, int height, int width, int speed){
		this.o = o;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.height = height;
		this.width = width;
		openImgs();
		movement = new Timer(200,e->{
				 incrementMotionRotation();
		});
		movement.start();
	}
	public void openImgs(){
		switch(o){
		case ZANPTO:
		URL imageUrl = getClass().getResource("/imgs/Zanpto.png");
		try {
			spriteSheet = ImageIO.read(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int xBuffer = 0;
		for(int index = 0; index < walk.length; index++){
		walk[index] = spriteSheet.getSubimage(xBuffer, 0, 57, 73);
		xBuffer+=57;
		}
		xBuffer = 250;
		for(int index = 0; index < jump.length; index++){
		jump[index] = spriteSheet.getSubimage(xBuffer, 0, 74, 76);
		xBuffer+=74;
		}
		
		
		
		
		
		
		break;
		}

	}
	public void jump(){
		Thread jumping = new Thread(new Runnable(){
			public void run(){
				for(int index = 0 ; index < 200; index++){ //some random time
					changeY(-speed); //just a little jump LOL
					try{
						Thread.sleep(1); //holds thread for gravity
					}catch(Exception e){
						
					}
				}
			}
		});
		jumping.start();
		
	}
	public boolean inAir(){
		return inAir;
	}
	public void onGround(){
		inAir = false;
	}
	public void toAir(){
		inAir = true;
	}
	public void incrementMotionRotation(){
		motionRotation++;
		if(motionRotation>=4)
			motionRotation = 0;
	}
	public ObjectType getType(){
		return o;
	}
	public void draw(Graphics g, int x, int y){
		if(!inAir){
		g.drawImage(walk[motionRotation], x, y,100,100, null);
		}else{
			g.drawImage(jump[motionRotation], x, y,100,100, null);
		}
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void changeX(int inc){
		x += inc;
	}
	public void changeY(int inc){
		y += inc;
	}
	public int getXVelocity(){
		return xVelocity;
	}
	public int getYVelocity(){
		return yVelocity;
	}
	public int getHeight(){
		return height;
	}
	public int getWidth(){
		return width;
	}
	public void changeXVelocity(int inc){
		xVelocity = inc;
	}
	public void changeYVelocity(int inc){
		yVelocity = inc;
	}
	public void changeSpeed(int inc){
		speed = inc;
	}
	public int getSpeed(){
		return speed;
	}
}
