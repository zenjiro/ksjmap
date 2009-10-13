import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 線データに対応するクラスです。
 */
public class LineData {
	/**
	 * 構成点の集合
	 */
	public Collection<PointData> points;
	/**
	 * 文字列
	 */
	public String label;
	/**
	 * 鉄道区分コード
	 */
	public Const.RailwayClassCode railwayClassCode;
	/**
	 * 事業者種別コード
	 */
	public Const.InstitutionTypeCode institutionTypeCode;

	/**
	 * 路線名
	 */
	public String lineName;

	/**
	 * 運営会社
	 */
	public String company;

	/**
	 * 駅名
	 */
	public String stationName;

	/**
	 * 線データを初期化します。
	 */
	public LineData() {
		this.points = new CopyOnWriteArrayList<PointData>();
	}

	@Override
	public String toString() {
		return "(" + this.railwayClassCode + ", " + this.institutionTypeCode + ", " + this.lineName + ", "
				+ this.company + ", " + this.stationName + ")" + this.points.toString();
	}
}
