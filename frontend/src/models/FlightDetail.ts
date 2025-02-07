/**
 * Representa una amenidad en la tarifa de un viajero (por ejemplo, equipaje incluido).
 */
export interface Amenity {
    name: string;
    chargeable: boolean;
  }
  
  /**
   * Representa el detalle de la tarifa de un viajero (cabina, clase, amenidades, etc.).
   */
  export interface TravelerFare {
    cabin: string;
    class: string;
    amenities: Amenity[];
  }
  
  /**
   * Representa un segmento dentro de un vuelo, con horario, aerolínea, número de vuelo, etc.
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
   * Representa un cargo o comisión dentro del desglose de precios de un vuelo.
   */
  export interface Fee {
    amount: string;
    type: string;
  }
  
  /**
   * Representa el precio asignado a un viajero específico (adulto, niño, etc.).
   */
  export interface TravelerPrice {
    travelerId: string;
    travelerType: string;
    price: string;
  }
  
  /**
   * Representa el desglose total de precios de un vuelo, incluyendo cargos, divisa y costos por viajero.
   */
  export interface PriceBreakdown {
    basePrice: string;
    totalPrice: string;
    currency: string;
    fees: Fee[];
    pricePerTraveler: TravelerPrice[];
  }
  
  /**
   * Representa el detalle completo de un vuelo, incluyendo segmentos y desglose de precios.
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
  