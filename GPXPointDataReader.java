import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * GPXデータを読み込むクラスです。
 */
public class GPXPointDataReader implements PointDataReader {

	@Override
	public Collection<PointData> read(InputStream in) throws XMLStreamException {
		Collection<PointData> ret = new ArrayList<PointData>();
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		double latitude = Double.NaN;
		double longitude = Double.NaN;
		for (; reader.hasNext(); reader.next()) {
			int type = reader.getEventType();
			switch (type) {
			case XMLStreamConstants.START_ELEMENT:
				if (reader.getLocalName().equals("trkpt")) {
					latitude = Double.parseDouble(reader.getAttributeValue(null, "lat"));
					longitude = Double.parseDouble(reader.getAttributeValue(null, "lon"));
				} else if (reader.getLocalName().equals("time")) {
					ret.add(new PointData(latitude, longitude, reader.getElementText()));
				}
				break;
			}
		}
		return ret;
	}
}
