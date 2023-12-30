package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.driver.model.SpotType.*;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user=userRepository3.findById(userId).get();
        if(user==null) throw new Exception("Cannot make reservation");

        ParkingLot parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        if(parkingLot==null) throw new Exception("Cannot make reservation");

        List<Spot> spotList=new ArrayList<>();
        for(Spot spot:parkingLot.getSpotList()) {
            int wheels=Integer.MAX_VALUE;
            if(spot.getSpotType()==TWO_WHEELER) wheels=2;
            else if(spot.getSpotType()==FOUR_WHEELER) wheels=4;

            if(numberOfWheels<=wheels) {
                spotList.add(spot);
            }
        }

        if(spotList.isEmpty()) throw new Exception("Cannot make reservation");

        spotList.sort(Comparator.comparingInt(Spot::getPricePerHour));
        Spot spot=spotList.get(0);
        spot.setOccupied(true);

        Reservation reservation=new Reservation();
        reservation.setId(userId);
        reservation.setSpot(spot);
        reservation.setNumberOfHours(timeInHours);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(spot);
        parkingLotRepository3.save(parkingLot);
        return reservationRepository3.save(reservation);
    }
}
