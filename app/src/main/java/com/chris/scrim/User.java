package com.chris.scrim;

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
    public User(String username, String firstName, int vitalizeRep, int profileImage, int avatarProfile) {
        this.username = username;
        this.firstName = firstName;
        this.vitalizeRep = vitalizeRep;
        this.avatarProfile = avatarProfile;
        this.profileImage = profileImage;
    }
    void setId(String id) {
        this.id = id;
    }
}
