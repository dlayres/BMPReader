import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class BMPReader {

	static int numRowPieces;
	static int numColPieces;
	static int tolerance = 10;
	static JFrame progressBarFrame;
	static JProgressBar progressBar;
	static Set<PuzzlePiece> pzlPieceSet = new HashSet<PuzzlePiece>();
	static PuzzlePiece pieceGrid[][];
	static Set<PuzzlePiece> visited = new HashSet<PuzzlePiece>();
	static MouseEvent me;
	static int tasks;
	static int tasksDone;
	static int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	static int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 40;
	static int screenWidthBoundary = (int)Math.round(screenWidth * 0.1);
	static int screenHeightBoundary = (int)Math.round(screenHeight * 0.1);

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




	public static void checkCloseness() {
		for(PuzzlePiece thisPiece: pzlPieceSet) {
			if(thisPiece.leftNeighbor != null) {
				PuzzlePiece otherPiece = thisPiece.leftNeighbor;
				JFrame thisFrame = thisPiece.attachedFrame;
				JFrame otherFrame = otherPiece.attachedFrame;
				if((Math.abs(thisFrame.getLocation().x - (otherFrame.getLocation().x + otherPiece.width)) <= tolerance) && (Math.abs(thisFrame.getLocation().y - otherFrame.getLocation().y) <= tolerance)){
					thisPiece.leftConnected = true;
					otherPiece.rightConnected = true;
				}
			}
			if(thisPiece.rightNeighbor != null) {
				PuzzlePiece otherPiece = thisPiece.rightNeighbor;
				JFrame thisFrame = thisPiece.attachedFrame;
				JFrame otherFrame = otherPiece.attachedFrame;
				if((Math.abs(otherFrame.getLocation().x - (thisFrame.getLocation().x + thisPiece.width)) <= tolerance) && (Math.abs(otherFrame.getLocation().y - thisFrame.getLocation().y) <= tolerance)){
					thisPiece.rightConnected = true;
					otherPiece.leftConnected = true;
				}
			}
			if(thisPiece.upNeighbor != null) {
				PuzzlePiece otherPiece = thisPiece.upNeighbor;
				JFrame thisFrame = thisPiece.attachedFrame;
				JFrame otherFrame = otherPiece.attachedFrame;
				if((Math.abs(thisFrame.getLocation().y - (otherFrame.getLocation().y + otherPiece.height)) <= tolerance) && (Math.abs(thisFrame.getLocation().x - otherFrame.getLocation().x) <= tolerance)){
					thisPiece.upConnected = true;
					otherPiece.downConnected = true;
				}
			}
			if(thisPiece.downNeighbor != null) {
				PuzzlePiece otherPiece = thisPiece.downNeighbor;
				JFrame thisFrame = thisPiece.attachedFrame;
				JFrame otherFrame = otherPiece.attachedFrame;
				if((Math.abs(otherFrame.getLocation().y - (thisFrame.getLocation().y + thisPiece.height)) <= tolerance) && (Math.abs(otherFrame.getLocation().x - thisFrame.getLocation().x) <= tolerance)){
					thisPiece.downConnected = true;
					otherPiece.upConnected = true;
				}
			}
		}
	}

	public static void checkAttachment(PuzzlePiece checkPiece) {
		if(visited.contains(checkPiece)) {
			return;
		}
		checkPiece.attachedFrame.toFront();
		if(checkPiece.upConnected) {
			checkPiece.upNeighbor.attachedFrame.setLocation(checkPiece.attachedFrame.getLocation().x, checkPiece.attachedFrame.getLocation().y - checkPiece.upNeighbor.height);
			visited.add(checkPiece);
			checkAttachment(checkPiece.upNeighbor);
		}
		if(checkPiece.downConnected) {
			checkPiece.downNeighbor.attachedFrame.setLocation(checkPiece.attachedFrame.getLocation().x, checkPiece.attachedFrame.getLocation().y + checkPiece.height);
			visited.add(checkPiece);
			checkAttachment(checkPiece.downNeighbor);
		}
		if(checkPiece.leftConnected) {
			checkPiece.leftNeighbor.attachedFrame.setLocation(checkPiece.attachedFrame.getLocation().x - checkPiece.leftNeighbor.width, checkPiece.attachedFrame.getLocation().y);
			visited.add(checkPiece);
			checkAttachment(checkPiece.leftNeighbor);
		}
		if(checkPiece.rightConnected) {
			checkPiece.rightNeighbor.attachedFrame.setLocation(checkPiece.attachedFrame.getLocation().x + checkPiece.width, checkPiece.attachedFrame.getLocation().y);
			visited.add(checkPiece);
			checkAttachment(checkPiece.rightNeighbor);
		}
	}



	public static void main(String[] args) {
		int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter the number of pieces in a row: ");
		numRowPieces = scan.nextInt();
		System.out.print("Enter the number of pieces in a column: ");
		numColPieces = scan.nextInt();
		int totalPieces = numRowPieces * numColPieces;
		tasks = 2 * totalPieces; 
		tasksDone = 0;

		progressBarFrame = new JFrame();
		progressBarFrame.setTitle("Loading puzzle");
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		progressBarFrame.setSize(350, 120);
		progressBarFrame.setLocation(screenWidth/2-progressBarFrame.getSize().width/2, screenHeight/2-progressBarFrame.getSize().height/2);
		progressBarFrame.add(progressBar);
		progressBarFrame.setVisible(true);
		JButton startPuzzle = new JButton("Start Puzzle");
		startPuzzle.setEnabled(false);
		startPuzzle.addActionListener(new ActionListener(){ // Listens for a button click
			public void actionPerformed(ActionEvent e) {
				for(PuzzlePiece nextPiece: pzlPieceSet) {
					nextPiece.attachedFrame.setVisible(true);
				}
				progressBarFrame.dispose();
			}
		});
		progressBarFrame.add(startPuzzle, BorderLayout.SOUTH);

		ImagePanel panel = new ImagePanel();
		int widthStep = panel.width / numRowPieces;
		int heightStep = panel.height / numColPieces;
		int widthBoundary = (int)(widthStep * 1.2);
		int heightBoundary = (int)(heightStep * 1.2);
		pieceGrid = new PuzzlePiece[numColPieces][numRowPieces];
		boolean doneI = false;
		boolean doneJ = false;


		int[] widths = new int[numRowPieces];
		int[] heights = new int[numColPieces];
		int widthIncrement = panel.width - (numRowPieces * widthStep);
		int heightIncrement = panel.height - (numColPieces * heightStep);
		int remainderWidthGap = numRowPieces / widthIncrement;
		int remainderHeightGap = numColPieces / heightIncrement;
		for(int i = 0; i < widths.length; i++) {
			widths[i] = widthStep;
		}
		for(int i = 0; i < heights.length; i++) {
			heights[i] = heightStep;
		}
		for(int i = 0; i < widthIncrement; i++) {
			widths[i * remainderWidthGap]++;
		}
		for(int i = 0; i < heightIncrement; i++) {
			heights[i * remainderHeightGap]++;
		}

		int cumulativeWidth = 0;
		int cumulativeHeight = 0;

		for(int i = 0; i < numRowPieces; i++) {
			for(int j = 0; j < numColPieces; j++) {
				ImagePanel imgPanel = new ImagePanel(panel);
				imgPanel.width = widths[i];
				imgPanel.height = heights[j];
				imgPanel.pixelColors = new Color[imgPanel.width * imgPanel.height];
				for(int k = 0; k < imgPanel.pixelColors.length; k++) {
					imgPanel.pixelColors[k] = panel.pixelColors[(((k / imgPanel.width) + cumulativeHeight) * panel.width) + ((k % imgPanel.width) + cumulativeWidth)];
				}
				PuzzlePiece newPiece = new PuzzlePiece(imgPanel);
				pzlPieceSet.add(newPiece);
				pieceGrid[j][i] = newPiece;
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setUndecorated(true);
				frame.setBackground(new Color(0,0,0,0));
				frame.setSize(imgPanel.width, imgPanel.height);
				frame.add(newPiece);
				int randX = (int)(Math.floor((Math.random() * (screenWidth - (2 * screenWidthBoundary + imgPanel.width)) + screenWidthBoundary)));
				int randY = (int)(Math.floor((Math.random() * (screenHeight - (2 * screenHeightBoundary + imgPanel.height)) + screenHeightBoundary)));
				frame.setLocation(randX, randY);
				newPiece.attachedFrame = frame;
				progressBar.setIndeterminate(false);
				SwingUtilities.invokeLater(new Runnable(){
					@Override public void run(){progressBar.setValue((int)Math.ceil(100 * (tasksDone / ((double)(tasks)))));}
				});
				tasksDone++;
				
				cumulativeHeight += heights[j];
			}
			cumulativeWidth += widths[i];
			cumulativeHeight = 0;
		}

		for(int i = 0; i < numColPieces; i++) {
			for(int j = 0; j < numRowPieces; j++) {
				if(i == 0) {
					pieceGrid[i][j].upNeighbor = pieceGrid[i + 1][j];
					pieceGrid[i][j].downNeighbor = null;
				}
				else if(i == numColPieces - 1) {
					pieceGrid[i][j].upNeighbor = null;
					pieceGrid[i][j].downNeighbor = pieceGrid[i - 1][j];
				}
				else {
					pieceGrid[i][j].upNeighbor = pieceGrid[i + 1][j];
					pieceGrid[i][j].downNeighbor = pieceGrid[i - 1][j];
				}

				if(j == 0) {
					pieceGrid[i][j].leftNeighbor = null;
					pieceGrid[i][j].rightNeighbor = pieceGrid[i][j + 1];
				}
				else if(j == numRowPieces - 1) {
					pieceGrid[i][j].leftNeighbor = pieceGrid[i][j - 1];
					pieceGrid[i][j].rightNeighbor = null;
				}
				else {
					pieceGrid[i][j].leftNeighbor = pieceGrid[i][j - 1];
					pieceGrid[i][j].rightNeighbor = pieceGrid[i][j + 1];
				}
				progressBar.setIndeterminate(false);
				SwingUtilities.invokeLater(new Runnable(){
					@Override public void run(){progressBar.setValue((int)Math.ceil(100 * (tasksDone / ((double)(tasks)))));}
				});
				tasksDone++;
			}
		}

		startPuzzle.setEnabled(true);

		while(true) {
			try{Thread.sleep(25);} catch(InterruptedException e){}
			for(PuzzlePiece nextPiece: pzlPieceSet) {
				if(nextPiece.moving) {
					visited = new HashSet<PuzzlePiece>();
					nextPiece.attachedFrame.setLocation(MouseInfo.getPointerInfo().getLocation().x - me.getX(), MouseInfo.getPointerInfo().getLocation().y  - me.getY());
					checkAttachment(nextPiece);

				}
				else {

				}
			}


		}


	}


}
