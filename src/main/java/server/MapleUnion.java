package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	// 角色卡ID与职业ID的映射
	// 注意WZ里的数据把所有职业ID都 /10 了
	private HashMap<Integer, Integer> cardIdMapping;

	/**
	 * 初始化
	 *
	 * @param 无
	 * @return 无
	 */
	public MapleUnion() {
		grids = new int[22][22];
		attrMapping = new int[16];
		for (int i = 0; i < 16; i++) {
			attributes[i] = i;
		}
		attributes = new int[16];
		moduleCoords = new HashMap<>();
		unionCoin = 0;
		// 插入映射数据
		cardIdMapping.put(71000011, 11);
		cardIdMapping.put(71000012, 12);
		cardIdMapping.put(71000013, 13);
		cardIdMapping.put(71000021, 21);
		cardIdMapping.put(71000022, 22);
		cardIdMapping.put(71000023, 23);
		cardIdMapping.put(71000031, 31);
		cardIdMapping.put(71000032, 32);
		cardIdMapping.put(71000041, 41);
		cardIdMapping.put(71000042, 42);
		cardIdMapping.put(71000043, 43);
		cardIdMapping.put(71000051, 51);
		cardIdMapping.put(71000052, 52);
		cardIdMapping.put(71000053, 53);
		cardIdMapping.put(71000311, 311);
		cardIdMapping.put(71000312, 312);
		cardIdMapping.put(71000321, 321);
		cardIdMapping.put(71000331, 331);
		cardIdMapping.put(71000351, 351);
		cardIdMapping.put(71000361, 361);
		cardIdMapping.put(71000211, 211);
		cardIdMapping.put(71000221, 221);
		cardIdMapping.put(71000231, 231);
		cardIdMapping.put(71000241, 241);
		cardIdMapping.put(71000271, 271);
		cardIdMapping.put(71000111, 111);
		cardIdMapping.put(71000121, 121);
		cardIdMapping.put(71000131, 131);
		cardIdMapping.put(71000141, 141);
		cardIdMapping.put(71000151, 151);
		cardIdMapping.put(71000511, 511);
		cardIdMapping.put(71000611, 611);
		cardIdMapping.put(71000651, 651);
		cardIdMapping.put(71000711, 1011);
		cardIdMapping.put(71000251, 251);
		cardIdMapping.put(71000921, 1421);
		cardIdMapping.put(71000371, 371);
		cardIdMapping.put(71000411, 411);
		cardIdMapping.put(71000421, 421);
		cardIdMapping.put(71000057, 57);
		cardIdMapping.put(71000058, 58);
		cardIdMapping.put(71000059, 59);
		cardIdMapping.put(71000821, 1121);
	}

	/**
	 * 在数据库里创建基于玩家的联盟数据表
	 *
	 * @param 无
	 * @return 无
	 */
	public void createTableInDB() throws SQLException {
		try (DruidPooledConnection con = DatabaseConnection.getInstance().getConnection()) {
			try (PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `maple_union` ("
					+ "`id` int(11) NOT NULL AUTO_INCREMENT, `playerid` int(11) NOT NULL DEFAULT '0',"
					+ "`grids` text, `attrMap` text, PRIMARY KEY (`id`)" + ") ;")) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {

					}
				}
			}
		}
	}

	/**
	 * 从数据库里载入玩家资料
	 *
	 * @param 无
	 * @return 无
	 */
	public void loadFromDb(int playerId) throws SQLException {
		try (DruidPooledConnection con = DatabaseConnection.getInstance().getConnection()) {
			try (PreparedStatement ps = con
					.prepareStatement("SELECT `playerid`,`grids`, `attrMap`  FROM `maple_union` WHERE playerid = ?")) {
				ps.setInt(1, playerId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {

					}
				}
			}
		}
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
	 * 更新 组件——坐标 索引
	 *
	 * @param 无
	 * @return 无
	 */
	public void updateModuleCoordsMap() {

	}
}
