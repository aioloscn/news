package com.aiolos.news.pojo.vo;

/**
 * @author Aiolos
 * @date 2020/10/22 7:20 下午
 */
public class UserBasicInfoVO {

    private String id;

    private String nickname;

    private String face;

    private Integer activeStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }
}
