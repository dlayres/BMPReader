import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BMPReader{
	
	public static int byteToInt(byte b) {
		String byteString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
		int total = 0;
		for(int i = 0; i < byteString.length(); i++) {
			total += ((int)byteString.charAt(i) - 48) * Math.pow(2, byteString.length() - 1 - i);
		}
		return total;
	}
	
	public static String intToHex(int v) {
		return Integer.toHexString(v);
	}
	
	public static int hexToInt(String s) {
		return Integer.parseInt(s, 16);
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(1000, 1000);
		ImagePanel panel = new ImagePanel();
		frame.setSize((panel.width + 1) * panel.res + 6, (panel.height + 3) * panel.res + 8);
		
		frame.add(panel);
		frame.setVisible(true);
		
		panel.paintAgain();
		
		
		
		
		
		
		
		
	}
}
