import React, { FC } from "react";

interface Stop {
  airportCode: string;
  layoverTime: string; // p. ej.: "1h 30m"
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
  stops?: Stop[];                // escalas
  totalPrice: string;           // "29952.00"
  currency: string;             // "MXN"
  pricePerTraveler: string;     // "29952.00"
}

interface FlightCardProps {
  flight: Flight;
  onCardClick?: () => void;
}

// Función de ayuda para formatear solo la hora y minutos en 12h con am/pm
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
      {/* left col */}
      <div className="md:flex-1">

        {/* times */}
        <p className="text-sm font-semibold">
          {departureTime} - {arrivalTime}
        </p>

        {/* trayectory */}
        <p className="mt-1 text-sm">
          {flight.departureAirportName} ({flight.departureAirportCode}) &rarr;{" "}
          {flight.arrivalAirportName} ({flight.arrivalAirportCode})
        </p>

        {/* durations */}
        <p className="mt-1 text-sm text-gray-700">
          {flight.totalFlightTime} {stopsLabel}
        </p>

        {/* stops */}
        {numberOfStops > 0 && (
        <ul className="mt-1 text-xs text-gray-500">
            {flight.stops?.map((stop, index) => (
            <li key={index}>
                {stop.layoverTime} en {stop.airportCode}
            </li>
            ))}
        </ul>
        )}


        {/* airlines */}
        <p className="mt-2 text-sm text-gray-800 font-medium">
          {flight.airlineName} ({flight.airlineCode})
        </p>
        {flight.operatingAirlineName && (
          <p className="text-xs text-gray-500">
            Operado por {flight.operatingAirlineName} ({flight.operatingAirlineCode})
          </p>
        )}
      </div>

        {/* medium col  */}
      <div className="hidden md:block w-px bg-gray-300 mx-6 my-2"></div>

      {/* right col  */}
      <div className="mt-4 md:mt-0 md:w-1/4 flex flex-col items-start md:items-end">
        <p className="text-lg font-bold text-gray-900">
          ${flight.totalPrice} {flight.currency}
        </p>
        <p className="text-xs text-gray-500">total</p>

        <p className="text-md font-semibold mt-2">
          ${flight.pricePerTraveler} {flight.currency}
        </p>
        <p className="text-xs text-gray-500">por viajero</p>
      </div>
    </div>
  );
};
