package client;

import client.MapleTrait.MapleTraitType;
import client.buffstat.MapleBuffStat;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import constants.ItemConstants;
import constants.skills.冒险家.弓箭手;
import constants.skills.冒险家.弓箭手.神射手;
import constants.skills.冒险家.弓箭手.箭神;
import constants.skills.冒险家.战士;
import constants.skills.冒险家.战士.英雄;
import constants.skills.冒险家.战士.黑骑士;
import constants.skills.冒险家.海盗;
import constants.skills.冒险家.海盗.冲锋队长;
import constants.skills.冒险家.魔法师.主教;
import constants.skills.冒险家.海盗.火炮手;
import constants.skills.冒险家.海盗.船长;
import constants.skills.冒险家.海盗.龙的传人;
import constants.skills.冒险家.飞侠;
import constants.skills.冒险家.飞侠.侠盗;
import constants.skills.冒险家.飞侠.隐士;
import constants.skills.冒险家.魔法师;
import constants.skills.冒险家.魔法师.冰雷;
import constants.skills.冒险家.魔法师.火毒;
import constants.skills.双刀;
import constants.skills.双弩;
import constants.skills.反抗者.唤灵斗师;
import constants.skills.反抗者.尖兵;
import constants.skills.反抗者.恶魔复仇者;
import constants.skills.反抗者.恶魔猎手;
import constants.skills.反抗者.机械师;
import constants.skills.反抗者.豹弩游侠;
import constants.skills.反抗者.预备兵;
import constants.skills.古迹猎人;
import constants.skills.影魂异人;
import constants.skills.晓之阵.剑豪;
import constants.skills.林之灵;
import constants.skills.神之子;
import constants.skills.神炮王;
import constants.skills.米哈尔;
import constants.skills.翼人族;
import constants.skills.翼人族.圣晶使徒;
import constants.skills.英雄.双弩精灵;
import constants.skills.英雄.夜光;
import constants.skills.英雄.幻影;
import constants.skills.英雄.战神;
import constants.skills.英雄.隐月;
import constants.skills.英雄.龙神;
import constants.skills.诺巴.爆莉萌天使;
import constants.skills.诺巴.狂龙战士;
import constants.skills.超能力者;
import constants.skills.骑士团.初心者;
import constants.skills.骑士团.夜行者;
import constants.skills.骑士团.奇袭者;
import constants.skills.骑士团.炎术士;
import constants.skills.骑士团.风灵使者;
import constants.skills.骑士团.魂骑士;
import handling.world.WorldGuild;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildSkill;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.StructItemOption;
import server.StructSetItem;
//import server.StructSetItem.SetItem;
import server.StructSetItemStat;
import server.life.Element;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.JobPacket;

public class MapleCharacterStat implements Serializable {
    
    private static final long serialVersionUID = -679541993413738569L;
    private final Map<Integer, Integer> add_skill_coolTimeR = new HashMap<>();  //减少技能的冷却时间
    private final Map<Integer, Integer> add_skill_duration = new HashMap();
    private final Map<Integer, Integer> add_skill_targetPlus = new HashMap();
    private final Map<Integer, Integer> add_skill_attackCount = new HashMap();
    private final Map<Integer, Integer> add_skill_ignoreMobpdpR = new HashMap();
    private final Map<Integer, Integer> add_skill_bossDamageRate = new HashMap<>();  //增加攻击BOSS的伤害
    private final ConcurrentHashMap<Integer, Integer> setHandling = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> skillsIncrement = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> damageIncrease = new ConcurrentHashMap<>();
    private final EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
    private final List<Triple<Integer, String, Integer>> psdSkills = new ArrayList<>();
    private final List<Equip> durabilityHandling = new ArrayList<>();
    private final List<Equip> equipLevelHandling = new ArrayList<>();
    private transient float shouldHealHP;
    private transient float shouldHealMP;
    
    private transient int watk;         //物理攻击力
    private transient int inc_PAD;      //增加物理攻击力
    private transient int percent_atk;     //增加物理攻击百分比

    private transient int magic;        //魔法攻击力
    private transient int mdef;         //增加魔法攻击力
    private transient int mdefr;        //增加魔法攻击百分比

    private transient int wdef;         //物理防御力
    private transient int inc_PDD;      //增加物理防御力值
    private transient int percent_wdef; //增加物理防御力百分比

    private transient int percent_mdef; //魔法防御增加x%

    private transient int localstr;     //总力量
    private transient int inc_STR;      //增加str值
    private transient int inc_STRr;     //增加str百分比

    private transient int localdex;     //总敏捷
    private transient int inc_DEX;      //增加dex值
    private transient int inc_DEXr;     //增加dex百分比

    private transient int localint;     //总智力
    private transient int inc_INT;      //增加int值
    private transient int inc_INTr;     //增加int百分比

    private transient int localluk;     //总运气
    private transient int inc_LUK;      //增加luk值
    private transient int inc_LUKr;     //增加luk百分比

    private transient int localmaxhp;   //最大HP
    private transient int addmaxhp;      //增加maxhp值
    private transient int percent_hp;     //增加maxhp百分比

    private transient int localmaxmp;   //最大MP
    private transient int addmaxmp;      //增加maxmp值
    private transient int percent_mp;     //增加maxmp百分比

    private transient short passive_sharpeye_rate;   //触发爆击概率
    private transient short inc_Cr;     //增加爆击 passive_sharpeye_rate
    private transient short critical_damage_min;  // 最小爆击 爆击最小伤害
    private transient short critical_damage_max;  // 最大爆击 爆击最大伤害

    public transient int ASR;//异常状态抗性
    public transient int TER;//所有属性抗性

    private transient int trueMastery;      //熟练度
    private transient int onActive;     //爆莉萌天使重生概率

    public int hp;
    public int maxhp;
    public int mp;
    public int maxmp;
    public int str;
    public int dex;
    public int luk;
    public int int_;
    
    private transient byte passive_trueMastery;
    
    private transient float localmaxbasedamage;
    private transient float localmaxbasepvpdamage;
    private transient float localmaxbasepvpdamageL;
    public transient boolean hasClone;
    
    public transient double percent_boss_damage_rate; //BOSS攻击 //BOSS伤害增加x%
    public transient double inc_DAMr;//BOSS伤害 BDR boss damage rate
    public transient double percent_damage; //攻击增加 也就是角色面板的攻击数字增加x%
    public transient double percent_damage_rate;//伤害增加x%
    public transient double expBuff;
    public transient double incRewardProp;
    
    public transient double mesoGuard, mesoGuardMeso;
    public transient double mesoBuff;//金币BUFF
    public transient double dropMod;
    
    public transient int percent_ignore_mob_def_rate; //无视防御
    public transient int incMaxDF;        //装备增加DF魔法值
    public transient int ignoreDAM;       //忽略怪物伤害值
    public transient int ignoreDAM_prop;  //忽略怪物伤害值几率
    public transient int ignoreDAMr;      //忽略怪物伤害百分比
    public transient int ignoreDAMr_prop; //忽略怪物伤害百分比几率
    public transient int incMesoProp;     //金币掉落百分比
    public transient int jump; //跳跃力
    public transient int speed; //移动速度
    public transient int speedMax; //最大移动速度提高
    public transient int dodgeChance;//回避率增加 x%
    public transient int levelBonus; //减少装备的穿戴等级
    public transient int mpconPercent;//消耗更多的 Mp 来增加技能的伤害
    public transient int pickRate;//几率值
    public transient int raidenCount, raidenPorp; //奇袭者雷电能够获得数量和概率
    public transient int ignore_mob_damage_rate; //被怪物攻击受到的伤害减少x%
    public transient int incMaxDamage;//技能伤害最大值

    public transient int recoverHP, recoverMP, mpconReduce, mpconCost, reduceCooltime, coolTimeR, expLossReduceR, DAMreflect, DAMreflect_rate,
            hpRecover_Percent, hpRecoverProp, hpRecoverPercent, mpRecover, mpRecoverProp, mpRecoverPercent, RecoveryUP, BuffUP, RecoveryUP_Skill, BuffUP_Skill,
            incAllskill, combatOrders, defRange, BuffUP_Summon, harvestingTool,
            equipmentBonusExp, equippedSummon,
            pvpDamage, hpRecoverTime, mpRecoverTime, dot, dotTime, pvpRank, pvpExp;
    public transient int def, element_ice, element_fire, element_light, element_psn;
    
    public void recalcLocalStats(MapleCharacter chra) {
        recalcLocalStats(false, chra);
    }
    
    private void resetLocalStats(final int job) {
        watk = 0;                //物理攻击力
        inc_PAD = 0;             //增加魔法攻击力
        percent_atk = 0;            //增加魔法攻击百分比

        magic = 0;                //魔法攻击力
        mdef = 0;                 //增加魔法攻击力
        mdefr = 0;                //增加魔法攻击百分比

        wdef = 0;                //防御力
        inc_PDD = 0;             //增加防御力值
        percent_wdef = 0;        //增加防御力百分比
        percent_mdef = 0;        //魔法防御增加x%

        localstr = getStr();     //力量
        inc_STR = 0;             //增加str值
        inc_STRr = 0;            //增加str百分比

        localdex = getDex();     //敏捷
        inc_DEX = 0;             //增加dex值
        inc_DEXr = 0;            //增加dex百分比

        localint = getInt();     //智力
        inc_INT = 0;             //增加int值
        inc_INTr = 0;            //增加int百分比

        localluk = getLuk();     //运气
        inc_LUK = 0;             //增加luk值
        inc_LUKr = 0;            //增加luk百分比

        localmaxhp = getMaxHp(); //最大HP
        addmaxhp = 0;             //增加maxhp值
        percent_hp = 0;            //增加maxhp百分比

        localmaxmp = getMaxMp();
        addmaxmp = 0;             //增加maxmp值
        percent_mp = 0;            //增加maxmp百分比

        passive_sharpeye_rate = 5;            //爆击
        inc_Cr = 0;              //增加爆击
        critical_damage_min = 20;   //最小暴击伤害 
        critical_damage_max = 50;   //最大暴击伤害

        ASR = 0;//异常状态抗性
        TER = 0;//所有属性抗性
        dot = 0; //持续伤害时间
        dotTime = 0;//持续伤害时间
        trueMastery = 0;//熟练度

        onActive = 0;
        
        pvpDamage = 0;
        percent_damage_rate = 100.0;//总伤

        mesoGuard = 50.0;
        mesoGuardMeso = 0.0;
        
        expBuff = 100.0;//经验BUFF
        incRewardProp = 100.0;//掉宝BUFF
        mesoBuff = 100.0;//枫币BUFF
        recoverHP = 0;//恢复HP量
        recoverMP = 0;//恢复MP量

        incMesoProp = 0;
        reduceCooltime = 0;//技能冷却
        coolTimeR = 0;//技能冷却
        expLossReduceR = 0;
        DAMreflect = 0;
        DAMreflect_rate = 0;
        
        mpconReduce = 0;//MP消耗量减少百分比
        mpconCost = 100;//MP消耗量增加百分比

        ignoreDAMr = 0;                    //无视伤害百分比
        ignoreDAMr_prop = 0;               //无视伤害百分比概率
        ignoreDAM = 0;                     //无视伤害
        ignoreDAM_prop = 0;                //无视伤害概率

        percent_ignore_mob_def_rate = 0; //无视防御率
        percent_boss_damage_rate = 100.0;//BOSS伤 百分比boss伤害

        hpRecover_Percent = 0;                //HP恢复值
        hpRecoverProp = 0;                    //HP恢复概率
        hpRecoverPercent = 0;                 //HP恢复百分比
        mpRecover = 0;                        //MP恢复值
        mpRecoverProp = 0;                    //MP恢复概率
        mpRecoverPercent = 0;                 //MP恢复百分比

        equippedSummon = 0;
        hasClone = false;
        equipmentBonusExp = 0;
        RecoveryUP = 0; //药水恢复量增加

        RecoveryUP_Skill = 0;
        BuffUP = 0;
        BuffUP_Skill = 0;      //增益技能持续时间增加
        BuffUP_Summon = 0;
        dropMod = 1.0;
        incMaxDF = 0;
        incAllskill = 0;
        combatOrders = 0;//战斗命令,用于计算技能等级
        defRange = isRangedJob(job) ? 200 : 0;
        durabilityHandling.clear();
        equipLevelHandling.clear();
        skillsIncrement.clear();
        damageIncrease.clear();
        setHandling.clear();
        harvestingTool = 0;
        element_fire = 100;
        element_ice = 100;
        element_light = 100;
        element_psn = 100;
        def = 100;
        add_skill_targetPlus.clear();
        add_skill_duration.clear();
        add_skill_coolTimeR.clear();//减少技能的冷却时间
        speed = 100;//移动速度
        jump = 100;//跳跃力
        dodgeChance = 0;//回避率增加 x%
        levelBonus = 0;//减少装备的穿戴等级
        percent_damage = 0.0;//攻击增加 也就是角色面板的攻击数字增加x%
        mpconPercent = 100;//消耗更多的 Mp 来增加技能的伤害
        pickRate = 0;//几率值
        raidenCount = 0;//奇袭者雷电能够获得数量
        raidenPorp = 0;//奇袭者雷电能够获得概率
        ignore_mob_damage_rate = 0;//被怪物攻击受到的伤害减少x%
        incMaxDamage = 0;//技能伤害最大值
    }

