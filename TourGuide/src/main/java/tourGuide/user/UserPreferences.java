package tourGuide.user;

import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;
import tourGuide.DTO.UserPreferencesDto;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Getter @Setter
public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
	
	public UserPreferences() {
	}

	public UserPreferences(UserPreferencesDto userPreferencesDTO) {
		this.attractionProximity = userPreferencesDTO.getAttractionProximity();
		this.currency = Monetary.getCurrency(userPreferencesDTO.getCurrency());
		this.lowerPricePoint = Money.of(userPreferencesDTO.getLowerPricePoint(), currency);
		this.highPricePoint = Money.of(userPreferencesDTO.getHighPricePoint(), currency);
		this.tripDuration = userPreferencesDTO.getTripDuration();
		this.ticketQuantity = userPreferencesDTO.getTicketQuantity();
		this.numberOfAdults = userPreferencesDTO.getNumberOfAdults();
		this.numberOfChildren = userPreferencesDTO.getNumberOfChildren();
	}
	

}
