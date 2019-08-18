package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import com.alibaba.druid.pool.DruidPooledConnection;

import constants.JobConstants;
import database.DatabaseConnection;

/**
 * @author Jessefjxm
 * @version 2019.8
 * @since 1.8
 */
public class MapleUnion {
	/**
	 * 记录各种卡的组件的坐标，以中心为{0,0}建立直角坐标系
	 * 
	 * @param 数据格式位：modules[品质][职业族][x][y]
	 * @param 品质：B-0                       A-1 S-2 SS-3 SS-4
	 * @param 职业族：战士-0                     魔法师-1 弓箭手-2 飞侠-3 海盗-4
	 */
	private static final int[][][][] modules = {
			// 品质 B
			{ { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } }, { { 0, 0 } } },
			// 品质 A
			{ { { 0, 0 }, { 0, 1 } }, { { 0, 0 }, { 0, 1 } }, { { 0, 0 }, { 0, 1 } }, { { 0, 0 }, { 0, 1 } },
					{ { 0, 0 }, { 0, 1 } } },
			// 品质 S
			{ { { 0, 0 }, { 0, 1 }, { 1, 0 } }, { { 0, 0 }, { 0, 1 }, { 0, -1 } }, { { 0, 0 }, { 0, 1 }, { 0, -1 } },
					{ { 0, 0 }, { 0, 1 }, { 0, -1 } }, { { 0, 0 }, { 0, 1 }, { 1, 0 } } },
			// 品质 SS
			{ { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } }, { { 0, 0 }, { -1, 1 }, { -1, -1 }, { -1, 0 } },
					{ { 0, 0 }, { 0, 1 }, { 0, -1 }, { 0, 2 } }, { { 0, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 } },
					{ { 0, 0 }, { 0, 1 }, { 1, 0 }, { -1, 1 } } },
			// 品质 SSS
			{ { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 }, { 0, 2 } },
					{ { 0, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } },
					{ { 0, 0 }, { 0, 1 }, { 0, -1 }, { 0, 2 }, { 0, -2 } },
					{ { 0, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 } },
					{ { 0, 0 }, { 0, 1 }, { 1, 0 }, { -1, 1 }, { -2, 1 } } } };
	/**
	 * 角色卡ID与职业ID的映射
	 * 
	 * @param 注意WZ里的数据把所有职业ID都/10了
	 * @param 第一个是正向，第二个是反向，一般用第二个
	 */
	private static final int[][] careerIdMapping = { { 11, 71000011 }, { 12, 71000012 }, { 13, 71000013 },
			{ 21, 71000021 }, { 22, 71000022 }, { 23, 71000023 }, { 31, 71000031 }, { 32, 71000032 }, { 41, 71000041 },
			{ 42, 71000042 }, { 43, 71000043 }, { 51, 71000051 }, { 52, 71000052 }, { 53, 71000053 }, { 311, 71000311 },
			{ 312, 71000312 }, { 321, 71000321 }, { 331, 71000331 }, { 351, 71000351 }, { 371, 71000371 },
			{ 361, 71000361 }, { 211, 71000211 }, { 221, 71000221 }, { 231, 71000231 }, { 241, 71000241 },
			{ 271, 71000271 }, { 111, 71000111 }, { 121, 71000121 }, { 131, 71000131 }, { 141, 71000141 },
			{ 151, 71000151 }, { 511, 71000511 }, { 611, 71000611 }, { 641, 71000641 }, { 651, 71000651 },
			{ 1011, 71000711 }, { 251, 71000251 }, { 1421, 71000921 }, { 1521, 71000922 }, { 1551, 71000951 },
			{ 1000090, 71009001 }, { 33, 71000952 }, { 411, 71000411 }, { 421, 71000421 }, { 57, 71000057 },
			{ 1121, 71000821 } };
	private static HashMap<Integer, Integer> cardIdMapping = new HashMap<>();
	// 整个拼图版的边长
	private static final int[] boardLength = { 20, 22 };
	// 升级联盟需要的 <等级之和> 与 <联盟币>
	private static final int[][][] upgradeUnionCost = {
			{ { 500, 0 }, { 1000, 120 }, { 1500, 140 }, { 2000, 150 }, { 2500, 160 } },
			{ { 3000, 170 }, { 3500, 430 }, { 4000, 450 }, { 4500, 470 }, { 5000, 490 } },
			{ { 5500, 510 }, { 6000, 930 }, { 6500, 960 }, { 7000, 1000 }, { 7500, 1030 } },
			{ { 8000, 1060 }, { 8500, 2200 }, { 9000, 2300 }, { 9500, 2350 }, { 10000, 2400 } } };

	// 可增益的属性对应的代号
	private enum AttributeTypeID {
		// TODO 如果客户端定义的顺序不同，以客户端的为准
		// 客户端SkillID = 71004000 + ordinal
		// 内圈默认顺序：力量，敏捷，智力，运气，物攻，魔攻，HP，MP
		STR, DEX, INT, LUK, PHYSICAL_ATTACK, MAGICAL_ATTACK, HP, MP,
		// 外圈默认顺序：状态异常抗性，经验获取，暴击率，首领伤害，稳如泰山，增益时间，无视防御，暴击伤害
		DEBUFF_RESISTANCE, EXP_GAIN, CRITICAL_CHANGE, BOSS_DAMAGE, STAND_CHANCE, BUFF_TIME, IGNORE_DEFENCE,
		CRITICAL_DAMAGE
	}

	// 每种属性每个格子的加成
	private static final double[] attrBonus = { 5, 5, 5, 5, 1, 1, 250, 250, 1, 0.25, 1, 1, 1, 1, 1, 0.5 };

	// 缓存玩家数据
	private HashMap<Integer, UnionInfo> playerInfo = new HashMap<>();

	/**
	 * 缓存每个玩家的联盟数据
	 *
	 */
	private class UnionInfo {
		// 玩家ID
		private int playerId;
		// 记录 <联盟格子> 占有情况，内容为角色卡skillID，如果是中心点则为-skillID
		private int[][] grids = new int[boardLength[0]][boardLength[1]];
		// TODO 确认扇区在客户端是不是也这么开始标号的
		// 记录 <每个扇区> 对应的是什么属性
		// 定义：扇区编号按从左到右、从上到下、从里到外，共计16个
		private int[] attrMapping = { AttributeTypeID.STR.ordinal(), AttributeTypeID.DEX.ordinal(),
				AttributeTypeID.INT.ordinal(), AttributeTypeID.LUK.ordinal(), AttributeTypeID.PHYSICAL_ATTACK.ordinal(),
				AttributeTypeID.MAGICAL_ATTACK.ordinal(), AttributeTypeID.HP.ordinal(), AttributeTypeID.MP.ordinal(),
				AttributeTypeID.DEBUFF_RESISTANCE.ordinal(), AttributeTypeID.EXP_GAIN.ordinal(),
				AttributeTypeID.CRITICAL_CHANGE.ordinal(), AttributeTypeID.BOSS_DAMAGE.ordinal(),
				AttributeTypeID.STAND_CHANCE.ordinal(), AttributeTypeID.BUFF_TIME.ordinal(),
				AttributeTypeID.IGNORE_DEFENCE.ordinal(), AttributeTypeID.CRITICAL_DAMAGE.ordinal() };
		// 记录 <每个扇区> 当前占据了多少格子，一个格子就加成一次，但是伤害不同
		private int[] sectionCount = new int[16];
		// 记录 <每种属性> 当前加成多少，顺序基于 enum AttributeTypeID
		private double[] attributes = new double[16];
		private HashMap<Integer, Integer> attributeSkill = new HashMap<>();
		// 记录 <各个组件> 的当前坐标，方便查询
		private HashMap<Integer, int[]> moduleCoords = new HashMap<>();
		// 角色卡等级和
		private int levelSum = 0;
		// 联盟等级
		private int unionLevel = 101;
		// 联盟币
		private int unionCoin = 0;
		// 受限于联盟等级，当前拼图版的有效边长
		private int[] curBoardLength = { 10, 12 };

		public UnionInfo(int playerId, int levelSum, int unionLevel, int unionCoin, String grids, String attrMapping) {
			this.levelSum = levelSum;
			this.playerId = playerId;
			this.unionLevel = unionLevel < 0 ? 101 : unionLevel;
			this.unionCoin = unionCoin < 0 ? 0 : unionCoin;
			// 载入具体数据
			if (grids.length() > 10) { // 数据非空
				int[] temp = fromString1D(grids);
				for (int i = 0; i < boardLength[0]; i++) {
					for (int j = 0; j < boardLength[1]; j++) {
						this.grids[i][j] = temp[i * boardLength[1] + j];
					}
				}
			}
			if (attrMapping.length() > 10) { // 数据非空
				this.attrMapping = fromString1D(attrMapping);
			}
			for (int i = 0; i < 16; i++) {
				this.attrMapping[i] = i;
			}
			// 更新当前拼图版的有效边长
			updateCurBoardLength();
			// 更新统计数据，并载入各个组件的角色卡ID-坐标映射
			for (int i = boardLength[0] / 2 - curBoardLength[0] / 2; i < boardLength[0] / 2
					+ curBoardLength[0] / 2; i++) {
				for (int j = boardLength[1] / 2 - curBoardLength[1] / 2; i < boardLength[1] / 2
						+ curBoardLength[1] / 2; j++) {
					if (this.grids[i][j] != 0) {
						int section = getSection(i, j);
						this.sectionCount[section] += 1;
					}
					if (this.grids[i][j] < 0) {
						this.moduleCoords.put(-this.grids[i][j], new int[] { i, j });
					}
				}
			}
			updateAttribute();
		}

		/**
		 * 计算给定格子所在的扇区
		 *
		 * @param x 数组坐标系定义下的x，即从上到下
		 * @param y 数组坐标系定义下的y，即从左到右
		 * @return 扇区编号，定义：扇区编号按从左到右、从上到下、从里到外，共计16个
		 */
		private int getSection(int x, int y) {
			/**
			 * 基准坐标矩阵
			 * 
			 * @param -1,-3 -1,-2 -1,-1 -1,0 -1,1 -1,2
			 * @param 0,-3  0,-2 0,-1 0,0 0,1 0,2
			 * @param 1,-3  1,-2 1,-1 1,0 1,1 1,2
			 * @param 2,-3  2,-2 2,-1 2,0 2,1 2,2
			 */
			int isOuter = (x < 5 || x > 14 || y < 5 || y > 16) ? 8 : 0;
			x -= boardLength[0] / 2 + 1;
			y -= boardLength[1] / 2;
			if (x <= 0 && y <= -1) {
				return isOuter + ((x - y >= 2) ? 7 : 0);
			} else if (x <= 0 && y >= 0) {
				return isOuter + ((x + y <= 0) ? 1 : 2);
			} else if (x >= 1 && y >= 0) {
				return isOuter + ((y >= x) ? 3 : 4);
			} else if (x >= 1 && y <= -1) {
				return isOuter + ((x + y >= 0) ? 5 : 6);
			}
			return -1;
		}

		public int getPlayerId() {
			return playerId;
		}

		public int getUnionCoin() {
			return unionCoin;
		}

		/**
		 * 序列化1D化的格子数组
		 *
		 * @param 无
		 * @return 序列化后的字符串
		 */
		public String getGrids() {
			int[] temp = new int[boardLength[0] * boardLength[1]];
			for (int i = 0; i < boardLength[0]; i++) {
				for (int j = 0; j < boardLength[1]; j++) {
					temp[i * boardLength[1] + j] = grids[i][j];
				}
			}
			return Arrays.toString(temp);
		}

		/**
		 * 序列化映射数组
		 *
		 * @param 无
		 * @return 序列化后的字符串
		 */
		public String getAttrMapping() {
			return Arrays.toString(attrMapping);
		}

		public double[] getAttributes() {
			return attributes;
		}

		public HashMap<Integer, Integer> getAttributeSkill() {
			return attributeSkill;
		}

		/**
		 * 更新当前拼图版的有效边长：302 205 203 201 104 101
		 *
		 * @param 无
		 * @return 无
		 */
		public void updateCurBoardLength() {
			if (this.unionLevel >= 104) {
				curBoardLength[0] += 2;
				curBoardLength[1] += 2;
			}
			if (this.unionLevel >= 201) {
				curBoardLength[0] += 2;
				curBoardLength[1] += 2;
			}
			if (this.unionLevel >= 203) {
				curBoardLength[0] += 2;
				curBoardLength[1] += 2;
			}
			if (this.unionLevel >= 205) {
				curBoardLength[0] += 2;
				curBoardLength[1] += 2;
			}
			if (this.unionLevel >= 302) {
				curBoardLength[0] += 2;
				curBoardLength[1] += 2;
			}
		}

		/**
		 * 联盟升级。会扩大边界范围。
		 *
		 * @param 无
		 * @return 是否成功
		 */
		public boolean upgradeUnion() {
			int nextLevel = unionLevel % 100 == 5 ? (unionLevel / 100 + 1) * 100 + 1 : unionLevel + 1;
			// 暂时就 4*5 个等级
			if (nextLevel < 101 || nextLevel > 405)
				return false;
			int[] index = upgradeUnionCost[nextLevel / 100 - 1][nextLevel % 100 - 1];
			if (levelSum >= index[0] && unionCoin >= index[1]) {
				// TODO 通知玩家？
				addCoin(-index[1]);
				unionLevel = nextLevel;
				updateCurBoardLength();
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 变更联盟币
		 *
		 * @param num 联盟币数量变化
		 * @return 无
		 */
		public void addCoin(int num) {
			this.unionCoin += num;
			// TODO 更新数据库？
		}

		/**
		 * 添加组件
		 *
		 * @param cardSkillId 角色卡编号
		 * @param quality     角色卡品质，0~4
		 * @param angle       组件逆时针旋转角度，是90的倍数
		 * @param coordNum    坐标值，等同于从1开始从左到右从上到下的位置数
		 * @return 是否成功
		 */
		public boolean addModule(int cardSkillId, int quality, int angle, int coordNum) {
			int careerGroup = findCareerGroup(cardSkillId);
			int[][] thisModule = modules[quality][careerGroup];
			// 绕中心顺时针旋转90°
			for (int i = 0; i < angle / 90; i++) {
				for (int[] m : thisModule) {
					int x = m[0], y = m[1];
					m[0] = y;
					m[1] = -x;
				}
			}
			// 合并为实际坐标
			int[] center = new int[] { coordNum / boardLength[1], coordNum % boardLength[1] };
			for (int[] m : thisModule) {
				m[0] += center[0];
				m[1] += center[1];
				// 坐标越界
				if (m[0] < boardLength[0] / 2 - curBoardLength[0] / 2
						|| m[0] >= boardLength[0] / 2 + curBoardLength[0] / 2
						|| m[1] < boardLength[1] / 2 - curBoardLength[1] / 2
						|| m[1] >= boardLength[1] / 2 + curBoardLength[1] / 2) {
					return false;
				}
			}
			// 终于可以放入了
			for (int[] m : thisModule) {
				int section = getSection(m[0], m[1]);
				sectionCount[section]++;
				this.grids[m[0]][m[1]] = cardSkillId;
			}

			// TODO 通知玩家？
			updateAttribute();
			return true;
		}

		/**
		 * 移除组件
		 *
		 * @param cardSkillId 角色卡编号
		 * @return 是否成功
		 */
		public boolean removeModule(int cardSkillId) {
			if (!moduleCoords.containsKey(cardSkillId))
				return false;
			int[] center = moduleCoords.get(cardSkillId);
			// 简单点，不用DFS了
			for (int i = -3; i <= 3; i++) {
				for (int j = -3; j <= 3; j++) {
					int x = center[0] + i, y = center[1] + j;
					if (x < 0 || x >= boardLength[0] || y < 0 || y >= boardLength[1])
						continue;
					int section = getSection(x, y);
					sectionCount[section]--;
					grids[x][y] = 0;
				}
			}
			moduleCoords.remove(cardSkillId);

			updateAttribute();
			return true;
		}

		/**
		 * 交换两个扇区对应的属性类型 TODO 抓包判断发来的属性类型是不是和默认布局一致
		 *
		 * @param sectionID 扇区编号
		 * @param skillID   新属性类型，SkillID = 71004000 + enum.ordinal()
		 * @return 是否成功
		 */
		public boolean updateAttrMap(int sectionID, int skillID) {
			// 寻找新属性目前存放在哪个扇区
			int sectionIDForNextAttr = -1;
			for (int i = 0; i < attrMapping.length; i++) {
				if (attrMapping[i] == skillID - 71004000) {
					sectionIDForNextAttr = i;
					break;
				}
			}
			// 找不到这个属性所在扇区，肯定哪里出问题了
			if (sectionIDForNextAttr < 0) {
				return false;
			}
			// 交换俩扇区的映射
			attrMapping[sectionIDForNextAttr] = attrMapping[sectionID];
			attrMapping[sectionID] = skillID - 71004000;

			updateAttribute();
			return true;
		}

		/**
		 * 更新属性统计值
		 *
		 * @param 无
		 * @return 无
		 */
		public void updateAttribute() {
			for (int i = 0; i < sectionCount.length; i++) {
				int attributeTypeID = attrMapping[i];
				attributes[attributeTypeID] = sectionCount[i] * attrBonus[attributeTypeID];
				attributeSkill.put(71004000 + attributeTypeID, sectionCount[i]);
			}
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
	 * 初始化
	 *
	 * @param 无
	 * @return 无
	 */
	public MapleUnion() {
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
				+ "`id` int(11) NOT NULL AUTO_INCREMENT, `playerid` int(11) NOT NULL,"
				+ "`levelsum` int(11) NOT NULL DEFAULT '0',`level` int(11) NOT NULL DEFAULT '101',`coin` int(11) NOT NULL DEFAULT '0', "
				+ "`grids` text NOT NULL DEFAULT '', `attrMap` text NOT NULL DEFAULT '', " + "PRIMARY KEY (`id`)"
				+ ") ;");
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
		ps = con.prepareStatement("SELECT `playerid`,`level`,`levelsum`,`coin`,`grids`, `attrMap`  FROM `maple_union`");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(1);
			playerInfo.put(playerId, new UnionInfo(playerId, rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5),
					rs.getString(6)));
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
	 * 判断角色卡所属的职业族
	 *
	 * @param cardSkillId 角色卡类型
	 * @return 战士-0 魔法师-1 弓箭手-2 飞侠-3 海盗-4
	 */
	public static int findCareerGroup(int cardSkillId) {
		return JobConstants.getTrueJobGrade(cardIdMapping.get(cardSkillId) * 10) - 1;
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
}
