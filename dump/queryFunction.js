const _ = require('lodash');

function query(data) {
  const uniqueCombinations = new Set();
  const result = [];

  data.forEach((item) => {
    const connectingRoutes = _.get(item, 'connectingFlightRoutes', []);
    const fareInfo = _.get(item, 'airlineFareInfo.detailedSearchFares[0].flightRouteFares', {});

    if (connectingRoutes.length === 2 && connectingRoutes[0].arrivalAirport === connectingRoutes[1].departureAirport) {
      const segments = _.get(connectingRoutes[0], 'segments', []);

      const dep = _.get(connectingRoutes[0], 'departureAirport', '');
      const arr = _.get(connectingRoutes[1], 'arrivalAirport', '');
      const connectingAirport = _.get(connectingRoutes[0], 'arrivalAirport', '');
      const airline = _.get(segments[0], 'airlineCode', '');
      const flightClass = _.get(connectingRoutes[0], 'routeInventories[0].seatPublishedClass', '');

      const departureDate = _.get(segments[0], 'departureDate', {});
      const date = formatDate(departureDate);

      const key = generateKey(dep, arr, connectingAirport, airline, flightClass);

      if (!uniqueCombinations.has(key) && !shouldExcludeFlightClass(flightClass)) {
        const adultFare = _.get(fareInfo, 'adultBaseFare.amount', '');
        const childFare = _.get(fareInfo, 'childBaseFare.amount', '');
        const sameAsAdult = childFare === '0';
        const isDirect = false;

        result.push({
          airline,
          dep,
          arr,
          adultFare,
          ...(sameAsAdult ? { sameAsAdult: true } : { childFare }),
          flightClass,
          connectingAirport,
          isDirect,
          date,
        });

        uniqueCombinations.add(key);
      }
    } else if (connectingRoutes.length === 1) {
      const connectingRoute = connectingRoutes[0];
      const segments = _.get(connectingRoute, 'segments', []);

      const dep = _.get(connectingRoute, 'departureAirport', '');
      const arr = _.get(connectingRoute, 'arrivalAirport', '');
      const connectingAirport = null;
      const airline = _.get(segments[0], 'airlineCode', '');
      const flightClass = _.get(connectingRoute, 'routeInventories[0].seatPublishedClass', '');

      const departureDate = _.get(segments[0], 'departureDate', {});
      const date = formatDate(departureDate);

      const key = generateKey(dep, arr, connectingAirport, airline, flightClass);

      if (!uniqueCombinations.has(key) && !shouldExcludeFlightClass(flightClass)) {
        const adultFare = _.get(fareInfo, 'adultBaseFare.amount', '');
        const childFare = _.get(fareInfo, 'childBaseFare.amount', '');
        const sameAsAdult = childFare === '0';
        const isDirect = true;

        result.push({
          airline,
          dep,
          arr,
          adultFare,
          ...(sameAsAdult ? { sameAsAdult: true } : { childFare }),
          flightClass,
          connectingAirport,
          isDirect,
          date,
        });

        uniqueCombinations.add(key);
      }
    }
  });

  return result;
}

function formatDate(dateObject) {
  const year = dateObject.year;
  const month = dateObject.month.padStart(2, '0');
  const day = dateObject.day.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function shouldExcludeFlightClass(flightClass) {
  const excludedFlightClasses = ['PROMO', 'OTHERS'];
  return excludedFlightClasses.includes(flightClass);
}

function generateKey(dep, arr, connectingAirport, airline, flightClass, adultFare = '', childFare = '', sameAsAdult = false) {
  return `${airline}_${dep}_${arr}_${flightClass}_${adultFare}_${childFare}_${sameAsAdult}_${connectingAirport}`;
}

// Contoh penggunaan
const jsonData = [
  {"connectingFlightRoutes":[],
    "airlineFareInfo":{}},
  {"connectingFlightRoutes":[],
    "airlineFareInfo":{}}
  // ... Data JSON
];

Source data -> JSON Traveloka .searchFlight

query create Flight =>

function query(data) {
  const resultSegments = [];

  _.chain(data)
    .flatMap('connectingFlightRoutes')
    .flatMap('segments')
    .forEach((segment) => {
      const departureAirport = _.get(segment, 'departureAirport', '');
      const arrivalAirport = _.get(segment, 'arrivalAirport', '');
      const flightNumber = _.get(segment, 'flightNumber', '').substring(3);
      const airlineCode = _.get(segment, 'airlineCode', '');
      const publishedClass = _.get(segment, 'segmentInventories[0].publishedClass', '');
      const departureDate = _.get(segment, 'departureDate', {});
      const formattedDepartureDate = `${departureDate.year}-${String(departureDate.month).padStart(2, '0')}-${String(departureDate.day).padStart(2, '0')}`;
      const departureTime = _.get(segment, 'departureTime', {});
      const formattedDepartureTime = `${String(departureTime.hour).padStart(2, '0')}.${String(departureTime.minute).padStart(2, '0')}`;
      const arrivalTime = _.get(segment, 'arrivalTime', {});
      const formattedArrivalTime = `${String(arrivalTime.hour).padStart(2, '0')}.${String(arrivalTime.minute).padStart(2, '0')}`;

      // Skip jika flightClass adalah "PROMO" atau "OTHERS"
      if (publishedClass === 'PROMO' || publishedClass === 'OTHERS') {
        return;
      }

      const newSegment = {
        dep: departureAirport,
        arr: arrivalAirport,
        airline: airlineCode,
        flightNumber: flightNumber,
        flightClass: publishedClass,
        dof: formattedDepartureDate,
        depTime: formattedDepartureTime,
        arrTime: formattedArrivalTime,
        stock: 150,
      };

      // Cek apakah ada duplikat berdasarkan beberapa properti
      const isDuplicate = _.some(resultSegments, (existingSegment) =>
        _.isEqual(_.pick(existingSegment, ['dep', 'arr', 'airline', 'flightNumber', 'flightClass']), _.pick(newSegment, ['dep', 'arr', 'airline', 'flightNumber', 'flightClass']))
      );

      // Jika tidak ada yang sama, tambahkan ke resultSegments
      if (!isDuplicate) {
        resultSegments.push(newSegment);
      }
    })
    .value();

  return resultSegments;
}

result =>
{
  "dep": "CGK",
  "arr": "DPS",
  "airline": "QZ",
  "flightNumber": "802",
  "flightClass": "ECONOMY",
  "dof": "2024-02-06",
  "depTime": "06.10",
  "arrTime": "09.05",
  "stock": 150
}

query edit Airline Image Link =>

