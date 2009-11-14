import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

/**
 * 地図を表示するパネルです。
 */
public class MapPanel extends JPanel {

	/**
	 * 点データの一覧
	 */
	public List<PointData> points;

	/**
	 * 鉄道データの一覧
	 */
	public List<LineData> railways;

	/**
	 * 道路データの一覧
	 */
	public List<LineData> roads;

	/**
	 * GPSログに含まれる点データの一覧
	 */
	public List<PointData> gpsPoints;

	/**
	 * 直前のマウスポインタ
	 */
	MouseEvent lastMouse;

	/**
	 * 中心のX座標[m]
	 */
	public double centerX;

	/**
	 * 中心のY座標[m]
	 */
	public double centerY;

	/**
	 * 表示倍率[px/m]
	 */
	public double zoom;

	/**
	 * 地図を表示するパネルを初期化します。
	 */
	public MapPanel() {
		this.points = new CopyOnWriteArrayList<PointData>();
		this.railways = new CopyOnWriteArrayList<LineData>();
		this.roads = new CopyOnWriteArrayList<LineData>();
		this.gpsPoints = new CopyOnWriteArrayList<PointData>();
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				MapPanel.this.lastMouse = e;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				MapPanel.this.centerX -= (e.getX() - MapPanel.this.lastMouse.getX()) / MapPanel.this.zoom;
				MapPanel.this.centerY += (e.getY() - MapPanel.this.lastMouse.getY()) / MapPanel.this.zoom;
				MapPanel.this.lastMouse = e;
				repaint();
			}
		});
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				MapPanel.this.centerX += (e.getX() - getWidth() / 2) / MapPanel.this.zoom;
				MapPanel.this.centerY -= (e.getY() - getHeight() / 2) / MapPanel.this.zoom;
				if (e.getWheelRotation() > 0) {
					MapPanel.this.zoom *= 1.1;
				} else {
					MapPanel.this.zoom /= 1.1;
				}
				MapPanel.this.centerX -= (e.getX() - getWidth() / 2) / MapPanel.this.zoom;
				MapPanel.this.centerY += (e.getY() - getHeight() / 2) / MapPanel.this.zoom;
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		try {
			draw((Graphics2D) graphics);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 地図を描画します。
	 * @param g 描画対象
	 * @throws IOException 入出力例外
	 */
	public void draw(Graphics2D g) throws IOException {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setFont(new Font("メイリオ", Font.PLAIN, 16));
		double radius = 3;
		TreeMap<Double, Point2D> fixedPoints = new TreeMap<Double, Point2D>();
		// 一般道路
		int principalPrefectualRoadWidth = (int) Math.max(1, 4 * this.zoom);
		int nationalRoadWidth = (int) Math.max(2, 6 * this.zoom);
		for (LineData line : this.roads) {
			Path2D path = toPath(line);
			if (path == null
					|| !path.intersects(-principalPrefectualRoadWidth, -principalPrefectualRoadWidth, getWidth()
							+ principalPrefectualRoadWidth * 2, getHeight() + principalPrefectualRoadWidth * 2)) {
				continue;
			}
			switch (line.roadTypeCode) {
			case NATIONAL_ROAD:
				g.setStroke(new BasicStroke(nationalRoadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(Color.ORANGE);
				g.draw(path);
				break;
			case PRINCIPAL_PREFECTUAL_ROAD:
				g
						.setStroke(new BasicStroke(principalPrefectualRoadWidth, BasicStroke.CAP_ROUND,
								BasicStroke.JOIN_ROUND));
				g.setColor(Color.YELLOW);
				g.draw(path);
				break;
			default:
			}
		}
		// 鉄道
		g.setColor(Color.WHITE);
		Collection<LineData> stations = new ArrayList<LineData>();
		int stationWidth = (int) Math.max(4, 20 * this.zoom);
		int jrWidth = (int) Math.max(2, 8 * this.zoom);
		int privateRailwayWidth = (int) Math.max(2, 6 * this.zoom);
		int lineWidthTwice = (int) Math.max(3, 3 * this.zoom);
		float jrDash = (float) Math.max(6, 50 * this.zoom);
		for (LineData line : this.railways) {
			Path2D path = toPath(line);
			if (path == null
					|| !path.intersects(-stationWidth, -stationWidth, getWidth() + stationWidth * 2, getHeight()
							+ stationWidth * 2)) {
				continue;
			}
			if (line.stationName == null) {
				if (line.railwayClassCode == Const.RailwayClassCode.JR) {
					g
							.setStroke(new BasicStroke(jrWidth + lineWidthTwice, BasicStroke.CAP_BUTT,
									BasicStroke.JOIN_ROUND));
					g.draw(path);
					g.setStroke(new BasicStroke(jrWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f,
							line.institutionTypeCode == Const.InstitutionTypeCode.JR ? new float[] { jrDash, jrDash }
									: new float[] { jrDash * 2, jrDash * 2 }, 0));
					g.setColor(Color.BLACK);
					g.draw(path);
					g.setColor(Color.WHITE);
				} else {
					g.setStroke(new BasicStroke(privateRailwayWidth));
					g.draw(path);
				}
			} else {
				stations.add(line);
			}
		}
		// 駅の枠線
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(stationWidth + lineWidthTwice, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		for (LineData line : stations) {
			Path2D path = toPath(line);
			g.draw(path);
		}
		// 駅の塗りつぶし
		for (LineData line : stations) {
			Path2D path = toPath(line);
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(stationWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
			g.draw(path);
		}
		// 高速道路
		int highwayWidth = (int) Math.max(3, 8 * this.zoom);
		g.setStroke(new BasicStroke(highwayWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (LineData line : this.roads) {
			if (line.roadTypeCode == Const.RoadTypeCode.HIGHWAY) {
				Path2D path = toPath(line);
				if (path == null
						|| !path.intersects(-principalPrefectualRoadWidth, -principalPrefectualRoadWidth, getWidth()
								+ principalPrefectualRoadWidth * 2, getHeight() + principalPrefectualRoadWidth * 2)) {
					continue;
				}
				g.setColor(Color.GREEN);
				g.draw(path);
			}
		}
		// 駅名
		for (LineData line : stations) {
			Path2D path = toPath(line);
			if (this.zoom >= 0.2) {
				double x = path.getBounds2D().getCenterX();
				double y = path.getBounds2D().getCenterY();
				fixedPoints.put(x, new Point2D.Double(x, y));
			}
		}
		// 公共施設
		g.setColor(Color.WHITE);
		int imageRadius = 40;
		for (PointData point : this.points) {
			Point2D point2D = UTMUtil.toUTM(point.longitude, point.latitude);
			double x = (point2D.getX() - this.centerX) * this.zoom + getWidth() / 2;
			double y = getHeight() / 2 - (point2D.getY() - this.centerY) * this.zoom;
			if (x + imageRadius > 0 && x < getWidth() + imageRadius && y + imageRadius > 0
					&& y < getHeight() + imageRadius) {
				if (this.zoom >= 0.2) {
					switch (point.roughCode) {
					case POST:
					case HOSPITAL:
					case FIRE: {
						BufferedImage image = point.roughCode.getImage();
						if (!overLaps(x, y, image.getWidth(), fixedPoints)) {
							drawSymbol(g, image, x, y);
							fixedPoints.put(x, new Point2D.Double(x, y));
						}
						break;
					}
					default:
						switch (point.detailCode) {
						case ART_MUSEUM:
							if (!overLaps(x, y, g.getFontMetrics().getHeight(), fixedPoints)) {
								drawSymbol(g, "美", x, y);
								fixedPoints.put(x, new Point2D.Double(x, y));
							}
							break;
						case NURSERY:
							if (!overLaps(x, y, g.getFontMetrics().getHeight(), fixedPoints)) {
								drawSymbol(g, "保", x, y);
								fixedPoints.put(x, new Point2D.Double(x, y));
							}
							break;
						case CHILDREN_HOUSE:
							if (!overLaps(x, y, g.getFontMetrics().getHeight(), fixedPoints)) {
								drawSymbol(g, "児", x, y);
								fixedPoints.put(x, new Point2D.Double(x, y));
							}
							break;
						case KINDERGARTEN:
							if (!overLaps(x, y, g.getFontMetrics().getHeight(), fixedPoints)) {
								drawSymbol(g, "幼", x, y);
								fixedPoints.put(x, new Point2D.Double(x, y));
							}
							break;
						default:
							if (point.detailCode.hasImageFile()) {
								BufferedImage image = point.detailCode.getImage();
								if (!overLaps(x, y, image.getWidth(), fixedPoints)) {
									drawSymbol(g, image, x, y);
									fixedPoints.put(x, new Point2D.Double(x, y));
								}
								//							} else {
								//								g.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
								//								Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(point.label, g);
								//								g.draw(new Rectangle2D.Double(x + radius * 2, y - labelBounds.getHeight() / 2,
								//										labelBounds.getWidth(), labelBounds.getHeight()));
								//								drawString(g, point.label, x + radius * 2, y + labelBounds.getHeight() / 2
								//										- g.getFontMetrics().getDescent(), false);
							}
						}
					}
				}
			}
		}
		// 駅名
		g.setStroke(new BasicStroke(3));
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		for (LineData line : stations) {
			Path2D path = toPath(line);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line.stationName, g);
			if (this.zoom >= 0.2) {
				double x = path.getBounds2D().getCenterX();
				double y = path.getBounds2D().getCenterY();
				//				g.draw(new Rectangle2D.Double(x - stringBounds.getWidth() / 2, y - stringBounds.getHeight() / 2,
				//						stringBounds.getWidth(), stringBounds.getHeight()));
				g.setColor(Color.BLACK);
				drawString(g, line.stationName, x - stringBounds.getWidth() / 2, y + stringBounds.getHeight() / 2
						- g.getFontMetrics().getDescent(), true);
				g.setColor(Color.WHITE);
				drawString(g, line.stationName, x - stringBounds.getWidth() / 2, y + stringBounds.getHeight() / 2
						- g.getFontMetrics().getDescent(), false);
			}
		}
		// GPS軌跡
		g.setColor(Color.RED);
		for (PointData point : this.gpsPoints) {
			Point2D point2D = UTMUtil.toUTM(point.longitude, point.latitude);
			double x = (point2D.getX() - this.centerX) * this.zoom + getWidth() / 2;
			double y = getHeight() / 2 - (point2D.getY() - this.centerY) * this.zoom;
			if (x + radius > 0 && x < getWidth() + radius && y + radius > 0 && y < getHeight() + radius) {
				g.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
			}
		}
	}

	/**
	 * 指定した点と描画済みの点の距離が指定した距離未満かどうかを調べます。
	 * @param x X座標
	 * @param y Y座標
	 * @param distance 距離
	 * @param fixedPoints 描画済みの点の一覧
	 * @return 重なるかどうか
	 */
	private boolean overLaps(double x, double y, double distance, TreeMap<Double, Point2D> fixedPoints) {
		for (Map.Entry<Double, Point2D> entry : fixedPoints.subMap(new Double(x - distance), new Double(x + distance))
				.entrySet()) {
			if (entry.getValue().distanceSq(x, y) < distance * distance) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 線データを描画可能なPath2Dオブジェクトに変換します。
	 * @param line 線データ
	 * @return Path2Dオブジェクト
	 */
	private Path2D toPath(LineData line) {
		Path2D path = null;
		for (PointData point : line.points) {
			Point2D point2D = UTMUtil.toUTM(point.longitude, point.latitude);
			double x = (point2D.getX() - this.centerX) * this.zoom + getWidth() / 2;
			double y = getHeight() / 2 - (point2D.getY() - this.centerY) * this.zoom;
			if (path == null) {
				path = new Path2D.Double(Path2D.WIND_NON_ZERO, line.points.size());
				path.moveTo(x, y);
			} else {
				path.lineTo(x, y);
			}
		}
		return path;
	}

	/**
	 * 地図記号を描画します。
	 * @param g 描画対象
	 * @param image 記号の画像
	 * @param x X座標[px]
	 * @param y Y座標[px]
	 */
	private void drawSymbol(Graphics2D g, BufferedImage image, double x, double y) {
		AffineTransform transform = new AffineTransform();
		transform.translate(x, y);
		transform.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		g.drawImage(image, transform, null);
	}

	/**
	 * 文字を描画します。
	 * @param g 描画対象
	 * @param symbol 文字
	 * @param x X座標[px]
	 * @param y Y座標[px]
	 */
	private void drawSymbol(Graphics2D g, String symbol, double x, double y) {
		Rectangle2D symbolBounds = g.getFontMetrics().getStringBounds(symbol, g);
		//		g.draw(new Rectangle2D.Double(x - symbolBounds.getWidth() / 2, y - symbolBounds.getHeight() / 2, symbolBounds
		//				.getWidth(), symbolBounds.getHeight()));
		drawString(g, symbol, x - symbolBounds.getWidth() / 2, y + symbolBounds.getHeight() / 2
				- g.getFontMetrics().getDescent(), false);
	}

	/**
	 * 自動的に表示倍率を設定します。
	 */
	public void zoomAutomatically() {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (PointData point : this.gpsPoints) {
			Point2D point2D = UTMUtil.toUTM(point.longitude, point.latitude);
			minX = Math.min(minX, point2D.getX());
			maxX = Math.max(maxX, point2D.getX());
			minY = Math.min(minY, point2D.getY());
			maxY = Math.max(maxY, point2D.getY());
		}
		this.centerX = (minX + maxX) / 2;
		this.centerY = (minY + maxY) / 2;
		double width = maxX - minX;
		double height = maxY - minY;
		double zoomX = this.getWidth() / width;
		double zoomY = this.getHeight() / height;
		this.zoom = Math.min(zoomX, zoomY);
	}

	/**
	 * 文字列を描画します。
	 * @param g 描画対象
	 * @param string 文字列
	 * @param x X座標
	 * @param y Y座標
	 * @param isOutline 白抜きにするかどうか
	 */
	void drawString(Graphics2D g, String string, double x, double y, boolean isOutline) {
		TextLayout layout = new TextLayout(string, g.getFont(), g.getFontRenderContext());
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.translate(x, y);
		if (isOutline) {
			g.draw(layout.getOutline(affineTransform));
		} else {
			g.fill(layout.getOutline(affineTransform));
		}
	}
}
