package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightDataRequestDTO;
import id.synrgy.travimate.dto.request.FlightRequestDTO;
import id.synrgy.travimate.dto.response.FlightDTO;
import id.synrgy.travimate.dto.response.FlightSearchDTO;
import id.synrgy.travimate.model.*;

import java.time.LocalDate;
import java.util.*;

public interface FlightService {

    FlightDTO mapToDTO(Flight flight);

    List<Object> editAirlines(List<EditAirlineUrl> dtos);
    Airport findAirportByIATACode (String iataCode);
    Airline findAirlineByIATACode (String iataCode);

    List<Object> createFlightDataDTO(List<FlightDataRequestDTO> flightDataRequestDTOList);

    Route createRoutes(String airline, String dep, String arr,
                       String connectingAirport, FlightData flightData);

    Set<FlightSearchDTO> searchFlightResult(String dep, String arr, LocalDate dateDep, LocalDate dateArr, String flightClass,
                                            Boolean isAroundTrip);

    List<Object> createFlight(List<FlightRequestDTO> flightRequestDTOList);

    FlightData findFlightDataByID(UUID id);

    Flight findFlightByID(UUID id);

    Flight save(Flight flight);
}
