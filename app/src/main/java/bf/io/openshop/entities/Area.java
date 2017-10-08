package bf.io.openshop.entities;

/**
 * Created by Arun1234 on 8/4/2017.
 */

public class Area
{
    private Long id;
    private String name;
    private String areaCode;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
