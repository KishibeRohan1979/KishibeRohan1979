package com.tzp.myWebTest.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class BilibiliComment implements Serializable {

    @ApiModelProperty(value = "评论的id", name = "rpid")
    private String rpid;

    @ApiModelProperty(value = "评论的创建时间", name = "ctime")
    private Long ctime;

    @ApiModelProperty(value = "评论的回复数量", name = "rcount")
    private Integer rcount;

    @ApiModelProperty(value = "评论的点赞数", name = "like")
    private Integer like;

    @ApiModelProperty(value = "评论者的uid", name = "userid")
    private String userid;

    @ApiModelProperty(value = "评论者的昵称", name = "uname")
    private String uname;

    @ApiModelProperty(value = "评论者的性别", name = "sex")
    private String sex;

    @ApiModelProperty(value = "评论者的个人简介", name = "sign")
    private String sign;

    @ApiModelProperty(value = "记录用户是不是高级会员（小闪电）1，是；0，不是", name = "isSeniorMember")
    private Integer isSeniorMember;

    @ApiModelProperty(value = "评论者等级", name = "userLevel")
    private Integer userLevel;

    @ApiModelProperty(value = "评论者头像的渲染信息", name = "userSailing")
    private Map<String, Object> userSailing;

    @ApiModelProperty(value = "评论者的头像地址", name = "avatar")
    private String avatar;

    @ApiModelProperty(value = "是不是up的老粉", name = "isContractor")
    private Boolean isContractor;

    @ApiModelProperty(value = "老粉牌子的称呼", name = "contractDesc")
    private String contractDesc;

    // 记录会员信息（type=0 非会员， type=1 大会员， type=2 年度大会员），需要注意的是4月1日可能会不同，但是我没办法测试了
    @ApiModelProperty(value = "评论者的会员类型", name = "vipType")
    private Integer vipType;

    @ApiModelProperty(value = "记录现在是不是会员", name = "vipStatus")
    private Integer vipStatus;

    @ApiModelProperty(value = "vip渲染信息", name = "vipLabel")
    private Map<String, Object> vipLabel;

    @ApiModelProperty(value = "评论者的粉丝牌渲染信息", name = "fansDetail", notes = "注意：有些用户可能没有带粉丝牌，返回为空，该值包括一些其他信息")
    private Map<String, Object> fansDetail;

    @ApiModelProperty(value = "评论者的粉丝牌等级", name = "fansLevel")
    private Integer fansLevel;

    @ApiModelProperty(value = "评论者的粉丝牌内容", name = "fansName")
    private String fansName;

    @ApiModelProperty(value = "评论者的论点（即评论内容）", name = "thisUserMessage")
    private String thisUserMessage;

    @ApiModelProperty(value = "评论的图片列表", name = "pictures")
    private List<Object> pictures;

    @ApiModelProperty(value = "评论的渲染信息", name = "messageEmote")
    private Map<String, Object> messageEmote;

    @ApiModelProperty(value = "这个评论up是不是点赞了（true或false）", name = "isUpLikeThisMessage")
    private Boolean isUpLikeThisMessage;

    @ApiModelProperty(value = "这个评论up有没有回复（true或false）", name = "isUpReplyThisMessage")
    private Boolean isUpReplyThisMessage;

    @ApiModelProperty(value = "记录评论是不是置顶", name = "isTop")
    private boolean isTop;

    @ApiModelProperty(value = "up主的uid", name = "upUid")
    private String upperUid;

    @ApiModelProperty(value = "视频的avid", name = "avid")
    private String avid;
//
//    @ApiModelProperty(value = "视频的bvid", name = "bvid")
//    private String bvid;
//
//    @ApiModelProperty(value = "是否可见，0可见，1不可见", name = "isDelete")
//    private Integer isDelete;

}
