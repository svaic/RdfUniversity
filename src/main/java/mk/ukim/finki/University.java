package mk.ukim.finki;

public class University {
    String name;
    String image;
    String longitute;
    String latitude;
    String place;

    public University(String name, String image, String longitute, String latitude, String place) {
        this.name = name;
        this.image = image;
        this.longitute = longitute;
        this.latitude = latitude;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLongitute() {
        return longitute;
    }

    public void setLongitute(String longitute) {
        this.longitute = longitute;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
