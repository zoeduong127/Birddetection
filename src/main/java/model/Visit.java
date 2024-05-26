package model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;
import java.util.List;

public class Visit {
    @XmlAnyElement
    private int visitId;
    @XmlAnyElement
    private String species;
    @XmlAnyElement
    private Date arrival;
    @XmlAnyElement
    private Date departure;
    @XmlAnyElement
    private int visitLen;
    @XmlAnyElement
    private double accuracy;
    @XmlAnyElement
    private List<BirdImage> images;

    public Visit() {}

    public Visit(int visitId, String species, Date arrival, Date departure, int visitLen, double accuracy, List<BirdImage> images) {
        this.visitId = visitId;
        this.species = species;
        this.arrival = arrival;
        this.departure = departure;
        this.visitLen = visitLen;
        this.accuracy = accuracy;
        this.images = images;
    }

    public List<BirdImage> getImages() {
        return images;
    }

    public void setImages(List<BirdImage> images) {
        this.images = images;
    }

    public void addImage(BirdImage image) {
        this.images.add(image);
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public int getVisitLen() {
        return visitLen;
    }

    public void setVisitLen(int visitLen) {
        this.visitLen = visitLen;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
