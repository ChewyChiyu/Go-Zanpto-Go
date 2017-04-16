import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tiles {
	private ObjectType o;
	private int x;
	private int y;
	private int xVelocity;
	private int yVelocity;
	private int width;
	private int height;
	private BufferedImage sprite;
	public Tiles(ObjectType o, int x, int y, int width, int height,BufferedImage sprite){
		this.o = o;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		xVelocity = -5;
	}
	public void draw(Graphics g, int x , int y ){
		g.drawImage(sprite, x, y, width,height, null);
	}
	public ObjectType getType(){
		return o;
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
}
