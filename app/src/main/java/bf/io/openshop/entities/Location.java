package bf.io.openshop.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arun1234 on 8/4/2017.
 */

public class Location
{
    private Long id;
    private String state;
    private String name;
    private String cityCode;
    private List<Area> areas;

    public Location() {
    }

    public String getGoogleUa() {
        return googleUa;
    }

    public void setGoogleUa(String googleUa) {
        this.googleUa = googleUa;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @SerializedName("google_ua")

    private String googleUa;
    private String language;
    private String currency;

    public Location(String testShop, String s) {
    }

    public Long getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
