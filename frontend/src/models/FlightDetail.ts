/**
 * Represents an amenity in a traveler's fare (e.g., baggage, seat selection).
 */
export interface Amenity {
    name: string;
    chargeable: boolean;
  }
  
  /**
   * Represents a traveler's fare details (cabin, class, amenities, etc.).
   */
  export interface TravelerFare {
    cabin: string;
    class: string;
    amenities: Amenity[];
  }
  
  /**
   * Represents a segment in a flight, including airline, flight number, timing, etc.
   */
  export interface Segment {
    departureTime: string;
    arrivalTime: string;
    airlineCode: string;
    airlineName: string;
    flightNumber: string;
    aircraftType: string;
    travelerFares: TravelerFare[];
    layoverTime?: string;
  }
  
  /**
   * Represents a fee or charge in the flight's price breakdown.
   */
  export interface Fee {
    amount: string;
    type: string;
  }
  
  /**
   * Represents the price assigned to a specific traveler (adult, child, etc.).
   */
  export interface TravelerPrice {
    travelerId: string;
    travelerType: string;
    price: string;
  }
  
  /**
   * Represents the total breakdown of the flight's pricing, including fees, currency, etc.
   */
  export interface PriceBreakdown {
    basePrice: string;
    totalPrice: string;
    currency: string;
    fees: Fee[];
    pricePerTraveler: TravelerPrice[];
  }
  
  /**
   * Represents the complete detail of a flight, including segments and a price breakdown.
   */
  export interface FlightDetail {
    id: string;
    initialDeparture: string;
    finalArrival: string;
    departureAirportCode: string;
    departureAirportName: string;
    arrivalAirportCode: string;
    arrivalAirportName: string;
    totalFlightTime: string;
    segments: Segment[];
    priceBreakdown: PriceBreakdown;
  }
  