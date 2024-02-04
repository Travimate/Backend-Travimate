package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightDataRequestDTO;
import id.synrgy.travimate.dto.request.FlightRequestDTO;
import id.synrgy.travimate.dto.response.FlightDTO;
import id.synrgy.travimate.dto.response.FlightDataDTO;
import id.synrgy.travimate.dto.response.FlightSearchDTO;
import id.synrgy.travimate.model.Airline;
import id.synrgy.travimate.model.Airport;
import id.synrgy.travimate.model.FlightData;
import id.synrgy.travimate.model.Route;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FlightService {
    Airport createAirport(Airport airport);

    List<Object> editAirlines(List<EditAirlineUrl> dtos);

    Airport findAirportByIATACode (String iataCode);
    Airline findAirlineByIATACode (String iataCode);
    Optional<Route> findRouteById (String id);

    List<Object> createFlightDataDTO(List<FlightDataRequestDTO> flightDataRequestDTOList);

//    FlightDataDTO createFlightData(FlightDataRequestDTO flightDataRequest);

    Route createRoutes(String airline, String dep, String arr,
                       String connectingAirport, FlightData flightData);

//    FlightDTO createFlight(String dep, String arr, String airline,
//                           int flightNumber, String flightClass, Date dof, LocalTime depTime,
//                           LocalTime arrTime, Integer stock);

    Set<FlightSearchDTO> searchFlightResult(String dep, String arr, Date dateDep, Date dateArr, String flightClass,
                                            Boolean isAroundTrip);

    List<Object> createFlight(List<FlightRequestDTO> flightRequestDTOList);

//    FlightDTO createFlightWithDTO(FlightRequest flightRequest);
}
