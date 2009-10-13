/**
 * 点データに対応するクラスです。
 */
public class PointData {
	/**
	 * 緯度
	 */
	public double latitude;
	/**
	 * 経度
	 */
	public double longitude;
	/**
	 * 文字列
	 */
	public String label;
	/**
	 * 住所
	 */
	public String address;
	/**
	 * 公共施設大分類コード
	 */
	public Const.FacilityRoughCode roughCode;
	/**
	 * 公共施設小分類コード
	 */
	public Const.FacilityDetailCode detailCode;
	
	/**
	 * 点データを初期化します。
	 * @param latitude 緯度
	 * @param longitude 経度
	 * @param label 文字列
	 */
	public PointData(double latitude, double longitude, String label) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.label = label;
		this.roughCode = Const.FacilityRoughCode.OTHERS;
		this.detailCode = Const.FacilityDetailCode.OTHERS;
	}

	@Override
	public String toString() {
		return this.label + "@" + this.address + "(" + this.latitude + ", " + this.longitude + ")";
	}
}
