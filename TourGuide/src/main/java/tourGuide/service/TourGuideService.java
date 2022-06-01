package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;


import tourGuide.DTO.RecentUserLocationDto;
import tourGuide.DTO.UserPreferencesDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.DTO.NearestAttractionDto;


import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;


@Service
public class TourGuideService {

    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    @Autowired
    private  RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        Locale.setDefault(Locale.US);
        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }
    public List<UserReward> getUserRewards(User user) {
        // la liste des rewards
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        // si les lieux visités par un user est>0
        //on choisit sa derniere location visité
        //sinon trackUserLocation
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    public VisitedLocation trackUserLocation(User user) {
        // a partir de gps utilise on recupere la location du user
        //on l'ajoute aux endroits visités
        //on calcule le nombre de rewards pour ce user et on retourne la location visite
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public User getUser(String userName) {
        //on recupere le user present dans internalUserMap
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        // on recupere la liste de tout les users(valeur de internalUserMap)
        // on on les convertit en string
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        // si internalUserMap ne contient pas ce userName
        // on l'ajoute a internalUserMap
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        //fait la somme de reward d'un user
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        //provider contient le price
        //avec une api ket tripPricerApiKey
        //le id du user,sa preference(adultes,enfants,duree du voyage, et le cumul des points
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public NearbyAttractionDto getNearByAttractions(VisitedLocation visitedLocation) {

        List<NearestAttractionDto> nearestAttractionDtoList = new ArrayList<>();

        gpsUtil.getAttractions().forEach(attraction -> {
                    NearestAttractionDto nearestAttractionDto = new NearestAttractionDto(
                            attraction.attractionName,
                            attraction,
                            rewardsService.getDistance(attraction,visitedLocation.location),
                            rewardsService.getRewardPoints(attraction,visitedLocation.userId)
                    );

                    nearestAttractionDtoList.add(nearestAttractionDto);
                }
        );

        NearbyAttractionDto nearbyAttractionDto = new NearbyAttractionDto();
        nearbyAttractionDto.setUserLocation(visitedLocation.location);
        nearbyAttractionDto.setClosestAttractionsList(nearestAttractionDtoList
                .stream()
                .sorted(Comparator.comparingDouble(NearestAttractionDto::getDistanceUserToAttraction))
                .limit(5).collect(Collectors.toList())
        );

        return  nearbyAttractionDto;

    }
    public List<RecentUserLocationDto> getAllUsersCurrentLocation() {
        List<User> userList = this.getAllUsers();
        List<RecentUserLocationDto> recentUserLocationDtos = new CopyOnWriteArrayList<>();

        for (User user : userList) {
            recentUserLocationDtos.add(new RecentUserLocationDto(user.getUserId().toString(), user.getUserName(), user.getLastVisitedLocation().location));
        }

        return recentUserLocationDtos;
    }

    public UserPreferences setUserPreferences(String username, UserPreferences userPreferences) {
        User user = getUser(username);

        if (user != null) {
            user.setUserPreferences(userPreferences);
            return user.getUserPreferences();
        }
        return null;
    }

    public UserPreferences userUpdatePreferences(String userName, UserPreferencesDto userPreferencesDTO) {
        User user = getUser(userName);
        user.setUserPreferences(new UserPreferences(userPreferencesDTO));
        return user.getUserPreferences();
    }



    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i-> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}

