// FlightCard.tsx

import { FC } from "react";

interface Stop {
  airportCode: string;
  layoverTime: string; // e.g.: "1h 30m"
}

interface Flight {
  id: string;
  initialDeparture: string;     // "2025-02-06T20:30:00"
  finalArrival: string;         // "2025-02-08T13:30:00"
  departureAirportCode: string; // "MEX"
  departureAirportName: string; // "BENITO JUAREZ INTL"
  arrivalAirportCode: string;   // "CAN"
  arrivalAirportName: string;   // "BAIYUN INTL"
  airlineCode: string;          // "AM"
  airlineName: string;          // "AEROMEXICO"
  operatingAirlineCode?: string;
  operatingAirlineName?: string;
  totalFlightTime: string;      // "27h 00m"
  stops?: Stop[];                // layovers
  totalPrice: string;           // "29952.00"
  currency: string;             // "MXN"
  pricePerTraveler: string;     // "29952.00"
}

interface FlightCardProps {
  flight: Flight;
  onCardClick?: () => void;
}

// Helper function to format time to 12-hour format with am/pm
const formatTime = (datetime: string): string => {
  const options: Intl.DateTimeFormatOptions = {
    hour: "numeric",
    minute: "numeric",
    hour12: true,
  };
  return new Date(datetime).toLocaleTimeString([], options);
};

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
    <div onClick={handleClick} className="border border-gray-300 rounded-md p-4 flex flex-col md:flex-row md:justify-between md:items-start mb-4 bg-white">
      {/* Left column */}
      <div className="md:flex-1">

        {/* Times */}
        <p className="text-sm font-semibold">
          {departureTime} - {arrivalTime}
        </p>

        {/* Trajectory */}
        <p className="mt-1 text-sm">
          {flight.departureAirportName} ({flight.departureAirportCode}) &rarr;{" "}
          {flight.arrivalAirportName} ({flight.arrivalAirportCode})
        </p>

        {/* Durations */}
        <p className="mt-1 text-sm text-gray-700">
          {flight.totalFlightTime} {stopsLabel}
        </p>

        {/* Stops */}
        {numberOfStops > 0 && (
        <ul className="mt-1 text-xs text-gray-500">
            {flight.stops?.map((stop, index) => (
            <li key={index}>
                {stop.layoverTime} at {stop.airportCode}
            </li>
            ))}
        </ul>
        )}

        {/* Airlines */}
        <p className="mt-2 text-sm text-gray-800 font-medium">
          {flight.airlineName} ({flight.airlineCode})
        </p>
        {flight.operatingAirlineName && (
          <p className="text-xs text-gray-500">
            Operated by {flight.operatingAirlineName} ({flight.operatingAirlineCode})
          </p>
        )}
      </div>

      {/* Medium column  */}
      <div className="hidden md:block w-px bg-gray-300 mx-6 my-2"></div>

      {/* Right column  */}
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