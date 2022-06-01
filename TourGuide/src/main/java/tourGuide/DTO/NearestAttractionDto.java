package tourGuide.DTO;

import gpsUtil.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NearestAttractionDto {
    private String attractionName;
    private Location attractionLocation;
    private double distanceUserToAttraction;
    private int rewardPoints;
}
