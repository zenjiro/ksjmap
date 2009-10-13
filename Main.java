import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
		mapPanel.lines.addAll(new RailwayLineDataReader(min, max).read(new FileInputStream(new File("N02-08.xml"))));
		Collection<PointData> points = new GPXPointDataReader().read(new FileInputStream(new File(
				"20090913-yamanote.gpx")));
		Interpolator interpolator = new Interpolator(points);
		mapPanel.zoom = 0.3;
		int division = 1;
		int offsetSec = 0; // -16
		DateFormat format = DateFormat.getDateTimeInstance();
		Mode mode = Mode.RENDER;
		// 品川→田町
		Setting map379 = new Setting("map379", format.parse("2009/9/13 12:02:50"), 6 * 60 + 1, 0);
		// 田町→浜松町
		Setting map380 = new Setting("map380", format.parse("2009/9/13 12:14:36"), 4 * 60 + 52, 0);
		// 浜松町→新橋
		Setting map381 = new Setting("map381", format.parse("2009/9/13 13:02:34"), 4 * 60 + 16, 0);
		// 新橋→有楽町
		Setting map383 = new Setting("map383", format.parse("2009/9/13 13:13:42"), 4 * 60 + 1, 0);
		// 有楽町→東京
		Setting map384 = new Setting("map384", format.parse("2009/9/13 13:21:20"), 3 * 60 + 4, 0);
		// 東京→途中（淡路町）
		Setting map385 = new Setting("map385", format.parse("2009/9/13 13:31:00"), 5 * 60 + 51, 0);
		// 途中（淡路町）→神田、ちょっとカメラ右に傾いた
		Setting map386 = new Setting("map386", format.parse("2009/9/13 13:41:44"), 4 * 60 + 18, 600);
		// 神田→秋葉原
		Setting map387 = new Setting("map387", format.parse("2009/9/13 13:50:42"), 2 * 60 + 39, 0);
		// 秋葉原→御徒町
		Setting map388 = new Setting("map388", format.parse("2009/9/13 14:00:26"), 4 * 60 + 40, 0);
		// 御徒町→上野、アメ横は人が多いのでカットしよう。
		Setting map389 = new Setting("map389", format.parse("2009/9/13 14:07:28"), 4 * 60 + 12, 0);
		// 上野→途中（上野中）
		Setting map390 = new Setting("map390", format.parse("2009/9/13 14:15:50"), 7 * 60 + 14, 0);
		// 途中（上野中）→鶯谷
		Setting map391 = new Setting("map391", format.parse("2009/9/13 14:24:16"), 1 * 60 + 51, 600);
		// 鶯谷→日暮里
		Setting map392 = new Setting("map392", format.parse("2009/9/13 14:29:02"), 6 * 60 + 42, 0);
		// 日暮里→途中（東日暮里5）
		Setting map393 = new Setting("map393", format.parse("2009/9/13 14:41:14"), 1 * 60 + 18, 0);
		// 途中（東日暮里5）→途中（根岸小）
		Setting map394 = new Setting("map394", format.parse("2009/9/13 14:43:48"), 1 * 60 + 28, 2 * 60 + 34);
		// 途中（根岸小）→西日暮里
		Setting map395 = new Setting("map395", format.parse("2009/9/13 14:49:52"), 7 * 60 + 9, 8 * 60 + 38);
		// 西日暮里→途中
		Setting map396 = new Setting("map396", format.parse("2009/9/13 15:01:40"), 2 * 60 + 6, 0);
		// 途中→田端
		Setting map397 = new Setting("map397", format.parse("2009/9/13 15:04:42"), 4 * 60 + 51, 182);
		for (Setting setting : new Setting[] { new Setting("01-gotanda", format.parse("2009/9/13 11:24:00"), 1, 0),
				new Setting("02-osaki", format.parse("2009/9/13 11:35:00"), 1, 0),
				new Setting("03-shinagawa", format.parse("2009/9/13 11:50:00"), 1, 0),
				new Setting("04-tamachi", format.parse("2009/9/13 12:10:00"), 1, 0),
				new Setting("05-hamamatsucho", format.parse("2009/9/13 12:24:00"), 1, 0),
				new Setting("06-shinbashi", format.parse("2009/9/13 13:09:00"), 1, 0),
				new Setting("07-yurakucho", format.parse("2009/9/13 13:19:00"), 1, 0),
				new Setting("08-tokyo", format.parse("2009/9/13 13:27:00"), 1, 0),
				new Setting("09-kanda", format.parse("2009/9/13 13:48:00"), 1, 0),
				new Setting("10-akihabara", format.parse("2009/9/13 13:57:00"), 1, 0),
				new Setting("11-okachimachi", format.parse("2009/9/13 14:06:00"), 1, 0),
				new Setting("12-ueno", format.parse("2009/9/13 14:13:00"), 1, 0),
				new Setting("13-uguisudani", format.parse("2009/9/13 14:27:00"), 1, 0),
				new Setting("14-nippori", format.parse("2009/9/13 14:38:00"), 1, 0),
				new Setting("15-nishinippori", format.parse("2009/9/13 14:59:00"), 1, 0),
				new Setting("16-tabashi", format.parse("2009/9/13 15:10:00"), 1, 0),
				new Setting("17-komagome", format.parse("2009/9/13 15:37:00"), 1, 0),
				new Setting("18-sugamo", format.parse("2009/9/13 15:48:00"), 1, 0),
				new Setting("19-otsuka", format.parse("2009/9/13 15:59:00"), 1, 0),
				new Setting("20-ikebukuro", format.parse("2009/9/13 16:14:00"), 1, 0),
				new Setting("21-mejiro", format.parse("2009/9/13 16:32:00"), 1, 0),
				new Setting("22-takadanobaba", format.parse("2009/9/13 16:42:00"), 1, 0),
				new Setting("23-shinokubo", format.parse("2009/9/13 16:54:00"), 1, 0),
				new Setting("24-shinjuku", format.parse("2009/9/13 17:05:00"), 1, 0),
				new Setting("25-yoyogi", format.parse("2009/9/13 17:15:00"), 1, 0),
				new Setting("26-harajuku", format.parse("2009/9/13 17:30:00"), 1, 0),
				new Setting("27-shibuya", format.parse("2009/9/13 17:44:00"), 1, 0),
				new Setting("28-ebisu", format.parse("2009/9/13 18:25:00"), 1, 0),
				new Setting("29-meguro", format.parse("2009/9/13 18:42:00"), 1, 0),
				new Setting("30-gotanda", format.parse("2009/9/13 18:51:00"), 1, 0), }) {
			process(mode, setting, offsetSec, division, frame, mapPanel, interpolator, clockPanel);
		}
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
	 * @param frame フレーム
	 * @param panel パネル
	 * @param interpolator 補間を行うオブジェクト
	 * @param clockPanel 時計を表示するパネル
	 * @throws InterruptedException 割り込み例外
	 * @throws IOException 入出力例外
	 */
	private static void process(Mode mode, Setting setting, int offsetSec, int division, JFrame frame, MapPanel panel,
			Interpolator interpolator, ClockPanel clockPanel) throws InterruptedException, IOException {
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
				Thread.sleep(1000 / division);
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
					new File(setting.directory).mkdir();
					ImageIO.write(clockImage, "PNG", new File(new Formatter().format("%s/clock-%04d.png",
							setting.directory, i / division + 1).toString()));
				}
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
