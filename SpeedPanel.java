import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * 速度を表示するパネルです。
 */
public class SpeedPanel extends JPanel {

	/**
	 * 時刻
	 */
	public double speed;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw((Graphics2D) g);
	}

	/**
	 * 時刻を描画します。
	 * @param g 描画対象
	 */
	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(new Font("Seoge", Font.BOLD, getHeight()));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(Color.WHITE);
		int x = g.getFontMetrics().stringWidth("00");
		int y = getHeight() - g.getFontMetrics().getDescent() / 2;
		String integer = String.valueOf((int) this.speed);
		g.drawString(integer, x - g.getFontMetrics().stringWidth(integer), y);
		g.setFont(new Font("Seoge", Font.BOLD, getHeight() / 2));
		g.drawString(String.valueOf((int) (this.speed * 10) % 10), x + getHeight() / 15, g.getFontMetrics().getHeight()
				- g.getFontMetrics().getDescent());
		g.setFont(new Font("Seoge", Font.PLAIN, getHeight() / 3));
		g.drawString("km/h", x + getHeight() / 20, y);
	}
}
