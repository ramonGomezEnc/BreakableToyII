import { useState } from "react";

interface FlightSearchForm {
  departureAirportKeyword: string;
  isDepartureCode: boolean;
  arrivalAirportKeyword: string;
  isArrivalCode: boolean;
  departureDate: string;
  arrivalDate: string;
  numAdults: number;
  currency: "MXN" | "USD" | "EUR";
  nonStop: boolean;
}

const today = new Date().toISOString().split("T")[0];

/**
 * Custom hook that manages state and validation for a flight search form.
 * @param initialValues Optional initial values for the form.
 * @returns An object containing form data, errors, and form handlers.
 */
export const useFlightSearchForm = (initialValues?: Partial<FlightSearchForm>) => {
  const [formData, setFormData] = useState<FlightSearchForm>({
    departureAirportKeyword: initialValues?.departureAirportKeyword || "",
    isDepartureCode: initialValues?.isDepartureCode ?? true,
    arrivalAirportKeyword: initialValues?.arrivalAirportKeyword || "",
    isArrivalCode: initialValues?.isArrivalCode ?? true,
    departureDate: initialValues?.departureDate || today,
    arrivalDate: initialValues?.arrivalDate || "",
    numAdults: initialValues?.numAdults ?? 1,
    currency: initialValues?.currency || "MXN",
    nonStop: initialValues?.nonStop ?? false,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? (e.target as HTMLInputElement).checked : value,
    }));
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (formData.isDepartureCode && formData.departureAirportKeyword.length !== 3) {
      newErrors.departureAirportKeyword = "Departure airport code must have 3 letters.";
    }

    if (formData.isArrivalCode && formData.arrivalAirportKeyword.length !== 3) {
      newErrors.arrivalAirportKeyword = "Arrival airport code must have 3 letters.";
    }

    if (formData.departureAirportKeyword === formData.arrivalAirportKeyword) {
      newErrors.departureAirportKeyword =
        "Departure airport code cannot be the same as the arrival airport code.";
    }

    const todayDate = new Date(today);
    const departure = new Date(formData.departureDate);
    const arrival = formData.arrivalDate ? new Date(formData.arrivalDate) : null;

    if (departure < todayDate) {
      newErrors.departureDate = "Departure date cannot be earlier than today.";
    }

    if (arrival && arrival < departure) {
      newErrors.arrivalDate = "Arrival date cannot be earlier than the departure date.";
    }

    if (formData.numAdults < 1) {
      newErrors.numAdults = "There must be at least 1 adult.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  return {
    formData,
    errors,
    handleChange,
    validateForm,
    setFormData,
  };
};
