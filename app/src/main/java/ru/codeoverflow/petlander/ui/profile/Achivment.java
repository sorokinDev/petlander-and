package ru.codeoverflow.petlander.ui.profile;

public class Achivment {
    private String nameAch;
    private String descriptionAch;
    private String url;
    public Achivment(String nameAch,String descriptionAch,String url){
        this.nameAch = nameAch;
        this.descriptionAch = descriptionAch;
        this.url = url;
    }
    public String getNameAch(){
        return nameAch;
    }

    public String getDescriptionAch() {
        return descriptionAch;
    }

    public void setDescriptionAch(String descriptionAch) {
        this.descriptionAch = descriptionAch;
    }

    public void setNameAch(String nameAch) {
        this.nameAch = nameAch;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
