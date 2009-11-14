import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.xml.stream.XMLStreamException;

/**
 * 国土数値情報のオフラインビューアです。
 */
public class Main {
	/**
	 * メインメソッド
	 * @param args コマンドライン引数
	 * @throws XMLStreamException XMLストリーム例外
	 * @throws InterruptedException 割り込み例外
	 * @throws ParseException 解析例外
	 * @throws IOException 入出力例外
	 */
	public static void main(String[] args) throws XMLStreamException, InterruptedException, ParseException, IOException {
		JFrame frame = new JFrame("てすと");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setLayout(new BorderLayout(1, 1));
		final MapPanel mapPanel = new MapPanel();
		frame.add(mapPanel);
		mapPanel.setPreferredSize(new Dimension(200, 200));
		final ClockPanel clockPanel = new ClockPanel();
		frame.add(clockPanel, BorderLayout.BEFORE_FIRST_LINE);
		clockPanel.setPreferredSize(new Dimension(200, 30));
		final SpeedPanel speedPanel = new SpeedPanel();
		frame.add(speedPanel, BorderLayout.AFTER_LAST_LINE);
		speedPanel.setPreferredSize(new Dimension(200, 40));
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		mapPanel.points.addAll(new FacilityPointDataReader().read(new FileInputStream(new File("P02-06_13.xml"))));
		LatLong min = new LatLong(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		LatLong max = new LatLong(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (PointData point : mapPanel.points) {
			min.latitude = Math.min(min.latitude, point.latitude);
			min.longitude = Math.min(min.longitude, point.longitude);
			max.latitude = Math.max(max.latitude, point.latitude);
			max.longitude = Math.max(max.longitude, point.longitude);
		}
		mapPanel.railways.addAll(new RailwayLineDataReader(min, max).read(new FileInputStream(new File("N02-08.xml"))));
		mapPanel.roads.addAll(new RoadLineDataReader(new InputStreamReader(new FileInputStream(
				new File("N01_07L_台.txt")), "SJIS")).read(new FileInputStream(new File("N01-07L-2K-13.txt"))));
		Collection<PointData> points = new GPXPointDataReader()
				.read(new FileInputStream(new File("20090920-tokyo.gpx")));
		Interpolator interpolator = new Interpolator(points);
		mapPanel.zoom = 0.4;
		int offsetSec = -15;
		int division = 30;
		int previewSpeed = 4;
		Mode mode = Mode.RENDER;
//		process(mode, new Setting("../410", DateFormat.getDateTimeInstance().parse("2009/9/20 9:27:58"), 60 * 13 + 18, 0),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
//		process(mode, new Setting("../411", DateFormat.getDateTimeInstance().parse("2009/9/20 9:42:52"), 60 * 1 + 15, 60),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
//		process(mode, new Setting("../412", DateFormat.getDateTimeInstance().parse("2009/9/20 9:44:24"), 60 * 10 + 12, 120),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
//		process(mode, new Setting("../413", DateFormat.getDateTimeInstance().parse("2009/9/20 9:54:56"), 60 * 11 + 56, 300),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
//		process(mode, new Setting("../414", DateFormat.getDateTimeInstance().parse("2009/9/20 10:07:14"), 60 * 28 + 5, 120),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
//		process(mode, new Setting("../415", DateFormat.getDateTimeInstance().parse("2009/9/20 10:48:16"), 60 * 5 + 43, 600),
//				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
		process(mode, new Setting("../416", DateFormat.getDateTimeInstance().parse("2009/9/20 10:58:32"), 60 * 46 + 34, 600),
				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
		process(mode, new Setting("../417", DateFormat.getDateTimeInstance().parse("2009/9/20 12:12:14"), 60 * 2 + 30, 120),
				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
		process(mode, new Setting("../418", DateFormat.getDateTimeInstance().parse("2009/9/20 13:09:04"), 60 * 3 + 24, 120),
				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
		process(mode, new Setting("../419", DateFormat.getDateTimeInstance().parse("2009/9/20 13:37:40"), 60 * 1 + 35, 120),
				offsetSec, division, previewSpeed, frame, mapPanel, interpolator, clockPanel, speedPanel);
		if (mode == Mode.RENDER) {
			frame.dispose();
		}
	}

	/**
	 * 処理します。
	 * @param mode モード
	 * @param setting 設定
	 * @param offsetSec 時刻の補正[秒]。動画のタイムスタンプが遅れている場合、負の値を指定する。
	 * @param division 1秒あたりのフレーム数
	 * @param previewSpeed プレビュー時の速度
	 * @param frame フレーム
	 * @param panel パネル
	 * @param interpolator 補間を行うオブジェクト
	 * @param clockPanel 時計を表示するパネル
	 * @param speedPanel 速度を表示するパネル
	 * @throws InterruptedException 割り込み例外
	 * @throws IOException 入出力例外
	 */
	private static void process(Mode mode, Setting setting, int offsetSec, int division, int previewSpeed,
			JFrame frame, MapPanel panel, Interpolator interpolator, ClockPanel clockPanel, SpeedPanel speedPanel)
			throws InterruptedException, IOException {
		panel.gpsPoints.clear();
		for (int i = -setting.preSeconds * division; i < setting.seconds * division; i++) {
			Date date = new Date(setting.date.getTime() + 1000 * i / division + offsetSec * 1000);
			PointData point = interpolator.get(date);
			Point2D point2 = UTMUtil.toUTM(point.longitude, point.latitude);
			if (i % (division * 10) == 0) {
				panel.gpsPoints.add(point);
			}
			if (i < 0) {
				continue;
			}
			switch (mode) {
			case DEFAULT:
				if (i == 0) {
					panel.centerX = point2.getX();
					panel.centerY = point2.getY();
					panel.repaint();
					clockPanel.date = date;
					clockPanel.repaint();
					speedPanel.speed = 0;
					speedPanel.repaint();
				}
				continue;
			case PREVIEW:
				panel.centerX = point2.getX();
				panel.centerY = point2.getY();
				panel.repaint();
				if (i % division == 0) {
					clockPanel.date = date;
					clockPanel.repaint();
				}
				{
					int msec = 1000;
					PointData p1 = interpolator.get(new Date(date.getTime() - msec / 2));
					PointData p2 = interpolator.get(new Date(date.getTime() + msec / 2));
					speedPanel.speed = UTMUtil.toUTM(p1.longitude, p1.latitude).distance(
							UTMUtil.toUTM(p2.longitude, p2.latitude))
							* 3600 / 1000 * 1000 / msec;
				}
				speedPanel.repaint();
				Thread.sleep(1000 / division / previewSpeed);
				break;
			case RENDER:
				panel.centerX = point2.getX();
				panel.centerY = point2.getY();
				panel.repaint();
				BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_BGR);
				panel.draw(image.createGraphics());
				new File(setting.directory).mkdir();
				ImageIO.write(image, "PNG", new File(new Formatter()
						.format("%s/map-%06d.png", setting.directory, i + 1).toString()));
				if (i % division == 0) {
					clockPanel.date = date;
					clockPanel.repaint();
					BufferedImage clockImage = new BufferedImage(clockPanel.getWidth(), clockPanel.getHeight(),
							BufferedImage.TYPE_INT_BGR);
					clockPanel.draw(clockImage.createGraphics());
					ImageIO.write(clockImage, "PNG", new File(new Formatter().format("%s/clock-%04d.png",
							setting.directory, i / division + 1).toString()));
				}
				{
					int msec = 1000;
					PointData p1 = interpolator.get(new Date(date.getTime() - msec / 2));
					PointData p2 = interpolator.get(new Date(date.getTime() + msec / 2));
					speedPanel.speed = UTMUtil.toUTM(p1.longitude, p1.latitude).distance(
							UTMUtil.toUTM(p2.longitude, p2.latitude))
							* 3600 / 1000 * 1000 / msec;
				}
				speedPanel.repaint();
				BufferedImage speedImage = new BufferedImage(speedPanel.getWidth(), speedPanel.getHeight(),
						BufferedImage.TYPE_INT_BGR);
				speedPanel.draw(speedImage.createGraphics());
				ImageIO.write(speedImage, "PNG", new File(new Formatter().format("%s/speed-%06d.png",
						setting.directory, i + 1).toString()));
				break;
			}
		}
	}

	/**
	 * 表示モード
	 */
	public static enum Mode {
		/**
		 * デフォルト
		 */
		DEFAULT,
		/**
		 * 自動再生のプレビュー
		 */
		PREVIEW,
		/**
		 * 画像ファイルにレンダリング
		 */
		RENDER,
	}

	/**
	 * 設定をカプセル化するクラス
	 */
	public static class Setting {
		/**
		 * 生成するサブディレクトリ名
		 */
		String directory;
		/**
		 * 撮影開始時刻
		 */
		Date date;
		/**
		 * 撮影時間[秒]
		 */
		int seconds;
		/**
		 * 事前に表示するGPS軌跡の長さ[秒]
		 */
		int preSeconds;

		/**
		 * コンストラクタです。
		 * @param directory 生成するサブディレクトリ名
		 * @param date 撮影開始時刻
		 * @param seconds 撮影時間[秒]
		 * @param preSeconds 事前に表示するGPS軌跡の長さ[秒]
		 */
		public Setting(String directory, Date date, int seconds, int preSeconds) {
			this.directory = directory;
			this.date = date;
			this.seconds = seconds;
			this.preSeconds = preSeconds;
		}
	}
}
