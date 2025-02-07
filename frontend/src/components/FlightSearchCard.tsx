/* eslint-disable @typescript-eslint/no-explicit-any */
import { FC, FormEvent, useEffect } from "react";
import { useFlightSearchForm } from "../hooks/useFlightSearchForm";

interface FlightSearchCardProps {
  onSearch: (params: Record<string, any>) => void;
  initialData?: Record<string, any>;
  layoutClasses?: string;
  layoutForm?: string;
}

/**
 * A component that shows a flight search form, using a custom form hook.
 * Receives optional initial data to populate the form, and a callback for performing the search.
 * @param onSearch Callback to handle the final search submission.
 * @param initialData Optional initial values (usually from the URL or other state).
 * @param layoutClasses Classes to style the container if needed.
 */
export const FlightSearchCard: FC<FlightSearchCardProps> = ({
  onSearch,
  initialData = {},
  layoutClasses = "bg-white shadow-md rounded-lg p-6 w-full max-w-md",
  layoutForm = "flex flex-col space-y-4"
}) => {
  const { formData, errors, handleChange, validateForm, setFormData } =
    useFlightSearchForm(initialData);

  useEffect(() => {
    setFormData((prev) => ({
      ...prev,
      ...initialData,
    }));
  }, [initialData, setFormData]);

  const handleSubmit = (e: FormEvent) => {
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
    <div className={layoutClasses}>
      <form onSubmit={handleSubmit} className={layoutForm}>
        <div className="flex flex-col">
          <label className="text-sm font-medium">Departure Airport</label>
          <input
            type="text"
            name="departureAirportKeyword"
            value={formData.departureAirportKeyword}
            onChange={handleChange}
            className="p-2 border border-gray-300 rounded"
            placeholder="e.g. MEX"
          />
          {errors.departureAirportKeyword && (
            <p className="text-red-500 text-xs">{errors.departureAirportKeyword}</p>
          )}
        </div>

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

        <div className="flex flex-col">
          <label className="text-sm font-medium">Arrival Airport</label>
          <input
            type="text"
            name="arrivalAirportKeyword"
            value={formData.arrivalAirportKeyword}
            onChange={handleChange}
            className="p-2 border border-gray-300 rounded"
            placeholder="e.g. CAN"
          />
          {errors.arrivalAirportKeyword && (
            <p className="text-red-500 text-xs">{errors.arrivalAirportKeyword}</p>
          )}
        </div>

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
  );
};