const _ = require('lodash');

function query(data) {
  return _.chain(data)
    .map((item) => {
      const connectingRoutes = _.get(item, 'connectingFlightRoutes', []);
      const fareInfo = _.get(item, 'airlineFareInfo.detailedSearchFares[0].flightRouteFares', {});

      if (connectingRoutes.length === 2) {
        const segments1 = _.get(connectingRoutes[0], 'segments', []);
        const segments2 = _.get(connectingRoutes[1], 'segments', []);

        if (segments1.length === 2 && segments1[0].arrivalAirport === segments1[1].departureAirport) {
                  const dep1 = segments1[0].departureAirport;
                  const arr1 = segments1[1].arrivalAirport;
                  const connectingAirport1 = segments1[0].arrivalAirport;
                  const airline1 = segments1[0].airlineCode;
                  const flightClass1 = _.get(connectingRoutes[0], 'routeInventories[0].seatPublishedClass', '');

                  const adultFare1 = _.get(fareInfo, 'adultBaseFare.amount', '');
                  const childFare1 = _.get(fareInfo, 'childBaseFare.amount', '');
                  const sameAsAdult1 = childFare1 === '0';
                  const isDirect1 = false;

                  const dep2 = segments2[0].departureAirport;
                  const arr2 = segments2[0].arrivalAirport;
                  const connectingAirport2 = segments1[0].arrivalAirport;
                  const airline2 = segments1[0].airlineCode;
                  const flightClass2 = _.get(connectingRoutes[0], 'routeInventories[0].seatPublishedClass', '');

                  const adultFare2 = _.get(fareInfo, 'adultBaseFare.amount', '');
                  const childFare2 = _.get(fareInfo, 'childBaseFare.amount', '');
                  const sameAsAdult2 = childFare2 === '0';
                  const isDirect2 = false;

                  return [
                    {
                      airline: airline1,
                      dep: dep1,
                      arr: arr1,
                      adultFare: adultFare1,
                      ...(sameAsAdult1 ? { sameAsAdult: true } : { childFare: childFare1 }),
                      flightClass: flightClass1,
                      connectingAirport: connectingAirport1,
                      isDirect: isDirect1,
                    },
                    {
                      airline: airline2,
                      dep: dep2,
                      arr: arr2,
                      adultFare: adultFare2,
                      ...(sameAsAdult2 ? { sameAsAdult: true } : { childFare: childFare2 }),
                      flightClass: flightClass2,
                      connectingAirport: connectingAirport2,
                      isDirect: isDirect2,
                    },
                  ];
                }

        } else if (segments1.length === 1) {
          const dep = segments1[0].departureAirport;
          const arr = segments2[0].arrivalAirport;
          const connectingAirport = segments1[0].arrivalAirport;
          const airline = segments1[0].airlineCode;
          const flightClass = _.get(connectingRoutes[0], 'routeInventories[0].seatPublishedClass', '');

          const adultFare = _.get(fareInfo, 'adultBaseFare.amount', '');
          const childFare = _.get(fareInfo, 'childBaseFare.amount', '');
          const sameAsAdult = childFare === '0';
          const isDirect = false;

          return {
            airline,
            dep,
            arr,
            adultFare,
            ...(sameAsAdult ? { sameAsAdult } : { childFare }),
            flightClass,
            connectingAirport,
            isDirect,
          };
        }
      } else if (connectingRoutes.length === 1) {
        const connectingRoute = connectingRoutes[0];
        const segments = _.get(connectingRoute, 'segments', []);

        const dep = _.get(connectingRoute, 'departureAirport', '');
        const arr = _.get(connectingRoute, 'arrivalAirport', '');
        const connectingAirport = null;
        const airline = _.get(segments[0], 'airlineCode', '');
        const flightClass = _.get(connectingRoute, 'routeInventories[0].seatPublishedClass', '');

        const adultFare = _.get(fareInfo, 'adultBaseFare.amount', '');
        const childFare = _.get(fareInfo, 'childBaseFare.amount', '');
        const sameAsAdult = childFare === '0';
        const isDirect = true;

        return {
          airline,
          dep,
          arr,
          adultFare,
          ...(sameAsAdult ? { sameAsAdult } : { childFare }),
          flightClass,
          connectingAirport,
          isDirect,
        };
      }
    })
    .compact() // Remove undefined entries
    .value();
}

// Contoh penggunaan
const jsonData = [
  {"connectingFlightRoutes":[],
    "airlineFareInfo":{}},
  {"connectingFlightRoutes":[],
    "airlineFareInfo":{}}
  // ... Data JSON
];

const resultSegments = queryConnectingFlightRoutes(jsonData);
console.log(resultSegments);
