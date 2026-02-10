package com.project.bankwebapp.Services;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LocationService {

    private static final HashMap<String,double[]> CITY_COORD= new HashMap<>();

    static{
        CITY_COORD.put("New York, NY", new double[]{40.7128, -74.0060});
        CITY_COORD.put("London, UK", new double[]{51.5072, -0.1276});
        CITY_COORD.put("Paris, FR", new double[]{48.8566, 2.3522});
        CITY_COORD.put("Tokyo, JP", new double[]{35.6762, 139.6503});
        CITY_COORD.put("Berlin, DE", new double[]{52.5200, 13.4050});
        CITY_COORD.put("Sydney, AU", new double[]{-33.8623, 151.2077});
        CITY_COORD.put("Moscow, RU", new double[]{55.7558, 37.6173});
        CITY_COORD.put("Brisbane, AU", new double[]{-27.4698, 153.0251});
        CITY_COORD.put("Melbourne, AU", new double[]{-37.8136, 144.9631});
        CITY_COORD.put("Auckland, NZ", new double[]{-36.8485, 174.7633});
        CITY_COORD.put("Canberra, AU", new double[]{-35.2809, 149.1294});
        CITY_COORD.put("Perth, AU", new double[]{-31.9528, 115.8573});
    }

    public double getDistance(String city1, String city2){
        double[] coord1 = CITY_COORD.get(city1);
        double[] coord2 = CITY_COORD.get(city2);
        return calculateSphereDistance(coord1[0],coord1[1],coord2[0],coord2[1]);
    }

    // uses haversine formula which gives us accurate distance on a spheree
    public double calculateSphereDistance(double lat1,double lon1,double lat2,double lon2){
        final int RADIUS = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * c;
    }
}
