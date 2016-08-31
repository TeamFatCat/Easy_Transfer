package com.fatcat.easy_transfer.entity;

/**
 * Created by FatCat on 2016/8/3.
 */
public class Mp3Info {

    private long id;//歌曲ID 3
    private String title;//歌曲名称 0
    private String album;//专辑 7
    private long albumId;//专辑ID 6
    private String displayName;//显示名称 4
    private String artist;//歌手名称 2
    private long duration;//歌曲时长 1
    private long size;//歌曲大小 8
    private String url;//歌曲路径 5

    public Mp3Info(long id, String title, String album, long albumId, String displayName, String artist, long duration, long size, String url) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.albumId = albumId;
        this.displayName = displayName;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.url = url;
    }

    public  Mp3Info(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


}