    /**
     * 计算各类属性状态
     *
     * @param first_login 是否第一次登入
     * @param chra 角色实例
     */
    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {
        if (chra.isClone()) {
            return; //clones share MapleCharacterStat objects and do not need to be recalculated
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int oldmaxhp = localmaxhp;
        
        resetLocalStats(chra.getJob());
        for (MapleTraitType t : MapleTraitType.values()) {
            chra.getTrait(t).clearLocalExp();
        }
        StructItemOption soc;
        final Map<Skill, SkillEntry> sData = new HashMap<>();
        final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
        while (itera.hasNext()) {
            final Equip equip = (Equip) itera.next();
            if (equip.getPosition() == -11) {
                if (ItemConstants.类型.魔法武器(equip.getItemId())) {
                    final Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                    if (eqstat != null) { //slow, poison, darkness, seal, freeze
                        if (eqstat.containsKey("incRMAF")) {
                            element_fire = eqstat.get("incRMAF");
                        }
                        if (eqstat.containsKey("incRMAI")) {
                            element_ice = eqstat.get("incRMAI");
                        }
                        if (eqstat.containsKey("incRMAL")) {
                            element_light = eqstat.get("incRMAL");
                        }
                        if (eqstat.containsKey("incRMAS")) {
                            element_psn = eqstat.get("incRMAS");
                        }
                        if (eqstat.containsKey("elemDefault")) {
                            def = eqstat.get("elemDefault");
                        }
                    }
                }
            }
            if ((equip.getItemId() / 10000 == 166 && equip.getAndroid() != null
                    || equip.getItemId() / 10000 == 167) && chra.getAndroid() == null) {
                final Equip android = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem(ItemConstants.ANDROID);
                final Equip heart = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem(ItemConstants.HEART);
                if (android != null && heart != null) {
                    chra.setAndroid(equip.getAndroid());
                }
            }
            
            if (ItemConstants.类型.戒指(equip.getItemId())) {
//                    final Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                final Map<String, Integer> stats = ii.getEquipStats(equip.getItemId());
                if (stats != null) { //slow, poison, darkness, seal, freeze
                    if (stats.get("incMHP") != null) {
                        percent_hp += ii.getItemIncMHPr(equip.getItemId());
                    }
                }
            }
            
            chra.getTrait(MapleTraitType.craft).addLocalExp(equip.getCraft());
            
            inc_PAD += equip.getPAD();
            mdef += equip.getMAD();
            inc_PDD += equip.getPDD();
            inc_STR += equip.getStr();
            inc_DEX += equip.getDex();
            inc_INT += equip.getInt();
            inc_LUK += equip.getLuk();
            inc_STRr += equip.getStatR();
            inc_DEXr += equip.getStatR();
            inc_INTr += equip.getStatR();
            inc_LUKr += equip.getStatR();
            addmaxhp += equip.getMaxHP();
            if (MapleJob.is恶魔复仇者(chra.getJob())) {
                addmaxhp += equip.getCHUC() * 60;
            }
            addmaxmp += equip.getMaxMP();
            percent_hp += ii.getItemIncMHPr(equip.getItemId());
            percent_mp += ii.getItemIncMMPr(equip.getItemId());
            inc_DAMr += equip.getBDR();
            percent_ignore_mob_def_rate += equip.getIMDR();
            pvpDamage += equip.getPVPDamage();
            percent_damage_rate *= ((double) equip.getDamR() + 100.0) / 100.0;
            
            speed += equip.getSpeed();
            jump += equip.getJump();
            magic += equip.getMAD();
            mdef += equip.getMDD();
            
            if (equip.getItemId() / 1000 == 1099) {
                incMaxDF += equip.getMaxMP();
            }
            
            switch (equip.getItemId()) {
                case 1112594://雪花天使的祝福
                    equippedSummon = 1090;//大天使
                    break;
                case 1112585://天使的祝福
                    equippedSummon = 1085;//大天使
                    break;
                case 1112586://黑天使的祝福
                    equippedSummon = 1087;//黑天使
                    break;
                case 1112663://白天使的祝福
                    equippedSummon = 1179;//白天使
                    break;
                case 1112735://白天使的祝福
                    equippedSummon = 80001154;//天使的翅膀
                    break;
                case 1113020://战神祝福戒指
                    equippedSummon = 80001262;//战神的祝福
                    break;
                case 1113008://能感受到#c恶魔#的气息。
                    equippedSummon = 80000052;// 恶魔之息
                    break;
                case 1113009://可以召唤出#c小恶魔#。
                    equippedSummon = 80000053;// 恶魔召唤
                    break;
                case 1113010://可以和#c大恶魔#签订灵魂契约
                    equippedSummon = 80000054;// 恶魔契约
                    break;
                case 1113189://白色天堂戒指（太阳）
                    equippedSummon = 80010067;//太阳天堂
                    break;
                case 1113190://白色天堂戒指（暴雨）
                    equippedSummon = 80010068;//暴雨天堂
                    break;
                case 1113191://白色天堂戒指（彩虹）
                    equippedSummon = 80010069;//彩虹天堂
                    break;
                case 1113192://白色天堂戒指（白雪）
                    equippedSummon = 80010070;//白雪天堂
                    break;
                case 1113193://白色天堂戒指（闪电）
                    equippedSummon = 80010071;//闪电白色天堂
                    break;
                case 1113194://白色天堂戒指（狂风）
                    equippedSummon = 80010072;//狂风天堂
                    break;
                case 1113204://[30天]白色天堂戒指（太阳）
                    equippedSummon = 80010075;//太阳天堂
                    break;
                case 1113205://[30天]白色天堂戒指（暴雨）
                    equippedSummon = 80010076;//暴雨天堂
                    break;
                case 1113206://[30天]白色天堂戒指（彩虹）
                    equippedSummon = 80010077;//彩虹天堂
                    break;
                case 1113207://[30天]白色天堂戒指（白雪）
                    equippedSummon = 80010078;//白雪天堂
                    break;
                case 1113208://[30天]白色天堂戒指（闪电）
                    equippedSummon = 80010079;//闪电白色天堂
                    break;
                case 1113209://[30天]白色天堂戒指（狂风）
                    equippedSummon = 80010080;//狂风天堂
                    break;
                case 1114207://玛瑙戒指“成长”
                case 1114226://“完成”玛瑙戒指
                case 1114238://重启玛瑙戒指“完成”
                case 1114213://玛瑙戒指“成长”
                    equippedSummon = 80011103;//蝾螈玛瑙
                    break;
                case 1114208://玛瑙戒指“成长”
                case 1114227://“完成”玛瑙戒指
                case 1114239://重启玛瑙戒指“完成”
                case 1114214://玛瑙戒指“成长”
                    equippedSummon = 80011104;//电子玛瑙
                    break;
                case 1114209://玛瑙戒指“成长”
                case 1114228://“完成”玛瑙戒指
                case 1114240://重启玛瑙戒指“完成”
                case 1114215://玛瑙戒指“成长”
                    equippedSummon = 80011105;//水神玛瑙
                    break;
                case 1114210://玛瑙戒指“成长”
                case 1114229://“完成”玛瑙戒指
                case 1114241://重启玛瑙戒指“完成”
                case 1114216://玛瑙戒指“成长”
                    equippedSummon = 80011106;//侏儒玛瑙
                    break;
                case 1114211://玛瑙戒指“成长”
                case 1114217://玛瑙戒指“成长”
                case 1114230://“完成”玛瑙戒指
                case 1114242://重启玛瑙戒指“完成”
                    equippedSummon = 80011107;//恶魔玛瑙
                    break;
                case 1114212://玛瑙戒指“成长”
                case 1114231://玛瑙戒指“成长”
                case 1114243://“完成”玛瑙戒指
                case 1114218://重启玛瑙戒指“完成”
                    equippedSummon = 80001519;//天使玛瑙
                    break;
                case 1114200://玛瑙戒指“相遇” 召唤#c元素玛瑙#。
                    equippedSummon = 80001518;//元素玛瑙
                    break;
                case 1114219://“初遇”玛瑙戒指 召唤#c元素玛瑙#。
                    equippedSummon = 80001715;//元素玛瑙
                    break;
                case 1114201://玛瑙戒指“共享” 召唤#c元素玛瑙#。
                case 1114220://玛瑙戒指“共享”召唤#c火焰玛瑙#。
                    equippedSummon = 80001519;//火焰玛瑙
                    break;
                default:
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
                    break;
            }

            //套装属性
            final Integer set = ii.getSetItemID(equip.getItemId());
            if (set != null && set > 0) {
                int value = 1;
                if (setHandling.containsKey(set)) {
                    value += setHandling.get(set);
                }
                setHandling.put(set, value); //套装ID 套装携带的数量
                //处理套装属性
                Pair<Integer, Integer> setix = handleEquipSetStats(ii, chra, first_login, sData, set, value);
                if (setix != null) {
                    localmaxhp += setix.getLeft();
                    localmaxmp += setix.getRight();
                }
            }
            //装备技能
            if (equip.getLevelUpType() > 0 && ii.getEquipSkills(equip.getItemId()) != null) {
                for (final int zzz : ii.getEquipSkills(equip.getItemId())) {
                    final Skill skil = SkillFactory.getSkill(zzz);
                    if (skil != null && skil.canBeLearnedBy(chra.getJob())) { //dont go over masterlevel :D
                        int value = 1;
                        if (skillsIncrement.get(skil.getId()) != null) {
                            value += skillsIncrement.get(skil.getId());
                        }
                        skillsIncrement.put(skil.getId(), value);
                    }
                }
            }
            final Pair<Integer, Integer> ix = handleEquipAdditions(ii, chra, first_login, sData, equip.getItemId());
            if (ix != null) {
                addmaxhp += ix.getLeft();
                addmaxmp += ix.getRight();
            }
            //装备星岩
            if (equip.getSocketState() > 15) {
                final int[] sockets = {equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                for (final int i : sockets) {
                    if (i > 0) {
                        soc = ii.getSocketInfo(i);
                        if (soc != null) {
                            addmaxhp += soc.get("incMHP");
                            addmaxmp += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            //装备潜能
            if (equip.getGrade(false) >= 17 || equip.getGrade(true) >= 17) {
                final int[] potentials = {equip.getOption(1, false), equip.getOption(2, false), equip.getOption(3, false), equip.getOption(1, true), equip.getOption(2, true), equip.getOption(3, true),};
                for (final int i : potentials) {
                    if (i > 0) {
                        soc = ii.getPotentialInfo(i).get(ii.getReqLevel(equip.getItemId()) / 10 <= 0 ? 0 : ii.getReqLevel(equip.getItemId()) / 10 >= 20 ? 19 : (ii.getReqLevel(equip.getItemId()) / 10) - 1);
                        if (soc != null) {
                            addmaxhp += soc.get("incMHP");
                            addmaxmp += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            //装备星岩
            if (equip.getSocketState() > 15) {
                final int[] sockets = {equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                for (final int i : sockets) {
                    if (i > 0) {
                        soc = ii.getSocketInfo(i);
                        if (soc != null) {
                            addmaxhp += soc.get("incMHP");
                            addmaxmp += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            if (equip.getDurability() > 0) {
                durabilityHandling.add(equip);
            }
            if (GameConstants.getMaxLevel(equip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? (equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())) : (equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId())))) {
                equipLevelHandling.add(equip);
            }
        }

        //套装
//        final Iterator<Entry<Integer, Integer>> iter = setHandling.entrySet().iterator();
//        while (iter.hasNext()) {
//            final Entry<Integer, Integer> entry = iter.next();
//            final StructSetItem set = ii.getSetItem(entry.getKey());
//            if (set != null) {
//                final Map<Integer, SetItem> itemz = set.getItems();
//                for (Entry<Integer, SetItem> ent : itemz.entrySet()) {
//                    if (ent.getKey() <= entry.getValue()) {
//                        SetItem se = ent.getValue();
//                        inc_PAD += se.incPAD;
//                        mdef += se.incMAD;
//                        inc_PDD += se.incPDD;
//                        inc_STR += se.incSTR + se.incAllStat;
//                        inc_DEX += se.incDEX + se.incAllStat;
//                        inc_INT += se.incINT + se.incAllStat;
//                        inc_LUK += se.incLUK + se.incAllStat;
//                        addmaxhp += se.incMHP;
//                        addmaxmp += se.incMMP;
//                        percent_hp += se.incMHPr;
//                        percent_mp += se.incMMPr;
//                        //套装里面的潜能属性处理
//                        if (se.option1 > 0 && se.option1Level > 0) {
//                            soc = ii.getPotentialInfo(se.option1).get(se.option1Level);
//                            if (soc != null) {
//                                addmaxhp += soc.get("incMHP");
//                                addmaxmp += soc.get("incMMP");
//                                handleItemOption(soc, chra, first_login, sData);
//                            }
//                        }
//                        if (se.option2 > 0 && se.option2Level > 0) {
//                            soc = ii.getPotentialInfo(se.option2).get(se.option2Level);
//                            if (soc != null) {
//                                addmaxhp += soc.get("incMHP");
//                                addmaxmp += soc.get("incMMP");
//                                handleItemOption(soc, chra, first_login, sData);
//                            }
//                        }
//                    }
//                }
//            }
//        }
        handleProfessionTool(chra);
        for (Item item : chra.getInventory(MapleInventoryType.CASH).newList()) {
            if (item.getItemId() / 10000 == 521) {
                double rate = ii.getExpCardRate(item.getItemId());
                if (item.getItemId() != 5210009 && rate > 1.0) {
                    if (!ii.isExpOrDropCardTime(item.getItemId()) || chra.getLevel() > ii.getExpCardMaxLevel(item.getItemId()) || (item.getExpiration() == -1L && !chra.isIntern())) {
                        if (item.getExpiration() == -1L && !chra.isIntern()) {
                            chra.dropMessage(5, ii.getName(item.getItemId()) + "属性错误，经验值加成无效。");
                        }
                        continue;
                    }
                }
            } else if (dropMod == 1.0 && item.getItemId() / 10000 == 536) {
                if (item.getItemId() >= 5360000 && item.getItemId() < 5360100) {
                    if (!ii.isExpOrDropCardTime(item.getItemId()) || (item.getExpiration() == -1L && !chra.isIntern())) {
                        if (item.getExpiration() == -1L && !chra.isIntern()) {
                            chra.dropMessage(5, ii.getName(item.getItemId()) + "属性错误，掉宝几率加成无效。");
                        }
                        continue;
                    }
                    dropMod = 2.0;
                }
            } else if (item.getItemId() == 5590001) { //高级装备特许证 - 拥有装备特许证后可以装备比自己等级高#c10级#的装备
                levelBonus = 10;
            } else if (levelBonus == 0 && item.getItemId() == 5590000) { //装备特许证 - 拥有装备特许证后可以装备比自己等级高#c5级#的装备
                levelBonus = 5;
            }
        }
        //其他栏道具
        for (Item item : chra.getInventory(MapleInventoryType.ETC).list()) {
            switch (item.getItemId()) {
                case 4030003:
                    break;
                case 4030004:
                    break;
                case 4031864:
                    break;
            }
        }
        
        if (first_login && chra.getLevel() >= 30) {
            //大天使,黑天使,白色天使
            int[] skills = {1085, 1087, 1179};
            for (int skillId : skills) {
                if (chra.isIntern()) { //!job lol
                    for (int i = 0; i < allJobs.length; i++) {
                        sData.put(SkillFactory.getSkill(skillId + allJobs[i]), new SkillEntry((byte) -1, (byte) 0, -1));
                    }
                } else {
                    sData.put(SkillFactory.getSkill(getSkillByJob(skillId, chra.getJob())), new SkillEntry((byte) -1, (byte) 0, -1));
                }
            }
        }
        /*
         * 宝盒属性加成
         */
        if (chra.getCoreAura() != null) {
            inc_PAD += chra.getCoreAura().getAtt();
            mdef += chra.getCoreAura().getMagic();
            inc_STR += chra.getCoreAura().getStr();
            inc_DEX += chra.getCoreAura().getDex();
            inc_INT += chra.getCoreAura().getInt();
            inc_LUK += chra.getCoreAura().getLuk();
        }

        // add to localmaxhp_ if percentage plays a role in it, else add_hp
        handleBuffStats(chra);//BUFF被动
//        if (MapleJob.is影魂异人(chra.getJob())) {
//        } else {
        handlePassiveSkills(chra);//被动技能
//        handleHyperPassiveSkills(chra);//超技被动
//        }

        if (chra.getGuildId() > 0) {
            final MapleGuild g = WorldGuild.getInstance().getGuild(chra.getGuildId());
            if (g != null && g.getSkills().size() > 0) {
                final long now = System.currentTimeMillis();
                for (MapleGuildSkill gs : g.getSkills()) {
                    if (gs.timestamp > now && gs.activator.length() > 0) {
                        final MapleStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                        inc_PAD += e.getPadX();
                        mdef += e.getMadX();
                        percent_wdef += e.getPddR();
                        inc_Cr += e.getCr();
                        expBuff *= (e.getEXPRate() + 100.0) / 100.0;
                    }
                }
            }
        }
        //角色卡
        for (Pair<Integer, Integer> ix : chra.getCharacterCard().getCardEffects()) {
            //System.out.println("[角色卡] 等级: " + ix.getRight() + " 技能: " + ix.getLeft() + " - " + SkillFactory.getSkillName(ix.getLeft()));
            final MapleStatEffect e = SkillFactory.getSkill(ix.getLeft()).getEffect(ix.getRight());

            RecoveryUP += e.getMpConEff();
            inc_Cr += e.getCr();
            BuffUP_Summon += e.getSummonTimeR();
            expLossReduceR += e.getExpLossReduceR();
            ASR += e.getAsrR();
            BuffUP_Skill += e.getBufftimeR();
            coolTimeR += e.getCoolTimeR();
            incMesoProp += e.getMesoR();
            critical_damage_max += e.getCriticaldamageMax();
            inc_PAD += e.getPadX();
            mdef += e.getMadX();
            inc_PDD += e.getPddX();
            percent_wdef += e.getPddR();
            addmaxhp += e.getMhpX();
            addmaxmp += e.getMmpX();
            percent_hp += e.getMhpR();
            percent_mp += e.getMmpR();
            inc_STR += e.getStrX() + e.getStrFX();
            inc_DEX += e.getDexX() + e.getDexFX();
            inc_INT += e.getIntX() + e.getIntFX();
            inc_LUK += e.getLukX() + e.getLukFX();
            inc_DAMr += e.getBdR();
            percent_ignore_mob_def_rate += e.getIgnoreMobpdpR();
        }
        //内在技能
        for (int i = 0; i < 3; i++) {
            InnerSkillValueHolder innerSkill = chra.getInnerSkills()[i];
            if (innerSkill == null) {
                continue;
            }
            MapleStatEffect e = SkillFactory.getSkill(innerSkill.getSkillId()).getEffect(innerSkill.getSkillLevel());
            if (e == null) {
                continue;
            }
            inc_PAD += e.getPadX();
            mdef += e.getMadX();
            inc_PDD += e.getPddX();
            percent_wdef += e.getPddR();
            percent_mdef += e.getMDEFRate();
            inc_STR += e.getStrFX();
            inc_DEX += e.getDexFX();
            inc_INT += e.getIntFX();
            inc_LUK += e.getLukFX();
            addmaxhp += e.getMhpX();
            addmaxmp += e.getMmpX();
            percent_hp += e.getMhpR();
            percent_mp += e.getMmpR();
            jump += e.getPassiveJump();
            speed += e.getPassiveSpeed();
            dodgeChance += e.getPercentAvoid();
        }

        //物理攻击力
        watk += inc_PAD;
        watk += Math.floor((watk * percent_atk) / 100.0f);

        //魔法攻击力
        magic += mdef;
        magic += Math.floor((magic * mdefr) / 100.0f);

        //总力量
        localstr += inc_STR;
        localstr += Math.floor((localstr * inc_STRr) / 100.0f);

        //总敏捷
        localdex += inc_DEX;
        localdex += Math.floor((localdex * inc_DEXr) / 100.0f);

        //总智力
        localint += inc_INT;
        localint += Math.floor((localint * inc_INTr) / 100.0f);

        //总运气
        localluk += inc_LUK;
        localluk += Math.floor((localluk * inc_LUKr) / 100.0f);

        //防御力
        wdef += chra.getTrait(MapleTraitType.will).getLevel();
        wdef += inc_PDD;
        wdef += Math.floor((localstr * 1.5) + ((localdex + localluk) * 0.4));
        wdef += Math.min(30000, Math.floor((wdef * percent_wdef) / 100.0f));

        //计算最大Hp先算倾向系统 在算技能增加上限 最后被动技能增加
        localmaxhp += (chra.getTrait(MapleTraitType.will).getLevel() / 5) * 100;
        localmaxhp += addmaxhp; //必须在百分比之前计算
        localmaxhp += Math.floor((localmaxhp * percent_hp) / 100.0f); //百分比必须最后计算
        localmaxhp = Math.min(500000, Math.abs(Math.max(-500000, localmaxhp)));

        //计算最大Mp先算倾向系统 在算被动技能增加 最后计算技能增加上限
        localmaxmp += chra.getTrait(MapleTraitType.sense).getLevel() * 20;
        localmaxmp += addmaxmp; //必须在百分比之前计算
        localmaxmp += Math.floor((localmaxmp * percent_mp) / 100.0f); //百分比必须最后计算
        localmaxmp = Math.min(500000, Math.abs(Math.max(-500000, localmaxmp)));

        //触发爆击概率
        passive_sharpeye_rate += inc_Cr;
        passive_sharpeye_rate += Math.floor((passive_sharpeye_rate) / 100.0f);

        //BOSS攻击 //BOSS伤害增加x%
        percent_boss_damage_rate += inc_DAMr;

        //无视防御
        percent_ignore_mob_def_rate += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        ASR += chra.getTrait(MapleTraitType.will).getLevel() / 5;
        pvpDamage += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        
        calculateFame(chra);
        
        if (chra.getEventInstance() != null && chra.getEventInstance().getName().startsWith("PVP")) { //hack
            localmaxhp = Math.min(40000, localmaxhp * 3); //approximate.
            localmaxmp = Math.min(20000, localmaxmp * 2);
            //not sure on 20000 cap
            MapleStatEffect eff;
            for (int i : pvpSkills) {
                Skill skil = SkillFactory.getSkill(i);
                if (skil != null && skil.canBeLearnedBy(chra.getJob())) {
                    sData.put(skil, new SkillEntry((byte) 1, (byte) 0, -1));
                    eff = skil.getEffect(1);
                    switch ((i / 1000000) % 10) {
                        case 1:
                            if (eff.getX() > 0) {
                                pvpDamage += (wdef / eff.getX());
                            }
                            break;
                        case 3:
                            hpRecoverProp += eff.getProp();
                            hpRecover_Percent += eff.getX();
                            mpRecoverProp += eff.getProp();
                            mpRecover += eff.getX();
                            break;
                        case 5:
                            inc_Cr += eff.getProp();
                            critical_damage_max = 100;
                            break;
                    }
                    break;
                }
            }
            eff = chra.getStatForBuff(MapleBuffStat.CS_Morph);
            if (eff != null && eff.getSourceId() % 10000 == 1105) { //ice knight
                localmaxhp = 500000;
                localmaxmp = 500000;
            }
        }
        chra.changeSkillLevel_Skip(sData, false);
        if (MapleJob.is恶魔猎手(chra.getJob())) {
            localmaxmp = GameConstants.getMPByJob(chra.getJob());
            localmaxmp += incMaxDF;
        } else if (MapleJob.is神之子(chra.getJob())) {
            localmaxmp = 100;
        }
        CalcPassive_trueMastery(chra);//武器熟练度
        recalcPVPRank(chra);
        if (first_login) {
            chra.silentEnforceMaxHpMp();
            relocHeal(chra);
        } else {
            chra.enforceMaxHpMp();
        }
        calculateMaxBaseDamage(Math.max(magic, watk), pvpDamage, chra);
        trueMastery = Math.min(100, trueMastery);
        critical_damage_min = (short) Math.min(critical_damage_min, critical_damage_max);
        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            chra.updatePartyMemberHP();
        }
    }

    /*
    被动技能处理
     */
    private void handlePassiveSkills(MapleCharacter chra) {
        /*psdSkills.clear();
        for (Skill sk : chra.getSkills().keySet()) {
//        for (Integer sk : chra.getSkills().keySet()) {
            Skill skill = SkillFactory.getSkill(sk.getId());
            if (skill != null && skill.getPsd() == 1) {
                Triple<Integer, String, Integer> psdSkill = new Triple<>(0, "", 0);
                psdSkill.left = skill.getPsdSkill();
                psdSkill.mid = skill.getPsdDamR();
                psdSkill.mid = skill.getTargetPlus();
                psdSkill.right = skill.getId();
                psdSkills.add(psdSkill);
            }
        }*/
        //通用被动技能和特殊被动技能
        handlePassiveSkill_sxinshou(chra);
        //冒险家新手被动技能
        handlePassiveSkill_smaoxianjia(chra);
        //骑士团新手被动技能
        handlePassiveSkill_qishituan(chra);
        //炎术士技能和特殊技能
        handlePassiveSkill_yanshu(chra);
        //黑骑士技能和特殊技能
        handlePassiveSkill_heiqishi(chra);
        //火毒技能和特殊技能
        handlePassiveSkill_huodu(chra);
        //冰雷技能和特殊技能
        handlePassiveSkill_binlei(chra);
        //主教技能和特殊技能
        handlePassiveSkill_zhujiao(chra);
        //神射手技能和特殊技能
        handlePassiveSkill_shenshe(chra);
        //箭神技能和特殊技能
        handlePassiveSkill_jianshen(chra);
        //隐士技能和特殊技能
        handlePassiveSkill_yinshi(chra);
        //侠盗技能和特殊技能
        handlePassiveSkill_xiadao(chra);
        //双刀技能和特殊技能
        handlePassiveSkill_shuangdao(chra);
        //冲锋队长技能和特殊技能
        handlePassiveSkill_duizhang(chra);
        //船长技能和特殊技能
        handlePassiveSkill_chuanzhang(chra);
        //神炮王技能和特殊技能
        handlePassiveSkill_shenpaowang(chra);
        //龙的传人技能和特殊技能
        handlePassiveSkill_longdechuangren(chra);
        //魂骑士技能和特殊技能
        handlePassiveSkill_hunqishi(chra);
        //炎术士技能和特殊技能
        handlePassiveSkill_yanshushi(chra);
        //风灵使者技能和特殊技能
        handlePassiveSkill_fengling(chra);
        //夜行者技能和特殊技能
        handlePassiveSkill_yexingzhe(chra);
        //奇袭者技能和特殊技能
        handlePassiveSkill_qixizhe(chra);
        //战神技能和特殊技能
        handlePassiveSkill_zhansheng(chra);
        //龙神技能和特殊技能
        handlePassiveSkill_longsheng(chra);
        //双弩精灵技能和特殊技能
        handlePassiveSkill_shuangnu(chra);
        //幻影技能和特殊技能
        handlePassiveSkill_huanying(chra);
        //隐月和特殊技能
        handlePassiveSkill_yingyue(chra);
        //夜光和特殊技能
        handlePassiveSkill_yeguang(chra);
        //恶魔猎手和特殊技能
        handlePassiveSkill_emo(chra);
        //恶魔复仇者和特殊技能
        handlePassiveSkill_emofuchou(chra);
        //唤灵斗师和特殊技能
        handlePassiveSkill_huanglingdoushi(chra);
        //豹弩游侠和特殊技能
        handlePassiveSkill_baonuyouxia(chra);
        //机械师和特殊技能
        handlePassiveSkill_jixieshi(chra);
        //尖兵和特殊技能
        handlePassiveSkill_jianbin(chra);
        //爆破手和特殊技能
        handlePassiveSkill_baopo(chra);
        //剑豪技能和特殊技能
        handlePassiveSkill_jianhao(chra);
        //米哈尔和特殊技能
        handlePassiveSkill_mihaer(chra);
        //狂龙战士和特殊技能
        handlePassiveSkill_kuanglong(chra);
        //爆莉萌天使和特殊技能
        handlePassiveSkill_tianshi(chra);
        //神之子和特殊技能
        handlePassiveSkill_shenzhizi(chra);
        //林之灵和特殊技能
        handlePassiveSkill_linzhiling(chra);
        //超能力者和特殊技能
        handlePassiveSkill_chaonenglizhe(chra);
        //影魂异人和特殊技能
        handlePassiveSkill_yonghunyiren(chra);
        //圣晶使徒和特殊技能
        handlePassiveSkill_shenjin(chra);
        //古迹猎人和特殊技能
        handlePassiveSkill_guyilianren(chra);
        
    }

    /*
         * ---------------------------------
         * 古迹猎人和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_guyilianren(MapleCharacter chra) {
        
        if (MapleJob.is古迹猎人(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(古迹猎人.上古指引);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
            }
            
        }
    }

    /*
         * ---------------------------------
         * 圣晶使徒和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shenjin(MapleCharacter chra) {
        
        if (MapleJob.is圣晶使徒(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(圣晶使徒.祝福标记);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
            }
            bx = SkillFactory.getSkill(圣晶使徒.翼人精通);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
            }
        }
    }

    /*
         * ---------------------------------
         * 影魂异人和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yonghunyiren(MapleCharacter chra) {
        
        if (MapleJob.is影魂异人(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(影魂异人.发动融合); //112100015 - 拉伊之心 强化 - 转换成雪豹状态后，攻击速度及魔法攻击力提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
            }
        }
    }

    /*
         * ---------------------------------
         * 超能力者技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_chaonenglizhe(MapleCharacter chra) {
        
        if (MapleJob.is超能力者(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(超能力者.内在1); //112100015 - 拉伊之心 强化 - 转换成雪豹状态后，攻击速度及魔法攻击力提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
            }
            bx = SkillFactory.getSkill(超能力者.内在2); //112100015 - 拉伊之心 强化 - 转换成雪豹状态后，攻击速度及魔法攻击力提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
            }
            bx = SkillFactory.getSkill(超能力者.精神集中_维持); //112100015 - 拉伊之心 强化 - 转换成雪豹状态后，攻击速度及魔法攻击力提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                BuffUP_Skill += eff.getBuffTimeRate();
            }
        }
    }

    /*
         * ---------------------------------
         * 林之灵技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_linzhiling(MapleCharacter chra) {
        
        if (MapleJob.is林之灵(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(林之灵.精灵集中); //110000800 - 精灵集中 - 攻击BOSS怪时,精灵之力会更强。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
                passive_sharpeye_rate += eff.getCritical();
                percent_boss_damage_rate += eff.getBossDamage();
            }
            bx = SkillFactory.getSkill(林之灵.林之灵之修养); //110000513 - 林之灵之修养 - 林之灵每次获得经验值时,潜力上升,自动得到成长。\r\n从60级开始可以学习。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localint += eff.getIntX();
                localluk += eff.getLukX();
                passive_sharpeye_rate += eff.getCritical();
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
                percent_boss_damage_rate += eff.getBossDamage();
                percent_damage += eff.getMagicDamage();
                wdef += eff.getWdefX();
                mdef += eff.getMdefX();
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            /*
                 * 超级技能处理
             */
            bx = SkillFactory.getSkill(林之灵.致命三角_额外目标); //112120047 - 致命三角 -额外目标 - 利用致命三角攻击的怪物数量增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(林之灵.致命三角, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(林之灵.致命三角_额外攻击); //112120048 - 致命三角 - 额外攻击 - 致命三角的攻击次数增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(林之灵.致命三角, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(林之灵.编队攻击_额外目标); //112120050 - 编队攻击  - 额外目标 - 利用编队攻击攻击的怪物数量增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(林之灵.编队攻击, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(林之灵.伙伴发射_额外目标); //112120053 - 伙伴发射 - 额外目标 - 利用伙伴发射攻击的怪物数量增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(林之灵.伙伴发射, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(林之灵.伙伴发射2, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(林之灵.伙伴发射3, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(林之灵.伙伴发射4, bx.getEffect(bof).getTargetPlus());
            }
            /*
                 * 模式技能处理
             */
            int buffSourceId = chra.getBuffSource(MapleBuffStat.ANIMAL_CHANGE);
            if (buffSourceId == 林之灵.巨熊模式) {
                bx = SkillFactory.getSkill(林之灵.波波之粮食储备); //112000011 - 波波之粮食储备 - 巨熊状态下最大HP和智力提高一定量。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    localint += eff.getIntX();
                }
                bx = SkillFactory.getSkill(林之灵.波波之坚韧); //112000012 - 波波之坚韧 - 巨熊状态下，可以无视怪物一定量的防御力，并且攻击速度提高。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_ignore_mob_def_rate += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(林之灵.怒之乱击); //112000003 - 怒之乱击 - 连续按下#前爪挥击#技能时，该技能会在第4击时发动。每次按下技能键会给前方的敌人造成连续攻击。\n[发动命令]: #c前爪挥击#3击以后，连续按下按键时
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    addDamageIncrease(林之灵.前爪挥击, eff.getDAMRate());
                    addDamageIncrease(林之灵.前爪挥击2, eff.getDAMRate());
                    addDamageIncrease(林之灵.前爪挥击3, eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(林之灵.波波之致命一击); //112000014 - 波波之致命一击 - 巨熊状态下爆击率、最小伤害、最大伤害提高，魔法攻击力永久增加。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    passive_sharpeye_rate += eff.getCritical();
                    critical_damage_max += eff.getCriticalMax();
                    critical_damage_min += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(林之灵.波波之勇猛); //112000013 - 波波之勇猛 - 巨熊状态下，增加一定%的魔法攻击力。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_damage += eff.getMagicDamage();
                }
                bx = SkillFactory.getSkill(林之灵.集中打击); //112001009 - 集中打击 - 一定时间内，变成集中攻击‘一个敌人’的状态，伤害大幅提升。此外，巨熊的身体得到强化。最大HP和MP，以及防御力增加。并可以无视怪物一定量的防御力。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    percent_mp += eff.getPercentMP();
                    percent_wdef += eff.getWDEFRate();
                    percent_mdef += eff.getMDEFRate();
                    percent_ignore_mob_def_rate += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(林之灵.火焰屁_强化); //112000020 - 火焰屁 强化 - 强化魔法攻击力，将火焰屁的火焰强化成更加强大的地狱火。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    addDamageIncrease(林之灵.火焰屁, eff.getDAMRate());
                }
            } else if (buffSourceId == 林之灵.雪豹模式) { //雪豹模式
                bx = SkillFactory.getSkill(林之灵.拉伊之力_强化); //112100013 - 拉伊之力 强化 - 强化雪豹的力量，提高魔法攻击力、智力、移动速度和跳跃力。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    localint += eff.getIntX();
                    magic += eff.getMagicX();
                    jump += eff.getPassiveJump();
                    speed += eff.getPassiveSpeed();
                }
                bx = SkillFactory.getSkill(林之灵.雪豹咆哮); //112100003 - 雪豹咆哮 - 在#c雪豹强袭#之后，向前方咆哮，造成额外伤害。此外，#c雪豹重斩#和#c雪豹强袭#的伤害及可攻击的怪物数量得到提高。\n[发动命令]：施展#c雪豹强袭#后，再次按下#c[攻击]#键时
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    addDamageIncrease(林之灵.雪豹重斩, eff.getDAMRate());
                    addDamageIncrease(林之灵.雪豹强袭, eff.getDAMRate());
                    addDamageIncrease(林之灵.雪豹_未知, eff.getDAMRate());
                    addTargetPlus(林之灵.雪豹重斩, eff.getTargetPlus());
                    addTargetPlus(林之灵.雪豹强袭, eff.getTargetPlus());
                    addTargetPlus(林之灵.雪豹_未知, eff.getTargetPlus());
                }
                bx = SkillFactory.getSkill(林之灵.男子汉姿态); //112100006 - 男子汉姿态 - 在#c男子汉步伐#后，展现雪豹的帅气姿态，并对地面施以巨大冲击，造成额外的范围伤害，受到伤害的敌人陷入眩晕。此外，#c男子汉之舞，男子汉步伐#攻击的怪物数量增加。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    addDamageIncrease(林之灵.男子汉之舞, eff.getDAMRate());
                    addDamageIncrease(林之灵.男子汉步伐, eff.getDAMRate());
                    addTargetPlus(林之灵.男子汉之舞, eff.getTargetPlus());
                    addTargetPlus(林之灵.男子汉步伐, eff.getTargetPlus());
                }
                bx = SkillFactory.getSkill(林之灵.拉伊之牙_强化); //112100010 - 拉伊之牙 强化 - 有一定概率秒杀对方。对BOSS怪无效。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                }
                bx = SkillFactory.getSkill(林之灵.拉伊之爪_强化); //112100014 - 拉伊之爪 强化 - 雪豹状态下使用的特定主动技能的攻击伤害和攻击次数增加。\r\n#c相关技能 : 雪豹重斩，雪豹强袭, 雪豹咆哮, 男子汉之舞, 男子汉步伐, 迅雷冲刺#
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_ignore_mob_def_rate += eff.getIgnoreMob();
                    addDamageIncrease(林之灵.雪豹重斩, eff.getDAMRate());
                    addDamageIncrease(林之灵.雪豹强袭, eff.getDAMRate());
                    addDamageIncrease(林之灵.雪豹咆哮, eff.getDAMRate());
                    addDamageIncrease(林之灵.雪豹_未知, eff.getDAMRate());
                    addDamageIncrease(林之灵.男子汉之舞, eff.getDAMRate());
                    addDamageIncrease(林之灵.男子汉步伐, eff.getDAMRate());
                    addDamageIncrease(林之灵.迅雷冲刺, eff.getDAMRate());
                    addDamageIncrease(林之灵.进阶迅雷冲刺, eff.getDAMRate());
                    addTargetPlus(林之灵.雪豹重斩, eff.getTargetPlus());
                    addTargetPlus(林之灵.雪豹强袭, eff.getTargetPlus());
                    addTargetPlus(林之灵.雪豹咆哮, eff.getTargetPlus());
                    addTargetPlus(林之灵.雪豹_未知, eff.getTargetPlus());
                    addTargetPlus(林之灵.男子汉之舞, eff.getTargetPlus());
                    addTargetPlus(林之灵.男子汉步伐, eff.getTargetPlus());
                    addTargetPlus(林之灵.迅雷冲刺, eff.getTargetPlus());
                    addTargetPlus(林之灵.进阶迅雷冲刺, eff.getTargetPlus());
                }
                bx = SkillFactory.getSkill(林之灵.拉伊之心_强化); //112100015 - 拉伊之心 强化 - 转换成雪豹状态后，攻击速度及魔法攻击力提高。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
//                        accuracy += eff.getAccX();
                    percent_damage += eff.getMagicDamage();
                    percent_ignore_mob_def_rate += eff.getIgnoreMob();
                }
            } else if (buffSourceId == 林之灵.猛鹰模式) { //猛鹰模式

            } else if (buffSourceId == 林之灵.猫咪模式) { //猫咪模式
                bx = SkillFactory.getSkill(林之灵.阿尔之萌); //112120015 - 阿尔之萌 - 猫咪状态下,智力永久增加。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    localint += bx.getEffect(bof).getIntX();
                }
            }
        }
    }

    /*
         * ---------------------------------
         * 神之子技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shenzhizi(MapleCharacter chra) {
        
        if (MapleJob.is神之子(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
//            if (chra.isZeroSecondLook()) {
            bx = SkillFactory.getSkill(神之子.精准大剑); //101000103 - 精准大剑 - 提高大剑系列武器的熟练度、攻击力和攻击速度。此外，施展大剑系列技能时，受到攻击的敌人数量越少，伤害越强。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                percent_damage_rate += eff.getMobCountDamage();
                percent_boss_damage_rate += eff.getBossDamage();
            }
            bx = SkillFactory.getSkill(神之子.固态身体); //101100102 - 固态身体 - 强化贝塔的身体，可以增加物理防御力、魔法防御力、增加状态异常抗性、增加所有属性抗性，同时会提高稳如泰山的几率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                wdef += eff.getWdefX();
                mdef += eff.getMdefX();
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
//                } else {
            bx = SkillFactory.getSkill(神之子.精准太刀); //101000203 - 精准太刀 - 提高太刀系列武器的熟练度和攻击力。使用太刀时增加伤害、攻击速度、移动速度和跳跃力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            bx = SkillFactory.getSkill(神之子.强化之躯); //101100203 - 强化之躯 - 强化阿尔法的身体，增加最大HP、最大时间之力和暴击率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                passive_sharpeye_rate += eff.getCritical(); //爆击概率
            }
            bx = SkillFactory.getSkill(神之子.圣光照耀); //101120207 - 圣光照耀 - 增加阿尔法的最大和最小暴击伤害，同时有一定的几率给对象造成出血状态，并恢复自己的HP。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                critical_damage_max += eff.getCriticalMax();
            }
//                }
            bx = SkillFactory.getSkill(神之子.决意时刻); //100000279 - 决意时刻 - 继承并获得伦娜女神的强大力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getDAMRate();
                localstr += bx.getEffect(bof).getStrX();
                percent_hp += eff.getPercentHP();
                speedMax += eff.getSpeedMax();
            }
        }
    }

    /*
         * ---------------------------------
         * 爆莉萌天使技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_tianshi(MapleCharacter chra) {
        
        if (MapleJob.is狂龙战士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(爆莉萌天使.亲和Ⅰ); //65000003 - 亲和Ⅰ - 提升与爱丝卡达的亲和力，身体感到轻盈。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(爆莉萌天使.精准灵魂手铳); //65100003 - 精准灵魂手铳 - 提升灵魂手铳的熟练度和命中值、物理攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(爆莉萌天使.内心之火); //65100004 - 内心之火 - 永久提升敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localdex += bx.getEffect(bof).getDexX();
            }
            bx = SkillFactory.getSkill(爆莉萌天使.亲和Ⅱ); //65100005 - 亲和Ⅱ - 提升与爱丝卡达的亲和力，接受到战斗的经验，抵抗力提升。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(爆莉萌天使.亲和Ⅲ); //65110006 - 亲和Ⅲ - 提升与爱丝卡达的亲和力，提升敏捷，熟读秘传。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localdex += eff.getDexX();
                percent_damage += eff.getDAMRate();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(爆莉萌天使.灵魂吸取_强化); //65120043 - 灵魂吸取-强化 - 提升灵魂吸取的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(爆莉萌天使.灵魂吸取Ⅱ, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(爆莉萌天使.灵魂吸取Ⅰ, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(爆莉萌天使.大地冲击波_强化); //65120046 - 大地冲击波-强化 - 提升大地冲击波的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(爆莉萌天使.大地冲击波, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(爆莉萌天使.大地冲击波_缩短冷却时间); //65120048 - 大地冲击波-缩短冷却时间 - 缩短大地冲击波的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(爆莉萌天使.大地冲击波, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(爆莉萌天使.三位一体_强化); //65120049 - 灵魂共鸣-强化 - 提升灵魂共鸣的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(爆莉萌天使.灵魂共鸣, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(爆莉萌天使.三位一体_重生); //65120050 - 灵魂共鸣-缩短冷却时间 - 缩短灵魂共鸣的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(爆莉萌天使.灵魂共鸣, bx.getEffect(bof).getCooltimeReduceR());
            }
        }
    }

    /*
         * ---------------------------------
         * 狂龙战士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_kuanglong(MapleCharacter chra) {
        
        if (MapleJob.is狂龙战士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(狂龙战士.钢铁之墙); //60000222 - 钢铁之墙 - 具备钢铁意志的狂龙战士获得额外体力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(狂龙战士.皮肤保护); //61000003 - 皮肤保护 - 强化皮肤，永久提升防御力，有一定概率进入不被击退的状态。和变身的稳如泰山效果重叠。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWdefX();
                percent_mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(狂龙战士.双重跳跃); //61001002 - 双重跳跃 - 跳跃中再次跳跃一次后，移动至远处。额外的永久增加移动速度和最大移动速度。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                speed += bx.getEffect(bof).getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(狂龙战士.飞龙斩1次强化); //61100009 - 飞龙斩1次强化 - 强化飞龙斩的攻击力。\n前置技能：#c飞龙斩20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(狂龙战士.飞龙斩Ⅰ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅲ, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.内心火焰); //61100007 - 内心火焰 - 永久提升力量和HP。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                percent_hp += eff.getPercentHP();
            }
            bx = SkillFactory.getSkill(狂龙战士.飞龙斩_2次强化); //61110015 - 飞龙斩 2次强化 - 强化飞龙斩的攻击力。\n前置技能：#c飞龙斩1次强化 2级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(狂龙战士.飞龙斩Ⅰ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅲ, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.进阶内心火焰); //61110007 - 进阶内心火焰 - 永久提升力量和HP。\n前置技能：#c内心火焰10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                percent_hp += eff.getPercentHP();
            }
            bx = SkillFactory.getSkill(狂龙战士.进阶剑刃之壁); //61120007 - 进阶剑刃之壁 - 强化剑刃之壁技能。召唤3把使用中的剑。召唤出的剑画出一道轨迹，寻找并攻击怪物。额外的永久提升攻击力。\n前置技能：#c剑刃之壁20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(狂龙战士.飞龙斩_3次强化); //61120020 - 飞龙斩 3次强化 - 强化飞龙斩的攻击力。\n前置技能：#c飞龙斩2次强化2级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(狂龙战士.飞龙斩Ⅰ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(狂龙战士.飞龙斩Ⅲ, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.无敌之勇); //61120011 - 无敌之勇 - 无视怪物的防御。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(狂龙战士.怒雷屠龙斩_强化); //61120043 - 怒雷屠龙斩-强化 - 提升怒雷屠龙斩的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(狂龙战士.怒雷屠龙斩, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(狂龙战士.怒雷屠龙斩_变身, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.怒雷屠龙斩_坚持); //61120044 - 怒雷屠龙斩-坚持 - 提升怒雷屠龙斩的减速持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(狂龙战士.怒雷屠龙斩, bx.getEffect(bof).getDuration());
                addBuffDuration(狂龙战士.怒雷屠龙斩_变身, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(狂龙战士.怒雷屠龙斩_额外攻击); //61120045 - 怒雷屠龙斩-额外攻击 - 提升怒雷屠龙斩的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(狂龙战士.怒雷屠龙斩, bx.getEffect(bof).getAttackCount());
                addAttackCount(狂龙战士.怒雷屠龙斩_变身, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(狂龙战士.恶魔之息_强化); //61120046 - 恶魔之息-强化 - 提升恶魔之息的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(狂龙战士.恶魔之息, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.恶魔之息_暴怒坚持); //61120047 - 恶魔之息-暴怒坚持 - 提升恶魔之息产生的火焰持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(狂龙战士.恶魔之息_暴怒, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(狂龙战士.恶魔之息_暴怒强化); //61120048 - 恶魔之息-暴怒强化 - 提升恶魔之息产生的火焰的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(狂龙战士.恶魔之息_暴怒, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.扇击_强化); //61120049 - 扇击-强化 - 提升扇击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(狂龙战士.扇击, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(狂龙战士.扇击_1, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(狂龙战士.扇击_变身, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(狂龙战士.扇击_坚持); //61120050 - 扇击-坚持 - 提升扇击的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(狂龙战士.扇击, bx.getEffect(bof).getDuration());
                addBuffDuration(狂龙战士.扇击_1, bx.getEffect(bof).getDuration());
                addBuffDuration(狂龙战士.扇击_变身, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(狂龙战士.扇击_额外攻击); //61120051 - 扇击-额外攻击 - 提升扇击的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(狂龙战士.扇击, bx.getEffect(bof).getAttackCount());
                addAttackCount(狂龙战士.扇击_1, bx.getEffect(bof).getAttackCount());
                addAttackCount(狂龙战士.扇击_变身, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 米哈尔技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_mihaer(MapleCharacter chra) {
        
        if (MapleJob.is米哈尔(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(米哈尔.灵魂盾); //51000001 - 灵魂盾 - 提升灵魂盾的力量，提高防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWdefX();
                percent_mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(米哈尔.灵魂敏捷); //51000002 - 灵魂敏捷 - 永久提升命中值和移动速度、跳跃力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
//                    accuracy += eff.getAccX();
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(米哈尔.增加HP); //51000000 - 增加HP - 永久增加最大HP。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(米哈尔.物理训练); //51100000 - 物理训练 - 通过锻炼身体，永久提升力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(米哈尔.专注); //51110001 - 专注 - 集中精神，永久增加力量，攻击力提升1阶段。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
            }
            bx = SkillFactory.getSkill(米哈尔.战斗精通); //51120000 - 战斗精通 - 攻击时无视一定程度的怪物防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(米哈尔.进阶终极攻击); //51120002 - 进阶终结攻击 - 永久增加攻击力和命中率，终结攻击的发动概率和伤害值大幅提升。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                addDamageIncrease(米哈尔.终极攻击, eff.getDamage());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(米哈尔.闪耀爆炸_强化); //51120046 - 闪耀爆炸-强化 - 增加闪耀爆炸的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(米哈尔.闪耀爆炸, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(米哈尔.闪耀爆炸_额外目标); //51120047 - 闪耀爆炸-额外目标 - 增加闪耀爆炸攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(米哈尔.闪耀爆炸, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(米哈尔.闪耀爆炸_额外攻击); //51120048 - 闪耀爆炸-额外攻击 - 增加闪耀爆炸的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(米哈尔.闪耀爆炸, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(米哈尔.灵魂抨击_强化); //51120049 - 灵魂抨击-强化 - 增加灵魂抨击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(米哈尔.灵魂抨击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(米哈尔.灵魂抨击_额外攻击); //51120051 - 灵魂抨击-额外攻击 - 增加灵魂抨击的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(米哈尔.灵魂抨击, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 爆破手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_baopo(MapleCharacter chra) {
        
        if (MapleJob.is爆破手(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
//            bx = SkillFactory.getSkill(37001001);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    chra.getSpecialStat().setBullet(3);
//                }
//                bx = SkillFactory.getSkill(37100007);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    chra.getSpecialStat().setBullet(4);
//                }
//                bx = SkillFactory.getSkill(37110007);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    chra.getSpecialStat().setBullet(5);
//                }
//                bx = SkillFactory.getSkill(37120008);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    chra.getSpecialStat().setBullet(6);
//                }
            bx = SkillFactory.getSkill(37100004);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getAttackX();
            }
//                bx = SkillFactory.getSkill(37100005);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    this.mh += bx.getEffect(bof).getWdef2Dam();
//                    this.mi += bx.getEffect(bof).getAcc2Dam();
//                }
            bx = SkillFactory.getSkill(37000006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
//                bx = SkillFactory.getSkill(37110008);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    this.nB += bx.getEffect(bof).getASRRate();
//                    this.nC += bx.getEffect(bof).getTERRate();
//                }
//                bx = SkillFactory.getSkill(37110009);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    this.of += bx.getEffect(bof).getPadR();
//                }
//                bx = SkillFactory.getSkill(37120009);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    this.nB += bx.getEffect(bof).getASRRate();
//                    this.nC += bx.getEffect(bof).getTERRate();
//                }
//                bx = SkillFactory.getSkill(37120012);
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0) {
//                    this.of += bx.getEffect(bof).getPadR();
//                }
            bx = SkillFactory.getSkill(37100006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                percent_damage += (double) bx.getEffect(bof).getDAMRate();
            }
            bx = SkillFactory.getSkill(37120011);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                percent_ignore_mob_def_rate += (1.0 - percent_ignore_mob_def_rate) * (double) bx.getEffect(bof).getIgnoreMob() / 100.0;
//                    this.ol += bx.getEffect(bof).getDamAbsorbShieldR();
            }
        }
    }

    /*
         * ---------------------------------
         * 尖兵技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_jianbin(MapleCharacter chra) {
        
        if (MapleJob.is尖兵(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(尖兵.混合逻辑); //30020233 - 混合逻辑 - 采用混合逻辑设计，所有能力值永久提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += (eff.getStrRate() / 100.0) * str;
                localdex += (eff.getDexRate() / 100.0) * dex;
                localluk += (eff.getLukRate() / 100.0) * luk;
                localint += (eff.getIntRate() / 100.0) * int_;
            }
            bx = SkillFactory.getSkill(尖兵.高效输能); //HP,MP加1000
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addmaxhp += 1000;
                addmaxmp += 1000;
            }
            bx = SkillFactory.getSkill(尖兵.神经系统改造); //36000003 - 神经系统改造 - 强化脊柱的神经系统，提高和运动有关的所有数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
//                    accuracy += eff.getPercentAcc();
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(尖兵.超能力量); //36001002 - 超能力量 - 提高能量，增加伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(尖兵.精英支援); //36100005 - 精英支援 - 永久增加所有能力值
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(尖兵.尖兵精通); //36100006 - 尖兵精通 - 熟练度和物理攻击力提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(尖兵.精准火箭1次强化); //36100010 - 精准火箭1次强化 - 强化精准火箭的伤害。\n[需要技能]：#c精准火箭4级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(尖兵.精准火箭, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(尖兵.直线透视); //36101002 - 直线透视 - 在一定时间内激活视觉，看穿敌人的弱点，提高爆击率。消耗备用能量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(尖兵.精准火箭2次强化); //36110012 - 精准火箭2次强化 - 增强精准火箭的伤害。\n[需要技能]：#c精准火箭1次强化4级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                if (damageIncrease.containsKey(尖兵.精准火箭)) {
                    addDamageIncrease(尖兵.精准火箭, damageIncrease.get(尖兵.精准火箭) + eff.getDAMRate());
                }
            }
            bx = SkillFactory.getSkill(尖兵.双重防御); //36111003 - 双重防御 - 额外回避率100%，之后每次回避/受到攻击时，回避率和受到的伤害减少。此外，防御力永久增加。\n[需要技能]：#c直线透视5级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
                wdef += eff.getWdefX();
                mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(尖兵.精准火箭最终强化); //36120015 - 精准火箭最终强化 - 使精准火箭的伤害最大化。\n[需要技能]：#c精准火箭2次强化4级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                if (damageIncrease.containsKey(尖兵.精准火箭)) {
                    addDamageIncrease(尖兵.精准火箭, damageIncrease.get(尖兵.精准火箭) + eff.getDAMRate());
                }
            }
            bx = SkillFactory.getSkill(尖兵.神秘代码); //36121003 - 神秘代码 - 在一定时间内发挥出当前世界上不可能出现的强大力量，增加总伤害和攻击BOSS时的伤害，所有能力值永久增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(尖兵.刀锋之舞_强化); //36120044 - 刀锋之舞 - 强化 - 增加刀锋之舞的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(尖兵.刀锋之舞, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(尖兵.刀锋之舞_额外目标); //36120045 - 刀锋之舞 - 额外目标 - 增加刀锋之舞的目标对象数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(尖兵.刀锋之舞, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(尖兵.聚能脉冲炮_强化); //36120046 - 聚能脉冲炮 - 强化 - 增加聚能脉冲炮的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(尖兵.聚能脉冲炮_狙击, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(尖兵.聚能脉冲炮_炮击, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(尖兵.聚能脉冲炮_爆击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(尖兵.聚能脉冲炮_无视防御); //36120047 - 聚能脉冲炮 - 无视防御 - 增加聚能脉冲炮的无视防御比例
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(尖兵.聚能脉冲炮_狙击, bx.getEffect(bof).getIgnoreMob());
                addIgnoreMobpdpRate(尖兵.聚能脉冲炮_炮击, bx.getEffect(bof).getIgnoreMob());
                addIgnoreMobpdpRate(尖兵.聚能脉冲炮_爆击, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(尖兵.聚能脉冲炮_额外目标); //36120048 - 聚能脉冲炮 - 额外目标 - 增加聚能脉冲炮的目标对象数量。但狙击单个敌人时不增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(尖兵.聚能脉冲炮_炮击, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(尖兵.聚能脉冲炮_爆击, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 机械师技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_jixieshi(MapleCharacter chra) {
        
        if (MapleJob.is机械师(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(机械师.物理训练); //物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(机械师.机械精通); //机械精通
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(机械师.机械防御系统); //机械精通
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
                percent_mp += bx.getEffect(bof).getPercentMP();
            }
//                bx = SkillFactory.getSkill(机械师.终极机甲); //终极机甲
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    trueMastery += bx.getEffect(bof).getMastery();
//                    percent_hp += bx.getEffect(bof).getPercentHP();
//                }
            bx = SkillFactory.getSkill(机械师.机器人精通); //机器人精通 - 提高所有召唤机器人的攻击力、自爆伤害和持续时间
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(机械师.机器人发射器_RM7, eff.getX());
                addDamageIncrease(机械师.机器人工厂_RM1_1, eff.getX());
                BuffUP_Summon += eff.getY();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(机械师.磁场_强化); //35120043 - 磁场-强化 - 增加磁场的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(机械师.磁场, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(机械师.磁场_坚持); //35120044 - 磁场-坚持 - 增加磁场的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(机械师.磁场, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(机械师.磁场_缩短冷却时间); //35120045 - 磁场-缩短冷却时间 - 减少磁场的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(机械师.磁场, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(机械师.支援波动器_HEX_强化); //35120046 - 支援波动器：H-EX-强化 - 增加支援波动器：H-EX的爆炸伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(机械师.支援波动器_HEX, bx.getEffect(bof).getDAMRate());
            }
//                bx = SkillFactory.getSkill(机械师.支援波动器_H_EX_组队强化); //35120047 - 支援波动器：H-EX-组队强化 - 增加支援波动器：H-EX强化的队员伤害。
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    addBuffDuration(机械师.磁场, bx.getEffect(bof).getDuration());
//                }
            bx = SkillFactory.getSkill(机械师.支援波动器_HEX_坚持); //35120048 - 支援波动器：H-EX-坚持 - 增加支援波动器：H-EX的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(机械师.支援波动器_HEX, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(机械师.集中射击_强化); //35120049 - 集中射击-强化 - 增加集中射击<SPLASH-F>和<IRON-B>的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(机械师.集中射击_SPLASH_F, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(机械师.集中射击_IRON_B, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(机械师.集中射击_SPLASH_F_额外目标); //35120050 - 集中射击：SPLASH-F-额外目标 - 增加集中射击：SPLASH-F攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(机械师.集中射击_SPLASH_F, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(机械师.集中射击_IRON_B_额外攻击); //35120051 - 集中射击：IRON-B-额外攻击 - 增加集中射击：IRON-B的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(机械师.集中射击_IRON_B, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 豹弩游侠技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_baonuyouxia(MapleCharacter chra) {
        
        if (MapleJob.is豹弩游侠(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(豹弩游侠.召唤美洲豹_灰);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_atk += Math.min(180, chra.getLevel());
                addDamageIncrease(豹弩游侠.召唤美洲豹_灰, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_黄, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_红, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_紫, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_蓝, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_剑, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_雪, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_玛瑙, Math.min(180, chra.getLevel()));
                addDamageIncrease(豹弩游侠.召唤美洲豹_铠甲, Math.min(180, chra.getLevel()));
            }
            eff = SkillFactory.getSkill(豹弩游侠.利爪狂风_EX).getEffect(1);
            if (eff != null) {
                addDamageIncrease(豹弩游侠.利爪狂风, eff.getDamage() + Math.max(180, chra.getLevel()));
            }
            eff = SkillFactory.getSkill(豹弩游侠.十字攻击_EX).getEffect(1);
            if (eff != null) {
                addDamageIncrease(豹弩游侠.十字攻击, eff.getDamage() + Math.max(180, chra.getLevel()) * 2);
            }
            
            bx = SkillFactory.getSkill(豹弩游侠.自动射击装置); //33000005 - 自动射击装置 - 将反抗者的技术和弩弓结合，可以更快、更简单地发射箭矢。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX(); //提升攻击力
//                    accuracy += eff.getAccX(); //提升命中
//                    percent_acc += eff.getPercentAcc(); //提升x%的命中
                defRange += eff.getRange(); //提升攻击距离
            }
            bx = SkillFactory.getSkill(豹弩游侠.物理训练); //33100010 - 物理训练 - 通过锻炼身体，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(豹弩游侠.美洲豹精通); //33120000 - 神弩手 - 弩系列武器的熟练度和物理攻击力、爆击最小伤害。\n前置技能：#c精准弩10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(豹弩游侠.美洲豹传动);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getWatk() * eff.getX();
                passive_sharpeye_rate += eff.getY() * eff.getX();
                critical_damage_min += 2 * eff.getX();
            }
            bx = SkillFactory.getSkill(豹弩游侠.暴走形态); //33120000 - 神弩手 - 弩系列武器的熟练度和物理攻击力、爆击最小伤害。\n前置技能：#c精准弩10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(豹弩游侠.神弩手); //33120000 - 神弩手 - 弩系列武器的熟练度和物理攻击力、爆击最小伤害。\n前置技能：#c精准弩10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                critical_damage_min += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(豹弩游侠.野性本能); //33120010 - 野性本能 - 攻击时无视怪物的部分物理防御力，有一定概率回避敌人的攻击。永久增加所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(豹弩游侠.进阶终极攻击); //33120011 - 进阶终极攻击 - 永久性地增加攻击力和命中率，终极弓弩技能的发动概率和伤害大幅上升。\n需要技能：#c终极弓弩20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                addDamageIncrease(豹弩游侠.终极弓弩, eff.getDamage());
            }
            /*
                 * 超级技能
             */
//                bx = SkillFactory.getSkill(豹弩游侠.音速震波_强化); //33120046 - 音速震波-强化 - 增加音速震波的伤害。
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    addDamageIncrease(豹弩游侠.音速震波, bx.getEffect(bof).getDAMRate());
//                }
//                bx = SkillFactory.getSkill(豹弩游侠.音速震波_额外目标); //33120047 - 音速震波-额外目标 - 增加音速震波攻击的怪物数。
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    addTargetPlus(豹弩游侠.音速震波, bx.getEffect(bof).getTargetPlus());
//                }
//                bx = SkillFactory.getSkill(豹弩游侠.音速震波_额外攻击); //33120048 - 音速震波-额外攻击 - 增加音速震波的攻击次数。
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    addAttackCount(豹弩游侠.音速震波, bx.getEffect(bof).getAttackCount());
//                }
            bx = SkillFactory.getSkill(豹弩游侠.奥义箭乱舞_强化); //33120049 - 奥义箭乱舞-强化 - 增加奥义箭乱舞的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(豹弩游侠.奥义箭乱舞, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(豹弩游侠.奥义箭乱舞_无视防御); //33120051 - 奥义箭乱舞-无视防御 - 增加奥义箭乱舞的无视防御力效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(豹弩游侠.奥义箭乱舞, bx.getEffect(bof).getIgnoreMob());
            }
        }
    }

    /*
         * ---------------------------------
         * 唤灵斗师技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_huanglingdoushi(MapleCharacter chra) {
        
        if (MapleJob.is唤灵斗师(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(唤灵斗师.长杖艺术); //32000015 - 长杖艺术 - 锻炼使用长杖的方法，把它变成一种技能。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(唤灵斗师.智慧激发); //32100007 - 智慧激发 - 通过精神修养，永久性地提高智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(唤灵斗师.普通转化); //32100008 - 普通转化 - 大幅增加自己的最大HP。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(唤灵斗师.战斗精通); //32110001 - 战斗精通 - 学习更高深的战斗技术，永久性增加伤害和爆击最小伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getDAMRate();
                critical_damage_min += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(唤灵斗师.斗战突击); //32111015 - 斗战突击 - 向前突进，击退多个敌人。施展后，可与#c致命冲击进行衔接使用#。此外，永久增加超级黑暗锁链的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(唤灵斗师.黑暗锁链, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(唤灵斗师.黑暗创世); //32121004 - 黑暗创世 - 用黑暗之光攻击最多15个敌人，使其在一定时间内昏迷。如学习了黑暗闪电技能，可以永久性增加黑暗闪电的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(唤灵斗师.黑暗闪电, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(唤灵斗师.暴怒对战); //32121010 - 暴怒对战 一定时间内进入可以集中精神攻击一个敌人的状态，大幅提高伤害值。同时永久激活身体，永久增加自己的最大HP和MP、防御力，可以无视怪物的部分防御力。即使受到敌人的攻击也不会解除增益。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP(); //最大Hp增加
                percent_mp += eff.getPercentMP(); //最大Mp增加
                percent_wdef += eff.getWDEFRate(); //增加物防
                percent_mdef += eff.getMDEFRate(); //增加魔防
                percent_ignore_mob_def_rate += eff.getIgnoreMob(); //无视怪物防御
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(唤灵斗师.黑暗创世_缩短冷却时间); //32120057 - 黑暗创世-缩短冷却时间 - 减少黑暗创世的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(唤灵斗师.黑暗创世, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(唤灵斗师.黑暗创世_强化); //32120058 - 黑暗创世-强化 - 增加黑暗创世的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(唤灵斗师.黑暗创世, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(唤灵斗师.避难所_缩短冷却时间); //32120063 - 避难所-缩短冷却时间 - 减少避难所的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(唤灵斗师.避难所, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(唤灵斗师.避难所_坚持); //32120064 - 避难所-坚持 - 提升避难所的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(唤灵斗师.避难所, bx.getEffect(bof).getDuration());
            }
        }
    }

    /*
         * ---------------------------------
         * 恶魔猎手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_emofuchou(MapleCharacter chra) {
        
        if (MapleJob.is恶魔复仇者(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(恶魔猎手.恶魔之血); //恶魔之血 - 魔族天生就拥有先天的强大意志和压倒性的领导力
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                chra.getTrait(MapleTraitType.will).setLevel(20, chra);
                chra.getTrait(MapleTraitType.charisma).setLevel(20, chra);
            }
            bx = SkillFactory.getSkill(恶魔复仇者.野性狂怒); //30010241 - 野性狂怒 - 由于愤怒，伤害增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.恶魔之力); //31010003 - 恶魔之力 - 永久增加移动速度和最高移动速度、跳跃力、爆击率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.铜墙铁壁); //31200004 - 铜墙铁壁 - 凭借坚强的意志，使物理防御力和魔法防御力大幅提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate(); //增加物防
                percent_mdef += eff.getMDEFRate(); //增加魔防
            }
            bx = SkillFactory.getSkill(恶魔复仇者.亡命剑精通); //31200005 - 亡命剑精通 - 永久增加亡命剑的熟练度和命中值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
//                eff = bx.getEffect(bof);
//                accuracy += eff.getAccX();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.心灵之力); //31200006 - 心灵之力 - 永久增加力量和防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                wdef += eff.getWdefX();
                mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.冒险岛勇士); //31200006 - 心灵之力 - 永久增加力量和防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.负荷缓解); //31210005 - 负荷缓解 - 永久缓解由于超越负荷而减少的生命吸收的HP吸收量。额外强化超越技能的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(恶魔复仇者.超越_十字斩, eff.getDAMRate()); //超越十字斩
                addDamageIncrease(恶魔复仇者.超越_十字斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_十字斩Ⅲ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_十字斩Ⅳ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_十字斩Ⅴ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_恶魔突袭, eff.getDAMRate()); //超越恶魔突袭
                addDamageIncrease(恶魔复仇者.超越_恶魔突袭Ⅱ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_恶魔突袭Ⅲ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_恶魔突袭Ⅳ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_恶魔突袭Ⅴ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_月光斩, eff.getDAMRate()); //超越月光斩
                addDamageIncrease(恶魔复仇者.超越_月光斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_月光斩Ⅲ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_月光斩Ⅳ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_月光斩Ⅴ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_处决, eff.getDAMRate()); //超越处决
                addDamageIncrease(恶魔复仇者.超越_处决Ⅱ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_处决Ⅲ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_处决Ⅳ, eff.getDAMRate());
                addDamageIncrease(恶魔复仇者.超越_处决Ⅴ, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(恶魔复仇者.进阶生命吸收); //31210006 - 进阶生命吸收 - 永久增加通过生命吸收吸收的HP数值。\r\n需要技能：#c生命吸收10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                hpRecover_Percent += eff.getX();
            }
            bx = SkillFactory.getSkill(恶魔复仇者.防御专精); //31220005 - 防御专精 - 永久提高使用盾牌的能力，有一定概率无视敌人的防御力，提高盾牌技能#c持盾突击#和#c追击盾#的攻击力。盾牌技能的防御力无视比例增加2倍。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
                addDamageIncrease(恶魔复仇者.持盾突击, eff.getX()); //持盾突击
                addDamageIncrease(恶魔复仇者.持盾突击_1, eff.getX());
                addDamageIncrease(恶魔复仇者.追踪之盾, eff.getX()); //追击盾
                addDamageIncrease(恶魔复仇者.追击盾_攻击, eff.getX());
            }
            /*
                     * 超级技能
             */
            bx = SkillFactory.getSkill(恶魔复仇者.追踪之盾_额外目标); //31220050 - 追击盾-额外目标 - 永久增加追击盾攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(恶魔复仇者.追踪之盾, bx.getEffect(bof).getZ());
                addAttackCount(恶魔复仇者.追击盾_攻击, bx.getEffect(bof).getZ());
            }
        }
    }

    /*
         * ---------------------------------
         * 恶魔猎手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_emo(MapleCharacter chra) {
        
        if (MapleJob.is恶魔猎手(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(恶魔猎手.恶魔之血); //恶魔之血 - 魔族天生就拥有先天的强大意志和压倒性的领导力
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
//                eff = bx.getEffect(bof);
                chra.getTrait(MapleTraitType.will).setLevel(20, chra);
                chra.getTrait(MapleTraitType.charisma).setLevel(20, chra);
            }
            bx = SkillFactory.getSkill(恶魔复仇者.野性狂怒); //30010241 - 野性狂怒 - 由于愤怒，伤害增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(恶魔猎手.HP增加); //HP增加 - 最大HP永久增加
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔血月斩1次强化); //恶魔血月斩1次强化 - 永久性地增加恶魔血月斩的伤害
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(恶魔猎手.恶魔血月斩, eff.getDAMRate()); //恶魔血月斩
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅱ, eff.getDAMRate());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅲ, eff.getDAMRate());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅳ, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(恶魔猎手.物理训练); //物理训练 - 永久性地增加力量和敏捷
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔血月斩2次强化); //恶魔血月斩2次强化 - 提高恶魔血月斩的伤害。\n需要技能：#c恶魔血月斩1次强化1级#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(恶魔猎手.恶魔血月斩, eff.getX()); //恶魔血月斩
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅱ, eff.getX());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅲ, eff.getX());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅳ, eff.getX());
            }
            bx = SkillFactory.getSkill(恶魔猎手.邪恶拷问); //邪恶拷问 - 攻击状态异常的敌人时，伤害和爆击概率增加
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getX();
                percent_boss_damage_rate += eff.getX();
                passive_sharpeye_rate += eff.getY();
            }
            bx = SkillFactory.getSkill(恶魔猎手.黑暗束缚); //黑暗束缚 - 有一定概率使周围的多个敌人陷入无法行动的状态，造成持续伤害。拥有一定概率无视怪物防御力的被动效果，黑暗束缚的效果对BOSS同样有效
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(恶魔猎手.精神集中); //31110007 - 精神集中 - 通过集中精神，永久性地增加伤害，使攻击速度提高1个阶段
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_damage += bx.getEffect(bof).getDAMRate();
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔血月斩最终强化); //恶魔血月斩最终强化 - 最终对恶魔血月斩的伤害进行强化。\n需要技能：#c恶魔血月斩2次强化1级#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(恶魔猎手.恶魔血月斩, eff.getX());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅱ, eff.getX());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅲ, eff.getX());
                addDamageIncrease(恶魔猎手.恶魔血月斩Ⅳ, eff.getX());
            }
            bx = SkillFactory.getSkill(恶魔猎手.进阶精准武器); //进阶精准武器 - 将单手钝器、单手斧系列武器的熟练度提高到极限，增加爆击最小伤害和物理攻击力。\n需要技能：#c精准武器20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                trueMastery += eff.getMastery();
                critical_damage_min += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(恶魔猎手.皮肤硬化); //未知 难道是皮肤硬化 - 永久性地强化身体，减少敌人造成的伤害
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_wdef += bx.getEffect(bof).getT();
            }
            bx = SkillFactory.getSkill(恶魔猎手.死亡诅咒); //死亡诅咒 - 攻击时有一定概率使敌人一击必杀，一击必杀效果发动时，恢复自己的HP。攻击对象死亡时，有一定概率吸收一定量的精气。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                hpRecover_Percent += eff.getX();
                hpRecoverProp += eff.getProp();
            }
            /*
                     * 超级技能
             */
            bx = SkillFactory.getSkill(恶魔猎手.恶魔呼吸_强化); //31120043 - 恶魔呼吸-强化 - 增加恶魔呼吸的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(恶魔猎手.恶魔呼吸, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔呼吸_额外攻击); //31120044 - 恶魔呼吸-额外攻击 - 增加恶魔呼吸的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(恶魔猎手.恶魔呼吸, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(恶魔猎手.黑暗变形_强化); //31120047 - 黑暗变形-强化 - 增加黑暗变形的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(恶魔猎手.黑暗变形, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔冲击波_强化); //31120049 - 恶魔冲击波-强化 - 增加恶魔冲击波的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(恶魔猎手.恶魔冲击波, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(恶魔猎手.恶魔冲击波_额外攻击); //31120050 - 恶魔冲击波-额外攻击 - 增加恶魔冲击波的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(恶魔猎手.恶魔冲击波, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(恶魔猎手.蓝血); //31121054 - 蓝血 - 唤醒自己血液中的贵族血统，让力量觉醒。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                incMaxDamage += bx.getEffect(bof).getMaxDamageOver();
            }
        }
    }

    /*
         * ---------------------------------
         * 夜光技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yeguang(MapleCharacter chra) {
        
        if (MapleJob.is夜光(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(夜光.魔力延伸); //20040218 - 穿透 - 用穿透一切阻碍的光之力量，无视敌人的部分防御力
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_mp += bx.getEffect(bof).getPercentMP();
            }
            bx = SkillFactory.getSkill(夜光.穿透); //20040218 - 穿透 - 用穿透一切阻碍的光之力量，无视敌人的部分防御力
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(夜光.光之力量); //20040221 - 光之力量 - 与命运对抗的意志和魔法师特有的洞察力。智力值很高，受光之力量的保护而不受暗黑效果侵袭。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localint += eff.getIntX();
                chra.getTrait(MapleTraitType.will).setLevel(20, chra);
                chra.getTrait(MapleTraitType.insight).setLevel(20, chra);
            }
            bx = SkillFactory.getSkill(夜光.普通魔法防护); //27000003 - 普通魔法防护 - 受到的伤害由一定比例的MP代替。额外的永久增加物理、魔法防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                wdef += eff.getWdefX();
                mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(夜光.光束瞬移); //27001002 - 光束瞬移 - 变成光束移动。按住方向键施展技能就能瞬移到目标方向。瞬移后的短暂时间内会变成透明状态。作为被动效果，永久增加移动速度和跳跃力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(夜光.光明_黑暗魔法强化); //27000207 - 光明/黑暗魔法强化 - 永久提升光明/黑暗系列魔法的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(夜光.耀眼光球, eff.getMdRate());
                addDamageIncrease(夜光.仙女发射, eff.getMdRate());
                addDamageIncrease(夜光.闪爆光柱, eff.getMdRate());
                addDamageIncrease(夜光.超级光谱, eff.getMdRate());
                addDamageIncrease(夜光.闪耀救赎, eff.getMdRate());
                addDamageIncrease(夜光.死亡之刃, eff.getMdRate());
                addDamageIncrease(夜光.闪电反击, eff.getMdRate());
                addDamageIncrease(夜光.晨星坠落_爆炸, eff.getMdRate());
                addDamageIncrease(夜光.黑暗降临, eff.getMdRate());
                addDamageIncrease(夜光.虚空重压, eff.getMdRate());
                addDamageIncrease(夜光.暗锁冲击, eff.getMdRate());
                addDamageIncrease(夜光.死亡之刃, eff.getMdRate());
                addDamageIncrease(夜光.启示录, eff.getMdRate());
                addDamageIncrease(夜光.绝对死亡, eff.getMdRate());
            }
            bx = SkillFactory.getSkill(夜光.智慧激发); //27100006 - 智慧激发 - 永久提升智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(夜光.抵抗之魔法盾); //27111004 - 抵抗之魔法盾 - 使用可以无视状态异常的保护罩。使用无视状态异常的效果，且使用次数超过指定次数以上时，进入冷却时间。作为被动效果，增加所有属性抗性和状态异常抵抗力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                ASR += bx.getEffect(bof).getASRRate();
                TER += bx.getEffect(bof).getTERRate();
            }
            bx = SkillFactory.getSkill(夜光.暗光精通); //27120008 - 暗光精通 - 增加平衡增益的持续时间并加快使用各系列技能时光和黑暗增加的速度。额外增加黑色死亡的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(夜光.死亡之刃, eff.getDAMRate());
//                    gauge_x = eff.getX();
                addBuffDuration(夜光.平衡, eff.getDuration());
                addBuffDuration(夜光.平衡_hide, eff.getDuration());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(夜光.闪电反击_强化); //27120043 - 闪电反击-强化 - 提升闪电反击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(夜光.闪电反击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(夜光.启示录_强化); //27120046 - 启示录-强化 - 提升启示录的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(夜光.启示录, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(夜光.启示录_额外目标); //27120048 - 启示录-额外目标 - 提升启示录攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(夜光.启示录, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(夜光.绝对死亡_强化); //27120049 - 绝对死亡-强化 - 提升绝对死亡的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(夜光.绝对死亡, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(夜光.绝对死亡_额外目标); //27120050 - 绝对死亡-额外目标 - 提升绝对死亡攻击的怪物数
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(夜光.绝对死亡, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 隐月技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yingyue(MapleCharacter chra) {
        
        if (MapleJob.is隐月(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(隐月.乾坤一体); //25000105 - 乾坤一体 - 吸收天地之息，调节身体，增加物理防御力/魔法防御力和最大HP/MP。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP(); //最大Hp增加
                percent_mp += eff.getPercentMP(); //最大Mp增加
                wdef += eff.getWdefX(); //物理防御力
                mdef += eff.getWdefX(); //魔法防御力
            }
            bx = SkillFactory.getSkill(隐月.后方移动); //25101205 - 后方移动 - 向后方滑动，永久增加回避率。\n#c在使用技能过程中可以移动。#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getX();
            }
            bx = SkillFactory.getSkill(隐月.力量锻炼); //25100108 - 力量锻炼 - 通过锻炼身体, 永久性增加力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
            }
            bx = SkillFactory.getSkill(隐月.精灵凝聚第3招); //25110107 - 精灵凝聚第3招 - 强化与精灵的团结，永久增加攻击力和伤害。\n[需要技能]：#c精灵凝聚第2招10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(隐月.招魂式); //25110108 - 招魂式 - 将精灵的力量与身体融合，物理防御力、魔法防御力、增加状态异常抗性、所有属性抗性增加。[需要技能]：#c乾坤一体20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                wdef += eff.getWdefX(); //物理防御力
                mdef += eff.getWdefX(); //魔法防御力
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(隐月.精灵凝聚第4招); //25120112 - 精灵凝聚第4招 - 强化与精灵的团结，永久性地在攻击时无视敌人的部分防御力，BOSS攻击力增加。[需要技能]：#c精灵凝聚第3招20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob(); //无视防御
                percent_boss_damage_rate += eff.getBossDamage(); //BOSS伤害
            }
            /*
                 * 处理超级技能
             */
            bx = SkillFactory.getSkill(隐月.鬼斩_额外攻击); //25120148 - 鬼斩-额外攻击 - 增加鬼斩的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(隐月.鬼斩, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(隐月.爆流拳_额外目标); //25120150 - 爆流拳-额外目标 - 增加爆流拳攻击的敌人数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(隐月.爆流拳, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(隐月.爆流拳Ⅱ, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(隐月.爆流拳Ⅲ, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(隐月.爆流拳Ⅳ, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 幻影技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_huanying(MapleCharacter chra) {
        
        if (MapleJob.is幻影(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(幻影.迅捷幻影); //24001002 - 迅捷幻影 - 永久性提高移动速度、最大移动速度、跳跃力，在跳跃中使用时，可以向前方飞行很远距离。技能等级越高，移动距离越远。可以用作跳跃键。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
            }
            bx = SkillFactory.getSkill(幻影.快速逃避); //24000003 - 快速逃避 - 永久提高回避率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getX();
            }
            bx = SkillFactory.getSkill(幻影.超级幸运星); //24100006 - 超级幸运星 - 永久提高运气。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(幻影.神秘的运气); //24111002 - 神秘的运气 - 最幸运的幻影可以永久性地提高运气。使用技能时，进入可以避免一次死亡并恢复体力的幸运状态。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(幻影.幻影突击); //24111006 - 幻影突击 - 用卡片组成巨大的枪，和枪一起突击，发动连续半月斩。\n可以在使用和风卡浪后作为连续技使用，此时和风卡浪的延迟时间减少。永久增加和风卡浪的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(幻影.和风卡浪, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(幻影.黑色秘卡); //24120002 - 黑色秘卡 - 幻影的攻击造成爆击时，有一定概率从幻影身上飞出卡片，自动攻击周围的敌人。#c此时卡片值增加1。#\n此外回避概率永久性增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getX();
            }
            bx = SkillFactory.getSkill(幻影.暮光祝福); //24121003 - 暮光祝福 - 向后跳跃，用卡片向前方的敌人发动猛烈攻击。\n可以在使用幻影突击后作为连续技使用，此时幻影突击的延迟时间减少。此外永久提高幻影突击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(幻影.幻影突击, eff.getDAMRate());
                addDamageIncrease(幻影.幻影突击1, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(幻影.手杖专家); //24120006 - 手杖专家 - 增加手杖系列武器的熟练度、物理攻击力、爆击最小伤害。\n需要技能：#c精准手杖20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX(); //增加攻击
                trueMastery += eff.getMastery(); //熟练度
                critical_damage_min += eff.getCriticalMin(); //爆击最小伤害
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(幻影.卡片风暴_强化); //24120043 - 卡片风暴-强化 - 提升卡片风暴的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(幻影.卡片风暴, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(幻影.卡片风暴_缩短冷却时间); //24120044 - 卡片风暴-缩短冷却时间 - 卡片风暴的冷却时间缩短。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(幻影.卡片风暴, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(幻影.卡片风暴_额外目标); //24120045 - 卡片风暴-额外目标 - 提升卡片风暴攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(幻影.卡片风暴, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(幻影.蓝光连击_强化); //24120046 - 蓝光连击-强化 - 提升蓝光连击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(幻影.蓝光连击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(幻影.蓝光连击_额外目标); //24120047 - 蓝光连击-额外目标 - 提升蓝光连击攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(幻影.蓝光连击, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 双弩精灵技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shuangnu(MapleCharacter chra) {
        
        if (MapleJob.is双弩精灵(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(双弩.精灵的祝福); //20021110 - [种族特性技能]借助古代精灵的祝福，可以回到埃欧雷，永久性地提高经验值获得量
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) { //去掉这个
                //expBuff *= (bx.getEffect(bof).getEXPRate() + 100.0) / 100.0;
            }
            bx = SkillFactory.getSkill(双弩.王者资格); //20020112 - 王者资格 - 精灵的国王一出生就拥有快速的动作和移动速度，以及致命的魅力
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                chra.getTrait(MapleTraitType.charm).setLevel(20, chra);
            }
            bx = SkillFactory.getSkill(双弩.潜力激发); //23000001 - 潜力激发 - 永久性地激活体内潜藏的力量。移动速度和最大移动速度上限增加，有一定概率回避敌人的攻击
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(双弩.物理训练); //23100008 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(双弩.终结箭); //23100004 - 终结箭 - 用箭矢扫射被冲锋拳打到空中的敌人。只能在冲锋拳之后使用。\n需要技能：#c冲锋拳1级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getProp();
            }
            bx = SkillFactory.getSkill(双弩.爆裂飞腿); //23110006 - 爆裂飞腿- 用箭矢扫射被冲锋拳打到空中的敌人。只能在冲锋拳之后使用。需要技能：冲锋拳1级以上
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.冲锋拳, bx.getEffect(bof).getDAMRate()); //冲锋拳 - 快速击退前方的多个敌人，并将其打到空中。攻击打到空中的敌人，可以造成额外伤害。
            }
            bx = SkillFactory.getSkill(双弩.伊师塔之环); //23121000 - 伊师塔之环 - 借助传说中的武器伊师塔的力量，快速向前方的敌人发射箭矢。按住技能键时，可以持续发射箭矢。此外还拥有永久性增加急袭双杀伤害的被动效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.急袭双杀, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.传说之矛); //23121002 - 传说之矛 - 在跳跃的同时投下传说之枪，攻击前方的多个敌人。可以使对象的防御力一定程度减少，100%判定为爆击。此外还拥有永久性地增加飞叶龙卷风伤害的被动效果。\n需要技能：#c飞叶龙卷风10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.飞叶龙卷风, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.古老意志); //23121004 - 古老意志 - 在一定时间内获得古代精灵的祝福，伤害和HP增加，永久性地提高火焰咆哮的回避率。\n需要技能：#c火焰咆哮5级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getProp();
            }
            bx = SkillFactory.getSkill(双弩.双弩枪专家); //23120009 - 双弩枪专家 - 增加双弩枪系列武器的熟练度、物理攻击力和爆击最小伤害。\n需要技能：#c精准双弩枪20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                critical_damage_min += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(双弩.防御突破); //23120010 - 防御突破 - 攻击时有一定概率100%无视敌人的防御力。对BOSS怪同样有效。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(双弩.旋转月瀑坠击); //23120011 - 旋转月瀑坠击 - 用冲锋拳将敌人打到空中后使用的连续技能。快速旋转，向打到空中的敌人发动连续攻击。拥有永久性增加冲锋拳伤害的被动效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.冲锋拳, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.进阶终极攻击); //23120012 - 进阶终极攻击 - 永久性地增加攻击力和命中率，终极：双弩枪技能的发动概率和伤害大幅上升。\n需要技能：#c终极：双弩枪20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                addDamageIncrease(双弩.精准双弩枪, eff.getDamage());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(双弩.伊师塔之环_强化); //23120043 - 伊师塔之环-强化 - 提升伊师塔之环的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.伊师塔之环, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.伊师塔之环_链接强化); //23120045 - 伊师塔之环-链接强化 - 提升伊师塔之环伤害增加量。\n前置技能：#c伊师塔之环1级以上
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.急袭双杀, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.传说之矛_强化); //23120049 - 传说之矛-强化 - 提升传说之矛的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.传说之矛, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双弩.传说之矛_链接强化); //23120051 - 传说之矛-链接强化 - 提升飞叶龙卷风的伤害增加量。\n前置技能：#c传说之矛1级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双弩.飞叶龙卷风, bx.getEffect(bof).getDAMRate());
            }
        }
    }

    /*
         * ---------------------------------
         * 龙神技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_longsheng(MapleCharacter chra) {
        
        if (MapleJob.is龙神(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(龙神.链接魔力); //2000006 - MP增加 - 永久增加最大MP，根据等级MP也会额外的增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_mp += eff.getPercentMP(); //最大Mp增加
                addmaxmp += eff.getLevelToMaxMp() * chra.getLevel(); //等级乘以倍率
            }
            
            magic += chra.getTotalSkillLevel(SkillFactory.getSkill(龙神.龙魂)); //龙魂 - 通过和龙交感增加魔力
            bx = SkillFactory.getSkill(龙神.智慧激发); //22120001 - 智慧激发 - 通过精神修养，永久性地提高智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(龙神.魔力激化); //22150000 - 魔力激化 - 消耗更多的MP，提高所有攻击魔法的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                percent_damage_rate += eff.getY();
                percent_boss_damage_rate += eff.getY();
            }
            bx = SkillFactory.getSkill(龙神.龙的愤怒); //22160000 - 龙的愤怒 - 自己的MP维持在一定范围内时，增强龙的集中力，提高魔力。MP超出有效范围时，效果消失。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_damage += bx.getEffect(bof).getDamage();
            }
            bx = SkillFactory.getSkill(龙神.魔法精通); //22170001 - 魔法精通 - 提高魔法熟练度和魔力，提高爆击最小伤害。\n需要技能：#c咒语精通10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                magic += eff.getX();
                trueMastery += eff.getMastery();
                critical_damage_min += eff.getCriticalMin();
            }
            /*
                 * 超级技能
             */
        }
    }

    /*
         * ---------------------------------
         * 战神技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_zhansheng(MapleCharacter chra) {
        
        if (MapleJob.is战神(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(战神.物理训练); //21100008 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(战神.冰雪矛); //21101006 - 冰雪矛 - 永久性地增加伤害，使用技能时有一定概率为矛附加冰属性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_damage += bx.getEffect(bof).getDAMRate();
            }
            bx = SkillFactory.getSkill(战神.进阶矛连击强化); //21110000 - 进阶矛连击强化 - 状态异常抗性概率、稳如泰山概率、爆击概率、爆击最大伤害提高。此外，斗气点数每累积10点，状态异常抗性概率、稳如泰山概率、爆击概率攻击力额外提高。\n需要技能：#c矛连击强化10级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCritical(); //爆击概率
                critical_damage_max += eff.getCriticalMax(); //爆击最大伤害
                ASR += eff.getASRRate(); //状态异常抗性概率
            }
            bx = SkillFactory.getSkill(战神.分裂攻击); //21110010 - 分裂攻击 - 一定程度上无视怪物的防御力，打猎BOSS怪时，可以造成额外伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob(); //无视防御
                percent_boss_damage_rate += eff.getBossDamage(); //BOSS伤害
            }
            bx = SkillFactory.getSkill(战神.防守策略); //21120004 - 防守策略 - 学会更高层次的防御技术，永久性地减少从敌人那里受到的伤害，提高体力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(战神.进阶终极攻击); //21120012 - 进阶终极攻击 - 永久性地增加攻击力和命中率，终极矛技能的发动概率和伤害大幅上升。\n需要技能：#c终极矛20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                addDamageIncrease(战神.终极矛, eff.getDamage());
            }
            bx = SkillFactory.getSkill(战神.迅捷移动); //21120011 - 迅捷移动 - 战神突进、终极投掷的伤害进一步提高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(战神.战神突进, eff.getDAMRate());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(战神.恐惧风暴_附加目标);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addTargetPlus(战神.加速终端_恐惧风暴, eff.getTargetPlus());
                addTargetPlus(战神.加速终端_恐惧风暴_2, eff.getTargetPlus());
                addTargetPlus(战神.加速终端_恐惧风暴_3, eff.getTargetPlus());
            }
            bx = SkillFactory.getSkill(战神.激素狂飙_坚持);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addBuffDuration(战神.激素狂飙, eff.getDuration());
            }
            bx = SkillFactory.getSkill(战神.比昂德_额外攻击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addAttackCount(战神.比昂德, eff.getAttackCount());
                addAttackCount(战神.比昂德_2击, eff.getAttackCount());
                addAttackCount(战神.比昂德_3击, eff.getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 奇袭者技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_qixizhe(MapleCharacter chra) {
        
        if (MapleJob.is奇袭者(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(奇袭者.光速); //15000023 - 光速 - 通过操控一股闪电力量，使身体变得更加敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
                raidenCount += eff.getV();
                raidenPorp += eff.getProp();
            }
            bx = SkillFactory.getSkill(奇袭者.元素_闪电); //15001022 - 元素：闪电 - 召唤出青白色的闪电元素，获得其的力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                raidenCount += eff.getV();
                raidenPorp += eff.getProp();
            }
            bx = SkillFactory.getSkill(奇袭者.体力锻炼); //15100024 - 体力锻炼 - 通过坚持不懈的修炼，永久提升力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
            }
            bx = SkillFactory.getSkill(奇袭者.雷魄); //15100025 - 雷魄 - 更加熟练地使用闪电力量，威力提升一个阶段。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                raidenCount += eff.getV();
                raidenPorp += eff.getProp();
            }
            bx = SkillFactory.getSkill(奇袭者.雷帝); //15110026 - 雷帝 - 将闪电力量置于自己的支配之下，更积极地操控这种力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                raidenCount += eff.getV();
                raidenPorp += eff.getProp();
            }
            bx = SkillFactory.getSkill(奇袭者.刺激); //15120007 - 刺激 - 利用电气刺激全身的神经，形成比任何人都更强韧的肉体。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                //toto 回避率增加 x% er
                percent_hp += eff.getPercentHP();
                ignore_mob_damage_rate += eff.getIgnoreMobDamR(); //受到伤害减少x%
            }
            bx = SkillFactory.getSkill(奇袭者.雷神); //15120008 - 雷神 - 精通闪电力量，领悟其中蕴含的精髓。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                raidenCount += eff.getV();
                raidenPorp += eff.getProp();
                passive_sharpeye_rate += eff.getCritical(); //爆击概率
                critical_damage_min += eff.getCriticalMin(); //爆击最小伤害
            }
            /*
                 * 超级技能处理
             */
            bx = SkillFactory.getSkill(奇袭者.疾风_强化); //15120043 - 疾风-强化 - 提升疾风技能的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(奇袭者.疾风, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(奇袭者.台风, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(奇袭者.疾风_额外目标); //15120044 - 疾风-额外目标 - 增加使用疾风技能可攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(奇袭者.疾风, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(奇袭者.台风, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(奇袭者.疾风_额外攻击);  //15120045 - 疾风-额外攻击 - 增加疾风技能的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(奇袭者.疾风, bx.getEffect(bof).getAttackCount());
                addAttackCount(奇袭者.台风, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(奇袭者.霹雳_强化); //15120046 - 霹雳-强化 - 提升霹雳技能的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(奇袭者.霹雳, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(奇袭者.霹雳_额外目标); //15120047 - 霹雳-额外目标 - 增加使用霹雳技能可攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(奇袭者.霹雳, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(奇袭者.霹雳_额外攻击);  //15120048 - 霹雳-额外攻击 - 增加霹雳技能的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(奇袭者.霹雳, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(奇袭者.毁灭_强化); //15120049 - 毁灭-强化 - 提升毁灭技能的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(奇袭者.毁灭, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(奇袭者.毁灭_无视防御); //15120050 - 毁灭-无视防御 - 提升毁灭技能的怪物防御率无视数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(奇袭者.毁灭, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(奇袭者.毁灭_BOSS杀手); //15120051 - 毁灭-BOSS杀手 - 提升毁灭技能对BOSS的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
            }
        }
    }

    /*
         * ---------------------------------
         * 夜行者技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yexingzhe(MapleCharacter chra) {
        
        if (MapleJob.is夜行者(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(夜行者.投掷精通); //14100023 - 投掷精通 - 增加伤害，提高拳套系列武器的熟练度和命中值，增加飞镖的最大持有数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(夜行者.物理训练); //14100025 - 物理训练 - 通过锻炼身体永久性地提高幸运。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(夜行者.永恒黑暗); //永恒黑暗 - 和黑暗融为一体，永久增加最大HP、增加状态异常抗性、增加所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                ASR += eff.getASRRate();
            }
            bx = SkillFactory.getSkill(夜行者.黑暗祝福); //14120006 - 黑暗祝福 - 获得黑暗的祝福，能力强化。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            bx = SkillFactory.getSkill(夜行者.黑暗预兆_缩短冷却时间); //14120046 - 黑暗预兆-缩短冷却时间 - 减少黑暗预兆的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(夜行者.黑暗预兆, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(夜行者.黑暗预兆_额外目标); //14120047 - 黑暗预兆-额外目标 - 增加黑暗预兆攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(夜行者.黑暗预兆, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 风灵使者技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_fengling(MapleCharacter chra) {
        
        if (MapleJob.is风灵使者(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(风灵使者.风影漫步); //13001021 - 风影漫步 - 借助风之力量，快速向前方突进。突进时无视怪物的攻击。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                jump += eff.getPassiveJump();
                speed += eff.getPassiveSpeed();
            }
            bx = SkillFactory.getSkill(风灵使者.风之私语); //13000023 - 风之私语 - 听取风之私语，习得古代知识，拥有轻捷的身手。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
//                    accuracy += eff.getAccX();
                defRange += eff.getRange();
            }
            bx = SkillFactory.getSkill(风灵使者.物理训练); //13100026 - 物理训练 - 通过锻炼身体，永久提升力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(风灵使者.轻如鸿毛); //13110025 - 轻如鸿毛 - 身体变得像羽毛一样轻盈，可以灵巧地避开敌人的攻击。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(风灵使者.重振精神); //13110026 - 重振精神 - 借助风之力量，摆脱无法回避的情况，获得反击的机会。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWdefX();
                percent_mdef += eff.getMdefX();
                dodgeChance += eff.getER();
            }
            /*
                 * 超级技能处理
             */
            bx = SkillFactory.getSkill(风灵使者.狂风肆虐_强化); //13120043 - 狂风肆虐-强化 - 增加狂风肆虐的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(风灵使者.狂风肆虐Ⅰ, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.狂风肆虐Ⅰ_强化, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.狂风肆虐Ⅱ, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.狂风肆虐Ⅱ_强化, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.狂风肆虐Ⅲ, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.狂风肆虐Ⅲ_强化, bx.getEffect(bof).getDAMRate());
            }
            //已经在触发的时候处理 13120044 - 狂风肆虐-增强 - 提升狂风肆虐的触发概率。
            bx = SkillFactory.getSkill(风灵使者.狂风肆虐_二次机会); //13120045 - 狂风肆虐-二次机会 - 可以使狂风肆虐再次命中同一个对象。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(风灵使者.狂风肆虐Ⅰ, 2);
                addAttackCount(风灵使者.狂风肆虐Ⅰ_强化, 2);
                addAttackCount(风灵使者.狂风肆虐Ⅱ, 2);
                addAttackCount(风灵使者.狂风肆虐Ⅱ_强化, 2);
                addAttackCount(风灵使者.狂风肆虐Ⅲ, 2);
                addAttackCount(风灵使者.狂风肆虐Ⅲ_强化, 2);
            }
            bx = SkillFactory.getSkill(风灵使者.旋风箭_强化); //13120046 - 旋风箭-强化 - 增加旋风箭的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(风灵使者.旋风箭, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(风灵使者.旋风箭_溅射, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(风灵使者.旋风箭_额外目标); //13120047 - 旋风箭-额外目标 - 增加旋风箭攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(风灵使者.旋风箭, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(风灵使者.旋风箭_溅射, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(风灵使者.旋风箭_额外攻击);  //13120048 - 旋风箭-额外攻击 - 增加旋风箭的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(风灵使者.旋风箭, bx.getEffect(bof).getAttackCount());
                addAttackCount(风灵使者.旋风箭_溅射, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(风灵使者.天空之歌_强化); //13120049 - 天空之歌-强化 - 增加天空之歌的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(风灵使者.天空之歌, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(风灵使者.天空之歌_无视防御); //13120050 - 天空之歌-无视防御 - 提升天空之歌的无视怪物防御数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(风灵使者.天空之歌, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(风灵使者.天空之歌_BOSS杀手); //13120051 - 天空之歌-BOSS杀手 - 提升天空之歌的BOSS攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
            }
        }
    }

    /*
         * ---------------------------------
         * 炎术士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yanshushi(MapleCharacter chra) {
        
        if (MapleJob.is炎术士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(炎术士.MP增加);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_mp += bx.getEffect(bof).getPercentMP();
            }
            bx = SkillFactory.getSkill(炎术士.魔法爆击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                TER += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(炎术士.卓越才能); //12000025 - 卓越才能 - 利用受到祝福的才能增加最大魔力，等级越高，魔力增加量越高。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_mp += eff.getPercentMP(); //最大Mp增加
                addmaxmp += eff.getLevelToMaxMp() * chra.getLevel(); //等级乘以倍率
            }
            bx = SkillFactory.getSkill(炎术士.弱点分析); //12110026 - 弱点分析 - 通过敏锐的分析看透敌人的弱点，提高咒语造成爆击的概率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCritical(); //爆击概率
                critical_damage_min += eff.getCriticalMin(); //爆击最小伤害
            }
            bx = SkillFactory.getSkill(炎术士.顿悟); //12110027 - 顿悟 - 咒语达到更高的境界，快速成长。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            /*
                 * 处理超级技能
             */
            bx = SkillFactory.getSkill(炎术士.轨道烈焰_灵魂攻击);  //12120045 - 轨道烈焰-灵魂攻击 - 轨道烈焰IV的伤害降为280%，但攻击次数增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(炎术士.轨道烈焰_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰II_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰III_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰IV_攻击, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(炎术士.灭绝之焰_额外攻击);  //12120046 - 灭绝之焰-额外攻击 - 增加灭绝之焰的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(炎术士.灭绝之焰_LINK, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(炎术士.灭绝之焰_额外目标);  //12120048 - 灭绝之焰-额外目标 - 增加灭绝之焰攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(炎术士.灭绝之焰_LINK, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 魂骑士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_hunqishi(MapleCharacter chra) {
        
        if (MapleJob.is魂骑士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(魂骑士.灵魂鸣响);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_wdef += eff.getWdefX();
                percent_mdef += eff.getMdefX();
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
                speedMax += eff.getSpeedMax();
            }
            bx = SkillFactory.getSkill(魂骑士.灵魂守卫);//共20级,1级加100,共2000HP
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
//                percent_hp += eff.getPercentHP();
//                percent_hp += eff.getHpR();
//                percent_hp += bx.getEffect(bof).getPercentHP();
//                percent_hp += eff.getMhpR();
//                percent_hp += eff.getMaxHpX();
//                percent_hp += eff.getPercentHP();//最大HP增加 eff.getLevel()*0.9D
                addmaxhp += 100 * eff.getLevel();
            }
            bx = SkillFactory.getSkill(魂骑士.元素_灵魂); //11001022 - 元素：灵魂 - 召唤出灵魂元素，获得其的力量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(魂骑士.人神一体); //11100026 - 人神一体 - 同时修炼身心，打好坚实的基本功。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(魂骑士.钢铁之轮); //11110025 - 钢铁之轮 - 凭借超越常人的坚强意志，摆脱逆境。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(魂骑士.心灵呐喊); //11110026 - 心灵呐喊 - 集中精神，给全身注入活力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                localstr += eff.getStrX();
            }
            bx = SkillFactory.getSkill(魂骑士.幻千之刃); //11120008 - 幻千之刃 - 以幻千之刃的攻击削弱敌人的防御。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            /*
                 * 超级技能处理
             */
            bx = SkillFactory.getSkill(魂骑士.斩刺_强化); //11120046 - 斩刺-强化 - 提升新月斩和烈日之刺的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(魂骑士.新月斩, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(魂骑士.烈日之刺, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(魂骑士.斩刺_额外目标); //11120047 - 斩刺-额外目标 - 增加使用新月斩和烈日之刺攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(魂骑士.新月斩, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(魂骑士.烈日之刺, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(魂骑士.斩刺_额外攻击);  //11120048 - 斩刺-额外攻击 - 增加新月斩和烈日之刺的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(魂骑士.新月斩, bx.getEffect(bof).getAttackCount());
                addAttackCount(魂骑士.烈日之刺, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(魂骑士.极速之舞_强化); //11120049 - 极速之舞-强化 - 增加月光之舞和极速霞光的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(魂骑士.月光之舞, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(魂骑士.月光之舞_连续攻击, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(魂骑士.极速霞光, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(魂骑士.极速霞光_向下攻击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(魂骑士.极速之舞_无视防御); //11120050 - 极速之舞-无视防御 - 增加月光之舞和极速霞光的无视怪物防御数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(魂骑士.月光之舞, bx.getEffect(bof).getIgnoreMob());
                addIgnoreMobpdpRate(魂骑士.月光之舞_连续攻击, bx.getEffect(bof).getIgnoreMob());
                addIgnoreMobpdpRate(魂骑士.极速霞光, bx.getEffect(bof).getIgnoreMob());
                addIgnoreMobpdpRate(魂骑士.极速霞光_向下攻击, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(魂骑士.极速之舞_BOSS杀手); //11120051 - 极速之舞-BOSS杀手 - 增加月光之舞和极速霞光的BOSS攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
            }
        }
    }

    /*
         * ---------------------------------
         * 龙的传人技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_longdechuangren(MapleCharacter chra) {
        
        if (MapleJob.is龙的传人(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(龙的传人.侠客之道); //5080022 - 侠客之道 - 熟练侠客的一切基本技巧.提升命中值,回避值,射程,移动速度,最高移动速度上限.\r\n
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                defRange += eff.getRange();
//                    accuracy += eff.getAcc();
                speed += eff.getPassiveSpeed();
                speedMax += eff.getSpeedMax();
            }
            bx = SkillFactory.getSkill(龙的传人.侠客秘诀); //5700011 - 侠客秘诀 - 透过特殊锻炼方式永久提升力量,敏捷与体力.另外,有几率发动格挡效果.\r\n
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                percent_hp += eff.getPercentHP(); //最大Hp增加
                percent_mp += eff.getPercentMP(); //最大Mp增加
            }
            bx = SkillFactory.getSkill(龙的传人.宏武典籍); //5710022 - 宏武典籍 - 强化使用短枪与指节的技巧,大幅提升短枪与指节的爆击几率和物理攻击力.
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getDAMRate();
                percent_boss_damage_rate += eff.getDAMRate();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            bx = SkillFactory.getSkill(龙的传人.金刚不坏); //5720061 - 金刚不坏 - 爆发所有真气强化身体,永久提升和防御有关的所有能力.大幅强化耐力并使攻击力剧增.
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP(); //最大Hp增加
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(龙的传人.龙魂流星拳_增加目标); //5720044 - 龙魂流星拳-增加目标 - 增加龙魂流星拳攻击怪物的数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(龙的传人.龙魂流星拳, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(龙的传人.龙魂流星拳_次数强化); //5720045 - 龙魂流星拳-次数强化 - 增加龙魂流星拳的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(龙的传人.龙魂流星拳, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(龙的传人.真气爆炸_次数强化); //5720048 - 真气爆炸-次数强化 - 增加真气爆炸的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(龙的传人.真气爆炸, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 神炮王技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shenpaowang(MapleCharacter chra) {
        
        if (MapleJob.is神炮王(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(神炮王.升级火炮); //升级火炮 - 对大炮进行改良，永久性地提高攻击力和防御力
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(神炮王.海盗训练); //海盗训练 - 通过海盗的秘密修炼，提高力量和敏捷
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(神炮王.猴子超级炸弹); //猴子超级炸弹
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(神炮王.猴子炸药桶, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(神炮王.生命强化); //生命强化 - 永久性地强化体力、防御力、增加状态异常抗性
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP(); //以前是这个 好像有点问题加不满血getHpR()
                ASR += eff.getASRRate();
                percent_wdef += eff.getWDEFRate();
            }
            bx = SkillFactory.getSkill(神炮王.火炮强化); //火炮强化 - 永久性地强化大炮，提高攻击力和攻击速度
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(神炮王.极限燃烧弹); //极限燃烧弹 - 将大炮的性能提高到极限，永久性地增加伤害。此外，攻击时有一定概率在一定程度上无视怪物的防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getDAMRate();
                percent_boss_damage_rate += eff.getDAMRate();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(神炮王.双胞胎猴子支援_增加); //5320043 - 双胞胎猴子支援-增加 - 降低双胞胎猴子支援的伤害，增加攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(神炮王.双胞胎猴子支援, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(神炮王.双胞胎猴子支援_坚持); //5320044 - 双胞胎猴子支援-坚持 - 增加双胞胎猴子支援的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(神炮王.双胞胎猴子支援, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(神炮王.加农火箭炮_强化); //5320046 - 加农火箭炮-强化 - 增加加农火箭炮的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(神炮王.加农火箭炮, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(神炮王.加农火箭炮_额外目标); //5320047 - 加农火箭炮-额外目标 - 增加加农火箭炮攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(神炮王.加农火箭炮, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(神炮王.加农火箭炮_额外攻击); //5320048 - 加农火箭炮-额外攻击 - 增加加农火箭炮的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(神炮王.加农火箭炮, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(神炮王.集中炮击_强化); //5320049 - 集中炮击-强化 - 增加集中炮击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(神炮王.集中炮击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(神炮王.集中炮击_额外攻击); //5320051 - 集中炮击-额外攻击 - 增加集中炮击的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(神炮王.集中炮击, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 船长技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_chuanzhang(MapleCharacter chra) {
        
        if (MapleJob.is船长(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(船长.物理训练); //5200009 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX(); //提高力量
                localdex += eff.getDexX(); //提高敏捷
            }
            bx = SkillFactory.getSkill(船长.坚忍不拔); //HP MP 加600。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addmaxhp += eff.getX();
                addmaxmp += eff.getX();
            }
            bx = SkillFactory.getSkill(船长.指挥船员); //HP MP 加600。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addmaxhp += eff.getMaxHpX();
                addmaxmp += eff.getMaxMpX();
            }
            bx = SkillFactory.getSkill(船长.无尽追击); //暴击伤害微提高20%。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                inc_Cr += eff.getCr(); //爆击概率
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(船长.金属风暴_强化); //5220049 - 金属风暴-强化 - 增加金属风暴的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(船长.金属风暴, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(船长.金属风暴_BOSS杀手); //5220051 - 金属风暴-BOSS杀手 - 增加金属风暴对BOSS的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
            }
        }
    }

    /*
         * ---------------------------------
         * 冲锋队长技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_duizhang(MapleCharacter chra) {
        
        if (MapleJob.is冲锋队长(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(冲锋队长.物理训练); //5100010 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX(); //提高力量
                localdex += eff.getDexX(); //提高敏捷
            }
            bx = SkillFactory.getSkill(冲锋队长.蛇拳); //5121015 - 蛇拳 - 在一定时间内召唤出遗忘的毒蛇之魂，增加攻击力。被动效果是提高状态异常和所有属性抗性，提高拳甲熟练度。\n需要技能：#c精准拳20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate(); //提高状态异常
            }
            bx = SkillFactory.getSkill(冲锋队长.重装碾压); //5120014 - 重装碾压 - 攻击时有一定概率100%无视敌人的防御力。对BOSS怪同样有效。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getX();
            }
            double energyrate = 1.0;
            if (chra.getTotalSkillLevel(冲锋队长.终极冲击) > 0) {
                bx = SkillFactory.getSkill(冲锋队长.终极冲击);
                bof = chra.getTotalSkillLevel(bx);
                eff = bx.getEffect(bof);
                watk += (eff.getWatk() * energyrate);
                wdef += (eff.getEnhancedWdef() * energyrate);
                mdef += (eff.getEnhancedMdef() * energyrate);
                speed += (eff.getSpeed() * energyrate);
//                    accuracy += (eff.getAcc() * energyrate);
            } else if (chra.getTotalSkillLevel(冲锋队长.超级冲击) > 0) {
                bx = SkillFactory.getSkill(冲锋队长.超级冲击);
                bof = chra.getTotalSkillLevel(bx);
                eff = bx.getEffect(bof);
                watk += (eff.getWatk() * energyrate);
                wdef += (eff.getEnhancedWdef() * energyrate);
                mdef += (eff.getEnhancedMdef() * energyrate);
                speed += (eff.getSpeed() * energyrate);
//                    accuracy += (eff.getAcc() * energyrate);
            } else if (chra.getTotalSkillLevel(冲锋队长.能量获得) > 0) {
                bx = SkillFactory.getSkill(冲锋队长.能量获得);
                bof = chra.getTotalSkillLevel(bx);
                eff = bx.getEffect(bof);
                wdef += (eff.getEnhancedWdef() * energyrate);
                mdef += (eff.getEnhancedMdef() * energyrate);
                speed += (eff.getSpeed() * energyrate);
//                    accuracy += (eff.getAcc() * energyrate);
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(冲锋队长.激怒拳_强化); //5120046 - 激怒拳-强化 - 增加激怒拳的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(冲锋队长.激怒拳, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(冲锋队长.暴怒拳, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(冲锋队长.激怒拳_BOSS杀手); //5120047 - 激怒拳-BOSS杀手 - 增加激怒拳的BOSS攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBossDamageRate(冲锋队长.激怒拳, bx.getEffect(bof).getBossDamage());
                addBossDamageRate(冲锋队长.暴怒拳, bx.getEffect(bof).getBossDamage());
            }
            bx = SkillFactory.getSkill(冲锋队长.激怒拳_额外攻击); //5120048 - 激怒拳-额外攻击 - 增加激怒拳的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(冲锋队长.激怒拳, bx.getEffect(bof).getAttackCount());
                addAttackCount(冲锋队长.暴怒拳, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(冲锋队长.能量爆炸_强化); //5120049 - 能量爆炸-强化 - 增加能量爆炸的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(冲锋队长.能量爆炸, bx.getEffect(bof).getDAMRate());
                addDamageIncrease(冲锋队长.双重爆炸, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(冲锋队长.能量爆炸_额外目标); //5120050 - 能量爆炸-额外目标 - 增加能量爆炸攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(冲锋队长.能量爆炸, bx.getEffect(bof).getTargetPlus());
                addTargetPlus(冲锋队长.双重爆炸, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(冲锋队长.能量爆炸_额外攻击); //5120051 - 能量爆炸-额外攻击 - 增加能量爆炸的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(冲锋队长.能量爆炸, bx.getEffect(bof).getAttackCount());
                addAttackCount(冲锋队长.双重爆炸, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 双刀技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shuangdao(MapleCharacter chra) {
        
        if (MapleJob.is暗影双刀(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(双刀.物理训练); //4310006 - 物理训练 - 通过锻炼身体，永久提高运气和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(双刀.生命偷取); //4330007 - 生命偷取 - 攻击时有一定概率恢复HP。但一次恢复的HP不能超过最大值的20%。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                hpRecoverProp += eff.getProp();
                hpRecover_Percent += eff.getX();
            }
            bx = SkillFactory.getSkill(双刀.永恒黑暗); //4330008 - 永恒黑暗 - 和黑暗融为一体，永久增加最大HP、增加状态异常抗性、增加所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                ASR += eff.getASRRate();
            }
            bx = SkillFactory.getSkill(双刀.影子闪避); //4330009 - 影子闪避 - 有一定概率可以回避敌人的攻击，成功回避后的1秒之内提升攻击力，并且接下来的攻击一定会以爆击形式击中。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(双刀.傀儡召唤); //4341006 - 傀儡召唤 - 永久增加暗影双刀的防御力和回避概率，并且使用技能时，将召唤为镜像分身的分身分离出来，做成土堆。土堆可以吸引敌人的攻击，并吸收部分伤害，以保护自己不受敌人伤害。只能在镜像分身技能有效期间内使用。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate();
                percent_mdef += eff.getMDEFRate();
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(双刀.终极斩); //4341002 - 终极斩 - 消耗大量HP，给多个敌人加以非常强力的攻击。有较低概率一击必杀，在一定时间内，部分攻击技能的伤害增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(双刀.流云斩, eff.getDAMRate());
                addDamageIncrease(双刀.双刀风暴, eff.getDAMRate());
                addDamageIncrease(双刀.龙卷风, eff.getDAMRate());
                addDamageIncrease(双刀.血雨腥风, eff.getDAMRate());
                addDamageIncrease(双刀.悬浮地刺, eff.getDAMRate());
                addDamageIncrease(双刀.暗影飞跃斩, eff.getDAMRate());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(双刀.血雨腥风_强化); //4340043 - 血雨腥风-强化 - 增加血雨腥风的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双刀.血雨腥风, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双刀.血雨腥风_额外目标); //4340044 - 血雨腥风-额外目标 - 增加血雨腥风攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(双刀.血雨腥风, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(双刀.血雨腥风_额外攻击); //4340045 - 血雨腥风-额外攻击 - 增加血雨腥风的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(双刀.血雨腥风, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(双刀.幽灵一击_强化); //4340046 - 幽灵一击-强化 - 增加幽灵一击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双刀.幽灵一击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双刀.幽灵一击_无视防御); //4340047 - 幽灵一击-无视防御 - 增加幽灵一击的无视怪物防御力数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(双刀.幽灵一击, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(双刀.幽灵一击_额外攻击); //4340048 - 幽灵一击-额外攻击 - 增加幽灵一击的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(双刀.幽灵一击, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(双刀.暴怒刀阵_强化); //4340055 - 暴怒刀阵 - 强化 - 增加暴怒刀阵的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(双刀.暴怒刀阵, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(双刀.暴怒刀阵_无视防御); //4340056 - 暴怒刀阵 - 无视防御 - 增加暴怒刀阵的防御力无视效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(双刀.暴怒刀阵, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(双刀.暴怒刀阵_额外目标); //4340057 - 暴怒刀阵 -额外目标 - 增加利用暴怒刀阵攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(双刀.暴怒刀阵, bx.getEffect(bof).getTargetPlus());
            }
        }
    }

    /*
         * ---------------------------------
         * 侠盗技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_xiadao(MapleCharacter chra) {
        
        if (MapleJob.is侠盗(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(侠盗.物理训练); //4200007 - 物理训练 - 通过锻炼身体，永久提高运气和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(侠盗.永恒黑暗); //4210013 - 永恒黑暗 - 和黑暗融为一体，永久增加最大HP、增加状态异常抗性、增加所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(侠盗.盾牌精通); //4200010 - 盾防精通 - 装备盾牌时，物理防御力和魔法防御力增加，回避率增加。
            bof = chra.getTotalSkillLevel(bx);
            Item shield = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10); //必须装备盾牌
            if (bof > 0 && bx != null && shield != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getX();
                percent_mdef += eff.getX();
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(侠盗.贪婪); //4210012 - 贪婪 - 修炼飞侠的秘籍，增加金币获得量。此外略微提高使用金币的所有技能的效率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                mesoBuff *= (eff.getMesoRate() + 100.0) / 100.0;
                pickRate += eff.getU();
                mesoGuard -= eff.getV();
                mesoGuardMeso -= eff.getW();
                addDamageIncrease(侠盗.金钱炸弹, eff.getX()); //金钱炸弹 - 使周围掉落的金币爆炸，给敌人造成伤害。但无法引爆其他人有优先权的金币，普通怪物最多可以重叠10个的伤害，BOSS怪物最多可以重叠15个的伤害。
            }
            bx = SkillFactory.getSkill(侠盗.侠盗本能); //4221013 - 侠盗本能 - 通过暗杀，唤醒侠盗的本能，增加攻击力，获得穿透敌人防御的能力。被动效果是攻击敌人时可以积累击杀点数，消耗积累的点数，可以增强攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(侠盗.一出双击); //4221007 - 一出双击 - 使用技能时，快速两次攻击多个敌人，有一定几率使敌人昏迷。此外，强化可以和一出双击相连接的所有3转以下的攻击技能。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(侠盗.回旋斩, eff.getDAMRate());
                addDamageIncrease(侠盗.神通术, eff.getDAMRate());
                addDamageIncrease(侠盗.炼狱, eff.getDAMRate());
                addDamageIncrease(侠盗.刀刃之舞, eff.getDAMRate());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(侠盗.金钱炸弹_强化); //4220043 - 金钱炸弹-强化 - 增加金钱炸弹的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(侠盗.金钱炸弹, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(侠盗.一出双击_强化); //4220046 - 一出双击-强化 - 增加一出双击的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(侠盗.一出双击, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(侠盗.一出双击_额外目标); //4220047 - 一出双击-额外目标  - 增加一出双击攻击的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(侠盗.一出双击, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(侠盗.一出双击_额外攻击); //4220048 - 一出双击-额外攻击 - 增加一出双击的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(侠盗.一出双击, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(侠盗.暗杀_强化); //4220049 - 暗杀-强化 - 增加暗杀的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(侠盗.暗杀, bx.getEffect(bof).getDAMRate());
            }
        }
    }

    /*
         * ---------------------------------
         * 隐士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yinshi(MapleCharacter chra) {
        
        if (MapleJob.is隐士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(隐士.物理训练); //4100007 - 物理训练 - 通过锻炼身体，永久提高运气和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(隐士.永恒黑暗); //4110008 - 永恒黑暗 - 和黑暗融为一体，永久增加最大HP、增加状态异常抗性、增加所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(隐士.娴熟飞镖术); //4110012 - 娴熟飞镖术 - 提高双飞斩、爆裂飞镖、风之护符、三连环光击破的#c总伤害#。\n此外，攻击时有一定几率不消耗飞镖，使当前持有的#c飞镖增加1个#。\n(但飞镖数量无法超出最大个数)\n此外娴熟飞镖术效果发动时，#c下一次攻击百分之百造成爆击#。(在暗器伤人状态下也可以造成爆击，但标枪数量不增加。)
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage += eff.getPercentDamageRate();
            }
            bx = SkillFactory.getSkill(隐士.药品吸收); //4110014 - 药品吸收 - 提高药水等恢复道具的效果。但对超级药水之类按百分比恢复的道具无效。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                RecoveryUP += eff.getX() - 100;
                BuffUP += eff.getY() - 100;
            }
            bx = SkillFactory.getSkill(隐士.黑暗祝福); //4121014 - 黑暗祝福 - 获得黑暗的祝福，可以对敌人发动更加致命的攻击。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(隐士.决战之巅_强化); //4120043 - 决战之巅-强化 - 增加决战之巅的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(隐士.决战之巅, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(隐士.决战之巅_额外目标); //4120044 - 决战之巅-额外目标 - 增加攻击决战之巅的怪物数量。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(隐士.决战之巅, bx.getEffect(bof).getTargetPlus());
            }
//                bx = SkillFactory.getSkill(标飞.决战之巅_增强); //4120045 - 决战之巅-增强 - 增加攻击决战之巅怪物的经验值和掉宝率。
//                bof = chra.getTotalSkillLevel(bx);
//                if (bof > 0 && bx != null) {
//                    addAttackCount(标飞.决战之巅, bx.getEffect(bof).getAttackCount());
//                }
            bx = SkillFactory.getSkill(隐士.模糊领域_BOSS杀手); //4120048 - 模糊领域-缩短冷却时间 - 减少模糊领域的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(隐士.模糊领域, bx.getEffect(bof).getCooltimeReduceR());
            }
            bx = SkillFactory.getSkill(隐士.四连镖_强化); //4120049 - 四连镖-强化  - 增加四连射的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(隐士.四连镖, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(隐士.四连镖_BOSS杀手); //4120050 - 四连镖-BOSS杀手  - 增加四连射的BOSS攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
            }
            bx = SkillFactory.getSkill(隐士.四连镖_额外攻击); //4120051 - 四连镖-额外攻击  - 增加四连射的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(隐士.四连镖, bx.getEffect(bof).getBulletCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 箭神技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_jianshen(MapleCharacter chra) {
        
        if (MapleJob.is箭神(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(箭神.物理训练); //3200006 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(箭神.闪避); //3210007 - 闪避 - 有一定概率可以回避敌人的攻击，回避成功后1秒内的攻击必定是爆击。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(箭神.射术精修); //3210015 - 射术精修 - 攻击时有一定概率在一定程度上无视怪物的防御力，永久性地增加命中率及总伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
//                    percent_acc += eff.getArRate(); //命中概率
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(箭神.冰凤凰); //3211005 - 冰凤凰 - 在一定时间内召唤带有冰属性的冰凤凰。冰凤凰最多同时攻击4个敌人。另外，会永久增加物理/魔法防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate(); //增加物防
                percent_mdef += eff.getMDEFRate(); //增加魔防
            }
            bx = SkillFactory.getSkill(箭神.寒冰爪钩); //3211010 - 寒冰爪钩 - 给范围之内最远处的敌人发射钩子造成伤害，并移动到敌人的背后。移动的时候不会碰到其他怪物，而且还会增加一定比率的体力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(箭神.治愈良药); //3211011 - 治愈良药 - 在冒险中，吃下所准备的药草后，立即摆脱异常状态。此外，所有属性抗性及异常状态抵抗永久增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(箭神.火眼晶晶_坚持); //3220043 - 火眼晶晶-坚持 - 火眼晶晶持续时间增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(箭神.火眼晶晶, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(箭神.火眼晶晶_无视防御); //3220044 - 火眼晶晶-无视防御 - 火眼晶晶效果上增加防御无视效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                
            }
            bx = SkillFactory.getSkill(箭神.火眼晶晶_神圣暴击); //3220045 - 火眼晶晶-神圣暴击 - 火眼晶晶效果中增加暴击率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                
            }
            bx = SkillFactory.getSkill(箭神.穿透箭_强化); //3220046 - 穿透箭-强化 - 增加穿透箭的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(箭神.穿透箭_四转, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(箭神.穿透箭_额外目标); //3220047 - 穿透箭-额外目标 - 增加穿透箭攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(箭神.穿透箭_四转, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(箭神.穿透箭_额外攻击); //3220048 - 穿透箭-额外攻击 - 增加穿透箭的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(箭神.穿透箭_四转, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(箭神.一击要害箭_强化); //3220049 - 一击要害箭-强化 - 增加一击要害箭的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(箭神.一击要害箭, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(箭神.一击要害箭_最大值提高); //3220050 - 一击要害箭-最大值提高 - 增加一击要害箭的伤害最大值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                
            }
            bx = SkillFactory.getSkill(箭神.一击要害箭_缩短冷却时间); //3220051 - 一击要害箭-缩短冷却时间 - 减少一击要害箭的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(箭神.一击要害箭, bx.getEffect(bof).getCooltimeReduceR());
            }
        }
    }

    /*
         * ---------------------------------
         * 神射手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_shenshe(MapleCharacter chra) {
        
        if (MapleJob.is神射手(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(神射手.物理训练); //3100006 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(神射手.精神集中); //3110012 - 精神集中 - 持续攻击时，集中力逐渐提高，异常状态抗性持续增加。拥有相应增益的情况下抵抗异常状态后，应用冷却时间。此外，永久增加异常状态抗性及所有属性抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();//异常状态抗性
                TER += eff.getTERRate();//所有属性抗性
            }
            bx = SkillFactory.getSkill(神射手.射术精修); //3110014 - 射术精修 - 攻击时有一定概率在一定程度上无视怪物的防御力，永久性地增加命中率及总伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
//                    percent_acc += eff.getArRate(); //命中概率
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(神射手.火凤凰); //3111005 - 火凤凰 - 召唤带有火属性的凤凰。凤凰最多同时攻击4个敌人，有一定概率造成昏迷。另外，永久增加物理/魔法防御力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate(); //增加物防 wdef
                percent_mdef += eff.getMDEFRate(); //增加魔防
            }
            bx = SkillFactory.getSkill(神射手.寒冰爪钩); //3111010 - 寒冰爪钩 - 给范围之内最远处的敌人发射钩子造成伤害，并移动到敌人的背后。移动的时候不会碰到其他怪物，而且还会增加一定比率的体力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(神射手.闪避); //3110007 - 闪避 - 有一定概率可以回避敌人的攻击，回避成功后1秒内的攻击必定是爆击。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(神射手.进阶终极攻击); //3120008 - 进阶终极攻击 - 永久性地增加攻击力和命中率，大幅提高终极武器的发动概率和伤害。\n需要技能：#c终极弓20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                addDamageIncrease(神射手.终极弓, eff.getDamage());
            }
            /*
                 * 超级技能
             */
            bx = SkillFactory.getSkill(神射手.火眼晶晶_坚持); //3120043 - 火眼晶晶-坚持 - 火眼晶晶持续时间增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(神射手.火眼晶晶, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(神射手.火眼晶晶_无视防御); //3120044 - 火眼晶晶-无视防御 - 火眼晶晶效果上增加防御无视效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                
            }
            bx = SkillFactory.getSkill(神射手.火眼晶晶_神圣暴击); //3120045 - 火眼晶晶-神圣暴击 - 火眼晶晶效果中增加暴击率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                
            }
            bx = SkillFactory.getSkill(神射手.骤雨箭矢_强化); //3120046 - 骤雨箭矢-强化 - 增加骤雨箭矢的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(神射手.骤雨箭矢, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(神射手.骤雨箭矢_额外目标); //3120047 - 骤雨箭矢-额外目标 - 使用骤雨箭矢攻击的怪物数量增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(神射手.骤雨箭矢, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(神射手.骤雨箭矢_额外攻击); //3120048 - 骤雨箭矢-额外攻击 - 增加骤雨箭矢的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(神射手.骤雨箭矢, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(神射手.暴风箭雨_强化); //3120049 - 暴风箭雨-强化 - 增加暴风箭雨的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(神射手.暴风箭雨, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(神射手.暴风箭雨_BOSS杀手); //3120050 - 暴风箭雨-BOSS杀手 - 增加暴风箭雨对BOSS的攻击力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBossDamageRate(神射手.暴风箭雨, bx.getEffect(bof).getBossDamage());
            }
            bx = SkillFactory.getSkill(神射手.暴风箭雨_灵魂攻击); //3120051 - 暴风箭雨-灵魂攻击 - 暴风箭雨的伤害变为原来的#x%，但攻击次数增多。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(神射手.暴风箭雨, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 主教技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_zhujiao(MapleCharacter chra) {
        
        if (MapleJob.is主教(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(主教.智慧激发); //2300007 - 智慧激发 - 通过精神修养，永久性增加智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(主教.魔力精通); //2320012 - 魔力精通 - 永久性增加魔力，增加对自己使用的所有增益的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                BuffUP_Skill += eff.getBuffTimeRate();
            }
            bx = SkillFactory.getSkill(主教.神秘瞄准术); //2320011 - 神秘瞄准术 - 攻击时可以无视怪物的部分防御力，持续攻击时提升所有攻击的伤害。有一定概率发动伤害提升效果，最多可以累积5次。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getX() * eff.getY();
                percent_boss_damage_rate += eff.getX() * eff.getY();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            /*
                 * 主教超级技能
             */
            bx = SkillFactory.getSkill(主教.神圣魔法盾_坚持); //2320044 - 神圣魔法盾-坚持 - 增加神圣魔法盾的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(主教.神圣魔法盾, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(主教.神圣魔法盾_缩短冷却时间); //2320045 - 神圣魔法盾-缩短冷却时间 - 减少神圣魔法盾的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(主教.神圣魔法盾, bx.getEffect(bof).getCooltimeReduceR());
            }
        }
    }

    /*
         * ---------------------------------
         * 冰雷技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_binlei(MapleCharacter chra) {
        
        if (MapleJob.is冰雷(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(冰雷.智慧激发); //2200007 - 智慧激发 - 通过精神修养，永久性增加智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(冰雷.极限魔力); //2210000 - 极限魔力（冰，雷） - HP较低的怪物有一定概率一击必杀，攻击受到持续伤害或处于昏迷、冻结、暗黑、麻痹状态的敌人，可以增加伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                dot += bx.getEffect(bof).getZ();
            }
            bx = SkillFactory.getSkill(冰雷.魔力激化); //2210001 - 魔力激化 - 消耗更多的MP，提高所有攻击魔法的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getCostMpRate();
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(冰雷.魔力精通); //2220013 - 魔力精通 - 永久性增加魔力，增加对自己使用的所有增益的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                BuffUP_Skill += eff.getBuffTimeRate();
            }
            bx = SkillFactory.getSkill(冰雷.神秘瞄准术); //2220010 - 神秘瞄准术 - 攻击时可以无视怪物的部分防御力，持续攻击时提升所有攻击的伤害。有一定概率发动伤害提升效果，最多可以累积5次。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getX() * eff.getY();
                percent_boss_damage_rate += eff.getX() * eff.getY();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            /*
                 * 冰雷超级技能
             */
            bx = SkillFactory.getSkill(冰雷.快速移动精通_强化); //2220043 - 快速移动精通-强化 - 增加快速移动精通的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(冰雷.快速移动精通, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(冰雷.快速移动精通_额外目标); //2220044 - 快速移动精通-额外目标 - 增加快速移动精通攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(冰雷.快速移动精通, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(冰雷.链环闪电_强化); //2220046 - 链环闪电-强化 - 增加链环闪电的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(冰雷.链环闪电, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(冰雷.链环闪电_额外目标); //2220047 - 链环闪电-额外目标 - 增加链环闪电攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(冰雷.链环闪电, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(冰雷.链环闪电_额外攻击); //2220048 - 链环闪电-额外攻击 - 增加链环闪电的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(冰雷.链环闪电, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(冰雷.冰河锁链_额外目标); //2220049 - 冰河锁链-额外目标 - 增加冰河锁链攻击的怪物数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addTargetPlus(冰雷.冰河锁链, bx.getEffect(bof).getTargetPlus());
            }
            bx = SkillFactory.getSkill(冰雷.冰河锁链_额外攻击); //2220050 - 冰河锁链-额外攻击 - 增加冰河锁链的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(冰雷.冰河锁链, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(冰雷.冰河锁链_缩短冷却时间); //2220051 - 冰河锁链-缩短冷却时间 - 减少冰河锁链的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(冰雷.冰河锁链, bx.getEffect(bof).getCooltimeReduceR());
            }
        }
    }

    /*
         * ---------------------------------
         * 火毒技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_huodu(MapleCharacter chra) {
        
        if (MapleJob.is火毒(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(火毒.智慧激发); //2100007 - 智慧激发 - 通过精神修养，永久性增加智力。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(火毒.极限魔力); //2110000 - 极限魔力（火，毒） - 增加自己的所有持续伤害技能的持续时间，攻击受到持续伤害或处于昏迷、冻结、暗黑、麻痹状态的敌人，可以增加伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                dotTime += eff.getX();
                dot += eff.getZ();
            }
            bx = SkillFactory.getSkill(火毒.魔力激化); //2110001 - 魔力激化 - 消耗更多的MP，提高所有攻击魔法的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getCostMpRate();
                percent_damage += eff.getDAMRate();
            }
            bx = SkillFactory.getSkill(火毒.迷雾爆发); //迷雾爆发 - 可以永久性地增加致命毒雾的持续伤害，使用技能时，设置在周围的致命毒雾爆炸，给敌人造成致命伤害。对象所中的持续伤害效果越多，造成的伤害越大。无法引爆其他人设置的毒雾。根据持续伤害效果的数量，伤害的增加幅度不超过5次。\n需要技能：#c致命毒雾20级以上#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addDamageIncrease(火毒.致命毒雾, eff.getX()); //致命毒雾 - 一定时间内在自己周围生成致命的毒雾，使所有敌人中毒。
            }
            bx = SkillFactory.getSkill(火毒.魔力精通); //2120012 - 魔力精通 - 永久性增加魔力，增加对自己使用的所有增益的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();//增加魔力
                BuffUP_Skill += eff.getBuffTimeRate();
            }
            bx = SkillFactory.getSkill(火毒.神秘瞄准术); //2120010 - 神秘瞄准术 - 攻击时可以无视怪物的部分防御力，持续攻击时提升所有攻击的伤害。有一定概率发动伤害提升效果，最多可以累积5次。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getX() * eff.getY();
                percent_boss_damage_rate += eff.getX() * eff.getY();
                percent_ignore_mob_def_rate += eff.getIgnoreMob();
            }
            /*
                 * 火毒超级技能
             */
            bx = SkillFactory.getSkill(火毒.美杜莎之眼_强化); //2120046 - 美杜莎之眼-强化 - 增加美杜莎之眼的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(火毒.美杜莎之眼, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(火毒.美杜莎之眼_额外攻击); //2120048 - 美杜莎之眼-额外攻击  - 增加美杜莎之眼的攻击次数
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(火毒.美杜莎之眼, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(火毒.迷雾爆发_额外攻击); //2120049 - 迷雾爆发-额外攻击 - 增加迷雾爆发的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(火毒.迷雾爆发, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(火毒.迷雾爆发_无视防御); //2120050 - 迷雾爆发-无视防御 - 提高迷雾爆发的无视怪物防御力效果。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(火毒.迷雾爆发, bx.getEffect(bof).getIgnoreMob());
            }
            bx = SkillFactory.getSkill(火毒.迷雾爆发_缩短冷却时间); //2120051 - 迷雾爆发-缩短冷却时间 - 减少迷雾爆发的冷却时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(火毒.迷雾爆发, bx.getEffect(bof).getCooltimeReduceR());
            }
        }
    }


    /*
         * ---------------------------------
         * 黑骑士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_heiqishi(MapleCharacter chra) {
        
        if (MapleJob.is黑骑士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            bx = SkillFactory.getSkill(黑骑士.物理训练); //1300009 - 物理训练 - 通过身体锻炼，永久性地提高力量和敏捷。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(黑骑士.抵抗力); //1310010 - 抵抗力 - 强化自己对异常状态的抗性。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                ASR += bx.getEffect(bof).getAsrR();
                TER += bx.getEffect(bof).getTerR();
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗至尊); //1310009 - 黑暗至尊 - 增加暴击率、暴击最小伤害，有一定几率将伤害的一部分转换成体力。但是，#c不能超过最大HP的一半。#
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                inc_Cr += eff.getCr();//增加暴击率
                critical_damage_min += eff.getCriticaldamageMin();//爆击最小伤害
                hpRecoverProp += eff.getProp();
                hpRecover_Percent += eff.getX();
            }
            bx = SkillFactory.getSkill(黑骑士.龙之献祭); //1321015 - 龙之献祭 - 吸收灵魂助力可以恢复体力，在一定时间内可以获得防御无视效果和BOSS攻击时伤害增加的效果，神枪降临技能不会冷却。另外会增加#c永久防御无视效果#。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMobpdpR();//无视怪x%防御
            }
            /*
                 * 黑骑士超级技能
             */
            bx = SkillFactory.getSkill(黑骑士.神圣之火_坚持); //1320043 - 神圣之火-坚持 - 增加神圣之火的持续时间。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addBuffDuration(黑骑士.神圣之火, bx.getEffect(bof).getDuration());
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗力量_强化); //1320046 - 黑暗力量-强化 - 增加黑暗力量提高的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_damage_rate += eff.getDamage();
                percent_boss_damage_rate += eff.getDamage();
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗力量_爆击伤害); //1320047 - 黑暗力量-爆击伤害 - 增加黑暗力量提高的最小爆击伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                critical_damage_min += eff.getCriticaldamageMin(); //爆击最小伤害
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗力量_爆击率); //1320048 - 黑暗力量-爆击率 - 增加黑暗力量提高的爆击率。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                inc_Cr += eff.getCr(); //爆击概率
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗穿刺_强化); //1320049 - 黑暗穿刺-强化 - 增加黑暗穿刺的伤害。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addDamageIncrease(黑骑士.黑暗穿刺, bx.getEffect(bof).getDamR());
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗穿刺_无视防御); //1320050 - 黑暗穿刺-无视防御 - 增加黑暗穿刺无视怪物防御力的数值。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addIgnoreMobpdpRate(黑骑士.黑暗穿刺, bx.getEffect(bof).getIgnoreMobpdpR());
            }
            bx = SkillFactory.getSkill(黑骑士.黑暗穿刺_额外攻击); //1320051 - 黑暗穿刺-额外攻击 - 增加黑暗穿刺的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addAttackCount(黑骑士.黑暗穿刺, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 炎术士技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_yanshu(MapleCharacter chra) {
        
        if (MapleJob.is炎术士(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            /*
                 * 处理超级技能
             */
            bx = SkillFactory.getSkill(炎术士.轨道烈焰_灵魂攻击);  //12120045 - 轨道烈焰-灵魂攻击 - 轨道烈焰IV的伤害降为280%，但攻击次数增加。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                addAttackCount(炎术士.轨道烈焰_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰II_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰III_攻击, bx.getEffect(bof).getAttackCount());
                addAttackCount(炎术士.轨道烈焰IV_攻击, bx.getEffect(bof).getAttackCount());
            }
            bx = SkillFactory.getSkill(炎术士.灭绝之焰_额外攻击);  //12120046 - 灭绝之焰-额外攻击 - 增加灭绝之焰的攻击次数。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                addAttackCount(炎术士.灭绝之焰_LINK, bx.getEffect(bof).getAttackCount());
            }
        }
    }

    /*
         * ---------------------------------
         * 剑豪技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_jianhao(MapleCharacter chra) {
        
        if (MapleJob.is剑豪(chra.getJob())) {
            Skill bx;
            int bof;
            MapleStatEffect eff;
            if (chra.getJob() >= 4111) {
                bx = SkillFactory.getSkill(剑豪.迅速);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    dodgeChance += eff.getPercentAvoid();//回避率
                }
            }
            if (chra.getJob() >= 4110) {
                bx = SkillFactory.getSkill(剑豪.秘剑_斑鸠);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
//                        accuracy += eff.getX();//命中,官方已删
                    critical_damage_max += eff.getCriticaldamageMax();//爆击最大伤害
                    critical_damage_min += eff.getCriticaldamageMin();//爆击最小伤害
                }
            }
            if (chra.getJob() >= 4100) {
                bx = SkillFactory.getSkill(剑豪.剑豪道);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();//总力量
                    localdex += eff.getDexX();//总敏捷
                }
            }
            bx = SkillFactory.getSkill(剑豪.神速无双_连击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addAttackCount(剑豪.神速无双, eff.getAttackCount());//攻击次数
            }
            bx = SkillFactory.getSkill(剑豪.瞬杀_连击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addAttackCount(剑豪.瞬杀, eff.getAttackCount());//攻击次数
            }
            bx = SkillFactory.getSkill(剑豪.瞬杀_散击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addTargetPlus(剑豪.瞬杀, eff.getTargetPlus());//增加技能攻击怪物的数量
            }
            bx = SkillFactory.getSkill(剑豪.一闪_连击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addAttackCount(剑豪.一闪, eff.getAttackCount());//攻击次数
            }
            bx = SkillFactory.getSkill(剑豪.一闪_即击);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                addCoolTimeReduce(剑豪.一闪, bx.getEffect(bof).getCoolTimeR());//冷却时间(减少)
            }
        }
    }


    /*
         * ---------------------------------
         * 冒险家新手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_smaoxianjia(MapleCharacter chra) {
        Skill bx;
        int bof;
        MapleStatEffect eff;
        if (MapleJob.is冒险家(chra.getJob())) {
            bx = SkillFactory.getSkill(74); //女皇的强化 - 可以装备比自己等级高的装备
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(80); //女皇的强化 - 可以装备比自己等级高的装备
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(10074); //未知 - 可以装备比自己等级高的装备
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(10080); //未知 - 可以装备比自己等级高的装备
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(110); //海盗祝福 - [种族特性技能]强化火炮手特有的坚韧，永久提高各种属性
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
            }
            bx = SkillFactory.getSkill(10110); //未知 - 永久提高各种属性
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }
        }
        switch (chra.getJob()) {
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132: {
                bx = SkillFactory.getSkill(战士.圣甲术); //1001003 - 圣甲术 - 在一定时间内增加自身的物理防御力，永久增加自身一定比率的最大体力，受到一定范围内的攻击时，减少所受的伤害。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(战士.战士精通); //1000009 - 战士精通 - 提高战士的基本素养增加移动速度和跳跃力、最大体力、最大移动速度。受到敌人攻击时，有一定的几率不会被击退。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    addmaxhp += eff.getLevelToMaxHp() * chra.getLevel(); //等级乘以倍率
                    jump += eff.getPassiveJump(); //移动速度上限
                    speedMax += eff.getSpeedMax(); //跳跃力
                }
                break;
            }
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232: {
                bx = SkillFactory.getSkill(魔法师.MP增加); //2000006 - MP增加 - 永久增加最大MP，根据等级MP也会额外的增加。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_mp += eff.getPercentMP(); //最大Mp增加
                    addmaxmp += eff.getLevelToMaxMp() * chra.getLevel(); //等级乘以倍率
                }
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322: {
                bx = SkillFactory.getSkill(弓箭手.弓箭手精通); //弓箭手精通 - 熟习弓箭手的基本技能。提高命中值、回避值、射程、移动速度、最大移动速度上限。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    defRange += eff.getRange();
//                    accuracy += eff.getAcc();
                }
                break;
            }
            case 400:
            case 410:
            case 411:
            case 412:
            case 420:
            case 421:
            case 422:
            case 431:
            case 432:
            case 433:
            case 434: {
                bx = SkillFactory.getSkill(飞侠.增益偷取); //4000010 - 增益偷取 - (无描述)
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ASR += eff.getASRRate();
                }
                bx = SkillFactory.getSkill(飞侠.轻功); //4001005 - 轻功 - 在一定时间内增加全体队员的移动速度和跳跃力，移动速度永久提高。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    speed += eff.getSpeed();
                    speedMax += eff.getSpeedMax();
                }
                bx = SkillFactory.getSkill(飞侠.侧移); //4000012 - 侧移 - 永久提高回避率。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                break;
            }
            case 500:
            case 510:
            case 511:
            case 512:
            case 520:
            case 521:
            case 522: {
                bx = SkillFactory.getSkill(海盗.快动作); //5000000 - 快动作 - 永久增加命中值、移动速度上限、跳跃力。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
//                    accuracy += eff.getAccX(); //命中值
                    jump += eff.getPassiveJump(); //移动速度上限
                    speedMax += eff.getSpeedMax(); //跳跃力
                }
                break;
            }
            case 2003: {
                bx = SkillFactory.getSkill(幻影.灵敏身手); //20030206 - 灵敏身手 - 幻影拥有卓越的洞察力和使用各种武器的能力。
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && bx != null) {
                    eff = bx.getEffect(bof);
                    localdex += eff.getDexX();
                    dodgeChance += eff.getER();
                    chra.getTrait(MapleTraitType.insight).setLevel(20, chra);
                    chra.getTrait(MapleTraitType.craft).setLevel(20, chra);
                }
                break;
            }
        }
    }

    /*
         * ---------------------------------
         * 骑士团新手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_qishituan(MapleCharacter chra) {
        Skill bx;
        int bof;
        MapleStatEffect eff;
        if (MapleJob.is骑士团(chra.getJob())) {
            bx = SkillFactory.getSkill(10000074); //女皇的呼唤 - 学习技能后，由于女皇的呼唤，最大HP和最大MP永久增加。
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                addmaxhp += eff.getX();
                addmaxmp += eff.getX();
            }
            bx = SkillFactory.getSkill(初心者.元素和声_力量);
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localstr += chra.getLevel() / 2;
            }
            bx = SkillFactory.getSkill(初心者.元素和声_敏捷);
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localdex += chra.getLevel() / 2;
            }
            bx = SkillFactory.getSkill(初心者.元素和声_智力);
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localint += chra.getLevel() / 2;
            }
            bx = SkillFactory.getSkill(初心者.元素和声_运气);
            bof = chra.getSkillLevel(bx);
            if (bof > 0 && bx != null) {
                localluk += chra.getLevel() / 2;
            }
        }
    }

    /*
         * ---------------------------------
         * 新手技能和特殊技能
         * ---------------------------------
     */
    private void handlePassiveSkill_sxinshou(MapleCharacter chra) {
        Skill bx;
        int bof;
        MapleStatEffect eff;
        
        bx = SkillFactory.getSkill(初心者.海盗祝福);
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getHpR();
            percent_mp += eff.getMpR();
        }
        bx = SkillFactory.getSkill(冲锋队长.HP增加); //5100009 - HP增加 - 通过锻炼身体永久性地增加最大HP。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null) {
            percent_hp += bx.getEffect(bof).getPercentHP();
        }
        bx = SkillFactory.getSkill(初心者.恶魔之怒);
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            percent_boss_damage_rate += eff.getBossDamage();
        }
        if (MapleJob.is反抗者(chra.getJob())) {
            bx = SkillFactory.getSkill(预备兵.效率提升); //效率提升 - 为了解决物资缺乏问题，学习高效使用药水的方法。
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                RecoveryUP += bx.getEffect(bof).getX() - 100;
            }
        }
        
        bx = SkillFactory.getSkill(80000000); //海盗祝福 - [链接技能]学习火炮手特有的强韧，永久性地提高各种属性。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getPercentHP();
            percent_mp += eff.getPercentMP();
        }
        bx = SkillFactory.getSkill(80000001); //恶魔之怒 - [链接技能]对象是BOSS怪时，唤醒内心的愤怒，造成更强的伤害。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            percent_boss_damage_rate += bx.getEffect(bof).getBossDamage();
        }
        bx = SkillFactory.getSkill(80000002); //致命本能 - 拥有通过卓越的洞察力，找到敌人致命弱点的本能。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            passive_sharpeye_rate += bx.getEffect(bof).getCritical();
        }
        bx = SkillFactory.getSkill(80000005); //穿透 - 用穿透一切阻碍的光之力量，无视敌人的部分防御力。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            percent_ignore_mob_def_rate += bx.getEffect(bof).getIgnoreMob();
        }
        bx = SkillFactory.getSkill(80000006); //钢铁之墙 - 具有比狂龙战士更出色的体力。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            percent_hp += bx.getEffect(bof).getPercentHP();
        }
        int[] skillIds = {
            80000007, //80000007 - Lv.1 魔族的高贵祝福 - 用魔族的气息提升自己的物理攻击力和魔法攻击力各3点。
            80000008, //80000008 - Lv.2 魔族的高贵祝福 - 用魔族的气息提升自己的物理攻击力和魔法攻击力各5点。
            80000009, //80000009 - Lv.3 魔族的高贵祝福 - 用魔族的气息提升自己的物理攻击力和魔法攻击力各7点。
            80000013, //80000013 - Lv.1 鲨鱼的气息 - 通过鲨鱼的气息，将自身的物理攻击力和魔法攻击力各提升3
            80000014, //80000014 - Lv.2 鲨鱼的气息 - 通过鲨鱼的气息，将自身的物理攻击力和魔法攻击力各提升5
            80000015, //80000015 - Lv.3 鲨鱼的气息 - 通过鲨鱼的气息，将自身的物理攻击力和魔法攻击力各提升7
            80000017, //80000017 - Lv.1 品克缤狂人 - 通过品克缤的可爱，将自身的物理攻击力和魔法攻击力各提升3
            80000018, //80000018 - Lv.2 品克缤狂人 - 通过品克缤的可爱，将自身的物理攻击力和魔法攻击力各提升5
            80000019, //80000019 - Lv.3 品克缤狂人 - 通过品克缤的可爱，将自身的物理攻击力和魔法攻击力各提升7
            80000020, //80000020 - Lv.1 龙的惊人祝福 - 通过龙的气息，将自身的物理攻击力和魔法攻击力各提升3
            80000021, //80000021 - Lv.2 龙的惊人祝福 - 通过龙的气息，将自身的物理攻击力和魔法攻击力各提升5
            80000022, //80000022 - Lv.3 龙的惊人祝福 - 通过龙的气息，将自身的物理攻击力和魔法攻击力各提升7
            80000026, //80000026 - Lv.1 因为熊 - 因为熊～因为熊～增益是因为熊！使自己的物理攻击力和魔法攻击力各提高3点。
            80000027, //80000027 - Lv.2 因为熊 - 因为熊～因为熊～增益是因为熊！使自己的物理攻击力和魔法攻击力各提高5点。
            80000028, //80000028 - Lv.3 因为熊 - 因为熊～因为熊～增益是因为熊！使自己的物理攻击力和魔法攻击力各提高7点。
            80000029, //80000029 - Lv.1 南瓜魔法 - 惊人的南瓜魔法！使自己的物理攻击力和魔法攻击力各提高3点。
            80000030, //80000030 - Lv.2 南瓜魔法 - 惊人的南瓜魔法！使自己的物理攻击力和魔法攻击力各提高5点。
            80000031, //80000031 - Lv.3 南瓜魔法 - 惊人的南瓜魔法！使自己的物理攻击力和魔法攻击力各提高7点。
            80000056, //80000056 - Lv.1 考拉的声援 - 可爱的考拉的声援！使自己的物理攻击力和魔法攻击力各增加3。
            80000057, //80000057 - Lv.2 考拉的声援 - 可爱的考拉的声援！使自己的物理攻击力和魔法攻击力各增加5。
            80000058, //80000058 - Lv.3 考拉的声援 - 可爱的考拉的声援！使自己的物理攻击力和魔法攻击力各增加7。
            80000063, //80000063 - Lv.1 松鼠变强 - 和可爱的松鼠一起变强吧！使自己的物理攻击力和魔法攻击力各增加4。
            80000064, //80000064 - Lv.2松鼠变强 - 和可爱的松鼠一起变强吧！使自己的物理攻击力和魔法攻击力各增加6。
            80000065, //80000065 - Lv.3松鼠变强 - 和可爱的松鼠一起变强吧！使自己的物理攻击力和魔法攻击力各增加8。
            80000072, //80000072 - Lv.1 New魔族的高贵祝福 - 由于魔族的气息，自己的物理攻击力和魔法攻击力各增加3。
            80000073, //80000073 - Lv.2 New魔族的高贵祝福 - 由于魔族的气息，自己的物理攻击力和魔法攻击力各增加5。
            80000074, //80000074 - Lv.3 New魔族的高贵祝福 - 由于魔族的气息，自己的物理攻击力和魔法攻击力各增加7。
            80000077, //80000077 - Lv.1 天使的神圣祝福 - 身体被天使神圣的气息所包围。自己的物理攻击力和魔法攻击力各提高3。
            80000078, //80000078 - Lv.2 天使的神圣祝福 - 身体被天使的神圣气息所包围。自己的物理攻击力和魔法攻击力各提高5。
            80000079, //80000079 - Lv.3 天使的神圣祝福 - 身体被天使的神圣气息所包围。自己的物理攻击力和魔法攻击力各提高7。
            80000081, //80000081 - Lv.1 鲁提鲁提 - 让人无法抗拒的鲁提的可爱俏皮。自己的物理攻击力和魔法攻击力各提高3。
            80000082, //80000082 - Lv.2 鲁提鲁提 - 让人无法抗拒的鲁提的可爱俏皮。自己的物理攻击力和魔法攻击力各提高5。
            80000083, //80000083 - Lv.3 鲁提鲁提 - 让人无法抗拒的鲁提的可爱俏皮。自己的物理攻击力和魔法攻击力各提高7。
            80000098, //80000098 - Lv.1 邪恶气息 - 身体被恶魔的邪恶气息所包围。自己的物理攻击力和魔法攻击力各提高3。
            80000099, //80000099 - Lv.2 邪恶气息 - 身体被恶魔的邪恶气息所包围。自己的物理攻击力和魔法攻击力各提高5。
            80000100, //80000100 - Lv.3 邪恶气息 - 身体被恶魔的邪恶气息所包围。自己的物理攻击力和魔法攻击力各提高7。
            80000101, //80000101 - Lv.1 难以置信！ - 鲁塔比斯的巨大气息袭来。自己的物理攻击力和魔法攻击力各提高3。
            80000102, //80000102 - Lv.2 难以置信！ - 鲁塔比斯的巨大气息袭来。自己的物理攻击力和魔法攻击力各提高5。
            80000103, //80000103 - Lv.3 难以置信！ - 鲁塔比斯的巨大气息袭来。自己的物理攻击力和魔法攻击力各提高7。
            80000111, //80000111 - Lv.1 身强力壮的铠鼠 - 身强力壮的铠鼠今天又来了。自己的物理攻击力和魔法攻击力各提高3。
            80000112, //80000112 - Lv.2 身强力壮的铠鼠 - 身强力壮的铠鼠今天又来了。自己的物理攻击力和魔法攻击力各提高5。
            80000113, //80000113 - Lv.3 身强力壮的铠鼠 - 身强力壮的铠鼠今天又来了。自己的物理攻击力和魔法攻击力各提高7。
            80000120, //80000120 - Lv.1 布迪军团长宠物 - 见识一下布迪军团长的力量。自己的物理攻击力和魔法攻击力各提高3。
            80000121, //80000121 - Lv.2 布迪军团长宠物 - 见识一下布迪军团长的力量。自己的物理攻击力和魔法攻击力各提高5。
            80000122, //80000122 - Lv.3 布迪军团长宠物 - 见识一下布迪军团长的力量。自己的物理攻击力和魔法攻击力各提高7。
        };
        for (int i : skillIds) {
            bx = SkillFactory.getSkill(i);
            if (bx != null) {
                bof = chra.getSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();//增加物理攻击力
                    magic += eff.getMagicX();//增加魔法攻击力
                }
            }
        }
        bx = SkillFactory.getSkill(80000025); //国庆闪光肩饰 - 可以激发国庆节特殊属性。
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getHpR();
            percent_mp += eff.getMpR();
            jump += eff.getPassiveJump();
            speed += eff.getPassiveSpeed();
        }
        bx = SkillFactory.getSkill(80000050); //80000050 - 野性狂怒 - 由于愤怒，伤害增加。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            percent_damage += eff.getDAMRate();//攻击增加 也就是角色面板的攻击数字增加x%
        }
        bx = SkillFactory.getSkill(80001040); //精灵的祝福 - [链接技能]获得古代精灵的祝福，可以回到埃欧雷去，经验值获得量永久提高
        bof = chra.getSkillLevel(bx);
        if (bof > 0 && bx != null) { //去掉这个
            //expBuff *= (bx.getEffect(bof).getEXPRate() + 100.0) / 100.0;
        }
        bx = SkillFactory.getSkill(80000047); //80000047 - 混合逻辑 - 采用混合逻辑设计，所有能力值永久提高。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            localstr += (eff.getStrRate() / 100.0) * str;
            localdex += (eff.getDexRate() / 100.0) * dex;
            localluk += (eff.getLukRate() / 100.0) * luk;
            localint += (eff.getIntRate() / 100.0) * int_;
        }
        bx = SkillFactory.getSkill(80010006); //80010006 - 精灵集中 - 攻击BOSS怪时,精灵之力会更强。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null) {
            eff = bx.getEffect(bof);
            percent_hp += eff.getPercentHP();
            percent_mp += eff.getPercentMP();
            passive_sharpeye_rate += eff.getCritical();
            percent_boss_damage_rate += eff.getBossDamage();
        }
        
        bx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            inc_PAD += eff.getX();
            mdef += eff.getY();
        }
        bx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            inc_PAD += eff.getX();
            mdef += eff.getY();
        }

        /*
         * 超级技能加属性
         */
        bx = SkillFactory.getSkill(80000400); //STR - 提高力量（STR）。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            localstr += bx.getEffect(bof).getStrX();
        }
        bx = SkillFactory.getSkill(80000401); //DEX - 提高敏捷（DEX）。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            localdex += bx.getEffect(bof).getDexX();
        }
        bx = SkillFactory.getSkill(80000402); //INT - 提高智力（INT）。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            localint += bx.getEffect(bof).getIntX();
        }
        bx = SkillFactory.getSkill(80000403); //LUK - 提高运气（LUK）。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            localluk += bx.getEffect(bof).getLukX();
        }
        bx = SkillFactory.getSkill(80000404); //HP - 提高最大HP。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            percent_hp += bx.getEffect(bof).getPercentHP();
        }
        bx = SkillFactory.getSkill(80000405); //MP - 提高最大MP。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            if (!MapleJob.is恶魔猎手(chra.getJob())) {
                percent_mp += bx.getEffect(bof).getPercentMP();
            }
        }
        bx = SkillFactory.getSkill(80000406); //DF - 提高最大恶魔精气。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            incMaxDF += bx.getEffect(bof).getIndieMaxDF();
        }
        bx = SkillFactory.getSkill(80000407); //移动速度 - 提高移动速度。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            speed += bx.getEffect(bof).getPassiveSpeed();
        }
        bx = SkillFactory.getSkill(80000408); //跳跃力 - 提高跳跃力。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            jump += bx.getEffect(bof).getPassiveJump();
        }
        bx = SkillFactory.getSkill(80000409); //爆击发动 - 提高爆击率。
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0 && bx != null && bx.isHyperStat()) {
            passive_sharpeye_rate += bx.getEffect(bof).getCritical();
        }

        /*
         * 精灵的祝福 和 女皇的祝福 属性加成
         * 精灵的祝福和女皇的祝福只发动等级更高的效果。
         */
        Skill skillBof = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        Skill skillEmpress = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        if (chra.getSkillLevel(skillBof) > 0 || chra.getSkillLevel(skillEmpress) > 0) {
            if (chra.getSkillLevel(skillBof) > chra.getSkillLevel(skillEmpress)) { //启用 精灵的祝福属性加成
                eff = skillBof.getEffect(chra.getSkillLevel(skillBof));
                watk += eff.getX();
                magic += eff.getY();
//                accuracy += eff.getX();命中已删
            } else {  //启用 女皇的祝福属性加成
                eff = skillEmpress.getEffect(chra.getSkillLevel(skillEmpress));
                watk += eff.getX();
                magic += eff.getY();
//                accuracy += eff.getX();命中已删
            }
        }
    }
    
    public static int getHyperSkillByJob(int skillId, int job) {
        switch (job) {
            case 2217:
            case 2218:
                return 22170000 + skillId;
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 434:
            case 512:
            case 522:
            case 532:
            case 1112:
            case 1312:
            case 1512:
            case 2112:
            case 2312:
            case 2412:
            case 2712:
            case 3112:
            case 3122:
            case 3212:
            case 3312:
            case 3512:
            case 3612:
            case 5112:
            case 6112:
            case 6512:
                return job * 10000 + skillId;
        }
        return skillId;
    }
    
    private void handleHyperPassiveSkills(MapleCharacter chra) {
        int prefix = chra.getJob() * 10000;
        Skill bx;
        int bof;
        MapleStatEffect eff;
        for (int i = 80000400; i <= 80000417; i++) {
//            System.out.println("handleHyperPassiveSkills() :" + i);
            bx = SkillFactory.getSkill(i);
            bof = chra.getSkillLevel(bx);
            if (bx != null && bx.isHyperStat() && bof > 0) {
                eff = bx.getEffect(bof);
                if (eff != null) {
                    switch (i - 80000400) {
                        case 0:
                            inc_STR += bof * 15;
                            break;
                        case 1:
                            inc_DEX += bof * 15;
                            break;
                        case 2:
                            inc_INT += bof * 15;
                            break;
                        case 3:
                            inc_LUK += bof * 15;
                            break;
                        //case 4:已经在被动那里处理了
                        //    inc_maxhp_rate += eff.getMhpR();
                        //    break;
                        //case 5:已经在被动那里处理了
                        //    inc_maxmp_rate += eff.getMmpR();
                        //    break;
                        case 9:
                            inc_Cr += eff.getCr();
                            break;
                        case 10:
                            critical_damage_min += eff.getCriticaldamageMin();
                            break;
                        case 11:
                            critical_damage_max += eff.getCriticaldamageMax();
                            break;
                        case 12:
                            percent_ignore_mob_def_rate += eff.getIgnoreMobpdpR();
                            break;
                        case 13:
                            percent_damage_rate += eff.getDamR();
                            break;
                        case 14:
                            percent_boss_damage_rate += eff.getBdR();
                            break;
                        case 15:
                            TER += eff.getTerR();
                            break;
                        case 16:
                            ASR += eff.getAsrR();
                            break;
                    }
                }
            }
        }
        for (int i = 30; i < 50; i++) {
            bx = SkillFactory.getSkill(prefix + i);
            bof = chra.getSkillLevel(bx);
            if (bx != null && bx.isHyper() && bof > 0) {
                eff = bx.getEffect(bof);
                String name = bx.getName();
                if (eff != null && name != null) {
                    int skill = GameConstants.findSkillByName(name.split(" - ")[0], prefix, 0);
                    if (skill != 0) {
                        Skill skil = SkillFactory.getSkill(skill);
                        if (skil != null && chra.getSkillLevel(skil) > 0) {
                            if (eff.getDamR() > 0) {
                                damageIncrease.put(skil.getId(), eff.getDamR());
                            }
                        }
                    }
                }
            }
        }
//        for (Map.Entry<Skill, SkillEntry> skill_5th : chra.getSkills().entrySet()) {
//            if (skill_5th.getKey().getvSkill() != -1) {
//                bx = skill_5th.getKey();
//                bof = chra.getSkillLevel(bx);
//                if (bof > 20) {
//                    eff = SkillFactory.getEffectbyLevel(bx.getId(), bof);
//                    if (eff.getTargetPlus_5th() > 0) {
//                        addTargetPlus(SkillFactory.getFivethEnforceSkillRe().get(bx.getId()), eff.getTargetPlus_5th());
//                    }
//                }
//
//            }
//        }
    }

    /*
    BUFF被动技能处理
     */
    private void handleBuffStats(MapleCharacter chra) {
        MapleStatEffect eff;
        Integer buff;
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndiePad);
        if (buff != null) {
            inc_PAD += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieMad);
        if (buff != null) {
            mdef += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndiePdd);
        if (buff != null) {
            inc_PDD += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieMhp);
        if (buff != null) {
            addmaxhp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieMhpR);
        if (buff != null) {
            percent_hp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieMmp);
        if (buff != null) {
            addmaxmp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieMmpR);
        if (buff != null) {
            percent_mp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieAllStat);
        if (buff != null) {
            inc_STR += buff;
            inc_DEX += buff;
            inc_INT += buff;
            inc_LUK += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieAsrR);
        if (buff != null) {
            ASR += buff;
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_BMageAura);
        if (eff != null && eff.getSourceId() == 唤灵斗师.蓝色灵气) {
            ignoreDAMr += eff.getY();
            ignoreDAMr_prop = 100;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_AranDrain);
        if (buff != null) {
            hpRecoverProp = 100;
            hpRecoverPercent += buff;
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_Invincible);
        if (eff != null && eff.getSourceId() == 主教.神之保护) {
            ignoreDAMr += eff.getX();
            ignoreDAMr_prop = 100;
        } else if (eff != null && eff.getSourceId() == 龙神.魔法盾) {
            ignoreDAMr += eff.getY();
            ignoreDAMr_prop = 100;
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_DamAbsorbShield);
        if (eff != null && eff.getSourceId() == 尖兵.双重防御) {
            ignoreDAMr += eff.getZ();
            ignoreDAMr_prop = 100;
        } else if (eff != null) {
            ignoreDAMr += eff.getX();
            ignoreDAMr_prop = 100;
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_DiabolikRecovery);
        if (eff != null) {
            shouldHealHP += (eff.getX() * localmaxhp) / 100;
            hpRecoverTime = eff.getW();
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_MaxHP);
        if (buff != null) {
            percent_hp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_MaxMP);
        if (buff != null) {
            percent_mp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_Emhp);
        if (buff != null) {
            addmaxhp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_Emmp);
        if (buff != null) {
            addmaxmp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.HAYATO_HP_R);
        if (buff != null) {
            percent_hp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.HAYATO_MP_R);
        if (buff != null) {
            percent_mp += buff;
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_RideVehicle);
        if (eff != null && eff.getSourceId() == 豹弩游侠.美洲豹骑士) {
//            critical += eff.getW();
//            percent_hp += eff.getZ();
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_ReshuffleSwitch);
        if (eff != null && eff.getSourceId() == 狂龙战士.模式转换_防御模式) {
            inc_PDD += eff.getPddX();
            percent_hp += eff.getMhpR();
        } else if (eff != null && eff.getSourceId() == 狂龙战士.模式转换_攻击模式) {
            inc_PAD += eff.getPadX();
            inc_Cr += eff.getCr();
            percent_boss_damage_rate += eff.getBdR();
        }
        buff = chra.getBuffedValue(MapleBuffStat.幸运钱);
        if (buff != null) {
            incMaxDamage += buff * 10000000;
        }
        //幸运骰子
        buff = chra.getBuffedValue(MapleBuffStat.CS_Dice);
        if (buff != null) {
            percent_wdef += GameConstants.getDiceStat(buff, 2);
            percent_hp += GameConstants.getDiceStat(buff, 3);
            percent_mp += GameConstants.getDiceStat(buff, 3);
            inc_Cr += GameConstants.getDiceStat(buff, 4);
            percent_damage_rate *= (GameConstants.getDiceStat(buff, 5) + 100.0) / 100.0;
            percent_boss_damage_rate *= (GameConstants.getDiceStat(buff, 5) + 100.0) / 100.0;
            expBuff *= (GameConstants.getDiceStat(buff, 6) + 100.0) / 100.0;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_DdR);
        if (buff != null) {
            percent_wdef += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_AsrR);
        if (buff != null) {
            ASR += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_TerR);
        if (buff != null) {
            TER += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Infinity);
        if (buff != null) {
            mdefr += buff - 1;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_PVPDamage);
        if (buff != null) {
            pvpDamage += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_PVPDamageSkill);
        if (buff != null) {
            pvpDamage += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_HowlingMaxMP);
        if (buff != null) {
            percent_mp += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_Str);
        if (buff != null) {
            inc_STR += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Dex);
        if (buff != null) {
            inc_DEX += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Int);
        if (buff != null) {
            inc_INT += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Luk);
        if (buff != null) {
            inc_LUK += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_IndieAllStat);
        if (buff != null) {
            inc_STR += buff;
            inc_DEX += buff;
            inc_INT += buff;
            inc_LUK += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Epdd);
        if (buff != null) {
            inc_PDD += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_Pdd);
        if (buff != null) {
            inc_PDD += buff;
        }
        
        buff = chra.getBuffedValue(MapleBuffStat.CS_BasicStatUp);
        if (buff != null) {
            final double d = buff.doubleValue() / 100.0;
            inc_STR += d * str; //base only
            inc_DEX += d * dex;
            inc_INT += d * int_;
            inc_LUK += d * luk;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_MaxLevelBuff);
        if (buff != null) {
            final double d = buff.doubleValue() / 100.0;
            inc_PAD += (int) (inc_PAD * d);
            mdef += (int) (mdef * d);
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_ComboAbilityBuff);
        if (buff != null) {
            inc_PAD += buff / 10;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_ExpBuffRate);
        if (buff != null) {
            expBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_ItemUpByItem);
        if (buff != null) {
            incRewardProp *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_MesoUpByItem);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_MesoUp);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
