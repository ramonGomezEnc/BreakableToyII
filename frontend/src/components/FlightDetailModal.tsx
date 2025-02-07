import React, { useEffect, useState } from "react";
import { FlightDetail } from "../models/FlightDetail";
import { getFlightDetail } from "../api/flightDetailApi";

interface FlightDetailModalProps {
  flightId: string;
  onClose: () => void;
}

/**
 * Modal that displays detailed information of a flight, including all itineraries,
 * segments, and price breakdown. Uses the new flight detail structure.
 */
export const FlightDetailModal: React.FC<FlightDetailModalProps> = ({
  flightId,
  onClose,
}) => {
  const [flightDetail, setFlightDetail] = useState<FlightDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFlightDetail = async () => {
      try {
        setLoading(true);
        const detail = await getFlightDetail(flightId);
        setFlightDetail(detail);
      } finally {
        setLoading(false);
      }
    };
    fetchFlightDetail();
  }, [flightId]);

  const formatTime = (datetime: string): string => {
    const options: Intl.DateTimeFormatOptions = {
      hour: "numeric",
      minute: "numeric",
      hour12: true,
    };
    return new Date(datetime).toLocaleTimeString([], options);
  };

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-md p-4 max-w-4xl w-full relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          className="absolute top-2 right-2 text-gray-600 hover:text-gray-800"
          onClick={onClose}
        >
          ✕
        </button>

        {loading && <p className="text-gray-600">Loading details...</p>}

        {!loading && flightDetail && (
          <div className="space-y-4">
            <h3 className="text-xl font-bold">Flight Details ID: {flightId}</h3>

            {/* Render each itinerary in this flight */}
            {flightDetail.itineraries.map((itinerary, iItin) => (
              <div
                key={iItin}
                className="border border-gray-200 rounded p-3 mb-4"
              >
                <h4 className="text-md font-semibold mb-2">
                  Itinerary {iItin + 1}
                </h4>
                <p className="text-sm text-gray-700">
                  {itinerary.departureAirportName} ({itinerary.departureAirportCode}) →{" "}
                  {itinerary.arrivalAirportName} ({itinerary.arrivalAirportCode})
                </p>
                <p className="text-xs text-gray-500">
                  Depart: {itinerary.initialDeparture} — Arrive: {itinerary.finalArrival}
                </p>
                <p className="text-xs text-gray-500 mb-3">
                  Duration: {itinerary.totalFlightTime}
                </p>

                {itinerary.segments.map((segment, iSeg) => (
                  <div
                    key={iSeg}
                    className="border border-gray-200 rounded p-3 mb-3"
                  >
                    <p className="text-sm font-semibold">
                      Segment {iSeg + 1}
                    </p>
                    <p className="text-xs text-gray-500">
                      {formatTime(segment.departureTime)} → {formatTime(segment.arrivalTime)}
                    </p>
                    <p className="text-sm mt-1">
                      {segment.airlineName} ({segment.airlineCode}) • Flight {segment.flightNumber}
                    </p>
                    <p className="text-xs text-gray-500">{segment.aircraftType}</p>

                    {segment.layoverTime && (
                      <p className="text-xs text-gray-400 mt-1">
                        Layover: {segment.layoverTime}
                      </p>
                    )}

                    {segment.travelerFares.map((fare, iFare) => (
                      <div key={iFare} className="mt-2 text-xs text-gray-700">
                        <p>
                          <strong>Cabin:</strong> {fare.cabin}, <strong>Class:</strong> {fare.class}
                        </p>
                        {fare.amenities.length > 0 && (
                          <div className="ml-3 mt-1">
                            <strong>Amenities:</strong>
                            <ul className="list-disc list-inside">
                              {fare.amenities.map((amenity, iAmenity) => (
                                <li key={iAmenity}>
                                  {amenity.name}{" "}
                                  {amenity.chargeable && "(Chargeable)"}
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            ))}

            <div className="flex flex-col md:flex-row justify-between">
              <div className="border border-gray-200 p-3 mb-4 md:mb-0 md:mr-4 flex-1">
                <h4 className="font-semibold">Price Breakdown</h4>
                <p className="text-sm">
                  <strong>Base Price:</strong> ${flightDetail.priceBreakdown.basePrice}{" "}
                  {flightDetail.priceBreakdown.currency}
                </p>
                <div className="text-sm mt-2">
                  <strong>Fees:</strong>
                  <ul className="list-disc list-inside ml-4">
                    {flightDetail.priceBreakdown.fees.map((fee, i) => (
                      <li key={i}>
                        {fee.type}: ${fee.amount}
                      </li>
                    ))}
                  </ul>
                </div>
                <p className="text-sm mt-2">
                  <strong>Total:</strong> ${flightDetail.priceBreakdown.totalPrice}{" "}
                  {flightDetail.priceBreakdown.currency}
                </p>
              </div>

              <div className="border border-gray-200 p-3 flex-1">
                <h4 className="font-semibold">Per Traveler</h4>
                {flightDetail.priceBreakdown.pricePerTraveler.map((tp, i) => (
                  <p key={i} className="text-sm mt-1">
                    {tp.travelerType} #{tp.travelerId}: ${tp.price}{" "}
                    {flightDetail.priceBreakdown.currency}
                  </p>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
