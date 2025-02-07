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
  export interface Itinerary {
    initialDeparture: string;
    finalArrival: string;
    departureAirportCode: string;
    departureAirportName: string;
    arrivalAirportCode: string;
    arrivalAirportName: string;
    airlineCode: string;
    airlineName: string;
    totalFlightTime: string;
    stops?: Stop[];
  }

  export interface Flight {
    id: string;
    itineraries: Itinerary[];
    totalPrice: string;
    currency: string;
    pricePerTraveler: string;
  }