package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.driver.model.PaymentMode.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        //Attempt a payment of amountSent for reservationId using the given mode ("cASh", "card", or "upi")
        //If the amountSent is less than bill, throw "Insufficient Amount" exception, otherwise update payment attributes
        //If the mode contains a string other than "cash", "card", or "upi" (any character in uppercase or lowercase), throw "Payment mode not detected" exception.
        //Note that the reservationId always exists
        PaymentMode paymentMode=null;
        if(mode.equalsIgnoreCase("cash")) paymentMode=CASH;
        else if(mode.equalsIgnoreCase("card")) paymentMode=CARD;
        else if(mode.equalsIgnoreCase("upi")) paymentMode=UPI;
        else throw new Exception("Payment mode not detected");

        Reservation reservation=reservationRepository2.findById(reservationId).get();
        Spot spot=reservation.getSpot();
        int pricePerHour=spot.getPricePerHour();
        int numberOfHours=reservation.getNumberOfHours();
        int bill=pricePerHour*numberOfHours;

        Payment payment=new Payment();
        if(amountSent<bill) throw new Exception("Insufficient Amount");
        payment.setPaymentMode(paymentMode);
        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return paymentRepository2.save(payment);
    }
}
