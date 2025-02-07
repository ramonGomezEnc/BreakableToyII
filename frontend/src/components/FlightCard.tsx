import { FC } from "react";
import { Flight } from "../models/Flight";

interface FlightCardProps {
  flight: Flight;
  onCardClick?: () => void;
}

/**
 * Formats a datetime string into a 12-hour format with am/pm.
 * @param datetime The ISO date/time string (e.g., "2025-02-06T20:30:00").
 * @returns A string with the formatted time, e.g. "8:30 PM".
 */
const formatTime = (datetime: string): string => {
  const options: Intl.DateTimeFormatOptions = {
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  };
  return new Date(datetime).toLocaleTimeString([], options);
};

/**
 * Renders a card with basic flight information (departure, arrival, duration, price, etc.).
 * @param flight The flight data to display.
 * @param onCardClick Callback when the card is clicked.
 */
export const FlightCard: FC<FlightCardProps> = ({ flight, onCardClick }) => {
  const departureTime = formatTime(flight.initialDeparture);
  const arrivalTime = formatTime(flight.finalArrival);
  const numberOfStops = flight.stops ? flight.stops.length : 0;
  const stopsLabel =
    numberOfStops === 0
      ? "(Nonstop)"
      : numberOfStops === 1
      ? "(1 stop)"
      : `(${numberOfStops} stops)`;

  const handleClick = () => {
    if (onCardClick) {
      onCardClick();
    }
  };

  return (
    <div
      onClick={handleClick}
      className="border border-gray-300 rounded-md p-4 flex flex-col md:flex-row md:justify-between md:items-start mb-4 bg-white cursor-pointer"
    >
      <div className="md:flex-1">
        <p className="text-sm font-semibold">
          {departureTime} - {arrivalTime}
        </p>
        <p className="mt-1 text-sm">
          {flight.departureAirportName} ({flight.departureAirportCode}) →{" "}
          {flight.arrivalAirportName} ({flight.arrivalAirportCode})
        </p>
        <p className="mt-1 text-sm text-gray-700">
          {flight.totalFlightTime} {stopsLabel}
        </p>
        {numberOfStops > 0 && (
          <ul className="mt-1 text-xs text-gray-500">
            {flight.stops?.map((stop, index) => (
              <li key={index}>
                {stop.layoverTime} at {stop.airportCode}
              </li>
            ))}
          </ul>
        )}
        <p className="mt-2 text-sm text-gray-800 font-medium">
          {flight.airlineName} ({flight.airlineCode})
        </p>
        {flight.operatingAirlineName && (
          <p className="text-xs text-gray-500">
            Operated by {flight.operatingAirlineName} ({flight.operatingAirlineCode})
          </p>
        )}
      </div>
      <div className="hidden md:block w-px bg-gray-300 mx-6 my-2"></div>
      <div className="mt-4 md:mt-0 md:w-1/4 flex flex-col items-start md:items-end">
        <p className="text-lg font-bold text-gray-900">
          ${flight.totalPrice} {flight.currency}
        </p>
        <p className="text-xs text-gray-500">total</p>
        <p className="text-md font-semibold mt-2">
          ${flight.pricePerTraveler} {flight.currency}
        </p>
        <p className="text-xs text-gray-500">per traveler</p>
      </div>
    </div>
  );
};
