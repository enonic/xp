import java.applet.Applet;
import java.awt.*;
 
// Applet code for the "Hello, world!" example.
// This should be saved in a file named as "HelloWorld.java".
public class HelloWorld extends Applet {
  // This method is mandatory, but can be empty (i.e., have no actual code).
  public void init() { 
    setBackground(Color.YELLOW);
  }
 
  // This method is mandatory, but can be empty.(i.e.,have no actual code).
  public void stop() { }
 
  // Print a message on the screen (x=20, y=10).
  public void paint(Graphics g) {
    g.drawString("Hello, world!", 20,10);

 
  // Draws a circle on the screen (x=40, y=30).
    g.drawArc(40,30,20,20,0,360);
  }
}