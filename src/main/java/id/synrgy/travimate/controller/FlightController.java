package id.synrgy.travimate.controller;


import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightRequest;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightService flightService;
    @Autowired
    FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @PostMapping("/flight")
    public ResponseEntity<Object> createFlight(
                                               @RequestParam String dep,
                                               @RequestParam String arr,
                                               @RequestParam String airline,
                                               @RequestParam int flightNumber,
                                               @RequestParam String flightClass,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date dof,
                                               @RequestParam @DateTimeFormat(pattern = "HH.mm") LocalTime depTime,
                                               @RequestParam @DateTimeFormat(pattern = "HH.mm") LocalTime arrTime,
                                               @RequestParam Integer stock){
        return ResponseHandler.generateResponseSuccess(flightService.createFlight(
                dep, arr, airline, flightNumber, flightClass, dof, depTime, arrTime, stock));
    }

    @PostMapping("/dto-flight")
    public ResponseEntity<Object> createFlight(@RequestBody FlightRequest flightRequest){
        return ResponseHandler.generateResponseSuccess(flightService.createFlightWithDTO(flightRequest));
    }

//    @PostMapping("/route")
//    public ResponseEntity<?> createRoute(@RequestParam String airline,
//                                         @RequestParam String dep,
//                                         @RequestParam String arr,
//                                         @RequestParam(required = false) String connectingAirport){
//        return ResponseHandler.generateResponseSuccess(flightService.createRoutes(airline, dep, arr, connectingAirport));
//    }

    @PostMapping("/flight-data")
    public ResponseEntity<?> createFlightData(@RequestParam String airline,
                                              @RequestParam String dep,
                                              @RequestParam String arr,
                                              @RequestParam Long adultFare,
                                              @RequestParam(required = false) Long childFare,
                                              @RequestParam Boolean sameAsAdult,
                                              @RequestParam String flightClass,
                                              @RequestParam(required = false) Integer stops,
                                              @RequestParam(required = false) String connectingAirport,
                                              @RequestParam boolean isDirect){
        return ResponseHandler.generateResponseSuccess(flightService.createFlightData(airline,
                dep, arr, stops, adultFare, childFare, sameAsAdult, flightClass, connectingAirport, isDirect));
    }

    @GetMapping("/find-journey")
    public ResponseEntity<?> findJourney(@RequestParam String dep,
                                         @RequestParam String arr,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDep,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateArr,
                                         @RequestParam String flightClass,
                                         @RequestParam boolean isAroundTrip){
        return ResponseHandler.generateResponseSuccess(flightService.searchFlightResult(dep, arr, dateDep,
                dateArr, flightClass, isAroundTrip));
    }

    @PostMapping("/edit-airline")
    public ResponseEntity<Object> createAirline(@RequestBody EditAirlineUrl airline){
        return ResponseHandler.generateResponseSuccess(flightService.editAirline(airline));
    }
    @GetMapping("/search-airline")
    public ResponseEntity<Object> searchAirline(@RequestParam String iataCode){
        return ResponseHandler.generateResponseSuccess(flightService.findAirlineByIATACode(iataCode));
    }
//    @PostMapping("/airport")
//    public ResponseEntity<Object> createAirport(Airport airport){
//        return ResponseHandler.generateResponseSuccess(flightService.createAirport(airport));
//    }

}
