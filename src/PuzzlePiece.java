import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class PuzzlePiece extends JPanel implements MouseListener{

	public JFrame attachedFrame = new JFrame();
	public JLabel pieceIcon;
	public BufferedImage bfdImg;
	public int width;
	public int height;
	public boolean moving = false;
	PuzzlePiece leftNeighbor;
	PuzzlePiece rightNeighbor;
	PuzzlePiece upNeighbor;
	PuzzlePiece downNeighbor;
	boolean leftConnected = false;
	boolean rightConnected = false;
	boolean upConnected = false;
	boolean downConnected = false;
	
	public PuzzlePiece(ImagePanel imgPanel) {
		width = imgPanel.width;
		height = imgPanel.height;
		Color[] pix = imgPanel.pixelColors;
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setBackground(new Color(0,0,0,0));
		addMouseListener(this);
		bfdImg = new BufferedImage(imgPanel.width, imgPanel.height, BufferedImage.TYPE_INT_ARGB);
		for(int i = 0; i < imgPanel.width; i++) {
			for(int j = 0; j < imgPanel.height; j++) {
				int colorsI = i;
				int colorsJ = imgPanel.height - j - 1;
				int rgb = pix[colorsJ * imgPanel.width + colorsI].getRGB();
				bfdImg.setRGB(i, j, rgb);
				BMPReader.progressBar.setIndeterminate(false);
				SwingUtilities.invokeLater(new Runnable(){
					@Override public void run(){BMPReader.progressBar.setValue((int)Math.ceil(100 * (BMPReader.tasksDone / ((double)(BMPReader.tasks)))));}
				});
				BMPReader.tasksDone++;
			}
		}
		pieceIcon = new JLabel(new ImageIcon(bfdImg));
		add(pieceIcon);
		repaint();
	}



	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		BMPReader.me = e;
		moving = true;	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		moving = false;
		BMPReader.checkCloseness();
	}

}
