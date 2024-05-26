package model;

import jakarta.xml.bind.annotation.XmlAnyElement;

import java.util.List;

public class ImageCollection {
    @XmlAnyElement
    private List<Visit> visits;

    public ImageCollection() {}

    public ImageCollection(List<Visit> visits) {
        this.visits = visits;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
}
