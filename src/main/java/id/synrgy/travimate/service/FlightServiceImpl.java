package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightRequest;
import id.synrgy.travimate.dto.response.*;
import id.synrgy.travimate.exception.ExistingResourceFoundException;
import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.*;
import id.synrgy.travimate.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService{

    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private final FlightRepository flightRepository;
    private final FlightDataRepository flightDataRepository;
    private final BaseFareRepository baseFareRepository;


    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    @Autowired
    FlightServiceImpl(AirportRepository airportRepository,
                      AirlineRepository airlineRepository,
                      RouteRepository routeRepository,
                      FlightRepository flightRepository,
                      FlightDataRepository flightDataRepository,
                      BaseFareRepository baseFareRepository){
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
        this.routeRepository = routeRepository;
        this.flightDataRepository = flightDataRepository;
        this.baseFareRepository = baseFareRepository;
    }

    @Override
    public FlightDTO createFlight(String dep, String arr, String airline, int flightNumber,
                                  String flightClass, Date dof, LocalTime depTime,
                                  LocalTime arrTime, Integer stock) {
        Flight flight = new Flight();
        flight.setFlightNumber(airline.toUpperCase()+flightNumber);
        flight.setDep(findAirportByIATACode(dep));
        flight.setArr(findAirportByIATACode(arr));
        flight.setAirline(findAirlineByIATACode(airline));
        flight.setFlightClass(Flight.FlightClass.valueOf(flightClass.toUpperCase()));
        flight.setDof(dof);
        flight.setDeparture_time(depTime);
        flight.setArrival_time(arrTime);

        Duration duration = Duration.between(depTime, arrTime);
        LocalTime flightTime = LocalTime.MIDNIGHT.plus(duration);
        flight.setFlight_time(flightTime);
        flight.setStock(stock);
        flightRepository.save(flight);
        return mapToDTO(flight);
    }

    @Override
    public FlightDTO createFlightWithDTO(FlightRequest flightRequest) {
        Flight flight = new Flight();
        String airline = flightRequest.getAirline();
        int flightNumber = flightRequest.getFlightNumber();

        flight.setFlightNumber(airline.toUpperCase() + flightNumber);
        flight.setDep(findAirportByIATACode(flightRequest.getDep()));
        flight.setArr(findAirportByIATACode(flightRequest.getArr()));
        flight.setAirline(findAirlineByIATACode(airline));
        flight.setFlightClass(Flight.FlightClass.valueOf(flightRequest.getFlightClass().toUpperCase()));
        flight.setDof(flightRequest.getDof());
        flight.setDeparture_time(flightRequest.getDepTime());
        flight.setArrival_time(flightRequest.getArrTime());

        Duration duration = Duration.between(flightRequest.getDepTime(), flightRequest.getArrTime());
        LocalTime flightTime = LocalTime.MIDNIGHT.plus(duration);
        flight.setFlight_time(flightTime);
        flight.setStock(flightRequest.getStock());

        flightRepository.save(flight);
        return mapToDTO(flight);
    }


    @Override
    public FlightDataDTO createFlightData(String airline, String dep, String arr, Integer stops,
                                          Long adultFare, Long childFare, Boolean sameAsAdult,
                                          String flightClass, String connectingAirport, Boolean isDirect) {

        List<FlightData> flightDataDirect = flightDataRepository.findDirectFlightsByDepartureAndArrival(
                findAirportByIATACode(dep), findAirportByIATACode(arr));

        FlightData flightData = new FlightData();
        Set<Route> routeSet = new LinkedHashSet<>();

        if(flightDataDirect.isEmpty()){
            routeSet.add(createRoutes(airline, dep, arr, null, flightData));
        }
        if(flightDataDirect.size()==1 && connectingAirport!=null){
            routeSet.add(createRoutes(airline, dep, arr, connectingAirport, flightData));
        }
        flightData.setRouteSet(routeSet);

        flightData.setOperated_airline(findAirlineByIATACode(airline));
        flightData.setDeparture(findAirportByIATACode(dep));
        flightData.setArrival(findAirportByIATACode(arr));

        BaseFare baseFare = createBaseFare(adultFare, childFare, sameAsAdult);
        flightData.setBaseFare(baseFare);

        flightData.setStops(stops);
        flightData.setIsDirect(isDirect);
        flightData.setFlightClass(Flight.FlightClass.valueOf(flightClass.toUpperCase()));

        if(routeSet.isEmpty()){
            throw new ExistingResourceFoundException("tersebut");
        }
        flightDataRepository.save(flightData);
        return mapToDTO(flightData, dep, arr);
    }
    private BaseFare createBaseFare(Long adultFare, Long childFare, Boolean sameAsAdult){
        BaseFare baseFare = new BaseFare();
        baseFare.setAdultBaseFare(adultFare);
        if(sameAsAdult){
            baseFare.setChildBaseFare(adultFare);
        } else {
            baseFare.setChildBaseFare(childFare);
        }
        baseFareRepository.save(baseFare);
        return baseFare;
    }

    @Override
    public Route createRoutes(String airline, String dep, String arr, String connectingAirport, FlightData flightData){
        Route route = createRoute(airline, dep, arr, flightData);
        if(connectingAirport==null){
            route.setConnecting_airport(null);
            routeRepository.save(route);
        } else {
            Optional<Route> existingRoute = routeRepository.findByRouteCodeAndOperatedAirline(
                    (dep+arr).toUpperCase(),
                    findAirportByIATACode(connectingAirport),
                    findAirlineByIATACode(airline));
            if(existingRoute.isEmpty()){
                route.setConnecting_airport(findAirportByIATACode(connectingAirport));
                routeRepository.save(route);
            } else {

                throw new ExistingResourceFoundException("Code = "+(dep+arr).toUpperCase()+
                        " dan Connecting Airport = "+connectingAirport.toUpperCase());
            }
        }
        return route;
    }

    private Route createRoute(String airline, String dep, String arr, FlightData flightData) {
        Route route = new Route();
        route.setCode((dep+arr).toUpperCase());
        route.setOperated_airline(findAirlineByIATACode(airline));
        route.setDeparture_airport(findAirportByIATACode(dep));
        route.setDestination_airport(findAirportByIATACode(arr));
        Set<FlightData> flightDataSet = new LinkedHashSet<>();
        flightDataSet.add(flightData);
        route.setFlightData(flightDataSet);
        return route;
    }

    @Override
    public Set<FlightSearchDTO> searchFlightResult(String dep, String arr, Date dateDep,
                                              Date dateArr, String flightClass, boolean isAroundTrip) {
        Set<FlightSearchDTO> flightSearchDTOSet = new LinkedHashSet<>();

        FlightSearchDTO flightSearchDTO = new FlightSearchDTO();
        flightSearchDTO.setDeparture(dep.toUpperCase());
        flightSearchDTO.setArrival(arr.toUpperCase());
        flightSearchDTO.setDateOfFlight(dateDep);
        flightSearchDTO.setDataInfo("Data di Tgl Berangkat");
        flightSearchDTO.setListOfFlight(findJourney(dep, arr, dateDep, flightClass));
        flightSearchDTOSet.add(flightSearchDTO);

        if(isAroundTrip){
            FlightSearchDTO flightSearchDTOBack = new FlightSearchDTO();
            flightSearchDTOBack.setDeparture(arr.toUpperCase());
            flightSearchDTOBack.setArrival(dep.toUpperCase());
            flightSearchDTOBack.setDateOfFlight(dateArr);
            flightSearchDTOBack.setDataInfo("Data di Tgl Kembali");
            flightSearchDTOBack.setListOfFlight(findJourney(arr, dep, dateArr, flightClass));
            flightSearchDTOSet.add(flightSearchDTOBack);
        }
        return flightSearchDTOSet;
    }

    private List<JourneyDTO> findJourney(String dep, String arr, Date dateOfFlight, String flightClass) {
        List<JourneyDTO> journeyDTOSet = new ArrayList<>();
        Set<FlightData> flightDataSet = new LinkedHashSet<>(flightDataRepository
                .findByDepartureAndArrival(findAirportByIATACode(dep), findAirportByIATACode(arr)));

        for(FlightData flightData : flightDataSet){
            Set<RouteDTO> routeDTOSet = flightData.getRouteSet().stream()
                    .map(route -> mapToDTO(route, dep, arr, dateOfFlight,
                            flightData.getIsDirect(), flightClass))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            JourneyDTO journey = new JourneyDTO();
            journey.setDeparture_airport(findAirportByIATACode(dep));
            journey.setArrival_airport(findAirportByIATACode(arr));
            journey.setFlightClass(flightData.getFlightClass());
            journey.setRoute(routeDTOSet);
            loopJourney(journey, routeDTOSet);
            journey.setBaseFare(flightData.getBaseFare());
            if (!journey.getRoute().isEmpty()) {
                journeyDTOSet.add(journey);
            }
        }

        journeyDTOSet.sort(Comparator.comparingLong(dto -> dto.getBaseFare().getAdultBaseFare()));
        return journeyDTOSet;
    }
    private void loopJourney(JourneyDTO journey, Set<RouteDTO> routeDTOSet){
        for(RouteDTO routeDTO : routeDTOSet){

            Set<Flight> flights = routeDTO.getFlights();
            if (flights.isEmpty()) {
                continue;
            }

            journey.setAirline_operator(routeDTO.getOperated_airline().stream()
                    .map(Airline::getAirline_name)
                    .collect(Collectors.joining(" + ")));

            journey.setAirline(routeDTO.getOperated_airline());

            journey.setDof(routeDTO.getFlights().stream()
                    .map(Flight::getDof)
                    .findFirst()
                    .orElse(null));
            journey.setDeparture_time(routeDTO.getFlights().stream()
                    .map(Flight::getDeparture_time)
                    .findFirst()
                    .orElse(null));
            journey.setArrival_time(routeDTO.getFlights().stream()
                    .map(Flight::getArrival_time)
                    .reduce((first, second) -> second)
                    .orElse(null));

            LocalTime arrivalFirstFlight = routeDTO.getFlights().stream()
                    .map(Flight::getArrival_time)
                    .findFirst()
                    .orElse(null);

            journey.setTransit_time_minutes(routeDTO.getFlights().stream()
                    .skip(1)
                    .findFirst()
                    .map(secondFlight -> Duration.between(arrivalFirstFlight, secondFlight.getDeparture_time()).toMinutes())
                    .orElse(null));

            journey.setTotal_flight_time_minutes(routeDTO.getFlights().stream()
                    .mapToLong(flight -> ChronoUnit.MINUTES.between(flight.getDeparture_time(), flight.getArrival_time()))
                    .sum());

            journey.setSeat_left(routeDTO.getFlights().stream()
                    .map(Flight::getStock)
                    .findFirst()
                    .orElse(null));

            int totalStops = routeDTOSet.size();
            if (totalStops == 1) {
                // Jika hanya satu RouteDTO ditemukan, set stops sesuai dengan RouteDTO tersebut
                RouteDTO singleRouteDTO = routeDTOSet.iterator().next();
                journey.setStops(singleRouteDTO.getStops());
            } else {
                // Jika lebih dari satu RouteDTO ditemukan, set stops dengan jumlah RouteDTO
                journey.setStops(totalStops);
            }
        }
    }

    public Flight findFlight(String dep, String arr, Date dof, String airline, String flightClass){
        if(dep.isEmpty() || arr.isEmpty() || airline.isEmpty()){
            return null;
        }
        Optional<Flight> flight = flightRepository.findByAirportAndAirline(dep, arr, dof, airline, flightClass);
        return flight.orElse(null);
    }

    private RouteDTO mapToDTO(Route route, String dep, String arr,
                              Date dateOfFlight, Boolean isDirect, String flightClass){
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setCode(route.getCode());
        routeDTO.setDeparture_airport(route.getDeparture_airport());
        routeDTO.setDestination_airport(route.getDestination_airport());
        routeDTO.setConnecting_airport(route.getConnecting_airport());

        String airline = route.getOperated_airline().getIata_code();
        String connectingAirport = null;
        if(route.getConnecting_airport()!=null){
            connectingAirport = route.getConnecting_airport().getIata_code();
        }
        Set<Flight> flightSet = new LinkedHashSet<>();
        if(isDirect){
            if(dep!=null && arr!=null && findFlight(dep, arr, dateOfFlight, airline, flightClass)!=null){
                flightSet.add(findFlight(dep, arr, dateOfFlight, airline, flightClass));
            }
        }
        if(!isDirect){
            if(dep!=null && connectingAirport!=null
                    && findFlight(dep, connectingAirport, dateOfFlight, airline, flightClass)!=null)
            {
                flightSet.add(findFlight(dep, connectingAirport, dateOfFlight, airline, flightClass));
            }
            if(connectingAirport!=null && arr!=null
                    && findFlight(connectingAirport, arr, dateOfFlight, airline, flightClass)!=null)
            {
                flightSet.add(findFlight(connectingAirport, arr, dateOfFlight, airline, flightClass));
            }
        }

        int stops = 0;
        if(flightSet.size()==2){
            stops=1;
        }

        routeDTO.setStops(stops);
        routeDTO.setFlights(flightSet);
        routeDTO.setOperated_airline(flightSet.stream().map(Flight::getAirline).collect(Collectors.toSet()));
        return routeDTO;
    }

    private FlightDataDTO mapToDTO(FlightData flightData, String dep, String arr){
        FlightDataDTO flightDataDTO = new FlightDataDTO();
        flightDataDTO.setId(flightData.getId());
        flightDataDTO.setDeparture(flightData.getDeparture());
        flightDataDTO.setArrival(flightData.getArrival());
        flightDataDTO.setBaseFare(flightData.getBaseFare());
        flightDataDTO.setFlightClass(flightData.getFlightClass());
        flightDataDTO.setOperated_airline(flightData.getOperated_airline());
        flightDataDTO.setIsDirect(flightData.getIsDirect());
        flightDataDTO.setRouteSet(flightData.getRouteSet().stream()
                .map(route -> mapToDTO(route, dep, arr, null,
                        flightData.getIsDirect(), flightData.getFlightClass().name()))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return flightDataDTO;
    }

    private FlightDTO mapToDTO(Flight flight){
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setId(flight.getId());
        flightDTO.setFlightNumber(flight.getFlightNumber());
        flightDTO.setDep(flight.getDep());
        flightDTO.setArr(flight.getArr());
        flightDTO.setAirline(flight.getAirline());
        flightDTO.setFlightClass(flight.getFlightClass());
        flightDTO.setDof(flight.getDof());
        flightDTO.setDeparture_time(flight.getDeparture_time());
        flightDTO.setArrival_time(flight.getArrival_time());
        flightDTO.setFlight_time(flight.getFlight_time());
        flightDTO.setStock(flight.getStock());
        return flightDTO;
    }

    @Override
    public Airport createAirport(Airport airport) {
        airportRepository.save(airport);
        return airport;
    }

    @Override
    public List<Object> editAirlines(List<EditAirlineUrl> dtos) {
        List<Object> results = new ArrayList<>();

        for (EditAirlineUrl dto : dtos) {
            Optional<Airline> airline = airlineRepository.findByIataCode(dto.getIataCode());

            if(airline.isPresent()){
                Airline existingAirline = airline.get();
                if(existingAirline.getImageUrl() == null){
                    existingAirline.setImageUrl(dto.getUrl());
                    airlineRepository.save(existingAirline);
                    results.add(existingAirline);
                } else {
                    results.add("Data dengan 'iataCode' = '" + dto.getIataCode() + "' sudah memiliki URL gambar.");
                }
            } else {
                results.add("Data dengan 'iataCode' = '" + dto.getIataCode() + "' tidak ditemukan.");
            }
        }
        return results;
    }


    @Override
    public Airport findAirportByIATACode(String iataCode) {
        return airportRepository.findByIataCode(iataCode.toUpperCase())
                .orElseThrow(()-> new ResourceNotFoundException(iataCode));
    }

    @Override
    public Airline findAirlineByIATACode(String iataCode) {
        return airlineRepository.findByIataCode(iataCode.toUpperCase())
                .orElseThrow(()-> new ResourceNotFoundException(iataCode));
    }

    @Override
    public Optional<Route> findRouteById(String id) {
        return routeRepository.findById(id.toUpperCase());
    }

}
