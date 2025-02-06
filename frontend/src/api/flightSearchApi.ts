import axios from "axios";

const BASE_URL = "http://localhost:9090/api/v1/";

interface FlightSearchParams {
  departureAirportKeyword: string;
  isDepartureCode: boolean;
  arrivalAirportKeyword: string;
  isArrivalCode: boolean;
  departureDate: string;
  arrivalDate?: string;
  numAdults: number;
  currency: string;
  nonStop: boolean;
}

export const searchFlights = async (params: FlightSearchParams) => {
  const realNonStop = !params.nonStop;

  const response = await axios.get(BASE_URL + "flights", {
    params: {
      departureAirportKeyword: params.departureAirportKeyword,
      isDepartureCode: params.isDepartureCode,
      arrivalAirportKeyword: params.arrivalAirportKeyword,
      isArrivalCode: params.isArrivalCode,
      departureDate: params.departureDate,
      arrivalDate: params.arrivalDate,
      numAdults: params.numAdults,
      currency: params.currency,
      nonStop: realNonStop
    },
  });

  console.log(response)

  return response.data;
};
