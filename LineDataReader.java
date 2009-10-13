import java.io.InputStream;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

/**
 * 線データを読み込むためのインターフェイスです。
 */
public interface LineDataReader {
	/**
	 * @param in 入力ストリーム
	 * @return 線データの集合
	 * @throws XMLStreamException XMLストリーム例外
	 */
	public Collection<LineData> read(InputStream in) throws XMLStreamException;
}
