package handling.opcode

import handling.WritableIntValueHolder

enum class GuildOpcode(private var code: Short) : WritableIntValueHolder {

    GuildReq_LoadGuild(0),
    GuildReq_FindGuildByCid(1),
    GuildReq_FindGuildByGID(2),
    GuildReq_InputGuildName(3),
    GuildReq_CheckGuildName(4),
    GuildReq_CreateGuildAgree(5),
    GuildReq_CreateNewGuild(6),
    GuildReq_InviteGuild(7),
    GuildReq_JoinGuild(8),
    GuildReq_JoinGuildDirect(9),
    GuildReq_UpdateJoinState(10),
    GuildReq_WithdrawGuild(11),
    GuildReq_KickGuild(12),
    GuildReq_RemoveGuild(13),
    GuildReq_IncMaxMemberNum(14),
    GuildReq_ChangeLevel(15),
    GuildReq_ChangeJob(16),
    GuildReq_SetGuildName(17),
    GuildReq_SetGradeName(18),
    GuildReq_SetMemberGrade(19),
    GuildReq_SetMark(20),
    GuildReq_SetNotice(21),
    GuildReq_InputMark(22),
    GuildReq_CheckQuestWaiting(23),
    GuildReq_CheckQuestWaiting2(24),
    GuildReq_InsertQuestWaiting(25),
    GuildReq_CancelQuestWaiting(26),
    GuildReq_RemoveQuestCompleteGuild(27),
    GuildReq_IncPoint(28),
    GuildReq_IncCommitment(29),
    GuildReq_DecGGP(30),
    GuildReq_DecIGP(31),
    GuildReq_SetQuestTime(32),
    GuildReq_ShowGuildRanking(33),
    GuildReq_SetSkill(34),
    GuildReq_SkillLevelSetUp(35),
    GuildReq_ResetGuildBattleSkill(36),
    GuildReq_UseActiveSkill(37),
    GuildReq_UseADGuildSkill(38),
    GuildReq_ExtendSkill(39),
    GuildReq_ChangeGuildMaster(40),
    GuildReq_FromGuildMember_GuildSkillUse(41),
    GuildReq_SetGGP(42),
    GuildReq_SetIGP(43),
    GuildReq_BattleSkillOpen(44),
    GuildReq_Search(45),
    GuildReq_CreateNewGuild_Block(46),
    GuildReq_CreateNewAlliance_Block(47),
    GuildRes_CreateGuildAgree_Reply(53),
    GuildRes_LoadGuild_Done(48),
    GuildRes_FindGuild_Done(49),
    GuildRes_CheckGuildName_Available(50),
    GuildRes_CheckGuildName_AlreadyUsed(51),
    GuildRes_CheckGuildName_Unknown(52),
    GuildRes_CreateNewGuild_Done(55),
    GuildRes_CreateNewGuild_AlreayJoined(56),
    GuildRes_CreateNewGuild_GuildNameAlreayExist(57),
    GuildRes_CreateNewGuild_Beginner(58),
    GuildRes_CreateNewGuild_Disagree(59),
    GuildRes_CreateNewGuild_NotFullParty(60),
    GuildRes_CreateNewGuild_Unknown(61),
    GuildRes_JoinGuild_Done(62),
    GuildRes_JoinGuild_AlreadyJoined(63),
    GuildRes_JoinGuild_AlreadyFull(64),
    GuildRes_JoinGuild_UnknownUser(65),
    GuildRes_JoinGuild_NonRequestFindUser(66),
    GuildRes_JoinGuild_Unknown(67),
    GuildRes_JoinRequest_Done(70),
    GuildRes_JoinRequest_DoneToUser(71),
    GuildRes_JoinRequest_AlreadyFull(72),
    GuildRes_JoinRequest_LimitTime(73),
    GuildRes_JoinRequest_Unknown(74),
    GuildRes_JoinCancelRequest_Done(75),
    GuildRes_WithdrawGuild_Done(76),
    GuildRes_WithdrawGuild_NotJoined(77),
    GuildRes_WithdrawGuild_Unknown(78),
    GuildRes_KickGuild_Done(79),
    GuildRes_KickGuild_NotJoined(80),
    GuildRes_KickGuild_Unknown(81),
    GuildRes_RemoveGuild_Done(82),
    GuildRes_RemoveGuild_NotExist(83),
    GuildRes_RemoveGuild_Unknown(84),
    GuildRes_RemoveRequestGuild_Done(85),
    GuildRes_InviteGuild_BlockedUser(86),
    GuildRes_InviteGuild_AlreadyInvited(87),
    GuildRes_InviteGuild_Rejected(88),
    GuildRes_AdminCannotCreate(89),
    GuildRes_AdminCannotInvite(90),
    GuildRes_IncMaxMemberNum_Done(91),
    GuildRes_IncMaxMemberNum_Unknown(92),
    GuildRes_ChangeMemberName(93),
    GuildRes_ChangeRequestUserName(94),
    GuildRes_ChangeLevelOrJob(95),
    GuildRes_NotifyLoginOrLogout(96),
    GuildRes_SetGradeName_Done(97),
    GuildRes_SetGradeName_Unknown(98),
    GuildRes_SetMemberGrade_Done(99),
    GuildRes_SetMemberGrade_Unknown(100),
    GuildRes_SetMemberCommitment_Done(101),
    GuildRes_SetMark_Done(102),
    GuildRes_SetMark_Unknown(103),
    GuildRes_SetNotice_Done(104),
    GuildRes_InsertQuest(105),
    GuildRes_NoticeQuestWaitingOrder(106),
    GuildRes_SetGuildCanEnterQuest(107),
    GuildRes_IncPoint_Done(108),
    GuildRes_ShowGuildRanking(109),
    GuildRes_SetGGP_Done(110),
    GuildRes_SetIGP_Done(111),
    GuildRes_GuildQuest_NotEnoughUser(112),
    GuildRes_GuildQuest_RegisterDisconnected(113),
    GuildRes_GuildQuest_NoticeOrder(114),
    GuildRes_Authkey_Update(115),
    GuildRes_SetSkill_Done(116),
    GuildRes_SetSkill_Extend_Unknown(117),
    GuildRes_SetSkill_LevelSet_Unknown(118),
    GuildRes_SetSkill_ResetBattleSkill(119),
    GuildRes_UseSkill_Success(120),
    GuildRes_UseSkill_Err(121),
    GuildRes_ChangeName_Done(122),
    GuildRes_ChangeName_Unknown(123),
    GuildRes_ChangeMaster_Unknown(125),
    GuildRes_BlockedBehaviorCreate(126),
    GuildRes_BlockedBehaviorJoin(128),
    GuildRes_ChangeMaster_Done(124),
    GuildRes_BattleSkillOpen(128);

    override fun getValue(): Short {
        return code
    }

    override fun setValue(newval: Short) {
        code = newval
    }

    companion object {
        fun getByAction(code: Short): GuildOpcode? {
            return GuildOpcode.values().firstOrNull { it.code == code }
        }
    }
}