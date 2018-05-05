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
import javax.swing.SwingUtilities;

public class ImagePanel extends JPanel{

	public Color[] pixelColors = new Color[0];
	public int width;
	public int height;
	public int fileLength;
	public int offset;
	public int res = 5;

	public ImagePanel(ImagePanel panel) {

	}

	public ImagePanel() {
		Path path = Paths.get("MARBLES.bmp");
		try {

			byte[] fileContents =  Files.readAllBytes(path);
			String[] hexContents = new String[fileContents.length];
			int[] intContents = new int[fileContents.length];

			width = BMPReader.hexToInt(BMPReader.intToHex(BMPReader.byteToInt(fileContents[21])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[20])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[19])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[18])));
			height = BMPReader.hexToInt(BMPReader.intToHex(BMPReader.byteToInt(fileContents[25])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[24])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[23])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[22])));
			fileLength = BMPReader.hexToInt(BMPReader.intToHex(BMPReader.byteToInt(fileContents[5])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[4])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[3])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[2])));
			offset = BMPReader.hexToInt(BMPReader.intToHex(BMPReader.byteToInt(fileContents[13])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[12])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[11])) + BMPReader.intToHex(BMPReader.byteToInt(fileContents[10])));

			BMPReader.tasks+=fileContents.length;
			BMPReader.tasks+=(((fileLength - offset) / 3) + (width * height));
			for(int i = 0; i < fileContents.length; i++) {
				BMPReader.progressBar.setIndeterminate(false);
				SwingUtilities.invokeLater(new Runnable(){
					@Override public void run(){BMPReader.progressBar.setValue((int)Math.ceil(100 * (BMPReader.tasksDone / ((double)(BMPReader.tasks)))));}
				});
				hexContents[i] = BMPReader.intToHex(BMPReader.byteToInt(fileContents[i]));
				intContents[i] = BMPReader.byteToInt(fileContents[i]);
				BMPReader.tasksDone++;
			}

			if(!((hexContents[0].equals("42")) && (hexContents[1].equals("4d")))) {
				System.out.println("This is not a valid bmp file");
				return;
			}


			pixelColors = new Color[width * height];

			if(((fileLength - offset) / 3) > (width * height)) {
				int index = 0;
				for(int i = 0; i < ((fileLength - offset) / 3); i++) {
					double completion = 82 + (18 * (((double)(i)) / ((fileLength - offset) / 3.0)));
					BMPReader.progressBar.setIndeterminate(false);
					SwingUtilities.invokeLater(new Runnable(){
						@Override public void run(){BMPReader.progressBar.setValue((int)Math.ceil(100 * (BMPReader.tasksDone / ((double)(BMPReader.tasks)))));}
					});
					BMPReader.tasksDone++;
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
					BMPReader.progressBar.setIndeterminate(false);
					SwingUtilities.invokeLater(new Runnable(){
						@Override public void run(){BMPReader.progressBar.setValue((int)Math.ceil(100 * (BMPReader.tasksDone / ((double)(BMPReader.tasks)))));}
					});
					BMPReader.tasksDone++;
				}
			}


			setSize(width * res, height * res);




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
