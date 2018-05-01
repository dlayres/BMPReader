import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BMPReader {
	
	static JFrame frame = new JFrame();
	static JFrame frame2 = new JFrame();
	static Set<PuzzlePiece> pzlPieceSet = new HashSet<PuzzlePiece>();
	
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
	
	public static JFrame makeNewFrame(PuzzlePiece piece1, PuzzlePiece piece2, PuzzlePiece newPzlPiece) {
		JFrame newFrame = new JFrame();
		newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newFrame.setUndecorated(true);
		newFrame.setBackground(new Color(0,0,0,0));
		newFrame.setSize(newPzlPiece.width, newPzlPiece.height);
		newFrame.add(newPzlPiece);
		newPzlPiece.attachedFrame = newFrame;
		pzlPieceSet.remove(piece1);
		pzlPieceSet.remove(piece2);
		pzlPieceSet.add(newPzlPiece);
		piece1.attachedFrame.dispose();
		piece2.attachedFrame.dispose();
		newPzlPiece.attachedFrame.setVisible(true);
		return newFrame;
	}
	
	
	
	public static void checkCloseness() {
		for(PuzzlePiece thisPiece: pzlPieceSet) {
			for(PuzzlePiece otherPiece: pzlPieceSet) {
				if(thisPiece == otherPiece) {
					continue;
				}
				else {
					JFrame thisFrame = thisPiece.attachedFrame;
					JFrame otherFrame = otherPiece.attachedFrame;
					if((Math.abs(thisFrame.getLocation().x - (otherFrame.getLocation().x + otherPiece.width)) <= 5) && (Math.abs(thisFrame.getLocation().y - otherFrame.getLocation().y) <= 5)){
						PuzzlePiece newPiece = thisPiece.combine(otherPiece, "left");
						JFrame newFrame = makeNewFrame(thisPiece, otherPiece, newPiece);
						newFrame.setLocation(otherFrame.getLocation().x, otherFrame.getLocation().y);
						return;
					}
					else if((Math.abs(otherFrame.getLocation().x - (thisFrame.getLocation().x + thisPiece.width)) <= 5) && (Math.abs(otherFrame.getLocation().y - thisFrame.getLocation().y) <= 5)){
						PuzzlePiece newPiece = thisPiece.combine(otherPiece, "right");
						JFrame newFrame = makeNewFrame(thisPiece, otherPiece, newPiece);
						newFrame.setLocation(thisFrame.getLocation().x, thisFrame.getLocation().y);
						return;
					}
					else if((Math.abs(thisFrame.getLocation().y - (otherFrame.getLocation().y + otherPiece.height)) <= 5) && (Math.abs(thisFrame.getLocation().x - otherFrame.getLocation().x) <= 5)){
						PuzzlePiece newPiece = thisPiece.combine(otherPiece, "up");
						JFrame newFrame = makeNewFrame(thisPiece, otherPiece, newPiece);
						newFrame.setLocation(otherFrame.getLocation().x, otherFrame.getLocation().y);
						return;
					}
					else if((Math.abs(otherFrame.getLocation().y - (thisFrame.getLocation().y + thisPiece.height)) <= 5) && (Math.abs(otherFrame.getLocation().x - thisFrame.getLocation().x) <= 5)){
						PuzzlePiece newPiece = thisPiece.combine(otherPiece, "down");
						JFrame newFrame = makeNewFrame(thisPiece, otherPiece, newPiece);
						newFrame.setLocation(thisFrame.getLocation().x, thisFrame.getLocation().y);
						return;
					}
					
					
					
				}
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		ImagePanel panel = new ImagePanel();
		panel.paintAgain();
		int widthStep = panel.width / 10;
		int heightStep = panel.height / 10;
		System.out.println(widthStep + " " + heightStep);
		boolean doneI = false;
		boolean doneJ = false;
		for(int i = 0; i < panel.width; i+=widthStep) {
			for(int j = 0; j < panel.height; j+=heightStep) {
				ImagePanel imgPanel = new ImagePanel(panel);
				if((panel.width - i) < (widthStep * 1.4)) {
					imgPanel.width = (panel.width - i);
					doneI = true;
				}
				else {
					imgPanel.width = widthStep;
				}
				if((panel.height - j) < (heightStep * 1.4)) {
					imgPanel.height = (panel.height - j);
					doneJ = true;
				}
				else {
					imgPanel.height = heightStep;
				}
				imgPanel.pixelColors = new Color[imgPanel.width * imgPanel.height];
				for(int k = 0; k < imgPanel.pixelColors.length; k++) {
					System.out.println("i: " + i + " j: " + j + " k: " + k);
					System.out.println((((k / widthStep) + j) * panel.width) + ((k % heightStep) + i));
				//	System.out.println(((k % heightStep) + i));
					imgPanel.pixelColors[k] = panel.pixelColors[(((k / imgPanel.width) + j) * panel.width) + ((k % imgPanel.width) + i)];
					//Thread.sleep(5000);
				}
				PuzzlePiece newPiece = new PuzzlePiece(imgPanel);
				pzlPieceSet.add(newPiece);
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setUndecorated(true);
				frame.setBackground(new Color(0,0,0,0));
				frame.setSize(imgPanel.width, imgPanel.height);
				frame.add(newPiece);
				newPiece.attachedFrame = frame;
				
				if(doneJ) {
					doneJ = false;
					continue;
				}
			}
			if(doneI) {
				doneI = false;
				break;
			}
		}
		
		
//		PuzzlePiece pzlPiece = new PuzzlePiece(panel);
//		PuzzlePiece pzlPiece2 = new PuzzlePiece(panel);
//		pzlPieceSet.add(pzlPiece);
//		pzlPieceSet.add(pzlPiece2);
//		
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setUndecorated(true);
//		frame.setSize(panel.width, panel.height);
//		frame.add(pzlPiece);
//		pzlPiece.attachedFrame = frame;
//		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame2.setUndecorated(true);
//		frame2.setSize(panel.width, panel.height);
//		frame2.add(pzlPiece2);
//		pzlPiece2.attachedFrame = frame2;
		
		for(PuzzlePiece nextPiece: pzlPieceSet) {
			nextPiece.attachedFrame.setVisible(true);
		}

		while(true) {
			try{Thread.sleep(25);} catch(InterruptedException e){}
			for(PuzzlePiece nextPiece: pzlPieceSet) {
				if(nextPiece.moving) {
					nextPiece.attachedFrame.setLocation(MouseInfo.getPointerInfo().getLocation().x - (nextPiece.width / 2), MouseInfo.getPointerInfo().getLocation().y  - (nextPiece.height / 2));
				}
			}
			
			
		}
		
		
	}


}
