package tourGuide.DTO;

import lombok.Getter;
import lombok.Setter;
import tourGuide.user.UserPreferences;

@Getter @Setter
public class UserPreferencesDto {
    private String username;
    private int attractionProximity;
    private String currency;
    private int lowerPricePoint;
    private int highPricePoint;
    private int tripDuration;
    private int ticketQuantity;
    private int numberOfAdults;
    private int numberOfChildren;


    public UserPreferencesDto() {
    }

    public UserPreferencesDto(String username, int attractionProximity, String currency, int lowerPricePoint,
                              int highPricePoint, int tripDuration, int ticketQuantity, int numberOfAdults,
                              int numberOfChildren) {
        this.username = username;
        this.attractionProximity = attractionProximity;
        this.currency = currency;
        this.lowerPricePoint = lowerPricePoint;
        this.highPricePoint = highPricePoint;
        this.tripDuration = tripDuration;
        this.ticketQuantity = ticketQuantity;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }

    public UserPreferencesDto(String username, UserPreferences userPreferences) {
        this.username = username;
        this.currency = userPreferences.getCurrency().toString();
        this.attractionProximity = userPreferences.getAttractionProximity();
        this.lowerPricePoint = userPreferences.getLowerPricePoint().getNumber().intValueExact();
        this.highPricePoint = userPreferences.getHighPricePoint().getNumber().intValueExact();
        this.tripDuration = userPreferences.getTripDuration();
        this.ticketQuantity = userPreferences.getTicketQuantity();
        this.numberOfAdults = userPreferences.getNumberOfAdults();
        this.numberOfChildren = userPreferences.getNumberOfChildren();
    }
}
