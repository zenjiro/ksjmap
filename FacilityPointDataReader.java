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
 * 公共施設を読み込むクラスです。
 */
public class FacilityPointDataReader implements PointDataReader {
	@Override
	public Collection<PointData> read(InputStream in) throws XMLStreamException {
		Collection<PointData> ret = new ArrayList<PointData>();
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		String id = null;
		Pattern locationPattern = Pattern.compile("([0-9.]+) ([0-9.]+)");
		Map<String, PointData> points = new HashMap<String, PointData>();
		for (; reader.hasNext(); reader.next()) {
			int type = reader.getEventType();
			switch (type) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().equals("GM_Point")) {
					id = reader.getAttributeValue(null, "id");
				} else if (reader.getLocalName().equals("DirectPosition.coordinate")) {
					Matcher locationMatcher = locationPattern.matcher(reader.getElementText());
					if (locationMatcher.matches()) {
						points.put(id, new PointData(Double.parseDouble(locationMatcher.group(1)), Double
								.parseDouble(locationMatcher.group(2)), ""));
					}
				} else if (reader.getLocalName().equals("POS")) {
					id = reader.getAttributeValue(null, "idref");
				} else if (reader.getLocalName().equals("NA0")) {
					points.get(id).label = reader.getElementText();
				} else if (reader.getLocalName().equals("ADS")) {
					points.get(id).address = reader.getElementText();
				} else if (reader.getLocalName().equals("PCA")) {
					points.get(id).roughCode = Const.FacilityRoughCode.get(Integer.parseInt(reader.getElementText()));
				} else if (reader.getLocalName().equals("PCI")) {
					points.get(id).detailCode = Const.FacilityDetailCode.get(Integer.parseInt(reader.getElementText()));
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				if (reader.getLocalName().equals("FB01")) {
					ret.add(points.get(id));
				}
				break;
			}
		}
		return ret;
	}
}
