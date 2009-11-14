import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 国土数値情報の道路データを読み込むクラス
 */
public class RoadLineDataReader implements LineDataReader {
	/**
	 * 道路の一覧
	 */
	Map<Integer, LineData> lines;

	/**
	 * コンストラクタ
	 * @param in リンク台帳ファイルの入力ストリームリーダ
	 */
	public RoadLineDataReader(Reader in) {
		this.lines = new HashMap<Integer, LineData>();
		final Scanner scanner = new Scanner(in);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.startsWith("DL")) {
				continue;
			}
			int code = Integer.parseInt(line.substring(3, 13).trim());
			int type = Integer.parseInt(line.substring(16, 18).trim());
			String label = line.substring(18).trim();
			LineData lineData = new LineData();
			lineData.label = label;
			lineData.roadTypeCode = Const.RoadTypeCode.get(type);
			this.lines.put(code, lineData);
		}
	}

	@Override
	public Collection<LineData> read(InputStream in) {
		Collection<LineData> ret = new ArrayList<LineData>();
		final Scanner scanner = new Scanner(in);
		LineData currentLineData = null;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("L")) {
				if (currentLineData != null) {
					ret.add(currentLineData);
				}
				int code = Integer.parseInt(line.substring(35, 45).trim());
				if (this.lines.containsKey(code)) {
					currentLineData = new LineData();
					currentLineData.label = this.lines.get(code).label;
					currentLineData.roadTypeCode = this.lines.get(code).roadTypeCode;
				} else {
					currentLineData = null;
				}
			} else {
				if (currentLineData != null) {
					final Scanner scanner2 = new Scanner(line);
					while (scanner2.hasNextInt()) {
						int x = scanner2.nextInt();
						if (scanner2.hasNextInt()) {
							int y = scanner2.nextInt();
							currentLineData.points.add(new PointData(y / 36000.0, x / 36000.0, null));
						}
					}
				}
			}
		}
		if (currentLineData != null) {
			ret.add(currentLineData);
		}
		return ret;
	}
}
