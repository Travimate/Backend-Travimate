package id.synrgy.travimate.controller;


import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightDataRequestDTO;
import id.synrgy.travimate.dto.request.FlightRequestDTO;
import id.synrgy.travimate.dto.response.ResponseHandler;
import id.synrgy.travimate.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightService flightService;
    @Autowired
    FlightController(FlightService flightService){
        this.flightService = flightService;
    }

    @PostMapping("/flight")
    public ResponseEntity<Object> createFlight(@RequestBody List<FlightRequestDTO> flightRequestDTO){
        return ResponseHandler.generateResponseSuccess(flightService.createFlight(flightRequestDTO));
    }

    @PostMapping("/flight-data")
    public ResponseEntity<?> createFlightData(@RequestBody List<FlightDataRequestDTO> flightDataDTOList){
        return ResponseHandler.generateResponseSuccess(flightService.createFlightDataDTO(flightDataDTOList));
    }

    @GetMapping("/find-journey")
    public ResponseEntity<?> findJourney(@RequestParam String dep,
                                         @RequestParam String arr,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDep,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateArr,
                                         @RequestParam String flightClass,
                                         @RequestParam(required = false) Boolean isAroundTrip){
        return ResponseHandler.generateResponseSuccess(flightService.searchFlightResult(dep, arr, dateDep,
                dateArr, flightClass, isAroundTrip));
    }

    @PostMapping("/edit-airline")
    public ResponseEntity<Object> createAirline(@RequestBody List<EditAirlineUrl> airline){
        return ResponseHandler.generateResponseSuccess(flightService.editAirlines(airline));
    }
    @GetMapping("/search-airline")
    public ResponseEntity<Object> searchAirline(@RequestParam String iataCode){
        return ResponseHandler.generateResponseSuccess(flightService.findAirlineByIATACode(iataCode));
    }


    //    @PostMapping("/flight-data")
//    public ResponseEntity<?> createFlightData(@RequestParam String airline,
//                                              @RequestParam String dep,
//                                              @RequestParam String arr,
//                                              @RequestParam Long adultFare,
//                                              @RequestParam(required = false) Long childFare,
//                                              @RequestParam Boolean sameAsAdult,
//                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date date,
//                                              @RequestParam String flightClass,
//                                              @RequestParam(required = false) Integer stops,
//                                              @RequestParam(required = false) String connectingAirport,
//                                              @RequestParam boolean isDirect){
//        return ResponseHandler.generateResponseSuccess(flightService.createFlightData(airline,
//                dep, arr, stops, adultFare, childFare, sameAsAdult, date, flightClass, connectingAirport, isDirect));
//    }


//    @PostMapping("/flight")
//    public ResponseEntity<Object> createFlight(
//                                               @RequestParam String dep,
//                                               @RequestParam String arr,
//                                               @RequestParam String airline,
//                                               @RequestParam int flightNumber,
//                                               @RequestParam String flightClass,
//                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date dof,
//                                               @RequestParam @DateTimeFormat(pattern = "HH.mm") LocalTime depTime,
//                                               @RequestParam @DateTimeFormat(pattern = "HH.mm") LocalTime arrTime,
//                                               @RequestParam Integer stock){
//        return ResponseHandler.generateResponseSuccess(flightService.createFlight(
//                dep, arr, airline, flightNumber, flightClass, dof, depTime, arrTime, stock));
//    }
}
