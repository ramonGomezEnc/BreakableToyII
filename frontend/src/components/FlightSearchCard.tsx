/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { FC } from "react";
import { useFlightSearchForm } from "../hooks/useFlightSearchForm";

interface FlightSearchCardProps {
  onSearch: (params: Record<string, any>) => void;
}

export const FlightSearchCard: FC<FlightSearchCardProps> = ({ onSearch }) => {
  const { formData, errors, handleChange, validateForm } = useFlightSearchForm();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;
    const queryParams = {
      departureAirportKeyword: formData.departureAirportKeyword,
      isDepartureCode: formData.isDepartureCode,
      arrivalAirportKeyword: formData.arrivalAirportKeyword,
      isArrivalCode: formData.isArrivalCode,
      departureDate: formData.departureDate,
      arrivalDate: formData.arrivalDate,
      numAdults: formData.numAdults,
      currency: formData.currency,
      nonStop: formData.nonStop,
      sortBy: "",
      order: "",
      page: 0,
    };

    onSearch(queryParams);
  };

  return (
    <div className="bg-white shadow-md rounded-lg p-6 w-full max-w-md">
      <form onSubmit={handleSubmit} className="flex flex-col space-y-4">

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="departureAirportKeyword">
            Departure Airport
          </label>
          <input
            type="text"
            id="departureAirportKeyword"
            name="departureAirportKeyword"
            value={formData.departureAirportKeyword}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
            placeholder="E.g., MEX or Mexico City"
          />
          {errors.departureAirportKeyword && (
            <p className="text-red-500 text-sm">
              {errors.departureAirportKeyword}
            </p>
          )}
        </div>

        <div className="flex items-center space-x-2">
          <input
            type="checkbox"
            id="isDepartureCode"
            name="isDepartureCode"
            checked={formData.isDepartureCode}
            onChange={handleChange}
            className="h-4 w-4"
          />
          <label htmlFor="isDepartureCode" className="text-gray-700">
            Use as IATA Code
          </label>
        </div>

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="arrivalAirportKeyword">
            Arrival Airport
          </label>
          <input
            type="text"
            id="arrivalAirportKeyword"
            name="arrivalAirportKeyword"
            value={formData.arrivalAirportKeyword}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
            placeholder="E.g., JFK or New York"
          />
          {errors.arrivalAirportKeyword && (
            <p className="text-red-500 text-sm">
              {errors.arrivalAirportKeyword}
            </p>
          )}
        </div>

        <div className="flex items-center space-x-2">
          <input
            type="checkbox"
            id="isArrivalCode"
            name="isArrivalCode"
            checked={formData.isArrivalCode}
            onChange={handleChange}
            className="h-4 w-4"
          />
          <label htmlFor="isArrivalCode" className="text-gray-700">
            Use as IATA Code
          </label>
        </div>

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="departureDate">
            Departure Date
          </label>
          <input
            type="date"
            id="departureDate"
            name="departureDate"
            value={formData.departureDate}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
          />
          {errors.departureDate && (
            <p className="text-red-500 text-sm">
              {errors.departureDate}
            </p>
          )}
        </div>

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="arrivalDate">
            Arrival Date (optional)
          </label>
          <input
            type="date"
            id="arrivalDate"
            name="arrivalDate"
            value={formData.arrivalDate}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
          />
          {errors.arrivalDate && (
            <p className="text-red-500 text-sm">
              {errors.arrivalDate}
            </p>
          )}
        </div>

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="numAdults">
            Number of Adults
          </label>
          <input
            type="number"
            id="numAdults"
            name="numAdults"
            value={formData.numAdults}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
            min={1}
          />
          {errors.numAdults && (
            <p className="text-red-500 text-sm">
              {errors.numAdults}
            </p>
          )}
        </div>

        <div>
          <label className="block text-gray-700 mb-1" htmlFor="currency">
            Currency
          </label>
          <select
            id="currency"
            name="currency"
            value={formData.currency}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
          >
            <option value="MXN">MXN</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
          </select>
        </div>

        <div className="flex items-center space-x-2">
          <input
            type="checkbox"
            id="nonStop"
            name="nonStop"
            checked={formData.nonStop}
            onChange={handleChange}
            className="h-4 w-4"
          />
          <label htmlFor="nonStop" className="text-gray-700">
            Direct Flights Only
          </label>
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition-colors"
        >
          Search Flights
        </button>
      </form>
    </div>
  );
};
