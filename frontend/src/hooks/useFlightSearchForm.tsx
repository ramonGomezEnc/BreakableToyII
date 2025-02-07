import { useState } from "react";

// Define the flight search form interface
interface FlightSearchForm {
  departureAirportKeyword: string;
  isDepartureCode: boolean;
  arrivalAirportKeyword: string;
  isArrivalCode: boolean;
  departureDate: string; // using string as the value for date inputs
  arrivalDate: string;
  numAdults: number;
  currency: "MXN" | "USD" | "EUR";
  nonStop: boolean;
}

const today = new Date().toISOString().split("T")[0];

export const useFlightSearchForm = () => {
  const [formData, setFormData] = useState<FlightSearchForm>({
    departureAirportKeyword: "",
    isDepartureCode: true,
    arrivalAirportKeyword: "",
    isArrivalCode: true,
    departureDate: today, // default to the current date
    arrivalDate: "",
    numAdults: 1,
    currency: "MXN",
    nonStop: false,
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
      newErrors.departureAirportKeyword =
        "The departure airport code must have 3 letters (IATA).";
    }

    if (formData.isArrivalCode && formData.arrivalAirportKeyword.length !== 3) {
      newErrors.arrivalAirportKeyword =
        "The arrival airport code must have 3 letters (IATA).";
    }

    if (formData.departureAirportKeyword === formData.arrivalAirportKeyword) {
      newErrors.departureAirportKeyword =
        "The departure airport code cannot be the same as the return airport code.";
    }

    const todayDate = new Date(today);
    const departure = new Date(formData.departureDate);
    const arrival = formData.arrivalDate ? new Date(formData.arrivalDate) : null;

    if (departure < todayDate) {
      newErrors.departureDate =
        "The departure date cannot be earlier than today.";
    }

    if (arrival && arrival < departure) {
      newErrors.arrivalDate =
        "The arrival date cannot be earlier than the departure date.";
    }

    // numAdults validation
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