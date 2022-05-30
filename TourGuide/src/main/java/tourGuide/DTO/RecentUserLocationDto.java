package tourGuide.DTO;

import gpsUtil.location.Location;

public class RecentUserLocationDto {
    private String userId;
    private Location userLocation;
    private  String userName;

    public RecentUserLocationDto(String userId, String userName, Location location) {
        this.userId = userId;
        this.userName = userName;
        this.userLocation = location;
    }
}
