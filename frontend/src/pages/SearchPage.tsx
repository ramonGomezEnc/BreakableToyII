/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchFlights } from "../api/flightSearchApi";
import { FlightCard } from "../components/FlightCard";
import { FlightDetailModal } from "../components/FlightDetailModal";
import { FlightSearchCard } from "../components/FlightSearchCard";

/**
 * Page that shows flight search results, sorting options, and a modal for flight details.
 */
export const SearchPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [flights, setFlights] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [selectedFlightId, setSelectedFlightId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  const PAGE_SIZE = 10;
  const currentPage = parseInt(searchParams.get("page") || "0", 10);
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  useEffect(() => {
    fetchFlights();
  }, [searchParams]);

  const fetchFlights = async () => {
    setLoading(true);
    try {
      const paramsObj: Record<string, any> = {};
      for (const [key, val] of searchParams.entries()) {
        paramsObj[key] = val;
      }
      paramsObj.size = PAGE_SIZE;
      const response = await searchFlights(paramsObj);
      setFlights(response.data);
      setTotalCount(response.counter);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handles the callback when the user submits the search form.
   * Updates the URL query parameters to trigger a new search.
   */
  const handleSearch = (updatedParams: Record<string, any>) => {
    const query = new URLSearchParams(updatedParams).toString();
    setSearchParams(query);
  };

  const handleSort = (sortBy: string, order: string) => {
    const updatedParams = Object.fromEntries(searchParams.entries());
    updatedParams.sortBy = sortBy;
    updatedParams.order = order;
    updatedParams.page = "0";
    setSearchParams(updatedParams);
  };

  const handlePageChange = (newPage: number) => {
    if (newPage < 0 || newPage >= totalPages) return;
    const updatedParams = Object.fromEntries(searchParams.entries());
    updatedParams.page = newPage.toString();
    setSearchParams(updatedParams);
  };

  const handleOpenModal = (flightId: string) => {
    setSelectedFlightId(flightId);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setSelectedFlightId(null);
    setShowModal(false);
  };

  /**
   * Builds an initial data object for the form from the current URL search params.
   */
  const buildInitialFormData = (): Record<string, any> => {
    const initialForm: Record<string, any> = {};
    for (const [key, value] of searchParams.entries()) {
      if (key === "numAdults") {
        initialForm[key] = parseInt(value, 10);
      } else if (
        key === "nonStop" ||
        key === "isDepartureCode" ||
        key === "isArrivalCode"
      ) {
        initialForm[key] = value === "true";
      } else {
        initialForm[key] = value;
      }
    }
    return initialForm;
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {showModal && selectedFlightId && (
        <FlightDetailModal flightId={selectedFlightId} onClose={handleCloseModal} />
      )}

      <div className="flex justify-center pt-6">
        <FlightSearchCard
          initialData={buildInitialFormData()}
          onSearch={handleSearch}
          layoutClasses="bg-white px-4 py-6 shadow"
          layoutForm="flex flex-wrap items-end gap-4"
        />
      </div>

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

      <div className="max-w-6xl mx-auto py-6 px-4">
        {loading && <p className="text-gray-700">Loading flights...</p>}
        {!loading && flights.length === 0 && (
          <p className="text-red-500">No flight information found. Try another search.</p>
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
