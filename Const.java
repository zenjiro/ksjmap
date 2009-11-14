import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 定数を集めたクラスです。
 */
public class Const {
	/**
	 * データフォーマット（点、線、面、メッシュ）
	 */
	public static enum Format {
		/**
		 * 点
		 */
		POINT,
		/**
		 * 線
		 */
		LINE,
		/**
		 * 面
		 */
		AREA,
		/**
		 * メッシュ
		 */
		MESH,
	}

	/**
	 * データ項目（標高・傾斜度メッシュ、土地利用メッシュ、道路、鉄道、行政区域、海岸線、湖沼、河川、空港、公共施設）
	 */
	public static enum Category {
		/**
		 * 標高・傾斜度メッシュ
		 */
		HEIGHT,
		/**
		 * 土地利用メッシュ
		 */
		USAGE,
		/**
		 * 道路
		 */
		ROAD,
		/**
		 * 鉄道
		 */
		RAILWAY,
		/**
		 * 行政区域
		 */
		CITY,
		/**
		 * 海岸線
		 */
		SEA,
		/**
		 * 湖沼
		 */
		LAKE,
		/**
		 * 河川
		 */
		RIVER,
		/**
		 * 空港
		 */
		AIRPORT,
		/**
		 * 公共施設
		 */
		FACILITY,
	}

	/**
	 * 公共施設大分類コード
	 */
	public static enum FacilityRoughCode {
		/**
		 * 建物
		 */
		BUILDING(null),
		/**
		 * 国の機関
		 */
		NATIONAL("public-office.png"),
		/**
		 * 地方公共団体
		 */
		CITY("public-office.png"),
		/**
		 * 厚生機関
		 */
		BENEFIT(null),
		/**
		 * 警察機関
		 */
		POLICE("police-office.png"),
		/**
		 * 消防署
		 */
		FIRE("fire-office.png"),
		/**
		 * 学校
		 */
		SCHOOL("school.png"),
		/**
		 * 病院
		 */
		HOSPITAL("hospital.png"),
		/**
		 * 郵便局
		 */
		POST("post-office.png"),
		/**
		 * 福祉施設
		 */
		WELFARE(null),
		/**
		 * その他
		 */
		OTHERS(null);

		/**
		 * 地図記号画像のファイル名
		 */
		String imageFile;

		/**
		 * 地図記号画像
		 */
		BufferedImage image;

		/**
		 * @param imageFile 地図記号画像のファイル名
		 */
		FacilityRoughCode(String imageFile) {
			this.imageFile = imageFile;
		}

		/**
		 * @return 地図画像があるかどうか
		 */
		public boolean hasImageFile() {
			return this.imageFile != null;
		}

		/**
		 * 地図記号画像を取得します。
		 * @return 地図記号画像
		 * @throws IOException 入出力例外
		 */
		public BufferedImage getImage() throws IOException {
			if (this.image == null) {
				this.image = ImageIO.read(Const.class.getResourceAsStream(this.imageFile));
			}
			return this.image;
		}

		/**
		 * 公共施設大分類コードから列挙型を求めます。
		 * @param code 公共施設大分類コード
		 * @return 列挙型
		 */
		public static FacilityRoughCode get(int code) {
			switch (code) {
			case 3:
				return BUILDING;
			case 11:
				return NATIONAL;
			case 12:
				return CITY;
			case 13:
				return BENEFIT;
			case 14:
				return POLICE;
			case 15:
				return FIRE;
			case 16:
				return SCHOOL;
			case 17:
				return HOSPITAL;
			case 18:
				return POST;
			case 19:
				return WELFARE;
			default:
				return OTHERS;
			}
		}
	}

	/**
	 * 公共施設小分類コード
	 */
	public static enum FacilityDetailCode {
		/**
		 * 美術館
		 */
		ART_MUSEUM(null),
		/**
		 * 資料館、記念館、博物館、科学館
		 */
		MUSEUM("museum.png"),
		/**
		 * 図書館
		 */
		LIBRARY("library.png"),
		/**
		 * 裁判所
		 */
		COURT("court.png"),
		/**
		 * 都道府県庁
		 */
		PREFECTURE(null),
		/**
		 * 区役所（東京都）、市役所
		 */
		CITY("city-office.png"),
		/**
		 * 区役所（政令指定都市）
		 */
		WARD("town-office.png"),
		/**
		 * 町村役場
		 */
		TOWN("town-office.png"),
		/**
		 * 幼稚園
		 */
		KINDERGARTEN(null),
		/**
		 * 保育所
		 */
		NURSERY(null),
		/**
		 * 警察本部、警察署
		 */
		POLICE_OFFICE("police-office.png"),
		/**
		 * 交番、駐在所、派出所
		 */
		POLICE_BOX("police-box.png"),
		/**
		 * 税務署
		 */
		TAX_OFFICE(null),
		/**
		 * 児童館
		 */
		CHILDREN_HOUSE(null),
		/**
		 * 官公署
		 */
		PUBLIC_OFFICE("public-office.png"),
		/**
		 * 小中学校
		 */
		SCHOOL("school.png"),
		/**
		 * 高校
		 */
		HIGH_SCHOOL("high-school.png"),
		/**
		 * 高専
		 */
		TECHNICAL_SCHOOL("technical-school.png"),
		/**
		 * 短大
		 */
		TWO_YEAR_COLLEGE("two-year-college.png"),
		/**
		 * 大学
		 */
		COLLEGE("college.png"),
		/**
		 * 気象台
		 */
		OBSERVATORY("observatory.png"),
		/**
		 * 自衛隊
		 */
		FORCE("force.png"),
		/**
		 * 保健所
		 */
		HEALTHCARE_CENTER("healthcare-center.png"),
		/**
		 * 老人ホーム
		 */
		NURSING_HOME("nursing-home.png"),
		/**
		 * その他
		 */
		OTHERS(null);