//        buff = chra.getBuffedValue(MapleBuffStat.CS_Acc);
//        if (buff != null) {
//            wAccuracy += buff;
//        }

        buff = chra.getBuffedValue(MapleBuffStat.CS_Pad);
        if (buff != null) {
            inc_PAD += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_SpiritLink);
        if (buff != null) {
            inc_Cr += buff;
            percent_damage_rate *= (buff + 100.0) / 100.0;
            percent_boss_damage_rate *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Epad);
        if (buff != null) {
            inc_PAD += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_EnergyCharged);
        if (eff != null) {
            inc_PAD += eff.getPad();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_Mad);
        if (buff != null) {
            mdef += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_NoDebuff);
        if (eff != null) {
            inc_Cr = 100; //INTENSE
            ASR = 100; //INTENSE

            inc_PDD += eff.getX();
            inc_PAD += eff.getX();
            mdef += eff.getX();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_DamR);
        if (buff != null) {
            percent_damage_rate *= (buff.doubleValue() + 100.0) / 100.0;
            percent_boss_damage_rate *= (buff.doubleValue() + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.CS_FinalCut);
        if (buff != null) {
            percent_damage_rate *= buff.doubleValue() / 100.0;
            percent_boss_damage_rate *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.CS_DojangBerserk);
        if (buff != null) {
            percent_damage_rate *= buff.doubleValue() / 100.0;
            percent_boss_damage_rate *= buff.doubleValue() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_Bless);
        if (eff != null) {
            inc_PAD += eff.getX();
            mdef += eff.getY();
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.CS_Concentration);
        if (buff != null) {
            mpconReduce += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_AdvancedBless);
        if (eff != null) {//进阶祝福
            inc_PAD += eff.getX();
            mdef += eff.getY();
            mpconReduce += eff.getMPConReduce();
//            addmaxhp += buff;
//            addmaxmp += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_MagicResistance);
        if (eff != null) {
            ASR += eff.getX();
        }
        
        eff = chra.getStatForBuff(MapleBuffStat.CS_ComboCounter);
        buff = chra.getBuffedValue(MapleBuffStat.CS_ComboCounter);
        if (eff != null && buff != null) {
            percent_damage_rate *= ((100.0 + ((eff.getV() + eff.getDamR()) * (buff - 1))) / 100.0);
            percent_boss_damage_rate *= ((100.0 + ((eff.getV() + eff.getDamR()) * (buff - 1))) / 100.0);
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_Beholder);
        if (eff != null) {
            trueMastery += eff.getMastery();
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_Mechanic);
        if (eff != null) {
            inc_Cr += eff.getCr();
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_RepeatEffect);
        if (eff != null && eff.getBerserk() > 0) {
            percent_damage_rate *= eff.getBerserk() / 100.0;
            percent_boss_damage_rate *= eff.getBerserk() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_DamR);
        if (eff != null) {
            percent_damage_rate *= (eff.getDamR() + 100.0) / 100.0;
            percent_boss_damage_rate *= (eff.getDamR() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_AssistCharge);
        if (eff != null) {
            percent_damage_rate *= eff.getDamage() / 100.0;
            percent_boss_damage_rate *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.HIDE_ATTACK);
        if (eff != null) {
            percent_damage_rate *= eff.getDamage() / 100.0;
            percent_boss_damage_rate *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_BlessingArmor);
        if (eff != null) {
            inc_PAD += eff.getEpad();
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.CS_DarkSight);
        if (buff != null) {
            percent_damage_rate *= (buff + 100.0) / 100.0;
            percent_boss_damage_rate *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.CS_Enrage);
        if (buff != null) {
            percent_damage_rate *= (buff + 100.0) / 100.0;
            percent_boss_damage_rate *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.CS_CombatOrders);
        if (buff != null) {
            combatOrders += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_SharpEyes);
        if (eff != null) {
            inc_Cr += eff.getX();
            critical_damage_max += eff.getCriticaldamageMax();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CS_SharpEyes);
        if (buff != null) {
            inc_Cr += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.CS_DispelItemOptionByField);
        if (eff != null) {
            inc_PAD = Integer.MAX_VALUE;
        }
    }
    
    public boolean checkEquipLevels(final MapleCharacter chr, long gain) {
        if (chr.isClone()) {
            return false;
        }
        boolean changed = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        List<Equip> seal = new ArrayList<>();
        for (Equip eq : all) {
            if (ItemConstants.类型.漩涡装备(eq.getItemId())) {
                seal.add(eq);
                continue;
            }
            int lv = eq.getEquipLevel();

//            eq.setItemEXP(Math.min(eq.getItemEXP() + gain, Long.MAX_VALUE));
            eq.setEXP64(Math.min(eq.getEXP64() + gain, Long.MAX_VALUE));
//            if (ItemConstants.类型.漩涡装备(eq.getItemId())) {
////                seal.add(new ModifyInventory(4, eq)); //刷新装备经验
//                chr.getClient().announce(CWvsContext.InventoryPacket.updateInventory(true, mods, chr));
//            }
            if (eq.getEXP64() > 8877400) {//8877400
                eq.setLevel((byte) (eq.getLevel() + 1));
                eq.setEXP64(0);
                
                for (int i = eq.getLevel(); i > 0; i--) {
                    final Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    //世界树的祝福卷轴
                    int extra = eq.getGrowthEnchant();
                    switch (extra) {
                        case 1:
                            inc.get(lv + i).put("incSTRMin", 1);
                            inc.get(lv + i).put("incSTRMax", 3);
                            break;
                        case 2:
                            inc.get(lv + i).put("incDEXMin", 1);
                            inc.get(lv + i).put("incDEXMax", 3);
                            break;
                        case 3:
                            inc.get(lv + i).put("incINTMin", 1);
                            inc.get(lv + i).put("incINTMax", 3);
                            break;
                        case 4:
                            inc.get(lv + i).put("incLUKMin", 1);
                            inc.get(lv + i).put("incLUKMax", 3);
                            break;
                        default:
                            break;
                    }
                    // inc.get(lvlz + i) 挑选和等级匹配的成长属性
                    if (inc != null && inc.containsKey(lv)) {
                        eq = ii.levelUpEquip(eq, inc.get(lv));
                    }
                    //检测装备是否可以获得技能
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) == null && GameConstants.getMaxLevel(eq.getItemId()) < (lv + i) && Math.random() < 0.1 && eq.getLevelUpType() <= 0 && ii.getEquipSkills(eq.getItemId()) != null) {
                        for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                            final Skill skil = SkillFactory.getSkill(zzz);
                            if (skil != null && skil.canBeLearnedBy(chr.getJob())) { //dont go over masterlevel :D
                                eq.setLevelUpType((byte) 1);
                                chr.dropMessage(5, "获得技能 " + skil.getName() + "");
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        checkEquipSealed(chr, gain, seal);//导致盾牌经验无限上升
        if (changed) {
            chr.equipChanged();
            chr.getClient().announce(EffectPacket.effect_ItemLevelup());
            chr.getMap().broadcastMessage(chr, EffectPacket.effect_ItemLevelup(chr), false);
        }
        return changed;
    }
    
    public void checkEquipSealed(MapleCharacter chr, long gain, List<Equip> eq) {
//        List<Equip> all = new ArrayList<>();
        List<ModifyInventory> mods = new ArrayList<>();
        for (Equip eqq : eq) {
            if (eqq != null && !GameConstants.canSealedLevelUp(eqq.getItemId(), eqq.getLevel(), eqq.getEXP64())) {
                eqq.gainSealedExp(gain);
                mods.add(new ModifyInventory(4, eqq)); //删除装备
            }
        }
        chr.getClient().announce(InventoryPacket.onInventoryOperation(true, mods, chr));
    }

//    public void checkEquipSealed(final MapleCharacter chr, long gain, List<Equip> eq) {
//        List<ModifyInventory> mods = new ArrayList<>();
//        for (Equip eqq : eq) {
//            if (eqq != null && eqq.getEXP64() < GameConstants.getExpForLevel(eqq.getLevel(), eqq.getItemId())) {
//                eqq.setEXP64(eqq.getEXP64() + 500000);
//                mods.add(new ModifyInventory(4, eqq)); //刷新装备经验
//            }
//        }
//        chr.getClient().announce(InventoryPacket.onInventoryOperation(true, mods, chr));
//    }
    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain) {
        return checkEquipDurabilitys(chr, gain, false);
    }
    
    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain, boolean aboveZero) {
        if (chr.isClone() || chr.inPVP()) {
            return true;
        }
        List<Equip> all = new ArrayList<>(durabilityHandling);
        for (Equip item : all) {
            if (item != null && ((item.getPosition() >= 0) == aboveZero)) {
                item.setDurability(item.getDurability() + gain);
                if (item.getDurability() < 0) { //shouldnt be less than 0
                    item.setDurability(0);
                }
            }
        }
        for (Equip eqq : all) {
            if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) { //> 0 went to negative
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
//                    chr.getClient().announce(InventoryPacket.getInventoryFull());
//                    chr.getClient().announce(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                durabilityHandling.remove(eqq);
                final short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }
    
    private void CalcPassive_trueMastery(final MapleCharacter player) {
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11) == null) {
            passive_trueMastery = 0;
            return;
        }
        final int skil;
        final MapleWeaponType weaponType = ItemConstants.武器类型(player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11).getItemId());
        boolean acc = true;
        switch (weaponType) {
            case 弓:
                skil = MapleJob.is骑士团(player.getJob()) ? 13100000 : 3100000;
                break;
            case 拳套:
                skil = MapleJob.is骑士团(player.getJob()) ? 14100000 : 4100000;
                break;
            case 手杖:
                skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
                break;
            case 加农炮:
                skil = 5300005;
                break;
            case 短剑:
            case 双刀:
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            case 弩:
                skil = MapleJob.is反抗者(player.getJob()) ? 33100000 : 3200000;
                break;
            case 单手斧:
            case 单手棍:
                skil = MapleJob.is反抗者(player.getJob()) ? 31100004 : (MapleJob.is骑士团(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000)); //hero/pally
                break;
            case 双手斧:
            case 单手剑:
            case 双手剑:
            case 双手棍:
                skil = MapleJob.is骑士团(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000); //hero/pally
                break;
            case 矛:
                skil = MapleJob.is战神(player.getJob()) ? 21100000 : 1300000;
                break;
            case 枪:
                skil = 1300000;
                break;
            case 指虎:
                skil = MapleJob.is骑士团(player.getJob()) ? 15100001 : 5100001;
                break;
            case 火枪:
                skil = MapleJob.is反抗者(player.getJob()) ? 35100000 : (MapleJob.is龙的传人(player.getJob()) ? 5700000 : 5200000);
                break;
            case 双弩枪:
                skil = 23100005;
                break;
            case 长杖:
            case 短杖:
                acc = false;
                skil = MapleJob.is反抗者(player.getJob()) ? 32100006 : (player.getJob() <= 212 ? 2100006 : (player.getJob() <= 222 ? 2200006 : (player.getJob() <= 232 ? 2300006 : (player.getJob() <= 2000 ? 12100007 : 22120002))));
                break;
            default:
                passive_trueMastery = 0;
                return;
        }
        if (player.getSkillLevel(skil) <= 0) {
            passive_trueMastery = 0;
            return;
        }
        final MapleStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
        if (acc) {
            if (skil == 35100000) {
                inc_PAD += eff.getX();
            }
        } else {
            mdef += eff.getX();
        }
        inc_Cr += eff.getCr();
        passive_trueMastery = (byte) eff.getMastery();
        trueMastery += eff.getMastery() + weaponType.getBaseMastery();
    }
    
    private void calculateFame(final MapleCharacter player) {
        player.getTrait(MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            player.getTrait(t).recalcLevel();
        }
    }
    
    public final short criticalDamageMin() {
        return critical_damage_min;
    }
    
    public final short criticalDamageMax() {
        return critical_damage_max;
    }
    
    public final short passive_sharpeye_rate() {
        return passive_sharpeye_rate;
    }
    
    public final byte passive_trueMastery() {
        return passive_trueMastery; //* 5 + 10 for trueMastery %
    }
    
    public final int trueMastery() {
        return trueMastery; //* 5 + 10 for trueMastery %
    }
    
    public final void calculateMaxBaseDamage(final int watk, final int pvpDamage, MapleCharacter chra) {
        if (watk <= 0) {
            localmaxbasedamage = 1;
            localmaxbasepvpdamage = 1;
        } else {
            final Item weapon = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            final Item shield = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
            final int job = chra.getJob();
            final MapleWeaponType weaponType = weapon == null ? MapleWeaponType.没有武器 : ItemConstants.武器类型(weapon.getItemId());
            final MapleWeaponType weapon2 = shield == null ? MapleWeaponType.没有武器 : ItemConstants.武器类型(shield.getItemId());
            int stat, statpvp;
            final boolean mage = MapleJob.is法师(job);
            switch (weaponType) {
                case 弓:
                case 弩:
                case 火枪:
                case 双弩枪:
                case 灵魂手铳:
                    stat = 4 * localdex + localstr;
                    statpvp = 4 * +dex + str;
                    break;
                case 短剑:
                case 双刀:
                    stat = 4 * localluk + localdex;
                    statpvp = 4 * +luk + dex;
                    break;
                case 拳套:
                case 手杖:
                    stat = 4 * localluk + localdex;
                    statpvp = 4 * +luk + dex;
                    break;
                case 能量剑:
                    stat = 4 * (localstr + localdex + localluk);
                    statpvp = 4 * (str + dex + luk);
                    break;
                case 亡命剑:
                    stat = localmaxhp / 7 + localstr;
                    statpvp = maxhp / 7 + str;
                    break;
                default:
                    if (mage) {
                        stat = 4 * localint + localluk;
                        statpvp = 4 * +int_ + luk;
                    } else {
                        stat = 4 * localstr + localdex;
                        statpvp = 4 * +str + dex;
                    }
                    break;
            }
            localmaxbasepvpdamage = weaponType.getMaxDamageMultiplier(job) * statpvp * (100.0f + (pvpDamage / 100.0f));
            localmaxbasepvpdamageL = weaponType.getMaxDamageMultiplier(job) * stat * (100.0f + (pvpDamage / 100.0f));
            localmaxbasedamage = weaponType.getMaxDamageMultiplier(job) * stat * (watk / 100.0f);
        }
    }
    
    public final float getHealHP() {
        return shouldHealHP;
    }
    
    public final float getHealMP() {
        return shouldHealMP;
    }
    
    public final void relocHeal(MapleCharacter chra) {
        if (chra.isClone()) {
            return;
        }
        final int playerjob = chra.getJob();
        
        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = MapleJob.is恶魔猎手(chra.getJob()) ? 0 : (3 + recoverMP + (inc_INT / 10)); // i think
        mpRecoverTime = 0;
        hpRecoverTime = 0;
        
        final Skill skl;
        final int lv;
        switch (playerjob) {
            case MapleJobID.勇士:
            case MapleJobID.英雄: {
                skl = SkillFactory.getSkill(英雄.自我恢复);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    MapleStatEffect eff = skl.getEffect(lv);
                    if (eff.getHp() > 0) {
                        shouldHealHP += eff.getHp();
                        hpRecoverTime = 4000;
                    }
                    shouldHealMP += eff.getMp();
                    mpRecoverTime = 4000;
                }
                break;
            }
            case MapleJobID.魂骑士3转:
            case MapleJobID.魂骑士4转: {
                skl = SkillFactory.getSkill(魂骑士.钢铁之轮);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    shouldHealHP += skl.getEffect(lv).getY();
                    mpRecoverTime = skl.getEffect(lv).getW();
                }
                break;
            }
            case MapleJobID.米哈尔3转:
            case MapleJobID.米哈尔4转: {
                skl = SkillFactory.getSkill(米哈尔.自我恢复);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    shouldHealHP += skl.getEffect(lv).getHp();
                    shouldHealMP += skl.getEffect(lv).getMp();
                    hpRecoverTime = 4000;
                    mpRecoverTime = 4000;
                }
                break;
            }
            case MapleJobID.双弩精灵1转:
            case MapleJobID.双弩精灵2转:
            case MapleJobID.双弩精灵3转:
            case MapleJobID.双弩精灵4转: {
                skl = SkillFactory.getSkill(双弩精灵.精灵恢复);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    shouldHealHP += (skl.getEffect(lv).getX() * localmaxhp) / 100;
                    hpRecoverTime = 4000;
                    shouldHealMP += (skl.getEffect(lv).getX() * localmaxmp) / 100;
                    mpRecoverTime = 4000;
                }
                break;
            }
            case MapleJobID.龙的传人2转:
            case MapleJobID.龙的传人3转:
            case MapleJobID.龙的传人4转: {
                skl = SkillFactory.getSkill(龙的传人.侠士的忍耐);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    final MapleStatEffect eff = skl.getEffect(lv);
                    shouldHealHP += eff.getX();
                    shouldHealMP += eff.getX();
                    hpRecoverTime = eff.getY();
                    mpRecoverTime = eff.getY();
                }
                break;
            }
            case MapleJobID.恶魔猎手3转:
            case MapleJobID.恶魔猎手4转: {
                skl = SkillFactory.getSkill(恶魔猎手.极限精气吸收);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    shouldHealMP += skl.getEffect(lv).getY();
                    mpRecoverTime = 4000;
                }
                break;
            }
            case MapleJobID.唤灵斗师1转:
            case MapleJobID.唤灵斗师2转:
            case MapleJobID.唤灵斗师3转:
            case MapleJobID.唤灵斗师4转: {
                skl = SkillFactory.getSkill(唤灵斗师.吸收灵气);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    shouldHealHP += skl.getEffect(lv).getHp();
                    hpRecoverTime = 4000;
                }
                break;
            }
            case MapleJobID.狂龙战士1转:
            case MapleJobID.狂龙战士2转:
            case MapleJobID.狂龙战士3转:
            case MapleJobID.狂龙战士4转: {
                skl = SkillFactory.getSkill(狂龙战士.自我恢复);
                lv = chra.getSkillLevel(skl);
                if (lv > 0) {
                    final MapleStatEffect eff = skl.getEffect(lv);
                    shouldHealHP += (eff.getX() * localmaxhp) / 100;
                    hpRecoverTime = eff.getW();
                    shouldHealMP += (eff.getX() * localmaxmp) / 100;
                    mpRecoverTime = eff.getW();
                }
                break;
            }
        }
        
        if (chra.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else if (chra.getMap() != null) { // Because Heal isn't multipled when there's a chair :)
            final float recvRate = chra.getMap().getRecoveryRate();
            if (recvRate > 0) {
                shouldHealHP *= recvRate;
                shouldHealMP *= recvRate;
            }
        }
    }
    
    public final void connectData(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeShort(str);
        mplew.writeShort(dex);
        mplew.writeShort(int_);
        mplew.writeShort(luk);
        mplew.writeInt(hp);
        mplew.writeInt(maxhp);
        mplew.writeInt(mp);
        mplew.writeInt(maxmp);
    }
    
    public final void zeroData(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeShort(-1);
        mplew.write(chr.getSecondGender()); //女是1  男是0 这里应该判断 是谁下面就加载另外一个的状态
        mplew.writeInt(hp);
        mplew.writeInt(mp);
        mplew.write(chr.getSecondSkin());
        mplew.writeInt(chr.getSecondHair());
        mplew.writeInt(chr.getSecondFace());
        mplew.writeInt(maxhp);
        mplew.writeInt(maxmp);
        mplew.writeInt(0);//dbcharZeroLinkCashPart
        mplew.writeInt(-1);//nMixBaseHairColor
        mplew.writeInt(0);//nMixAddHairColor
        mplew.writeInt(0);//nMixHairBaseProb
    }
    private final static int[] allJobs = {0, 10000, 10000000, 20000000, 20010000, 20020000, 20030000, 20040000, 20050000, 30000000, 30010000, 50000000};
    public final static int[] pvpSkills = {1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};

