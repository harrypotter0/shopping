package bf.io.openshop.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Arun1234 on 8/4/2017.
 */

public class LocationResponse
{
    Metadata metadata;

    @SerializedName("records")
    List<Location> locations;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
