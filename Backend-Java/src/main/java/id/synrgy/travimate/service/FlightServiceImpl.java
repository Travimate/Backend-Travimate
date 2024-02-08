package id.synrgy.travimate.service;

import id.synrgy.travimate.dto.request.EditAirlineUrl;
import id.synrgy.travimate.dto.request.FlightDataRequestDTO;
import id.synrgy.travimate.dto.request.FlightRequestDTO;
import id.synrgy.travimate.dto.response.*;
import id.synrgy.travimate.exception.ResourceNotFoundException;
import id.synrgy.travimate.model.*;
import id.synrgy.travimate.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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
    public List<Object> createFlight(List<FlightRequestDTO> flightRequestDTOList){
        List<Object> flightDTOList = new LinkedList<>();
        for (FlightRequestDTO flightRequestDTO : flightRequestDTOList) {
            flightDTOList.add(createFlightWithDTO(flightRequestDTO));
        }
        return flightDTOList;
    }
    public Object createFlightWithDTO(FlightRequestDTO flightRequestDTO) {
        Flight flight = new Flight();
        String airline = flightRequestDTO.getAirline();
        int flightNumber = flightRequestDTO.getFlightNumber();

        try {
            flight.setFlightNumber(airline.toUpperCase() + flightNumber);
            flight.setDep(findAirportByIATACode(flightRequestDTO.getDep()));
            flight.setArr(findAirportByIATACode(flightRequestDTO.getArr()));
            flight.setAirline(findAirlineByIATACode(airline));
            flight.setFlightClass(Flight.FlightClass.valueOf(flightRequestDTO.getFlightClass().toUpperCase()));
            flight.setDof(flightRequestDTO.getDof());

            LocalTime depTime = flightRequestDTO.getDepTimeAsLocalTime();
            LocalTime arrTime = flightRequestDTO.getArrTimeAsLocalTime();
            flight.setDeparture_time(depTime);
            flight.setArrival_time(arrTime);

            Duration duration = Duration.between(depTime, arrTime);
            LocalTime flightTime = LocalTime.MIDNIGHT.plus(duration);
            flight.setFlight_time(flightTime);
            flight.setStock(flightRequestDTO.getStock());

            flightRepository.save(flight);
            return mapToDTO(flight);
        } catch (ResourceNotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public List<Object> createFlightDataDTO(List<FlightDataRequestDTO> flightDataRequestDTOList){
        List<Object> flightDTOList = new LinkedList<>();
        for(FlightDataRequestDTO flightDataDTO : flightDataRequestDTOList){
            flightDTOList.add(createFlightData(flightDataDTO));
        }
        return flightDTOList;
    }

    public Object createFlightData(FlightDataRequestDTO flightDataRequest) {

        FlightData flightData = new FlightData();
        Set<Route> routeSet = new LinkedHashSet<>();

        String dep = flightDataRequest.getDep();
        String arr = flightDataRequest.getArr();
        LocalDate date = flightDataRequest.getDate();
        String connectingAirport = flightDataRequest.getConnectingAirport();
        Optional<FlightData> fd = null;

        try {
            if(connectingAirport!=null){
                fd = flightDataRepository.findFlightsByDepartureAndArrivalAndConnecting(
                        findAirportByIATACode(dep), findAirportByIATACode(arr), findAirportByIATACode(connectingAirport), date
                );
            } else {
                fd = flightDataRepository.findDirectFlightsByDepartureAndArrival(
                    findAirportByIATACode(dep), findAirportByIATACode(arr), date);
            }

            if(fd.isEmpty()){
                createNewFlightData(flightDataRequest, flightData);
                routeSet.add(createRoutes(flightDataRequest.getAirline(), dep, arr, connectingAirport, flightData));
                flightData.setRouteSet(routeSet);
                flightDataRepository.save(flightData);
                return mapToDTO(flightData, dep, arr);
            } else {
                return String.format("Data dengan dep, arr, dan tanggal: %s, %s, %s SUDAH ADA.", dep, arr, date);
            }
        } catch (ResourceNotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    private void createNewFlightData(FlightDataRequestDTO requestDTO, FlightData flightData) {
        flightData.setOperated_airline(findAirlineByIATACode(requestDTO.getAirline()));
        flightData.setDeparture(findAirportByIATACode(requestDTO.getDep()));
        if(requestDTO.getConnectingAirport()!=null){
            flightData.setConnecting(findAirportByIATACode(requestDTO.getConnectingAirport()));
        }
        flightData.setArrival(findAirportByIATACode(requestDTO.getArr()));
        flightData.setFlight_date(requestDTO.getDate());
        BaseFare baseFare = createBaseFare(requestDTO.getAdultFare(), requestDTO.getChildFare(),
                requestDTO.getSameAsAdult());
        flightData.setBaseFare(baseFare);
        flightData.setStops(requestDTO.getStops());
        flightData.setIsDirect(requestDTO.getIsDirect());
        flightData.setFlightClass(Flight.FlightClass.valueOf(requestDTO.getFlightClass().toUpperCase()));
        flightDataRepository.save(flightData);
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
    public Route createRoutes(String airline, String dep, String arr, String connectingAirport, FlightData flightData) {

        Optional<Route> existingRoute;

        if (connectingAirport == null) {
            existingRoute = routeRepository.findDirectRouteAndOperatedAirline(
                    (dep + arr).toUpperCase(), findAirlineByIATACode(airline));
        } else {
            existingRoute = routeRepository.findByRouteCodeAndOperatedAirline(
                    (dep + arr).toUpperCase(),
                    findAirportByIATACode(connectingAirport),
                    findAirlineByIATACode(airline));
        }
        if (existingRoute.isEmpty()) {
            Route route = createRoute(airline, dep, arr, flightData);
            System.out.println("connecting = "+connectingAirport);
            if (connectingAirport != null) {
                route.setConnecting_airport(findAirportByIATACode(connectingAirport));
            }
            routeRepository.save(route);
            return route;
        } else {
            return existingRoute.get();
        }
    }

    private Route createRoute(String airline, String dep, String arr, FlightData flightData) {
        Route route = new Route();
        route.setCode((dep + arr).toUpperCase());
        route.setOperated_airline(findAirlineByIATACode(airline));
        route.setDeparture_airport(findAirportByIATACode(dep));
        route.setDestination_airport(findAirportByIATACode(arr));
        Set<FlightData> flightDataSet = new LinkedHashSet<>();
        flightDataSet.add(flightData);
        route.setFlightData(flightDataSet);
        return route;
    }

    @Override
    public Set<FlightSearchDTO> searchFlightResult(String dep, String arr, LocalDate dateDep,
                                              LocalDate dateArr, String flightClass, Boolean isAroundTrip) {
        Set<FlightSearchDTO> flightSearchDTOSet = new LinkedHashSet<>();

        FlightSearchDTO flightSearchDTO = new FlightSearchDTO();
        flightSearchDTO.setDeparture(dep.toUpperCase());
        flightSearchDTO.setArrival(arr.toUpperCase());
        flightSearchDTO.setDateOfFlight(dateDep);
        flightSearchDTO.setDataInfo("Data di Tgl Berangkat");
        flightSearchDTO.setListOfFlight(findJourney(dep, arr, dateDep, flightClass));
        flightSearchDTOSet.add(flightSearchDTO);

        if(isAroundTrip !=null && isAroundTrip){
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

    private List<JourneyDTO> findJourney(String dep, String arr, LocalDate dateOfFlight, String flightClass) {
        List<JourneyDTO> journeyDTOList = new ArrayList<>();
        Set<FlightData> flightDataSet = new LinkedHashSet<>(flightDataRepository
                .findFlightDataByDepartureArrivalAndDate(
                        findAirportByIATACode(dep), findAirportByIATACode(arr), dateOfFlight));
        flightDataSet.forEach(flightData -> System.out.println("flight data = "+flightData));
        for(FlightData flightData : flightDataSet){
            List<RouteDTO> routeDTOSet = flightData.getRouteSet().stream()
                    .flatMap(route -> mapToDTO(route, dep, arr, dateOfFlight,
                            flightData.getIsDirect(), flightClass).stream())
                    .toList();

            for (RouteDTO routeDTO : routeDTOSet) {
                JourneyDTO journey = new JourneyDTO();
                journey.setFlightDataId(flightData.getId());
                journey.setDeparture_airport(findAirportByIATACode(dep));
                journey.setArrival_airport(findAirportByIATACode(arr));
                journey.setFlightClass(flightData.getFlightClass());
                journey.setRoute(Collections.singletonList(routeDTO));
                loopJourney(journey, Collections.singletonList(routeDTO));
                journey.setBaseFare(flightData.getBaseFare());
                if (!journey.getRoute().isEmpty()) {
                    journeyDTOList.add(journey);
                }
            }
        }

        journeyDTOList.sort(
                Comparator.comparingInt(JourneyDTO::getStops)
                        .thenComparingLong(dto -> dto.getBaseFare().getAdultBaseFare())
        );
        return journeyDTOList;
    }
    private void loopJourney(JourneyDTO journey, List<RouteDTO> routeDTOSet){
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

    public List<Flight> findFlight(String dep, String arr, LocalDate dof, String airline, String flightClass){
        if(dep.isEmpty() || arr.isEmpty() || airline.isEmpty()){
            return null;
        }
        return flightRepository.findByAirportAndAirline(dep, arr, dof, airline, flightClass);
    }

    private List<RouteDTO> mapToDTO(Route route, String dep, String arr,
                              LocalDate dateOfFlight, Boolean isDirect, String flightClass) {

        String airline = route.getOperated_airline().getIata_code();
        String connectingAirport = null;
        if (route.getConnecting_airport() != null) {
            connectingAirport = route.getConnecting_airport().getIata_code();
        }
        Set<Flight> directFlight = new LinkedHashSet<>();
        if (isDirect) {
            if (dep != null && arr != null && findFlight(dep, arr, dateOfFlight, airline, flightClass) != null) {
                directFlight.addAll(findFlight(dep, arr, dateOfFlight, airline, flightClass));
            }
        }

        Set<TransitFlight> transitFlights = new LinkedHashSet<>();
        Set<String> usedFlightNumbers = new HashSet<>(); // Menyimpan flightNumber yang sudah digunakan

        if (!isDirect) {
            List<Flight> depToConnectingFlights = findFlight(dep, connectingAirport, dateOfFlight, airline, flightClass);
            List<Flight> connectingToArrFlights = findFlight(connectingAirport, arr, dateOfFlight, airline, flightClass);

            if (depToConnectingFlights != null && connectingToArrFlights != null) {
                for (Flight firstFlight : depToConnectingFlights) {
                    for (Flight secondFlight : connectingToArrFlights) {
                        // Membuat TransitFlight hanya jika flightNumber belum digunakan
                        if (!usedFlightNumbers.contains(firstFlight.getFlightNumber())
                                && !usedFlightNumbers.contains(secondFlight.getFlightNumber())
                                && firstFlight.getArrival_time().isBefore(secondFlight.getDeparture_time())) {

                            TransitFlight transitFlight = new TransitFlight();
                            transitFlight.setFirstFlight(firstFlight);
                            transitFlight.setSecondFlight(secondFlight);

                            // Menambah flightNumber yang sudah digunakan ke dalam Set
                            usedFlightNumbers.add(firstFlight.getFlightNumber());
                            usedFlightNumbers.add(secondFlight.getFlightNumber());

                            transitFlights.add(transitFlight);
                        }
                    }
                }
            }
        }

        List<RouteDTO> routeDTOList = new LinkedList<>();
        // Direct flights
        for (Flight flight : directFlight) {
            Set<Flight> flights = Collections.singleton(flight);
            Set<Airline> airlines = Collections.singleton(flight.getAirline());
            RouteDTO routeDTO = createRouteDTO(route, flights, airlines, 0);
            routeDTOList.add(routeDTO);
        }

        // Transit flights
        for (TransitFlight transitFlight : transitFlights) {
            List<Flight> flightsInOrder = Arrays.asList(transitFlight.getFirstFlight(), transitFlight.getSecondFlight());

            // Memeriksa kembali urutan penerbangan sebelum membuat RouteDTO
            if (flightsInOrder.get(0).getArrival_time().isAfter(flightsInOrder.get(1).getDeparture_time())) {
                continue;
            }

            Set<Flight> flights = new LinkedHashSet<>(flightsInOrder);
            Set<Airline> airlines = new HashSet<>(Arrays.asList(
                    transitFlight.getFirstFlight().getAirline(), transitFlight.getSecondFlight().getAirline()));
            RouteDTO routeDTO = createRouteDTO(route, flights, airlines, 1);
            routeDTOList.add(routeDTO);
        }
        return routeDTOList;
    }

    private RouteDTO createRouteDTO(Route route, Set<Flight> flights, Set<Airline> airlines, int stops) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setCode(route.getCode());
        routeDTO.setDeparture_airport(route.getDeparture_airport());
        routeDTO.setDestination_airport(route.getDestination_airport());
        routeDTO.setConnecting_airport(route.getConnecting_airport());
        routeDTO.setStops(stops);
        routeDTO.setFlights(flights);
        routeDTO.setOperated_airline(airlines);
        return routeDTO;
    }

    private FlightDataDTO mapToDTO(FlightData flightData, String dep, String arr){
        FlightDataDTO flightDataDTO = new FlightDataDTO();
        flightDataDTO.setId(flightData.getId());
        flightDataDTO.setDeparture(flightData.getDeparture());
        flightDataDTO.setArrival(flightData.getArrival());
        flightDataDTO.setConnecting(flightData.getConnecting());
        flightDataDTO.setDate(flightData.getFlight_date());
        flightDataDTO.setBaseFare(flightData.getBaseFare());
        flightDataDTO.setFlightClass(flightData.getFlightClass());
        flightDataDTO.setOperated_airline(flightData.getOperated_airline());
        flightDataDTO.setIsDirect(flightData.getIsDirect());
//        flightDataDTO.setRouteSet(flightData.getRouteSet().stream()
//                .map(route -> createRouteDTO(route, dep, arr, null,
//                        flightData.getIsDirect(), flightData.getFlightClass().name()))
//                .collect(Collectors.toCollection(LinkedHashSet::new)));
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
