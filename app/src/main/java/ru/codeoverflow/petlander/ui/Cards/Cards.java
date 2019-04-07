package ru.codeoverflow.petlander.ui.Cards;

public class Cards {

    private String petsID;
    private String name;
    private String description;
    private String location;
    private String profileImageUrl;
    private String userID;

    public Cards(String petsID, String name, String profileImageUrl, String userID, String desc, String location){
        this.petsID = petsID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.userID = userID;
        this.description = desc;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPetsID(){
        return petsID;
    }

    public void setPetsID(String param){
        this.petsID = petsID;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }


    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserID(){
        return userID;
    }

    public void setUserID(String param){
        this.userID = userID;
    }

}