		/**
		 * 地図記号画像のファイル名
		 */
		String imageFile;

		/**
		 * 地図記号画像
		 */
		BufferedImage image;

		/**
		 * @param imageFile 地図記号画像のファイル名
		 */
		FacilityDetailCode(String imageFile) {
			this.imageFile = imageFile;
		}

		/**
		 * @return 地図記号画像があるかどうか
		 */
		public boolean hasImageFile() {
			return this.imageFile != null;
		}

		/**
		 * 地図記号画像を取得します。
		 * @return 地図記号画像
		 * @throws IOException 入出力例外
		 */
		public BufferedImage getImage() throws IOException {
			if (this.image == null) {
				this.image = ImageIO.read(Const.class.getResourceAsStream(this.imageFile));
			}
			return this.image;
		}

		/**
		 * 公共施設小分類コードから列挙型を求めます。
		 * @param code 公共施設小分類コード
		 * @return 列挙型
		 */
		public static FacilityDetailCode get(int code) {
			switch (code) {
			case 3001:
				return ART_MUSEUM;
			case 3002:
				return MUSEUM;
			case 3003:
				return LIBRARY;
			case 11100:
			case 11101:
			case 11102:
			case 11103:
			case 11110:
			case 11111:
			case 11112:
			case 11113:
			case 11114:
			case 11120:
			case 11121:
				return PUBLIC_OFFICE;
			case 11130:
				return FORCE;
			case 11131:
			case 11140:
			case 11142:
			case 11144:
			case 11150:
			case 11151:
			case 11152:
			case 11153:
			case 11160:
			case 11161:
			case 11170:
				return PUBLIC_OFFICE;
			case 11171:
				return TAX_OFFICE;
			case 11180:
			case 11181:
			case 11190:
			case 11191:
			case 11192:
			case 11200:
			case 11202:
			case 11203:
			case 11210:
			case 11211:
			case 11212:
			case 11213:
			case 11220:
			case 11221:
			case 11222:
				return PUBLIC_OFFICE;
			case 11223:
				return OBSERVATORY;
			case 11224:
			case 11230:
				return PUBLIC_OFFICE;
			case 11240:
				return COURT;
			case 12001:
				return PREFECTURE;
			case 12002:
				return CITY;
			case 12003:
				return WARD;
			case 12004:
				return TOWN;
			case 12005:
				return PUBLIC_OFFICE;
			case 13001:
				return HEALTHCARE_CENTER;
			case 14001:
			case 14002:
				return POLICE_OFFICE;
			case 14003:
			case 14004:
			case 14005:
				return POLICE_BOX;
			case 16001:
			case 16002:
			case 16003:
				return SCHOOL;
			case 16004:
				return HIGH_SCHOOL;
			case 16005:
				return TECHNICAL_SCHOOL;
			case 16006:
				return TWO_YEAR_COLLEGE;
			case 16007:
				return COLLEGE;
			case 16011:
				return KINDERGARTEN;
			case 19001:
			case 19002:
			case 19003:
			case 19004:
				return NURSING_HOME;
			case 19008:
				return CHILDREN_HOUSE;
			case 19013:
			case 19014:
				return NURSERY;
			default:
				return OTHERS;
			}
		}
	}

	/**
	 * 鉄道区分コード
	 */
	public static enum RailwayClassCode {
		/**
		 * JR
		 */
		JR,
		/**
		 * 私鉄
		 */
		PRIVATE,
		/**
		 * その他
		 */
		OTHERS,
	}

	/**
	 * 事業者種別コード
	 */
	public static enum InstitutionTypeCode {
		/**
		 * JR新幹線
		 */
		SHINKANSEN,
		/**
		 * JR在来線
		 */
		JR,
		/**
		 * 公営鉄道
		 */
		PUBLIC,
		/**
		 * 民営鉄道
		 */
		PRIVATE,
		/**
		 * 第3セクター
		 */
		THIRD_SECTOR,
	}

	/**
	 * 道路種別コード
	 */
	public static enum RoadTypeCode {
		/**
		 * 高速道路
		 */
		HIGHWAY,
		/**
		 * 国道
		 */
		NATIONAL_ROAD,
		/**
		 * 主要地方道
		 */
		PRINCIPAL_PREFECTUAL_ROAD;

		/**
		 * @param type 道路種別コード
		 * @return 道路種別コードの列挙型
		 */
		public static RoadTypeCode get(int type) {
			switch (type) {
			case 1:
				return HIGHWAY;
			case 2:
				return NATIONAL_ROAD;
			case 3:
				return PRINCIPAL_PREFECTUAL_ROAD;
			default:
				return null;
			}
		}
	}
}
