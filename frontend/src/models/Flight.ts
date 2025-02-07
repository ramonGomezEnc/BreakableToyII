/**
 * Representa una escala intermedia en un vuelo.
 */
export interface Stop {
    airportCode: string;
    layoverTime: string;
  }
  
  /**
   * Representa un vuelo básico para mostrar en listas o tarjetas.
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
  