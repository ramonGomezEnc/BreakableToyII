import React, { useEffect, useState } from "react";
import { FlightDetail } from "../models/FlightDetail";
import { getFlightDetail } from "../api/flightDetailApi";

interface FlightDetailModalProps {
  flightId: string;
  onClose: () => void;
}

/**
 * Modal that displays detailed information of a flight, including segments and price breakdown.
 * @param flightId The flight ID to fetch details from the backend.
 * @param onClose Function to close the modal.
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

            {flightDetail.segments.map((segment, i) => (
              <div
                key={i}
                className="border border-gray-200 rounded p-3 mb-4 flex justify-between"
              >
                <div className="flex-1">
                  <p className="text-sm font-semibold">Segment {i + 1}</p>
                  <p className="text-xs text-gray-500">
                    {segment.departureTime} → {segment.arrivalTime}
                  </p>
                  <p className="text-sm mt-1">
                    {flightDetail.departureAirportName} ({flightDetail.departureAirportCode}) →{" "}
                    {flightDetail.arrivalAirportName} ({flightDetail.arrivalAirportCode})
                  </p>
                  <p className="text-sm mt-1">
                    {segment.airlineName} ({segment.airlineCode}) {segment.flightNumber}
                  </p>
                  <p className="text-xs text-gray-500">{segment.aircraftType}</p>
                  {segment.layoverTime && (
                    <p className="text-xs text-gray-400 mt-1">
                      Layover: {segment.layoverTime}
                    </p>
                  )}
                </div>
              </div>
            ))}

            <div className="flex flex-col md:flex-row justify-between mt-4">
              <div className="border border-gray-200 p-3 mb-4 md:mb-0 md:mr-4 flex-1">
                <h4 className="font-semibold">Price Breakdown</h4>
                <p className="text-sm">
                  <strong>Base Price:</strong> ${flightDetail.priceBreakdown.basePrice}{" "}
                  {flightDetail.priceBreakdown.currency}
                </p>
                <div className="text-sm">
                  <strong>Fees:</strong>
                  <ul className="list-disc list-inside ml-4">
                    {flightDetail.priceBreakdown.fees.map((fee, i) => (
                      <li key={i}>
                        {fee.type}: ${fee.amount}
                      </li>
                    ))}
                  </ul>
                </div>
                <p className="text-sm">
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
