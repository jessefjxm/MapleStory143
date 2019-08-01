/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.quest;

/**
 * @author PlayDK
 */
public enum MedalQuest {

    新手冒险家(29005, 29015, 15, new int[]{100000000, 100020400, 100040000, 101000000, 101020300, 101040300, 102000000, 102020500, 102030400, 102040200, 103000000, 103020200, 103030400, 103040000, 104000000, 104020000, 106020100, 120000000, 120020400, 120030000}),
    冰峰雪域山脉探险家(29006, 29012, 50, new int[]{200000000, 200010100, 200010300, 200080000, 200080100, 211000000, 211030000, 211040300, 211041200, 211041800}),
    时间静止之湖探险家(29007, 29012, 40, new int[]{222000000, 222010400, 222020000, 220000000, 220020300, 220040200, 221020701, 221000000, 221030600, 221040400}),
    海底探险家(29008, 29012, 40, new int[]{230000000, 230010400, 230010200, 230010201, 230020000, 230020201, 230030100, 230040000, 230040200, 230040400}),
    武陵探险家(29009, 29012, 50, new int[]{251000000, 251010200, 251010402, 251010500, 250010500, 250010504, 250000000, 250010300, 250010304, 250020300}),
    尼哈沙漠探险家(29010, 29012, 70, new int[]{261030000, 261020401, 261020000, 261010100, 261000000, 260020700, 260020300, 260000000, 260010600, 260010300}),
    神木村探险家(29011, 29012, 70, new int[]{240000000, 240010200, 240010800, 240020401, 240020101, 240030000, 240040400, 240040511, 240040521, 240050000}),
    林中之城探险家(29014, 29015, 50, new int[]{105000000, 105010100, 105020100, 105020300, 105020400, 105030000, 105030100, 105030200, 105030300, 105030500});
    public final int questid;
    public final int level;
    public final int lquestid;
    public final int[] maps;

    MedalQuest(int questid, int lquestid, int level, int[] maps) {
        this.questid = questid; //infoquest = questid -2005, customdata = questid -1995
        this.level = level;
        this.lquestid = lquestid;
        this.maps = maps; //note # of maps
    }
}
