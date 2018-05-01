import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	public Color[] pixelColors = new Color[0];
	public int width = 1;
	public int height = 1;
	public int res = 5;
	
	public ImagePanel(ImagePanel panel) {
		
	}
	
	public ImagePanel() {
		Path path = Paths.get("MARBLES.bmp");
		try {
			
			byte[] fileContents =  Files.readAllBytes(path);
			String[] hexContents = new String[fileContents.length];
			int[] intContents = new int[fileContents.length];
			
			for(int i = 0; i < fileContents.length; i++) {
				hexContents[i] = BMPReader.intToHex(BMPReader.byteToInt(fileContents[i]));
				intContents[i] = BMPReader.byteToInt(fileContents[i]);
			}
			
			if(!((hexContents[0].equals("42")) && (hexContents[1].equals("4d")))) {
				System.out.println("This is not a valid bmp file");
				return;
			}
			
			int fileLength = BMPReader.hexToInt(hexContents[5] + hexContents[4] + hexContents[3] + hexContents[2]);
			int offset = BMPReader.hexToInt(hexContents[13] + hexContents[12] + hexContents[11] + hexContents[10]);
			width = BMPReader.hexToInt(hexContents[21] + hexContents[20] + hexContents[19] + hexContents[18]);
			height = BMPReader.hexToInt(hexContents[25] + hexContents[24] + hexContents[23] + hexContents[22]);
			
			
			pixelColors = new Color[width * height];
			
			if(((fileLength - offset) / 3) > (width * height)) {
				int index = 0;
				for(int i = 0; i < ((fileLength - offset) / 3); i++) {
					if(i % width == 0 && (i != 0)) {
						continue;
					}
					else {
						pixelColors[index] = new Color(BMPReader.hexToInt(hexContents[(i * 3) + offset + 2]), BMPReader.hexToInt(hexContents[(i * 3) + offset + 1]), BMPReader.hexToInt(hexContents[(i * 3) + offset]));
						index++;
					}
				}
			}
			else {
				pixelColors = new Color[(fileLength - offset) / 3];
				for(int i = 0; i < ((fileLength - offset) / 3); i++) {
					pixelColors[i] = new Color(BMPReader.hexToInt(hexContents[(i * 3) + offset + 2]), BMPReader.hexToInt(hexContents[(i * 3) + offset + 1]), BMPReader.hexToInt(hexContents[(i * 3) + offset]));
				}
			}
			
			//int j = 1419;
			//System.out.println(pixelColors[j].getRed() + " " + pixelColors[j].getGreen() + " " + pixelColors[j].getBlue());
			
			setSize(width * res, height * res);
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i = 0; i < pixelColors.length; i++) {
			int col = res * (i % width);
			int row = res * (height - (i / width) - 1);
			g.setColor(pixelColors[i]);
			g.fillRect(col, row, res, res);
			g.setColor(new Color(0, 0, 0));
		}
	}
	
	public void paintAgain() {
		repaint();
	}
	
	
}
