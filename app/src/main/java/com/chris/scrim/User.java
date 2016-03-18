package com.chris.scrim;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 3/1/2016.
 */
public class User {
    String id;
    String username;
    String firstName;
    int vitalizeRep;
    int avatarProfile;
    int profileImage;
    private List<String> favoriteList;

    public User(String username, String firstName, int vitalizeRep, int profileImage, int avatarProfile) {
        this.username = username;
        this.firstName = firstName;
        this.vitalizeRep = vitalizeRep;
        this.avatarProfile = avatarProfile;
        this.profileImage = profileImage;
        this.favoriteList = new ArrayList<>();
    }
    public void setUsername(String username) {
        this.username = username;
        avatarProfile = VitalizeApplication.getAvatarImage(username);
        profileImage = VitalizeApplication.getAvatarImage(username);
    }
    void setId(String id) {
        this.id = id;
    }
    public List<String> getFavoriteList(){
        return favoriteList;
    }

    public void addFavorite(ScrimArea theArea) {
        String name = theArea.getId();
        if (!favoriteList.contains(name)) {
            favoriteList.add(name);
        }
    }

    public void deleteFavorite(ScrimArea theArea) {
        String name = theArea.getId();
        if (favoriteList.contains(name)) {
            favoriteList.remove(name);
        }
    }
}
