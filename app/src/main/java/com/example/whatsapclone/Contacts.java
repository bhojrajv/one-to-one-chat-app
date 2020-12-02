package com.example.whatsapclone;

public class Contacts {
    public String name;
    public String status;
     public String image;

  public  Contacts(){

    }

    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgStatus() {
        return image;
    }

    public void setImgStatus(String imgStatus) {
        this.image = imgStatus;
    }
}
