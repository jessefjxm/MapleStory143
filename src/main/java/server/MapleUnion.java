package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import com.alibaba.druid.pool.DruidPooledConnection;

import client.BuddylistEntry;
import constants.JobConstants;
import database.DatabaseConnection;

/**
 * @author Jessefjxm
 * @version 2019.8
 * @since 1.8
 */
public class MapleUnion {
	// 记录各种卡的组件的坐标，以中心为{0,0}建立直角坐标系
	// 数据格式位：modules[品质][职业族][x][y]
	// 品质：B-0 A-1 S-2 SS-3 SS-4
	// 职业族：战士-0 魔法师-1 弓箭手-2 飞侠-3 海盗-4
	private static final int[][][][] modules = {
			// 品质 B
			{ { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } } },
			// 品质 A
			{ { { 0, 0 }, { 1, 0 } }, { { 0, 0 }, { 1, 0 } }, { { 0, 0 }, { 1, 0 } }, { { 0, 0 }, { 1, 0 } },
					{ { 0, 0 }, { 1, 0 } } },
			// 品质 S
			{ { { 0, 0 }, { 1, 0 }, { 0, -1 } }, { { 0, 0 }, { 1, 0 }, { -1, 0 } }, { { 0, 0 }, { 1, 0 }, { -1, 0 } },
					{ { 0, 0 }, { 1, 0 }, { -1, 0 } }, { { 0, 0 }, { 1, 0 }, { 0, -1 } } },
			// 品质 SS
			{ { { 0, 0 }, { 1, 0 }, { 0, -1 }, { 1, -1 } }, { { 0, 0 }, { 1, 1 }, { -1, 1 }, { 0, 1 } },
					{ { 0, 0 }, { 1, 0 }, { -1, 0 }, { 2, 0 } }, { { 0, 0 }, { 1, 0 }, { -1, 0 }, { 1, -1 } },
					{ { 0, 0 }, { 1, 0 }, { 0, -1 }, { 1, 1 } } },
			// 品质 SSS
			{ { { 0, 0 }, { 1, 0 }, { 0, -1 }, { 1, -1 }, { 2, 0 } },
					{ { 0, 0 }, { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } },
					{ { 0, 0 }, { 1, 0 }, { -1, 0 }, { 2, 0 }, { -2, 0 } },
					{ { 0, 0 }, { 1, 0 }, { -1, 0 }, { 1, -1 }, { 1, 1 } },
					{ { 0, 0 }, { 1, 0 }, { 0, -1 }, { 1, 1 }, { 1, 2 } } } };
	// 角色卡ID与职业ID的映射
	// 注意WZ里的数据把所有职业ID都 /10 了
	// 第一个是正向，第二个是反向，一般用第二个
	private int[][] careerIdMapping = { { 11, 71000011 }, { 12, 71000012 }, { 13, 71000013 }, { 21, 71000021 },
			{ 22, 71000022 }, { 23, 71000023 }, { 31, 71000031 }, { 32, 71000032 }, { 41, 71000041 }, { 42, 71000042 },
			{ 43, 71000043 }, { 51, 71000051 }, { 52, 71000052 }, { 53, 71000053 }, { 311, 71000311 },
			{ 312, 71000312 }, { 321, 71000321 }, { 331, 71000331 }, { 351, 71000351 }, { 371, 71000371 },
			{ 361, 71000361 }, { 211, 71000211 }, { 221, 71000221 }, { 231, 71000231 }, { 241, 71000241 },
			{ 271, 71000271 }, { 111, 71000111 }, { 121, 71000121 }, { 131, 71000131 }, { 141, 71000141 },
			{ 151, 71000151 }, { 511, 71000511 }, { 611, 71000611 }, { 641, 71000641 }, { 651, 71000651 },
			{ 1011, 71000711 }, { 251, 71000251 }, { 1421, 71000921 }, { 1521, 71000922 }, { 1551, 71000951 },
			{ 1000090, 71009001 }, { 33, 71000952 }, { 411, 71000411 }, { 421, 71000421 }, { 57, 71000057 },
			{ 1121, 71000821 } };
	private HashMap<Integer, Integer> cardIdMapping;
	// 缓存玩家数据
	private HashMap<Integer, UnionInfo> playerInfo;

	/**
	 * 缓存每个玩家的联盟数据
	 *
	 */
	private class UnionInfo {
		// 玩家ID
		private int playerId;
		// 记录联盟格子占有情况，内容为角色卡skillID
		private int[][] grids;
		// 记录每个扇区对应的是什么属性
		// 定义：扇区编号按从左到右、从上到下、从里到外，共计16个
		// 默认顺序：MP，力量，敏捷，智力，运气，物攻，魔攻，HP，暴击伤害，状态异常抗性，经验获取，暴击率，首领伤害，稳如泰山，增益时间，无视防御，暴击伤害
		private int[] attrMapping;
		// 记录每个扇区当前有多少加成，一个格子就是一点加成
		// 默认顺序：MP，力量，敏捷，智力，运气，物攻，魔攻，HP，暴击伤害，状态异常抗性，经验获取，暴击率，首领伤害，稳如泰山，增益时间，无视防御，暴击伤害
		private int[] attributes;
		// 记录各个组件的当前坐标，方便查询
		private HashMap<Integer, int[]> moduleCoords;
		// 联盟币
		private int unionCoin;

		public UnionInfo(int playerId, int unionCoin, String grids, String attrMapping) {
			this.playerId = playerId;
			this.unionCoin = unionCoin;
			this.grids = grids.length()<100 ? new int[22][22] : ;
			this.attrMapping = new int[16];
			for (int i = 0; i < 16; i++) {
				attributes[i] = i;
			}
			attributes = new int[16];
			moduleCoords = new HashMap<>();
		}

		public int getUnionCoin() {
			return unionCoin;
		}

		public String getGrids() {
			return Arrays.deepToString(grids);
		}

		public String getAttrMapping() {
			return Arrays.toString(attrMapping);
		}

		public int[] getAttributes() {
			return attributes;
		}
	}

	/**
	 * 反序列化1D int[] 数组
	 *
	 * @param string 序列化的字符串
	 * @return 无
	 */
	private static int[] fromString1D(String string) {
		String[] strings = string.replace("[", "").replace("]", "").split(", ");
		int result[] = new int[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(strings[i]);
		}
		return result;
	}

	/**
	 * 反序列化2D int[] 数组
	 *
	 * @param string 序列化的字符串
	 * @return 无
	 */
	private static int[] fromString2D(String string) {
		String[] strings = string.replace("[", "").replace("]", ">").split(", ");
		List<String> stringList = new ArrayList<>();
		List<String[]> tempResult = new ArrayList<>();
		for (String str : strings) {
			if (str.endsWith(">")) {
				str = str.substring(0, str.length() - 1);
				if (str.endsWith(">")) {
					str = str.substring(0, str.length() - 1);
				}
				stringList.add(str);
				tempResult.add(stringList.toArray(new String[stringList.size()]));
				stringList = new ArrayList<>();
			} else {
				stringList.add(str);
			}
		}
	}

	/**
	 * 初始化
	 *
	 * @param 无
	 * @return 无
	 */
	public MapleUnion() {
		playerInfo = new HashMap<>();
		// 数据库操作
		try {
			createTableInDB();
			loadFromDb();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 插入映射数据
		for (int[] map : careerIdMapping) {
			cardIdMapping.put(map[1], map[0]);
		}
	}

	/**
	 * 在数据库里创建基于玩家的联盟数据表
	 *
	 * @param 无
	 * @return 无
	 */
	public void createTableInDB() throws SQLException {
		DruidPooledConnection con = DatabaseConnection.getInstance().getConnection();
		PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `maple_union` ("
				+ "`id` int(11) NOT NULL AUTO_INCREMENT, `playerid` int(11) NOT NULL DEFAULT '0',"
				+ "`coin` int(11) NOT NULL DEFAULT '0', `grids` text NOT NULL DEFAULT '', `attrMap` text NOT NULL DEFAULT '', "
				+ "PRIMARY KEY (`id`)" + ") ;");
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	/**
	 * 从数据库里载入玩家资料
	 *
	 * @param 无
	 * @return 无
	 */
	public void loadFromDb() throws SQLException {
		DruidPooledConnection con = DatabaseConnection.getInstance().getConnection();
		PreparedStatement ps;
		ps = con.prepareStatement("SELECT `playerid`,`coin`,`grids`, `attrMap`  FROM `maple_union`");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(1);
			playerInfo.put(playerId, new UnionInfo(playerId, rs.getInt(2), rs.getString(3), rs.getString(4)));
		}
		ps.close();
		con.close();
	}

	/**
	 * 保存资料到数据库里
	 *
	 * @param 无
	 * @return 无
	 */
	public void saveToDb(int playerId) throws SQLException {
		if (playerInfo == null || playerInfo.size() == 0 || (playerId >= 0 && !playerInfo.containsKey(playerId))) {
			return;
		}
		DruidPooledConnection con = DatabaseConnection.getInstance().getConnection();
		PreparedStatement ps = null;
		if (playerId < 0) {
			for (int id : playerInfo.keySet()) {
				UnionInfo unionInfo = playerInfo.get(id);
				ps = con.prepareStatement(
						"INSERT INTO `maple_union` (`coin`,`grids`,`attrMap`) VALUES (" + unionInfo.getUnionCoin() + ","
								+ unionInfo.getGrids() + "," + unionInfo.getAttrMapping() + ");");
				ps.executeUpdate();
			}
		} else {
			UnionInfo unionInfo = playerInfo.get(playerId);
			ps = con.prepareStatement("INSERT INTO `maple_union` (`coin`,`grids`,`attrMap`) VALUES ("
					+ unionInfo.getUnionCoin() + "," + unionInfo.getGrids() + "," + unionInfo.getAttrMapping() + ");");
			ps.executeUpdate();
		}
		ps.close();
		con.close();
	}

	/**
	 * 添加组件
	 *
	 * @param cardSkillId 角色卡编号
	 * @param angle       组件逆时针旋转角度，是90的倍数
	 * @param coordNum    坐标值，等同于从1开始从左到右从上到下的位置数
	 * @return 是否成功
	 */
	public boolean addModule(int cardSkillId, int angle, int coordNum) {

		return true;
	}

	/**
	 * 移除组件
	 *
	 * @param cardSkillId 角色卡编号
	 * @return 是否成功
	 */
	public boolean removeModule(int cardSkillId) {

		return true;
	}

	/**
	 * 交换两个扇区对应的属性类型
	 *
	 * @param a 第一个属性类型
	 * @param b 第二吧一个属性类型
	 * @return 是否成功
	 */
	public void updateAttrMap(int a, int b) {

	}

	/**
	 * 更新属性统计值
	 *
	 * @param 无
	 * @return 是否成功
	 */
	public void updateAttribute() {

	}

	/**
	 * 判断角色卡所属的职业族
	 *
	 * @param cardSkillId 角色卡类型
	 * @return 战士-0 魔法师-1 弓箭手-2 飞侠-3 海盗-4
	 */
	public int findCareerGroup(int cardSkillId) {
		return JobConstants.getTrueJobGrade(cardIdMapping.get(cardSkillId) * 10);
	}

	/**
	 * 联盟升级。会扩大边界范围。
	 *
	 * @param currentLevel 当前等级
	 * @return 是否成功
	 */
	public void upgradeUnion(int currentLevel) {
	}

	/**
	 * 获得联盟币
	 *
	 * @param num 联盟币数量
	 * @return 无
	 */
	public void addCoin(int num) {
	}

	/**
	 * 获取NPC帮助信息，用脚本实现
	 *
	 * @param 无
	 * @return 是否成功
	 */
	public void callHelp() {
		// openNPC(9010106, "冒险岛联盟帮助");
	}

	/**
	 * 更新 组件——坐标 索引
	 *
	 * @param 无
	 * @return 无
	 */
	public void updateModuleCoordsMap() {

	}
}
