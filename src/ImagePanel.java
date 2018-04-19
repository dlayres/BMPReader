import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	public Color[] pixelColors = new Color[0];
	public int width = 1;
	public int height = 1;
	public int res = 5;
	
	public ImagePanel() {
		Path path = Paths.get("FLAG_B24.bmp");
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
			
			
			pixelColors = new Color[(fileLength - offset + 3) / 3];
			
			//System.out.println((fileLength - offset) / 3 + " " + width * height);
			
			if(((fileLength - offset) / 3) > (width * height)) {
				int jOffset = 0;
				boolean justOffset = false;
				for(int i = 0; i < ((fileLength - offset) / 3); i++) {
					if(((i - jOffset) % 51 == 0) && (i != 0) && (justOffset == false)) {
						jOffset++;
						justOffset = true;
						continue;
					}
					pixelColors[i - jOffset] = new Color(BMPReader.hexToInt(hexContents[(i * 3) + offset + 2]), BMPReader.hexToInt(hexContents[(i * 3) + offset + 1]), BMPReader.hexToInt(hexContents[(i * 3) + offset]));
					justOffset = false;
				}
			}
			else {
				for(int i = 0; i < ((fileLength - offset) / 3); i++) {
					pixelColors[i] = new Color(BMPReader.hexToInt(hexContents[(i * 3) + offset + 2]), BMPReader.hexToInt(hexContents[(i * 3) + offset + 1]), BMPReader.hexToInt(hexContents[(i * 3) + offset]));
				}
			}
			

			
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
