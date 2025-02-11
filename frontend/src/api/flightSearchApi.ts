import axios from "axios";

const BASE_URL = "http://localhost:9090/api/v1/";

export interface FlightSearchParams {
  departureAirportKeyword: string;
  isDepartureCode: boolean;
  arrivalAirportKeyword: string;
  isArrivalCode: boolean;
  departureDate: string;
  arrivalDate?: string;
  numAdults: number;
  currency: string;
  nonStop: boolean;
  sortBy?: string;
  order?: string;
  page?: number;
}

/**
 * Calls the flight search endpoint to get paginated, filtered results.
 * @param params Search parameters to filter flights.
 * @returns An object containing flight data and a total count of results.
 */
export const searchFlights = async (params: FlightSearchParams) => {
  console.log(BASE_URL)

  const response = await axios.get(`${BASE_URL}flights`, {
    params: {
      departureAirportKeyword: params.departureAirportKeyword,
      isDepartureCode: params.isDepartureCode,
      arrivalAirportKeyword: params.arrivalAirportKeyword,
      isArrivalCode: params.isArrivalCode,
      departureDate: params.departureDate,
      arrivalDate: params.arrivalDate,
      numAdults: params.numAdults,
      currency: params.currency,
      nonStop: params.nonStop,
      sortBy: params.sortBy,
      order: params.order,
      page: params.page,
    },
  });

  console.log(response)
  return response.data;
};
