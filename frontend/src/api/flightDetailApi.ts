import axios from "axios";
import { FlightDetail } from "../models/FlightDetail";

const BASE_URL = "http://localhost:9090/api/v1/";

/**
 * Fetches the detail of a specific flight by its ID.
 * @param flightId The ID of the flight.
 * @returns The full flight detail.
 */
export const getFlightDetail = async (flightId: string): Promise<FlightDetail> => {
  const response = await axios.get<FlightDetail>(`${BASE_URL}flights/${flightId}`);
  return response.data;
};
