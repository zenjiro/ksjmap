import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * 点列の補間をするクラスです。
 */
public class Interpolator {
	/**
	 * 点列
	 */
	public NavigableMap<Date, PointData> points;

	/**
	 * コンストラクタです。
	 */
	public Interpolator() {
		this.points = new TreeMap<Date, PointData>();
	}

	/**
	 * 点列を指定して点列の補間をするオブジェクトを初期化します。
	 * @param points 点列
	 * @throws ParseException 解析例外
	 */
	public Interpolator(Collection<PointData> points) throws ParseException {
		this.points = new TreeMap<Date, PointData>();
		addAll(points);
	}

	/**
	 * 点を追加します。
	 * 点のlabelフィールドが時刻として解釈されます。
	 * @param point 点
	 * @return 点列の補間をするオブジェクト
	 * @throws ParseException 解析例外
	 */
	public Interpolator add(PointData point) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date date = format.parse(point.label.replaceFirst("Z$", "+0000"));
		this.points.put(date, point);
		return this;
	}

	/**
	 * 点列を追加します。
	 * 点列のlabelフィールドが時刻として解釈されます。
	 * @param points 点列
	 * @return 点列の補間をするオブジェクト
	 * @throws ParseException 解析例外
	 */
	public Interpolator addAll(Collection<PointData> points) throws ParseException {
		for (PointData point : points) {
			add(point);
		}
		return this;
	}

	/**
	 * 指定した時刻の座標を取得します。
	 * @param date 時刻
	 * @return 座標
	 */
	public PointData get(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
		Map.Entry<Date, PointData> entry1 = this.points.floorEntry(date);
		Map.Entry<Date, PointData> entry2 = this.points.higherEntry(date);
		Date date1 = entry1.getKey();
		Date date2 = entry2.getKey();
		PointData point1 = entry1.getValue();
		PointData point2 = entry2.getValue();
		double rate = (double) (date.getTime() - date1.getTime()) / (date2.getTime() - date1.getTime());
		return new PointData(point1.latitude + (point2.latitude - point1.latitude) * rate, point1.longitude
				+ (point2.longitude - point1.longitude) * rate, format.format(date));
	}
}
