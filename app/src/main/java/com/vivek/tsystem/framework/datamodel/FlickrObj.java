package com.vivek.tsystem.framework.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vivek on 24/03/18.
 */

public class FlickrObj {

    @SerializedName("photos")
    private PhotosWrapper photosWrapper;

    @SerializedName("stat")
    private String stat;

    public PhotosWrapper getPhotosWrapper() {
        return photosWrapper;
    }

    public void setPhotosWrapper(PhotosWrapper photosWrapper) {
        this.photosWrapper = photosWrapper;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public static class PhotosWrapper {

        @SerializedName("page")
        private long page;

        @SerializedName("pages")
        private long pages;

        @SerializedName("perpage")
        private long perpage;

        @SerializedName("total")
        private String total;

        @SerializedName("photo")
        private List<Photo> photo = null;

        public long getPage() {
            return page;
        }

        public void setPage(long page) {
            this.page = page;
        }

        public long getPages() {
            return pages;
        }

        public void setPages(long pages) {
            this.pages = pages;
        }

        public long getPerpage() {
            return perpage;
        }

        public void setPerpage(long perpage) {
            this.perpage = perpage;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<Photo> getPhoto() {
            return photo;
        }

        public void setPhoto(List<Photo> photo) {
            this.photo = photo;
        }

    }

    public static class Photo {

        public Photo() {
        }

        public Photo(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        @SerializedName("id")
        private String id;
        
        @SerializedName("owner")
        private String owner;
        
        @SerializedName("secret")
        private String secret;
        
        @SerializedName("server")
        private String server;

        @SerializedName("farm")
        private int farm;

        @SerializedName("title")
        private String title;

        @SerializedName("ispublic")
        private int isPublic;

        @SerializedName("isfriend")
        private int isFriend;

        @SerializedName("isfamily")
        private int isFamily;

        private String photoUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public int getFarm() {
            return farm;
        }

        public void setFarm(int farm) {
            this.farm = farm;
        }

        public int getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(int isPublic) {
            this.isPublic = isPublic;
        }

        public int getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(int isFriend) {
            this.isFriend = isFriend;
        }

        public int getIsFamily() {
            return isFamily;
        }

        public void setIsFamily(int isFamily) {
            this.isFamily = isFamily;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }

}
