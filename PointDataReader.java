import java.io.InputStream;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

/**
 * 点データを読み込むためのインターフェイスです。
 */
public interface PointDataReader {
	/**
	 * @param in 入力ストリーム
	 * @return 点データの集合
	 * @throws XMLStreamException XMLストリーム例外
	 */
	public Collection<PointData> read(InputStream in) throws XMLStreamException;
}
