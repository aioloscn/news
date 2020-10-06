package com.aiolos.news.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Fans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String id;

    /**
     * 作家用户id
     */
    @Column(name = "writer_id")
    private String writerId;

    /**
     * 粉丝用户id
     */
    @Column(name = "fan_id")
    private String fanId;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 粉丝昵称
     */
    @Column(name = "fan_nickname")
    private String fanNickname;

    /**
     * 粉丝性别
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取作家用户id
     *
     * @return writer_id - 作家用户id
     */
    public String getWriterId() {
        return writerId;
    }

    /**
     * 设置作家用户id
     *
     * @param writerId 作家用户id
     */
    public void setWriterId(String writerId) {
        this.writerId = writerId == null ? null : writerId.trim();
    }

    /**
     * 获取粉丝用户id
     *
     * @return fan_id - 粉丝用户id
     */
    public String getFanId() {
        return fanId;
    }

    /**
     * 设置粉丝用户id
     *
     * @param fanId 粉丝用户id
     */
    public void setFanId(String fanId) {
        this.fanId = fanId == null ? null : fanId.trim();
    }

    /**
     * 获取粉丝头像
     *
     * @return face - 粉丝头像
     */
    public String getFace() {
        return face;
    }

    /**
     * 设置粉丝头像
     *
     * @param face 粉丝头像
     */
    public void setFace(String face) {
        this.face = face == null ? null : face.trim();
    }

    /**
     * 获取粉丝昵称
     *
     * @return fan_nickname - 粉丝昵称
     */
    public String getFanNickname() {
        return fanNickname;
    }

    /**
     * 设置粉丝昵称
     *
     * @param fanNickname 粉丝昵称
     */
    public void setFanNickname(String fanNickname) {
        this.fanNickname = fanNickname == null ? null : fanNickname.trim();
    }

    /**
     * 获取粉丝性别
     *
     * @return sex - 粉丝性别
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置粉丝性别
     *
     * @param sex 粉丝性别
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取省份
     *
     * @return province - 省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }
}