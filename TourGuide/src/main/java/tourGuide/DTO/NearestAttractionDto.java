package tourGuide.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import gpsUtil.location.Location;

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
