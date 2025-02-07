/**
 * Represents an intermediate stop in a flight.
 */
export interface Stop {
    airportCode: string;
    layoverTime: string;
  }
  
  /**
   * Represents basic flight information (for listing or flight cards).
   */
  export interface Flight {
    id: string;
    initialDeparture: string;
    finalArrival: string;
    departureAirportCode: string;
    departureAirportName: string;
    arrivalAirportCode: string;
    arrivalAirportName: string;
    airlineCode: string;
    airlineName: string;
    operatingAirlineCode?: string;
    operatingAirlineName?: string;
    totalFlightTime: string;
    stops?: Stop[];
    totalPrice: string;
    currency: string;
    pricePerTraveler: string;
  }
  