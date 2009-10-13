/**
 * 緯度、経度をカプセルかするクラスです。
 */
public class LatLong {
	/**
	 * 緯度
	 */
	public double latitude;
	/**
	 * 経度
	 */
	public double longitude;
	/**
	 * コンストラクタです。
	 * @param latitude 緯度
	 * @param longitude 経度
	 */
	public LatLong(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
