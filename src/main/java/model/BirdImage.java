package model;

import jakarta.xml.bind.annotation.XmlAnyElement;

import java.sql.Date;

public class BirdImage {
    @XmlAnyElement
    private int imageId;
    @XmlAnyElement
    private int visitId;
    @XmlAnyElement
    private Date date;
    @XmlAnyElement
    private String image_path;


    public BirdImage() {}
    public BirdImage(int imageId, int visitId, Date date, String image_path) {
        this.imageId = imageId;
        this.visitId = visitId;
        this.date = date;
        this.image_path = image_path;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
