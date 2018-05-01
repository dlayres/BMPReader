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
import javax.swing.border.EmptyBorder;

public class PuzzlePiece extends JPanel implements MouseListener{

	public JFrame attachedFrame = new JFrame();
	public JLabel pieceIcon;
	public BufferedImage bfdImg;
	public int width;
	public int height;
	public boolean moving = false;

	public PuzzlePiece(BufferedImage buffImg) {
		addMouseListener(this);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setBackground(new Color(0,0,0,0));
		bfdImg = buffImg;
		width = buffImg.getWidth();
		height = buffImg.getHeight();
		pieceIcon = new JLabel(new ImageIcon(buffImg));
		add(pieceIcon);
		repaint();
	}
	
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
			}
		}
		pieceIcon = new JLabel(new ImageIcon(bfdImg));
		add(pieceIcon);
		repaint();
	}


	public PuzzlePiece combine(PuzzlePiece other, String direction) {
		BufferedImage newImg = null;
		if(direction.equals("left") || direction.equals("right")) {
			newImg = new BufferedImage(this.width + other.width, Math.max(this.height, other.height), BufferedImage.TYPE_INT_ARGB);
			for(int i = 0; i < newImg.getWidth(); i++) {
				for(int j = 0; j < newImg.getHeight(); j++) {
					newImg.setRGB(i, j, 0);
				}
			}
			if(direction.equals("left")) {
				for(int i = 0; i < other.width; i++) {
					for(int j = 0; j < other.height; j++) {
						int rgb = other.bfdImg.getRGB(i, j);
						newImg.setRGB(i, j, rgb);
					}
				}
				for(int i = other.width; i < other.width + this.width; i++) {
					for(int j = 0; j < this.height; j++) {
						int rgb = this.bfdImg.getRGB(i - other.width, j);
						newImg.setRGB(i, j, rgb);
					}
				}
			}
			else {
				for(int i = 0; i < this.width; i++) {
					for(int j = 0; j < this.height; j++) {
						int rgb = this.bfdImg.getRGB(i, j);
						newImg.setRGB(i, j, rgb);
					}
				}
				for(int i = this.width; i < this.width + other.width; i++) {
					for(int j = 0; j < other.height; j++) {
						int rgb = other.bfdImg.getRGB(i - this.width, j);
						newImg.setRGB(i, j, rgb);
					}
				}
			}
		}
		else {
			newImg = new BufferedImage(Math.max(this.width, other.width), this.height + other.height, BufferedImage.TYPE_INT_ARGB);
			for(int i = 0; i < newImg.getWidth(); i++) {
				for(int j = 0; j < newImg.getHeight(); j++) {
					newImg.setRGB(i, j, 0);
				}
			}
			if(direction.equals("up")) {
				for(int i = 0; i < other.width; i++) {
					for(int j = 0; j < other.height; j++) {
						int rgb = other.bfdImg.getRGB(i, j);
						newImg.setRGB(i, j, rgb);
					}
				}
				for(int i = 0; i < this.width; i++) {
					for(int j = other.height; j < other.height + this.height; j++) {
						int rgb = this.bfdImg.getRGB(i, j - other.height);
						newImg.setRGB(i, j, rgb);
					}
				}
			}
			else {
				for(int i = 0; i < this.width; i++) {
					for(int j = 0; j < this.height; j++) {
						int rgb = this.bfdImg.getRGB(i, j);
						newImg.setRGB(i, j, rgb);
					}
				}
				for(int i = 0; i < other.width; i++) {
					for(int j = this.height; j < this.height + other.height; j++) {
						int rgb = other.bfdImg.getRGB(i, j - this.height);
						newImg.setRGB(i, j, rgb);
					}
				}
			}
		}
		
		PuzzlePiece  pzlPiece = new PuzzlePiece(newImg);
		return pzlPiece;
	}








	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		Graphics2D g2 = bfdImg.createGraphics();
//		g2.setColor(Color.BLUE);
//		g2.fillRect(1, 1, 50, 50);
	}







	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		moving = true;	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		moving = false;
		BMPReader.checkCloseness();
	}

}
