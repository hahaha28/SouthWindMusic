package com.dss.swmusic.entity;

import android.net.Uri;
import java.io.Serializable;

/**
 * 一首曲子
 */
public class Song implements Serializable {
    /**歌曲名*/
    private String name;
    /**路径*/
    private String path;
    /**所属专辑*/
    private String album;
    /**艺术家(作者)*/
    private String artist;
    /**文件大小*/
    private long size;
    /**时长*/
    private int duration;
    /**父文件夹*/
    private String parent;
    /**uri字符串*/
    private String uriStr;
    /**
     * 歌曲类型：0表示本地，1表示网络
     */
    private boolean songType;
    /**
     * 歌曲封面图片url
     */
    private String coverImageUrl;


    /**
     * @param name 歌曲名
     * @param path 路径
     * @param album 专辑
     * @param artist 艺术家
     * @param size 文件大小
     * @param duration 时长
     * @param parent 父文件夹
     * @param uriStr uri字符串
     */
    public Song(String name, String path, String album, String artist, long size, int duration, String parent, String uriStr) {
        this.name = name;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.size = size;
        this.duration = duration;
        this.parent = parent;
        this.uriStr = uriStr;

    }

    public Song() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getUriStr() {
        return uriStr;
    }

    public void setUriStr(String uriStr) {
        this.uriStr = uriStr;
    }

    public boolean isSongType() {
        return songType;
    }

    public void setSongType(boolean songType) {
        this.songType = songType;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}