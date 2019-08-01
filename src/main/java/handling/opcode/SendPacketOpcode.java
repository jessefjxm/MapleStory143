package handling.opcode;

import handling.ExternalCodeTableGetter;
import handling.WritableIntValueHolder;

import java.io.*;
import java.util.Properties;

public enum SendPacketOpcode implements WritableIntValueHolder {

    LOGIN_STATUS(0x0),
    CHAT_SERVER_STATUS(0x1),
    SERVERLIST(0x1),
    ENABLE_RECOMMENDED(0x3),
    CHARLIST(0x6),
    SERVER_IP(0x7),
    AUTO_LOGIN_STATUS(0x8),
    CHAR_NAME_RESPONSE(0xa),
    ADD_NEW_CHAR_ENTRY(0xb),
    DELETE_CHAR_RESPONSE(0xc),
    CHANGE_CHANNEL(0x11),
    PING(0x12),
    CHAT_SERVER_RESULT(0x14),
    CLIENT_AUTH(0x16),
    CS_USE(0xc),
    EVENT_CHECK(0xd),
    GUILD_CHAT(0x12),
    BUDDY_CHAT(0x13),
    PART_TIME(0x1e),
    LICENSE_RESULT(0x27),
    CHOOSE_GENDER(0x29),
    GENDER_SET(0x2a),
    UNK0007(0x2a),
    UNK0008(0x2b),
    SHOW_CHAR_CARDS(0x2d),
    SHOW_ACC_CASH(0x2e),
    SHOW_CHARSOLE(0x2f),
    WZ_CHECK(0x31),
    SERVERSTATUS(0x38),
    CHANNEL_SELECTED(0x7ffe),
    RSA_KEY(0x32),
    LOGIN_AUTH(0x39),
    CREAT_CHAR_AUTH(0x3c),
    CREATING_CONNECTION(0x24),
    SEND_RECOMMENDED(0x7ffe),
    SECONDPW_ERROR(0x7ffe),
    MODIFY_INVENTORY_ITEM(0x4f),
    UPDATE_INVENTORY_SLOT(0x50),
    UPDATE_STATS(0x51),
    GIVE_BUFF(0x52),
    CANCEL_BUFF(0x53),
    TEMP_STATS(0x54),
    TEMP_STATS_RESET(0x55),
    UPDATE_SKILLS(0x56),
    UPDATE_SKILL_TICK(0x57),
    SKILL_MEMORY(0x5d),
    FLAME_MARK(0x5e),
    FAME_RESPONSE(0x60),
    SHOW_STATUS_INFO(0x61),
    SHOW_NOTES(0x62),
    TROCK_LOCATIONS(0x63),
    LIE_DETECTOR(0x64),
    REPORT_RESPONSE(0x68),
    ENABLE_REPORT(0x69),
    UPDATE_MOUNT(0x6b),
    SHOW_QUEST_COMPLETION(0x6c),
    SEND_TITLE_BOX(0x6d),
    USE_SKILL_BOOK(0x6e),
    SP_RESET(0x6f),
    AP_RESET(0x70),
    DISTRIBUTE_ITEM(0x7ffe),
    EXPAND_CHARACTER_SLOTS(0x7ffe),
    FINISH_SORT(0x75),
    FINISH_GATHER(0x76),
    REPORT_RESULT(0x7ffe),
    TRADE_LIMIT(0x7ffe),
    UPDATE_GENDER(0x7ffe),
    CHAR_INFO(0x79),
    PARTY_OPERATION(0x7a),
    MEMBER_SEARCH(0x7b),
    PARTY_SEARCH(0x7d),
    EXPEDITION_OPERATION(0x82),
    BUDDYLIST(0x83),
    GUILD_OPERATION(0x86),
    ALLIANCE_OPERATION(0x87),
    SPAWN_PORTAL(0x88),
    MECH_PORTAL(0x7ffe),
    SERVERMESSAGE(0x89),
    PIGMI_REWARD(0x8a),
    OWL_OF_MINERVA(0x8c),
    YELLOW_CHAT(0x8d),
    ENGAGE_REQUEST(0x8e),
    ENGAGE_RESULT(0x8f),
    PET_PICKUP_MSG(0x93),
    PET_AUTO_EAT_MSG(0x94),
    SYNTHESIZING_MSG(0x95),
    SHOP_DISCOUNT(0x98),
    PLAYER_NPC(0x9b),
    NPC_HIDE(0x9d),
    HOURCHANGED(0xa0),
    FM_HIDE_MINIMAP(0xa1),
    ENERGY(0xa5),
    GHOST_POINT(0xa6),
    GHOST_STATUS(0xa7),
    FIELD_VALUE(0xa8),
    FAIRY_PEND_MSG(0xa9),
    SEND_PEDIGREE(0xaa),
    OPEN_FAMILY(0xab),
    FAMILY_MESSAGE(0xac),
    FAMILY_INVITE(0xad),
    FAMILY_JUNIOR(0xae),
    SENIOR_MESSAGE(0xaf),
    FAMILY(0xb0),
    REP_INCREASE(0xb1),
    FAMILY_LOGGEDIN(0xb2),
    FAMILY_BUFF(0xb3),
    FAMILY_USE_REQUEST(0xb4),
    LEVEL_UPDATE(0xb5),
    MARRIAGE_UPDATE(0xb6),
    JOB_UPDATE(0xb7),
    PENDANT_SLOT(0xb8),
    FOLLOW_REQUEST(0xb9),
    TOP_MSG(0xbb),
    MID_MSG(0xbd),
    CLEAR_MID_MSG(0xbe),
    MAP_EFFECT_MSG(0xbf),
    SPECIAL_TOP_MSG(0xc1),
    MAPLE_ADMIN(0xc2),
    MAPLE_ADMIN_ERROR(0xc3),
    UPDATE_JAGUAR(0xc4),
    UPDATA_WP(0xc6),
    ULTIMATE_EXPLORER(0xc8),
    PAM_SONG(0xc9),
    PROFESSION_INFO(0xca),
    UPDATE_IMP_TIME(0xcb),
    ITEM_POT(0xcc),
    UPDATE_LINKSKILL_RESULT(0xd1),
    DELETE_LINKSKILL_RESULT(0xd2),
    SET_LINKSKILL_RESULT(0xd3),
    MULUNG_MESSAGE(0xd7),
    MULUNG_DOJO_RANKING(0xd8),
    EQUIPPED_SKILL(0xdc),
    UPDATE_INNER_SKILL(0xe0),
    UPDATE_INNER_STATS(0xe2),
    CONFIRM_CROSS_HUNTER(0xe7),
    WHITEADDITIONAL_CUBE_RESULT(0x102),
    BLACKY_CUBE_EFFECT(0x103),
    MEMORIAL_CUBE_RESULT(0x104),
    MEMORIAL_CUBE_MODIFIED(0x105),
    DRESS_UP_INFO_MODIFIED(0x106),
    SKILL_ACTIVE(0x108),
    SKILL_NOT_ACTIVE(0x109),
    LOGIN_AUTHKEY(0x10a),
    GUILD_SEARCH_RESULTS(0x10f),
    SYSTEM_PROCESS_LIST(0x115),
    AVATAR_MEGA_RESULT(0x11a),
    AVATAR_MEGA(0x11b),
    AVATAR_MEGA_REMOVE(0x11c),
    MAPLE_EVENT(0x11d),
    LOVE_OPERATION(0x120),
    CANCEL_TITLE_EFFECT(0x121),
    PARTY_RANKING(0x13d),
    BOSS_MESSAGE(0x141),
    ENCHANTING_OPERATION(0x144),
    HAIDI_RESULT(0x147),
    UPDATE_CORE_AURA(0x156),
    GAME_EXIT(0x150),
    USE_TOWERCHAIR_SETTING_RESULT(0x176),
    VCORE_LIST_UPDATE(0x181),
    VCORE_SKILLEXP_RESULT(0x183),
    VCORE_ADD_PIECE_RESULT(0x184),
    VCORE_ADD_SKILL_RESULT(0x185),
    POTION_POT_MSG(0x18c),
    POTION_POT_UPDATE(0x18e),
    OPEN_WEB(0x19a),
    SEND_CLIPBOARD(0x19b),
    CHAR_CASH(0x19c),
    UNK00A6(0x7ffe),
    SHOW_PREDICT_CARD(0x1a3),
    BBS_OPERATION(0x1a4),
    FISHING_STORE(0x1aa),
    SHOW_PLAYER_CASH(0x1b4),
    PLAYER_CASH_UPDATE(0x1b5),
    SAVE_DAMSKIN(0x1bf),
    DELETE_DAMSKIN(0x1c0),
    LOGIN_WELCOME(0x1c9),
    SKILL_MACRO(0x1ca),
    WARP_TO_MAP(0x1cb),
    MTS_OPEN(0x1cc),
    CS_CHAR(0x1ce),
    CS_OPEN(0x1cf),
    RESET_SCREEN(0x7ffe),
    MAP_BLOCKED(0x1d0),
    SERVER_BLOCKED(0x1d1),
    PARTY_BLOCKED(0x1d2),
    SHOW_EQUIP_EFFECT(0x1d4),
    MULTICHAT(0x1d5),
    WHISPER(0x1d6),
    BOSS_ENV(0x1d8),
    MAP_EFFECT(0x1d9),
    CASH_SONG(0x1da),
    GM_EFFECT(0x1db),
    OX_QUIZ(0x1dc),
    GMEVENT_INSTRUCTIONS(0x1dd),
    CLOCK(0x1de),
    BOAT_EFF(0x1df),
    BOAT_EFFECT(0x1e0),
    LOGIN_SUCC(0x1e1),
    STOP_CLOCK(0x1e5),
    PYRAMID_UPDATE(0x1e7),
    PYRAMID_RESULT(0x1e8),
    QUICK_SLOT(0x1e9),
    MONSTER_NOTICE_MESSAGE(0x1f0),
    MONSTER_PHASE_CHANGE(0x1f1),
    MONSTER_ZONE_CHANGE(0x1f2),
    MOVE_PLATFORM(0x7ffe),
    PVP_INFO(0x7ffe),
    DIRECTION_STATUS(0x7ffe),
    IN_GAME_CUR_NODE_EVENT_END(0x1f5),
    GAIN_FORCE(0x1f6),
    ACHIEVEMENT_RATIO(0x1f8),
    QUICK_MOVE(0x1f9),
    CREATE_OBTACLEATOM(0x1fa),
    CLEAR_OBTACLEATOM(0x1fb),
    CHANGE_MOBZONESTATE_REQUEST(0x201),
    PQ_EFFECT(0x212),
    SPAWN_PLAYER(0x225),
    REMOVE_PLAYER_FROM_MAP(0x226),
    CHATTEXT(0x227),
    CHALKBOARD(0x228),
    UPDATE_CHAR_BOX(0x229),
    SHOW_CONSUME_EFFECT(0x22a),
    SHOW_SCROLL_EFFECT(0x22b),
    SHOW_SOULSCROLL_EFFECT(0x22c),
    SHOW_ENCHANTER_EFFECT(0x22d),
    SHOW_MAGNIFYING_EFFECT(0x22f),
    SHOW_POTENTIAL_RESET(0x230),
    SHOW_POTENTIAL_FINALPANEL(0x232),
    SHOW_ADDITIONAL_RESET(0x233),
    SHOW_ADDITIONAL_EFFECT(0x234),
    SHOW_FIREWORKS_EFFECT(0x7ffe),
    SHOW_REDCUBE_RESULT(0x237),
    SHOW_ADDITIONALCUBE_RESULT(0x238),
    TESLA_TRIANGLE(0x23d),
    FOLLOW_EFFECT(0x23e),
    CRAFT_EFFECT(0x240),
    CRAFT_COMPLETE(0x241),
    HARVESTED(0x243),
    SHOW_DAMAGE_SKIN(0x24a),
    SHOW_DAMAGE_SKIN_PREMIUM(0x24b),
    SOUL_MODE(0x24c),
    CANCEL_CHAIR(0x24d),
    FAMILIAR_OPERATION(0x24e),
    SHOW_INGAMECUBE_RESULT(0x258),
    GIVE_KSPSYCHIC(0x25b),
    ATTACK_KSPSYCHIC(0x25c),
    CANCEL_KSPSYCHIC(0x25d),
    GIVE_KSULTIMATE(0x25f),
    CANCEL_KSULTIMATE(0x260),
    MESO_CHAIR_ADD_MESO(0x263),
    SHOW_SHININGMIRROR_CUBE_RESULT(0x268),
    SHOW_NEBULITE_EFFECT(0x269),
    PET_ADD_SKILL(0x26a),
    EFFECT_SWITCH(0x273),
    SPAWN_PET(0x274),
    MOVE_PET(0x275),
    PET_CHAT(0x276),
    PET_NAMECHANGE(0x277),
    PET_EXCEPTION_LIST(0x279),
    PET_COLOR_CHANGE(0x27a),
    SHOW_PET(0x27d),
    PET_COMMAND(0x27e),
    DRAGON_SPAWN(0x27f),
    DRAGON_MOVE(0x280),
    DRAGON_REMOVE(0x282),
    ANDROID_SPAWN(0x283),
    ANDROID_MOVE(0x284),
    ANDROID_EMOTION(0x285),
    ANDROID_UPDATE(0x286),
    ANDROID_DEACTIVATED(0x287),
    SPAWN_LARGEWHITE(0x288),
    CHANGE_LITTLEWHITE(0x28a),
    REMOVE_LARGEWHITE(0x28d),
    MOVE_LITTLEWHITE(0x28f),
    REMOVE_LITTLEWHITE(0x290),
    SPAWN_LITTLEWHITE(0x294),
    MOVE_PLAYER(0x29b),
    CLOSE_RANGE_ATTACK(0x29c),
    RANGED_ATTACK(0x29d),
    MAGIC_ATTACK(0x29e),
    ENERGY_ATTACK(0x29f),
    SKILL_EFFECT(0x2a0),
    CANCEL_SKILL_EFFECT(0x2a2),
    DAMAGE_PLAYER(0x2a3),
    FACIAL_EXPRESSION(0x2a4),
    SHOW_ITEM_EFFECT(0x2a6),
    SHOW_TITLE_EFFECT(0x2a7),
    SHOW_UNK_EFFECT(0x2a9),
    SHOW_CHAIR(0x2ad),
    UPDATE_CHAR_LOOK(0x2ae),
    SHOW_FOREIGN_EFFECT(0x2af),
    GIVE_FOREIGN_BUFF(0x2b0),
    CANCEL_FOREIGN_BUFF(0x2b1),
    UPDATE_PARTYMEMBER_HP(0x2b2),
    LOAD_GUILD_NAME(0x2b3),
    LOAD_GUILD_ICON(0x2b4),
    LOAD_TEAM(0x2b5),
    SHOW_HARVEST(0x2b6),
    PVP_HP(0x2b7),
    SHOW_DRAGON_FLY(0x2b9),
    UPDATE_ZERO_LOOK(0x2be),
    REMOVE_ZERO_FROM_MAP(0x2c0),
    SHOW_SKILL_SKIN(0x2d2),
    SHOW_SPECIAL_ATTACK(0x2d3),
    USER_EMOTION_LOCAL(0x2d4),
    SHOW_SPECIAL_EFFECT(0x2d6),
    CURRENT_MAP_WARP(0x2d7),
    MESOBAG_SUCCESS(0x2d9),
    MESOBAG_FAILURE(0x2da),
    UPDATE_QUEST_INFO(0x2db),
    BUFF_BAR(0x2dc),
    PET_FLAG_CHANGE(0x2dd),
    PLAYER_HINT(0x2de),
    OPEN_WINDOW(0x2e4),
    REPAIR_WINDOW(0x2e6),
    INTRO_LOCK(0x2e8),
    INTRO_LOCK_MOVIE(0x2e9),
    CYGNUS_INTRO_DISABLE_UI(0x2ea),
    SUMMON_HINT(0x2eb),
    SUMMON_HINT_MSG(0x2ec),
    ARAN_COMBO(0x2f2),
    CRAFT_MESSAGE(0x2f6),
    SPOUSE_MESSAGE(0x2f7),
    ARAN_COMBO_RECHARGE(0x2f9),
    USER_EXPLOSION_ATTACK(0x2fb),
    FOLLOW_MOVE(0x2fc),
    FOLLOW_MSG(0x2fd),
    EXPERT_EFFECT(0x2fe),
    CREATE_ULTIMATE(0x2ff),
    HARVEST_MESSAGE(0x300),
    RUNE_ACTION(0x301),
    OPEN_BAG(0x302),
    DRAGON_BLINK(0x304),
    DIRECTION_INFO(0x305),
    REISSUE_MEDAL(0x306),
    CRIT_STATUS(0x307),
    PLAY_MOVIE(0x309),
    SHOW_CARTE(0x30c),
    LUMINOUS_COMBO(0x30f),
    PROTECTION_GAIN(0x30e),
    BOSS_HATRED(0x311),
    INDIVIDUAL_DEATH_COUNT_INFO(0x313),
    SWITCH_LUCKYMONEY(0x319),
    CHAR_REQUEST(0x31a),
    CANNON_PLATE(0x31c),
    PLAYER_DEAD(0x31e),
    AUTO_SITDOWN(0x323),
    EXTRA_ATTACK(0x327),
    SKILL_FEED(0x33d),
    RUNE_EFFECT(0x345),
    SCREEN_SHAKE(0x355),
    NPC_NOTICE(0x356),
    PANTHER_ATTACK(0x358),
    USER_BONUS_ATTACK_REQUEST(0x35b),
    OPEN_PANTHER_ATTACK(0x35d),
    COOLDOWN(0x35e),
    KSULTIMATE_ATTACK(0x368),
    SHOW_FIREWALL(0x371),
    REGISTER_EXTRA_SKILL(0x375),
    CANNON_SKILL_RESULT(0x37d),
    SHOW_SOMEONE_GIFT(0x383),
    VIEW_WORLDMAP(0x3a6),
    JIANQI_POINTS(0x3ad),
    SPAWN_SUMMON(0x3bd),
    REMOVE_SUMMON(0x3be),
    MOVE_SUMMON(0x3bf),
    SUMMON_ATTACK(0x3c0),
    SUMMON_SKILL(0x3c5),
    DAMAGE_SUMMON(0x3c6),
    SUMMON_SKILL_LINK(0x3c9),
    SPAWN_MONSTER(0x3d0),
    KILL_MONSTER(0x3d1),
    SPAWN_MONSTER_CONTROL(0x3d2),
    MOVE_MONSTER(0x3d6),
    MOVE_MONSTER_RESPONSE(0x3d7),
    DAMAGE_MONSTER(0x3d8),
    APPLY_MONSTER_STATUS(0x3d9),
    CANCEL_MONSTER_STATUS(0x3da),
    CANCEL_MONSTER_ACTION(0x3db),
    APPLY_MONSTER_STATUS_RESPONSE(0x3dc),
    MONSTER_SKILL(0x3e1),
    SHOW_MONSTER_HP(0x3e4),
    SHOW_RESULTS(0x3e5),
    CATCH_MONSTER(0x3e6),
    MONSTER_SKILL_DELAY(0x3ea),
    ESCORT_RETURN_BEFORE(0x3eb),
    ESCORT_STOP_END_PERMMISION(0x3ec),
    MONSTER_TELEPORT(0x3f1),
    MONSTER_SPECIAL_SKILL(0x3f3),
    MONSTER_LASER_CONTROL(0x405),
    MONSTER_DEMIAN_DELAYED_ATTACK_CREATE(0x40e),
    SPAWN_NPC(0x41c),
    REMOVE_NPC(0x41d),
    SPAWN_NPC_REQUEST_CONTROLLER(0x41f),
    NPC_ACTION(0x421),
    FORCE_MOVE_BY_SCRIPT(0x425),
    FORCE_FLIP_BY_SCRIPT(0x426),
    NPC_SPECIAL_ACTION(0x432),
    NPC_SET_SCRIPT(0x433),
    SPAWN_HIRED_MERCHANT(0x435),
    DESTROY_HIRED_MERCHANT(0x436),
    UPDATE_HIRED_MERCHANT(0x437),
    CHANGE_HIRED_MERCHANT_NAME(0x438),
    DROP_ITEM_FROM_MAPOBJECT(0x439),
    REMOVE_ITEM_FROM_MAP(0x43b),
    SPAWN_LOVE(0x43d),
    REMOVE_LOVE(0x43e),
    SPAWN_MIST(0x43f),
    REMOVE_MIST(0x441),
    SPAWN_DOOR(0x442),
    REMOVE_DOOR(0x443),
    MECH_DOOR_SPAWN(0x447),
    MECH_DOOR_REMOVE(0x448),
    REACTOR_HIT(0x44a),
    REACTOR_SPAWN(0x44c),
    REACTOR_DESTROY(0x44f),
    SPAWN_EXTRACTOR(0x454),
    REMOVE_EXTRACTOR(0x455),
    ROLL_SNOWBALL(0x456),
    HIT_SNOWBALL(0x457),
    SNOWBALL_MESSAGE(0x458),
    LEFT_KNOCK_BACK(0x459),
    HIT_COCONUT(0x45a),
    COCONUT_SCORE(0x45b),
    MONSTER_CARNIVAL_START(0x45e),
    MONSTER_CARNIVAL_OBTAINED_CP(0x45f),
    MONSTER_CARNIVAL_STATS(0x460),
    MONSTER_CARNIVAL_SUMMON(0x461),
    MONSTER_CARNIVAL_MESSAGE(0x462),
    MONSTER_CARNIVAL_DIED(0x463),
    MONSTER_CARNIVAL_LEAVE(0x464),
    MONSTER_CARNIVAL_RESULT(0x465),
    MONSTER_CARNIVAL_RANKING(0x466),
    SPAWN_RUNE(0x4cc),
    REMOVE_RUNE(0x4cd),
    RESPAWN_RUNE(0x4ce),
    DEMIAN_FLYING_SWORD_CREATE(0x4e0),
    DEMIAN_FLYING_SWORD_MAKEENTER_REQUEST(0x4e1),
    DEMIAN_FLYING_SWORD_MAKE_ENTER_INFO(0x4e2),
    DEMIAN_FLYING_SWORD_NODE(0x4e3),
    DEMIAN_FLYING_SWORD_TARGET(0x4e4),
    DEMIAN_CORRUPTION(0x4e5),
    SCENE_UI(0x513),
    NPC_TALK(0x55e),
    OPEN_NPC_SHOP(0x55f),
    CONFIRM_SHOP_TRANSACTION(0x560),
    OPEN_STORAGE(0x577),
    MERCH_ITEM_MSG(0x578),
    MERCH_ITEM_STORE(0x579),
    RPS_GAME(0x57b),
    MESSENGER(0x57c),
    PLAYER_INTERACTION(0x57e),
    WEDDING_PROGRESS(0x586),
    WEDDING_CREMONY_END(0x587),
    SHOW_CHANGE_POTENTIAL_MESO(0x589),
    SHOW_CHANGE_POTENTIAL_RESULT(0x58a),
    SHOW_ZERO_WEAPON_INFO(0x58d),
    DUEY(0x5a4),
    CS_UPDATE(0x5a6),
    CS_OPERATION(0x5a7),
    CS_UPDATE_MESO(0x5a8),
    CS_COLLOCATION(0x5a9),
    CS_POTIONS(0x5b1),
    CS_POTION_POT_UPDATE(0x5bc),
    LOBBY_TIME_ACTION(0x5c9),
    SIGIN_INFO(0x5d2),
    MIEMIE_RANKING(0x5de),
    SHOW_POTENTIAL_RESULT(0x5df),
    SHOW_BULLETIN_BOARD(0x5e0),
    WORLD_RECORD_EFFECT(0x5e6),
    KEYMAP(0x605),
    PET_AUTO_HP(0x606),
    PET_AUTO_MP(0x607),
    PET_AUTO_BUFF(0x608),
    PET_AUTO_UN(0x609),
    GOLD_HAMMER_RESULT(0x611),
    SHOW_SCROLL_TIP(0x7ffe),
    START_TV(0x7ffe),
    REMOVE_TV(0x7ffe),
    ENABLE_TV(0x7ffe),
    GET_MTS_TOKENS(0x7ffe),
    MTS_OPERATION(0x7ffe),
    SPAWN_ARROWS_TURRET(0x620),
    CANCEL_ARROWS_TURRET(0x622),
    ARROWS_TURRET_ACTION(0x625),
    BATTLE_STATISTICS(0x632),
    SHOW_VISITOR_RESULT(0x636),
    UPDATE_VISITOR_KILL(0x637),
    GIANT_BOSS_MAP(0x639),
    SHOW_PORTAL(0x63a),
    CS_HAMMER_RESPONSE(0x64a),
    PLATINUM_HAMMER(0x64d),
    DAY_OF_CHRONOSPHERE(0x650),
    ERROR_CHRONOSPHERE(0x651),
    TAP_JOY_INFO(0x653),
    TAP_JOY_DONE(0x654),
    TAP_JOY(0x655),
    TAP_JOY_NEXT_STAGE(0x656),
    LOGIN_MESSAGE(0x668),

    PAMS_SONG(-2),
    UNKNOWN(-2);

    static {
        reloadValues();
    }

    private int code = -2;

    SendPacketOpcode(int code) {
        this.code = code;
    }

    public static SendPacketOpcode getByType(int type) {
        for (SendPacketOpcode l : SendPacketOpcode.values()) {
            if (l.getValue() == type) {
                return l;
            }
        }
        return UNKNOWN;
    }

    public static Properties getDefaultProperties() throws IOException {
        Properties props = new Properties();
        FileInputStream fileInputStream = new FileInputStream("properties/sendops.properties");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "GBK");
        BufferedReader buff = new BufferedReader(inputStreamReader);
        props.load(buff);
        buff.close();
        inputStreamReader.close();
        fileInputStream.close();
        return props;
    }

    public static void reloadValues() {
        try {
            File file = new File("properties/sendops.properties");
            if (file.exists()) {
                ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
            }
        } catch (IOException e) {
            throw new RuntimeException("加载 sendops.properties 文件出现错误", e);
        }
    }

    @Override
    public short getValue() {
        return (short) code;
    }

    @Override
    public void setValue(short code) {
        this.code = code;
    }
}
