import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Date;
import java.util.Formatter;

import javax.swing.JPanel;

/**
 * 現在時刻を表示するパネルです。
 */
public class ClockPanel extends JPanel {

	/**
	 * 時刻
	 */
	public Date date;

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
		g.drawString(this.date == null ? "" : new Formatter().format("%tk:%tM", this.date, this.date).toString(), 0,
				getHeight() - g.getFontMetrics().getDescent() / 2);
	}
}
