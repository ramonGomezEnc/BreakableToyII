/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchFlights } from "../api/flightSearchApi"; // the function that calls Axios
import { useFlightSearchForm } from "../hooks/useFlightSearchForm";
import { FlightCard } from "../components/FlightCard";
import { FlightDetailModal } from "../components/FlightDetailModal";

export const SearchPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { formData, setFormData, errors, handleChange, validateForm } = useFlightSearchForm();

  const [flights, setFlights] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  // Here we store the total number of results returned by the backend
  const [totalCount, setTotalCount] = useState<number>(0);

  // Example fixed pageSize (number of results per page)
  const PAGE_SIZE = 10;

  // Modal states
  const [selectedFlightId, setSelectedFlightId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  // Current page (convert string to number)
  const currentPage = parseInt(searchParams.get("page") || "0", 10);

  // Calculate how many pages there are
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  // 1. On mount, initialize formData with the values from the URL
  useEffect(() => {
    const newFormData = { ...formData };

    for (const [key, value] of searchParams.entries()) {
      if (key === "numAdults") {
        newFormData[key] = parseInt(value, 10);
      } else if (
        key === "nonStop" ||
        key === "isDepartureCode" ||
        key === "isArrivalCode"
      ) {
        newFormData[key] = value === "true";
      } else {
        newFormData[key] = value;
      }
    }
    setFormData(newFormData);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 2. Every time the searchParams change, call the API
  useEffect(() => {
    fetchFlights();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  // API call
  const fetchFlights = async () => {
    setLoading(true);
    try {
      // Build the parameter object from the query
      const params: any = {};
      for (const [key, val] of searchParams.entries()) {
        params[key] = val;
      }

      // Ensure that the backend receives the size
      params.size = PAGE_SIZE;

      // Call your endpoint: { counter, data: [] }
      const response = await searchFlights(params);

      // Store the flight data and the total
      setFlights(response.data);
      setTotalCount(response.counter);
    } catch (error) {
      console.error("Error fetching flights:", error);
    } finally {
      setLoading(false);
    }
  };

  // Form submit handling
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    const updatedParams: Record<string, any> = {};

    Object.keys(formData).forEach((key) => {
      updatedParams[key] = formData[key as keyof typeof formData];
    });

    // When a new search is performed, reset the page to 0
    updatedParams.page = "0";

    setSearchParams(updatedParams);
  };

  // Sorting
  const handleSort = (sortBy: string, order: string) => {
    const updatedParams = Object.fromEntries(searchParams.entries());
    updatedParams.sortBy = sortBy;
    updatedParams.order = order;
    updatedParams.page = "0";

    setSearchParams(updatedParams);
  };

  // Navigate to another page
  const handlePageChange = (newPage: number) => {
    if (newPage < 0) return; // don't go below 0
    if (newPage >= totalPages) return; // don't go past the last page

    const updatedParams = Object.fromEntries(searchParams.entries());
    updatedParams.page = newPage.toString();

    setSearchParams(updatedParams);
  };

  // Open modal
  const handleOpenModal = (flightId: string) => {
    setSelectedFlightId(flightId);
    setShowModal(true);
  };

  // Close modal
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedFlightId(null);
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Modal for flight details */}
      {showModal && selectedFlightId && (
        <FlightDetailModal flightId={selectedFlightId} onClose={handleCloseModal} />
      )}

      {/* Top bar with the form */}
      <div className="bg-white px-4 py-6 shadow">
        <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-4">
          {/* Your airport inputs, dates, etc. */}
          {/* Departure */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Departure Airport</label>
            <input
              type="text"
              name="departureAirportKeyword"
              value={formData.departureAirportKeyword}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
              placeholder="e.g.: MEX"
            />
            {errors.departureAirportKeyword && (
              <p className="text-red-500 text-xs">{errors.departureAirportKeyword}</p>
            )}
          </div>
          {/* isDepartureCode */}
          <div className="flex items-center mt-4">
            <input
              type="checkbox"
              name="isDepartureCode"
              checked={formData.isDepartureCode}
              onChange={handleChange}
              className="mr-2"
            />
            <label className="text-sm">Use IATA code</label>
          </div>

          {/* Arrival */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Arrival Airport</label>
            <input
              type="text"
              name="arrivalAirportKeyword"
              value={formData.arrivalAirportKeyword}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
              placeholder="e.g.: CAN"
            />
            {errors.arrivalAirportKeyword && (
              <p className="text-red-500 text-xs">{errors.arrivalAirportKeyword}</p>
            )}
          </div>
          {/* isArrivalCode */}
          <div className="flex items-center mt-4">
            <input
              type="checkbox"
              name="isArrivalCode"
              checked={formData.isArrivalCode}
              onChange={handleChange}
              className="mr-2"
            />
            <label className="text-sm">Use IATA code</label>
          </div>

          {/* Departure date */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Departure Date</label>
            <input
              type="date"
              name="departureDate"
              value={formData.departureDate}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
            />
            {errors.departureDate && (
              <p className="text-red-500 text-xs">{errors.departureDate}</p>
            )}
          </div>

          {/* Arrival date */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Arrival Date</label>
            <input
              type="date"
              name="arrivalDate"
              value={formData.arrivalDate}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
            />
            {errors.arrivalDate && (
              <p className="text-red-500 text-xs">{errors.arrivalDate}</p>
            )}
          </div>

          {/* # Adults */}
          <div className="flex flex-col">
            <label className="text-sm font-medium"># Adults</label>
            <input
              type="number"
              name="numAdults"
              min={1}
              value={formData.numAdults}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
            />
            {errors.numAdults && (
              <p className="text-red-500 text-xs">{errors.numAdults}</p>
            )}
          </div>

          {/* Currency */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Currency</label>
            <select
              name="currency"
              value={formData.currency}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
            >
              <option value="MXN">MXN</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
            </select>
          </div>

          {/* NonStop */}
          <div className="flex items-center mt-4">
            <input
              type="checkbox"
              name="nonStop"
              checked={formData.nonStop}
              onChange={handleChange}
              className="mr-2"
            />
            <label className="text-sm">Non-stop flights only</label>
          </div>

          <button
            type="submit"
            className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition-colors h-fit"
          >
            Search
          </button>
        </form>
      </div>

      {/* Sorting */}
      <div className="max-w-6xl mx-auto mt-4 flex gap-2 justify-end px-4">
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("price", "ASC")}
        >
          Sort by Price (ASC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("price", "DES")}
        >
          Sort by Price (DESC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("duration", "ASC")}
        >
          Sort by Duration (ASC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("duration", "DES")}
        >
          Sort by Duration (DESC)
        </button>
      </div>

      {/* Results */}
      <div className="max-w-6xl mx-auto py-6 px-4">
        {loading && <p className="text-gray-700">Loading flights...</p>}

        {!loading && flights.length === 0 && (
          <p className="text-red-500">
            No flight information found. Try another search.
          </p>
        )}

        {!loading && flights.length > 0 && (
          <div className="grid gap-4">
            {flights.map((flight) => (
              <FlightCard
                key={flight.id}
                flight={flight}
                onCardClick={() => handleOpenModal(flight.id)}
              />
            ))}
          </div>
        )}

        {/* Pagination */}
        {!loading && flights.length > 0 && (
          <div className="mt-4 flex justify-center items-center space-x-4">
            <button
              className={`py-2 px-4 rounded ${
                currentPage === 0
                  ? "bg-gray-200 cursor-not-allowed"
                  : "bg-blue-600 text-white hover:bg-blue-700"
              }`}
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
            >
              Previous
            </button>
            <span className="font-medium">
              Page {currentPage + 1} of {totalPages}
            </span>
            <button
              className={`py-2 px-4 rounded ${
                currentPage >= totalPages - 1
                  ? "bg-gray-200 cursor-not-allowed"
                  : "bg-blue-600 text-white hover:bg-blue-700"
              }`}
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage >= totalPages - 1}
            >
              Next
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