//    public static int getSkillByJob2(int skillId, int job) {
//        if (MapleJob.is骑士团(job)) {
//            return skillId + 10000000;
//        } else if (MapleJob.is战神(job)) {
//            return skillId + 20000000;
//        } else if (MapleJob.is龙神(job)) {
//            return skillId + 20010000;
//        } else if (MapleJob.is双弩精灵(job)) {
//            return skillId + 20020000;
//        } else if (MapleJob.is幻影(job)) {
//            return skillId + 20030000;
//        } else if (MapleJob.is夜光(job)) {
//            return skillId + 20040000;
//        } else if (MapleJob.is隐月(job)) {
//            return skillId + 20050000;
//        } else if (MapleJob.is反抗者(job)) {
//            return skillId + 30000000;
//        } else if (MapleJob.is恶魔猎手(job) || MapleJob.is恶魔复仇者(job)) {
//            return skillId + 30010000;
//        } else if (MapleJob.is尖兵(job)) {
//            return skillId + 30020000;
//        } else if (MapleJob.is米哈尔(job)) {
//            return skillId + 50000000;
//        } else if (MapleJob.is狂龙战士(job)) {
//            return skillId + 60000000;
//        } else if (MapleJob.is爆莉萌天使(job)) {
//            return skillId + 60010000;
//        } else if (MapleJob.is神之子(job)) {
//            return skillId + 100000000;
//        } else if (MapleJob.is林之灵(job)) {
//            return skillId + 110000000;
//        } else if (MapleJob.is剑豪(job)) {
//            return skillId + 40010000;
//        } else if (MapleJob.is阴阳师(job)) {
//            return skillId + 40020000;
//        }
//        return skillId;
//    }
    public static int getSkillByJob(final int skillID, final int job) { //test
        return skillID + (MapleJob.getBeginner((short) job) * 10000);
    }

    //骑宠技能专用
    public static int getSkillByJob(int skillId, int job, int by) {
//        if (MapleJob.is骑士团(job)) {
//            return skillId + 10000000;
//        }
//        if (MapleJob.is战神(job)) {
//            return skillId + 20000000;
//        }
//        if (MapleJob.is龙神(job)) {
//            return skillId + 20010000;
//        }
//        if (MapleJob.is双弩精灵(job)) {
//            return skillId - 20020000;
//        }
//        if (MapleJob.is幻影(job)) {
//            return skillId + 20030000;
//        }
//        if (MapleJob.is夜光(job)) {
//            return skillId + 20040000;
//        }
//        if (MapleJob.is反抗者(job)) {
//            return skillId + 30000000;
//        }
//        if ((MapleJob.is恶魔猎手(job)) || (MapleJob.is恶魔复仇者(job))) {
//            return skillId + 30010000;
//        }
//        if (MapleJob.is尖兵(job)) {
//            return skillId + 30020000;
//        }
//        if (MapleJob.is米哈尔(job)) {
//            return skillId + 50000000;
//        }
//        if (MapleJob.is狂龙战士(job)) {
//            return skillId + 60000000;
//        }
//        if (MapleJob.is爆莉萌天使(job)) {
//            return skillId + 60010000;
//        }
//        if (MapleJob.is神之子(job)) {
//            return skillId + 100000000;
//        }
        return skillId;
    }
    
    public final int getSkillIncrement(final int skillID) {
        if (skillsIncrement.containsKey(skillID)) {
            return skillsIncrement.get(skillID);
        }
        return 0;
    }
    
    public final int getElementBoost(final Element key) {
        if (elemBoosts.containsKey(key)) {
            return elemBoosts.get(key);
        }
        return 0;
    }
    
    public final int getDamageIncrease(final int key) {
        if (damageIncrease.containsKey(key)) {
            return damageIncrease.get(key);
        }
        return 0;
    }
    
    public void heal_noUpdate(MapleCharacter chra) {
        setHp(getCurrentMaxHp(), chra);
        setMp(getCurrentMaxMp(chra.getJob()), chra);
    }
    
    public void heal(MapleCharacter chra) {
        heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, getCurrentMaxHp());
        chra.updateSingleStat(MapleStat.MP, getCurrentMaxMp(chra.getJob()));
    }
    
    public Pair<Integer, Integer> handleEquipAdditions(MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> sData, final int itemId) {
        final List<Triple<String, String, String>> additions = ii.getEquipAdditions(itemId);
        if (additions == null) {
            return null;
        }
        int localmaxhp_x = 0, localmaxmp_x = 0;
        int skillid = 0, skilllevel = 0;
        String craft, job, level;
        for (final Triple<String, String, String> add : additions) {
            if (add.getMid().contains("con")) {
                continue;
            }
            final int right = Integer.parseInt(add.getRight());
            switch (add.getLeft()) {
                case "elemboost":
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("elemVol") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        int value = Integer.parseInt(add.getRight().substring(1, add.getRight().length()));
                        final Element key = Element.getFromChar(add.getRight().charAt(0));
                        if (elemBoosts.get(key) != null) {
                            value += elemBoosts.get(key);
                        }
                        elemBoosts.put(key, value);
                    }
                    break;
                case "mobcategory": //skip the category, thinkings too expensive to have yet another Map<Integer, Integer> for damage calculations
                    if (add.getMid().equals("damage")) {
                        percent_damage_rate *= (right + 100.0) / 100.0;
                        percent_boss_damage_rate += (right + 100.0) / 100.0;
                    }
                    break;
                case "critical": // lv critical lvl?
                    boolean canJob = false,
                     canLevel = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJob = true;
                                }
                            }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                            canJob = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null) {
                        if (chra.getLevel() >= Integer.parseInt(level)) {
                            canLevel = true;
                        }
                    }
                    if ((job != null && canJob || job == null) && (level != null && canLevel || level == null)) {
                        switch (add.getMid()) {
                            case "prob":
                                inc_Cr += right;
                                break;
                            case "damage":
                                critical_damage_min += right;
                                critical_damage_max += right; //???CONFIRM - not sure if this is max or minCritDmg
                                break;
                        }
                    }
                    break;
                case "boss": // ignore prob, just add
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("damage") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        percent_boss_damage_rate *= (right + 100.0) / 100.0;
                    }
                    break;
                case "mobdie": // lv, hpIncRatioOnMobDie, hpRatioProp, mpIncRatioOnMobDie, mpRatioProp, modify =D, don't need mob to die
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        switch (add.getMid()) {
                            case "hpIncOnMobDie":
                                hpRecover_Percent += right;
                                hpRecoverProp += 5;
                                break;
                            case "mpIncOnMobDie":
                                mpRecover += right;
                                mpRecoverProp += 5;
                                break;
                        }
                    }
                    break;
                case "skill": // all these are additional skills
                    if (first_login) {
                        craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                        if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                            switch (add.getMid()) {
                                case "id":
                                    skillid = right;
                                    break;
                                case "level":
                                    skilllevel = right;
                                    break;
                            }
                        }
                    }
                    break;
                case "hpmpchange":
                    switch (add.getMid()) {
                        case "hpChangerPerTime":
                            recoverHP += right;
                            break;
                        case "mpChangerPerTime":
                            recoverMP += right;
                            break;
                    }
                    break;
                case "statinc":
                    boolean canJobx = false,
                     canLevelx = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJobx = true;
                                }
                            }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                            canJobx = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null && chra.getLevel() >= Integer.parseInt(level)) {
                        canLevelx = true;
                    }
                    if ((!canJobx && job != null) || (!canLevelx && level != null)) {
                        continue;
                    }
                    if (itemId == 1142367) {
                        final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (day != 1 && day != 7) {
                            continue;
                        }
                    }
                    switch (add.getMid()) {
                        case "incPAD":
                            inc_PAD += right;
                            break;
                        case "incMAD":
                            mdef += right;
                            break;
                        case "incSTR":
                            inc_STR += right;
                            break;
                        case "incDEX":
                            inc_DEX += right;
                            break;
                        case "incINT":
                            inc_INT += right;
                            break;
                        case "incLUK":
                            inc_LUK += right;
                            break;
                        case "incMHP":
                            localmaxhp_x += right;
                            break;
                        case "incMMP":
                            localmaxmp_x += right;
                            break;
                        case "incPDD":
                            inc_PDD += right;
                            break;
                        case "incACC":
                            //accuracy += right;
                            break;
                        case "incEVA":
                            break;
                        case "incMHPr":
                            percent_hp += right;
                            break;
                        case "incMMPr":
                            percent_mp += right;
                            break;
                    }
                    break;
            }
        }
        if (skillid != 0 && skilllevel != 0) {
            sData.put(SkillFactory.getSkill(skillid), new SkillEntry((byte) skilllevel, (byte) 0, -1));
        }
        return new Pair<>(localmaxhp_x, localmaxmp_x);
    }
    
    public Pair<Integer, Integer> handleEquipSetStats(MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> sData, int setId, int setCount) {
        //获取套装ID的信息
        StructSetItem setItem = ii.getSetItem(setId);
        if (setItem == null) {
            return null;
        }
        //套装属性信息 [激活需要的数量 套装属性信息]
        Map<Integer, StructSetItemStat> setItemStats = setItem.getSetItemStats();
//        System.err.println("套装编号: " + setItem.setItemID + " 名称: " + " 当前佩戴数量: " + setCount);
        int localmaxhp_x = 0, localmaxmp_x = 0;
        StructItemOption soc;
        for (Entry<Integer, StructSetItemStat> ent : setItemStats.entrySet()) {
            //System.err.println("套装编号: " + setItem.setItemID + " 名称: " + setItem.setItemName + " 激活套装属性ID: " + ent.getKey() + " 能否激活: " + (ent.getKey() == setCount));
            if (ent.getKey() == setCount) { //必须等于才处理
                //System.err.println("开始处理激活的套装属性信息....");
                StructSetItemStat setItemStat = ent.getValue();
                inc_STR += setItemStat.incSTR + setItemStat.incAllStat;
                inc_DEX += setItemStat.incDEX + setItemStat.incAllStat;
                inc_INT += setItemStat.incINT + setItemStat.incAllStat;
                inc_LUK += setItemStat.incLUK + setItemStat.incAllStat;
                inc_PAD += setItemStat.incPAD;
                mdef += setItemStat.incMAD;
                localmaxhp_x += setItemStat.incMHP;//固定值
                localmaxmp_x += setItemStat.incMMP;
                percent_hp += setItemStat.incMHPr;//百分比
                percent_mp += setItemStat.incMMPr;
                addmaxhp += localmaxhp_x;
//                System.err.println("setItemStat.incMHP: " + setItemStat.incMHP+ " setItemStat.incMHPr: "  + setItemStat.incMHPr);
                if (setItemStat.option1 > 0 && setItemStat.option1Level > 0) {
                    soc = ii.getPotentialInfo(setItemStat.option1).get(setItemStat.option1Level);
                    if (soc != null) {
                        localmaxhp_x += soc.get("incMHP");
                        localmaxmp_x += soc.get("incMMP");
                        handleItemOption(soc, chra, first_login, sData);
                    }
                }
                if (setItemStat.option2 > 0 && setItemStat.option2Level > 0) {
                    soc = ii.getPotentialInfo(setItemStat.option2).get(setItemStat.option2Level);
                    if (soc != null) {
                        localmaxhp_x += soc.get("incMHP");
                        localmaxmp_x += soc.get("incMMP");
                        handleItemOption(soc, chra, first_login, sData);
                    }
                }
            }
        }
        return new Pair<>(localmaxhp_x, localmaxmp_x);
    }
    
    public void handleItemOption(StructItemOption soc, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> hmm) {
        inc_PAD += soc.get("incPAD");
        percent_atk += soc.get("incPADr");
        if (soc.get("incPADlv") > 0) {
            inc_PAD += (chra.getLevel() / 10) * soc.get("incPADlv");
        }
        mdef += soc.get("incMAD");
        mdefr += soc.get("incMADr");
        if (soc.get("incMADlv") > 0) {
            mdef += (chra.getLevel() / 10) * soc.get("incMADlv");
        }
        inc_PDD += soc.get("incPDD");
        percent_wdef += soc.get("incPDDr");
        percent_mdef += soc.get("incMDDr");
        inc_STR += soc.get("incSTR");
        inc_DEX += soc.get("incDEX");
        inc_INT += soc.get("incINT");
        inc_LUK += soc.get("incLUK");
        if (soc.get("incSTRlv") > 0) {
            inc_STR += (chra.getLevel() / 10) * soc.get("incSTRlv");
        }
        if (soc.get("incDEXlv") > 0) {
            inc_DEX += (chra.getLevel() / 10) * soc.get("incDEXlv");
        }
        if (soc.get("incINTlv") > 0) {
            inc_INT += (chra.getLevel() / 10) * soc.get("incINTlv");
        }
        if (soc.get("incLUKlv") > 0) {
            inc_LUK += (chra.getLevel() / 10) * soc.get("incLUKlv");
        }
        
        inc_STRr += soc.get("incSTRr");
        inc_DEXr += soc.get("incDEXr");
        inc_INTr += soc.get("incINTr");
        inc_LUKr += soc.get("incLUKr");
        percent_hp += soc.get("incMHPr");
        percent_mp += soc.get("incMMPr");
        
        inc_DAMr += soc.get("incDAMr");
        percent_ignore_mob_def_rate += soc.get("percent_ignore_mob_def_rate");
        
        inc_Cr += soc.get("incCr");
        
        if (soc.get("boss") <= 0) {
            percent_damage_rate += soc.get("incDAMr");
        }
        recoverHP += soc.get("RecoveryHP"); // This shouldn't be here, set 4 seconds.
        recoverMP += soc.get("RecoveryMP"); // This shouldn't be here, set 4 seconds.
        if (soc.get("HP") > 0) { // Should be heal upon attacking
            hpRecover_Percent += soc.get("HP");
            hpRecoverProp += soc.get("prop");
        }
        if (soc.get("MP") > 0 && !MapleJob.is恶魔猎手(chra.getJob())) {
            mpRecover += soc.get("MP");
            mpRecoverProp += soc.get("prop");
        }
        if (soc.get("ignoreDAM") > 0) {
            ignoreDAM += soc.get("ignoreDAM");
            ignoreDAM_prop += soc.get("prop");
        }
        incAllskill += soc.get("incAllskill");
        if (soc.get("ignoreDAMr") > 0) {
            ignoreDAMr += soc.get("ignoreDAMr");
            ignoreDAMr_prop += soc.get("prop");
        }
        RecoveryUP += soc.get("RecoveryUP"); // only for hp items and skills
        critical_damage_min += soc.get("incCriticaldamageMin");
        critical_damage_max += soc.get("incCriticaldamageMax");
        TER += soc.get("incTerR"); // elemental resistance = avoid element damage from monster
        ASR += soc.get("incAsrR"); // abnormal status = disease
        if (soc.get("DAMreflect") > 0) {
            DAMreflect += soc.get("DAMreflect");
            DAMreflect_rate += soc.get("prop");
        }
        mpconReduce += soc.get("mpconReduce");
        reduceCooltime += soc.get("reduceCooltime"); // in seconds
        incMesoProp += soc.get("incMesoProp"); // mesos + %
        incRewardProp *= (100 + soc.get("incRewardProp")) / 100.0; // extra drop rate for item
        if (first_login && soc.get("skillID") > 0) {
            hmm.put(SkillFactory.getSkill(getSkillByJob(soc.get("skillID"), chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
        }
        percent_boss_damage_rate *= (soc.get("bdR") + 100.0) / 100.0;
        percent_ignore_mob_def_rate *= (soc.get("imdR") + 100.0) / 100.0;
        // poison, stun, etc (uses level field -> cast disease to mob/player), face?
    }
    
    public final void handleProfessionTool(final MapleCharacter chra) {
        if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
            final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) { //goes to first harvesting tool and stops
                final Equip equip = (Equip) itera.next();
                if (equip.getDurability() != 0 && (equip.getItemId() / 10000 == 150 && chra.getProfessionLevel(92000000) > 0) || (equip.getItemId() / 10000 == 151 && chra.getProfessionLevel(92010000) > 0)) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }
    
    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
        for (int i = 0; i < 10; i++) {
            if (pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
                pvpRank--;
                pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
            }
        }
    }
    
    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / localmaxhp);
    }
    
    public final void init(MapleCharacter chra) {
        recalcLocalStats(chra);
    }
    
    public final int getStr() {
        return str;
    }
    
    public final int getDex() {
        return dex;
    }
    
    public final int getInt() {
        return int_;
    }
    
    public final int getLuk() {
        return luk;
    }
    
    public final int getHp() {
        return hp;
    }
    
    public final int getMp() {
        return mp;
    }
    
    public final int getMaxHp() {
        return maxhp;
    }
    
    public final int getMaxMp() {
        return maxmp;
    }
    
    public final void setStr(final short str, MapleCharacter chra) {
        this.str = str;
        recalcLocalStats(chra);
    }
    
    public final void setDex(final short dex, MapleCharacter chra) {
        this.dex = dex;
        recalcLocalStats(chra);
    }
    
    public final void setInt(final short int_, MapleCharacter chra) {
        this.int_ = int_;
        recalcLocalStats(chra);
    }
    
    public final void setLuk(final short luk, MapleCharacter chra) {
        this.luk = luk;
        recalcLocalStats(chra);
    }
    
    public final boolean setHp(final int newhp, MapleCharacter chra) {
        return setHp(newhp, false, chra);
    }
    
    public final boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        MapleBuffStat revive;
        final int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        hp = thp;
        
        if (chra != null) {
            if (oldHp > hp && !chra.isAlive()) {
                revive = MapleBuffStat.CS_PreReviveOnce;
                // 技能免死在这
                if (chra.getBuffedValue(revive) != null) {
                    int percentage = chra.getBuffedValue(revive);
                    hp = ((int) (localmaxhp * (percentage / 100.0D)));
                    mp = ((int) (localmaxmp * (percentage / 100.0D)));
                    chra.updateSingleStat(MapleStat.HP, hp);
                    chra.updateSingleStat(MapleStat.MP, mp);
                    chra.cancelBuffStats(revive);
                    chra.setStance(0);
                    chra.changeMap(chra.getMap(), chra.getMap().getPortal(0));
                    String text = "以消耗掉幸运幻影的效果替代死亡，并回复最大值" + percentage + "%的HP。\r\n已被传送在无怪区域";
                    chra.getClient().announce(CField.EffectPacket.effect_NormalSpeechBalloon("#fs18#" + text, 3000, 0));
                    chra.dropMessage(6, text);
                } else if (chra.getBuffedValue(MapleBuffStat.CS_ReviveOnce) != null) {
                    revive = MapleBuffStat.CS_ReviveOnce;
                    int percentage = chra.getBuffedValue(revive);
                    hp = ((int) (localmaxhp * (percentage / 100.0D)));
                    mp = ((int) (localmaxmp * (percentage / 100.0D)));
                    chra.updateSingleStat(MapleStat.HP, hp);
                    chra.updateSingleStat(MapleStat.MP, mp);
                    chra.cancelBuffStats(revive);
                    chra.setStance(0);
                    String text = "黑暗重生效果替代死亡，并回复最大值" + percentage + "%的HP。\r\n已被传送在无怪区域";
                    chra.changeMap(chra.getMap(), chra.getMap().getPortal(0));
                    chra.getClient().announce(CField.EffectPacket.effect_NormalSpeechBalloon("#fs18#" + text, 3000, 0));
                    chra.dropMessage(6, text);
                } else if ((chra.isBuffFrom(MapleBuffStat.CS_PreReviveOnce, SkillFactory.getSkill(20050286))) || (chra.isBuffFrom(MapleBuffStat.CS_PreReviveOnce, SkillFactory.getSkill(80000169)))) {
                    int skillid = chra.getBuffSource(MapleBuffStat.CS_PreReviveOnce);//九死一生
                    if (SkillFactory.getSkill(skillid).getEffect(chra.getSkillLevel(skillid)).makeChanceResult()) {
                        thp = oldHp;
                        chra.dispelSkill(20050286);
                    }
                } else if (chra.isBuffFrom(MapleBuffStat.CS_PreReviveOnce, SkillFactory.getSkill(25111209))) {
                    chra.use幻灵招魂();
                    thp = oldHp;
                } else if (chra.isInvincible()) {
                    chra.addHP(chra.getMaxHp());
                } else {
                    // 战斗机器人效果添加的地方
                    //chra.getClient().announce(CField.getDeathTip_Effect2());//刷新0HP
                    //chra.getClient().announce(CUserLocal.onOpenUIOnDead(1, 0));//这里不需要
                    chra.playerDead();
                    //chra.getClient().announce(CField.getDeathTip_Effect((int)1));
                }
            }
            if (!silent) {
                //chra.checkBerserk();
                chra.updatePartyMemberHP();
            }
//            if ((oldHp > this.hp) && (!chra.isAlive())) {
//                chra.playerDead();
//            }
        }
        if (MapleJob.is恶魔复仇者(chra.getJob())) {
            chra.getClient().announce(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
        }
        return hp != oldHp;
    }
    
    public final boolean setMp(final int newmp, final MapleCharacter chra) {
        final int oldMp = mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = tmp;
        return mp != oldMp;
    }
    
    public final void setMaxHp(final int hp, MapleCharacter chra) {
        this.maxhp = hp;
        recalcLocalStats(chra);
    }
    
    public final void setMaxMp(final int mp, MapleCharacter chra) {
        this.maxmp = mp;
        recalcLocalStats(chra);
    }
    
    public final void setInfo(final int maxhp, final int maxmp, final int hp, final int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }
    
    public final int getTotalStr() {
        return localstr;
    }
    
    public final int getTotalDex() {
        return localdex;
    }
    
    public final int getTotalInt() {
        return localint;
    }
    
    public final int getTotalLuk() {
        return localluk;
    }
    
    public final int getTotalWatk() {
        return watk;
    }
    
    public final int getTotalMagic() {
        return magic;
    }
    
    public final int getDefense() {
        return wdef;
    }
    
    public final int getCurrentMaxHp() {
        return localmaxhp;
    }
    
    public final int getCurrentMaxMp() {
        return localmaxmp;
    }
    
    public final int getCurrentMaxMp(final int job) {
        if (MapleJob.is恶魔猎手(job)) {
            return GameConstants.getMPByJob(job);
        }
        if (MapleJob.is神之子(job)) {
            localmaxmp = 100;
        }
        return localmaxmp;
    }
    
    public final int getOnActive() {
        return onActive;
    }
    
    public final float getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }
    
    public final float getCurrentMaxBasePVPDamage() {
        return localmaxbasepvpdamage;
    }
    
    public final float getCurrentMaxBasePVPDamageL() {
        return localmaxbasepvpdamageL;
    }
    
    public final boolean isRangedJob(final int job) {
        return MapleJob.is龙的传人(job) || MapleJob.is双弩精灵(job) || MapleJob.is神炮王(job) || job == 400 || (job / 10 == 52) || (job / 100 == 3) || (job / 100 == 13) || (job / 100 == 14) || (job / 100 == 33) || (job / 100 == 35) || (job / 10 == 41);
    }

    /*
     * 增加技能的伤害
     */
    public void addDamageIncrease(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        if (damageIncrease.containsKey(skillId)) {
            int oldval = damageIncrease.get(skillId);
            damageIncrease.put(skillId, oldval + val);
        } else {
            damageIncrease.put(skillId, val);
        }
    }

    /*
     * 增加技能攻击怪物的数量
     */
    public void addTargetPlus(int skillId, int val) {
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (this.add_skill_targetPlus.containsKey(skillId)) {
            int oldval = (add_skill_targetPlus.get(skillId));
            this.add_skill_targetPlus.put(skillId, oldval + val);
        } else {
            this.add_skill_targetPlus.put(skillId, val);
        }
    }

    /*
     * 增加技能攻击怪物的次数
     */
    public void addAttackCount(int skillId, int val) {
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_attackCount.containsKey(skillId)) {
            int oldval = (add_skill_attackCount.get(skillId));
            add_skill_attackCount.put(skillId, oldval + val);
        } else {
            add_skill_attackCount.put(skillId, val);
        }
    }

    /*
     * 增加技能对BOSS的伤害
     */
    public void addBossDamageRate(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        if (add_skill_bossDamageRate.containsKey(skillId)) {
            int oldval = add_skill_bossDamageRate.get(skillId);
            add_skill_bossDamageRate.put(skillId, oldval + val);
        } else {
            add_skill_bossDamageRate.put(skillId, val);
        }
    }

    /*
     * 增加技能攻击怪物的无视概率
     */
    public void addIgnoreMobpdpRate(int skillId, int val) {
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_ignoreMobpdpR.containsKey(skillId)) {
            int oldval = (add_skill_ignoreMobpdpR.get(skillId));
            add_skill_ignoreMobpdpR.put(skillId, oldval + val);
        } else {
            add_skill_ignoreMobpdpR.put(skillId, val);
        }
    }

    /*
     * 增加技能BUFF的持续时间
     */
    public void addBuffDuration(int skillId, int val) {
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_duration.containsKey(skillId)) {
            int oldval = (add_skill_duration.get(skillId));
            add_skill_duration.put(skillId, oldval + val);
        } else {
            add_skill_duration.put(skillId, val);
        }
    }

    /*
     * 技能冷却时间减少
     */
    public int getReduceCooltimeRate(int skillId) {
        if (add_skill_coolTimeR.containsKey(skillId)) {
            return add_skill_coolTimeR.get(skillId);
        }
        return 0;
    }

    /*
     * 技能冷却时间减少
     */
    public void addCoolTimeReduce(int skillId, int val) {
        if (skillId < 0 || val <= 0) {
            return;
        }
        if (add_skill_coolTimeR.containsKey(skillId)) {
            int oldval = add_skill_coolTimeR.get(skillId);
            add_skill_coolTimeR.put(skillId, oldval + val);
        } else {
            add_skill_coolTimeR.put(skillId, val);
        }
    }
    
    public int getHealHp() {
        return Math.max(localmaxhp - hp, 0);
    }
    
    public int getHealMp(int job) {
        if (MapleJob.isNotMpJob(job)) {
            return 0;
        }
        return Math.max(localmaxmp - mp, 0);
    }

    /*
     * 额外增加攻击怪物数量
     */
    public int getMobCount(int skillId) {
        if (add_skill_targetPlus.containsKey(skillId)) {
            return add_skill_targetPlus.get(skillId);
        }
        return 0;
    }
}
