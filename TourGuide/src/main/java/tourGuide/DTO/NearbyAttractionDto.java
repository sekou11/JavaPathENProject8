package tourGuide.DTO;


import gpsUtil.location.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NearbyAttractionDto {

	private Location userLocation;
	private List<NearestAttractionDto> closestAttractionsList;

}



