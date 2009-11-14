import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * 鉄道データを読み込むクラスです。
 */
public class RailwayLineDataReader implements LineDataReader {

	/**
	 * 最小の緯度、経度
	 */
	public LatLong min;

	/**
	 * 最大の緯度、経度
	 */
	public LatLong max;

	/**
	 * 読み込む範囲を制限せずに鉄道データを読み込むオブジェクトを初期化します。
	 */
	public RailwayLineDataReader() {
	}

	/**
	 * 読み込む範囲を制限して鉄道データを読み込むオブジェクトを初期化します。
	 * @param min 最小の緯度、経度
	 * @param max 最大の緯度、経度
	 */
	public RailwayLineDataReader(LatLong min, LatLong max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public Collection<LineData> read(InputStream in) throws XMLStreamException {
		Collection<LineData> ret = new ArrayList<LineData>();
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		Mode mode = Mode.POINT;
		String id = null;
		Pattern locationPattern = Pattern.compile("([0-9.]+) ([0-9.]+)");
		Map<String, PointData> points = new HashMap<String, PointData>();
		Map<String, LineData> lines = new HashMap<String, LineData>();
		for (; reader.hasNext(); reader.next()) {
			int type = reader.getEventType();
			switch (type) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().equals("GM_Point")) {
					mode = Mode.POINT;
					id = reader.getAttributeValue(null, "id");
				} else if (reader.getLocalName().equals("GM_Curve")) {
					mode = Mode.CURVE;
					id = reader.getAttributeValue(null, "id");
					lines.put(id, new LineData());
				} else if (reader.getLocalName().equals("DirectPosition.coordinate")) {
					Matcher locationMatcher = locationPattern.matcher(reader.getElementText());
					if (locationMatcher.matches()) {
						PointData point = new PointData(Double.parseDouble(locationMatcher.group(1)), Double
								.parseDouble(locationMatcher.group(2)), "");
						if (this.min == null || this.max == null || this.min.latitude <= point.latitude
								&& point.latitude < this.max.latitude && this.min.longitude <= point.longitude
								&& point.longitude < this.max.longitude) {
							switch (mode) {
							case POINT:
								points.put(id, point);
								break;
							case CURVE:
								lines.get(id).points.add(point);
								break;
							}
						}
					}
				} else if (reader.getLocalName().equals("GM_PointRef.point")) {
					String idref = reader.getAttributeValue(null, "idref");
					if (points.containsKey(idref)) {
						lines.get(id).points.add(points.get(idref));
					}
				} else if (reader.getLocalName().equals("LOC")) {
					id = reader.getAttributeValue(null, "idref");
				} else if (reader.getLocalName().equals("RAC")) {
					switch (Integer.parseInt(reader.getElementText())) {
					case 11:
						lines.get(id).railwayClassCode = Const.RailwayClassCode.JR;
						break;
					case 12:
						lines.get(id).railwayClassCode = Const.RailwayClassCode.PRIVATE;
						break;
					default:
						lines.get(id).railwayClassCode = Const.RailwayClassCode.OTHERS;
					}
				} else if (reader.getLocalName().equals("INT")) {
					switch (Integer.parseInt(reader.getElementText())) {
					case 1:
						lines.get(id).institutionTypeCode = Const.InstitutionTypeCode.SHINKANSEN;
						break;
					case 2:
						lines.get(id).institutionTypeCode = Const.InstitutionTypeCode.JR;
						break;
					case 3:
						lines.get(id).institutionTypeCode = Const.InstitutionTypeCode.PUBLIC;
						break;
					case 4:
						lines.get(id).institutionTypeCode = Const.InstitutionTypeCode.PRIVATE;
						break;
					case 5:
						lines.get(id).institutionTypeCode = Const.InstitutionTypeCode.THIRD_SECTOR;
						break;
					}
				} else if (reader.getLocalName().equals("LIN")) {
					lines.get(id).lineName = reader.getElementText();
				} else if (reader.getLocalName().equals("OPC")) {
					lines.get(id).company = reader.getElementText();
				} else if (reader.getLocalName().equals("STN")) {
					lines.get(id).stationName = reader.getElementText();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				break;
			}
		}
		for (LineData line : lines.values()) {
			if (!line.points.isEmpty()) {
				ret.add(line);
			}
		}
		return ret;
	}

	/**
	 * 現在読み込み中のデータの種類
	 */
	enum Mode {
		/**
		 * 点データ
		 */
		POINT,
		/**
		 * 線データ
		 */
		CURVE,
	}
}
