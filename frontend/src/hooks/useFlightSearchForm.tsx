import { useState } from "react";

// Definimos la interface del formulario
interface FlightSearchForm {
  departureAirportKeyword: string;
  isDepartureCode: boolean;
  arrivalAirportKeyword: string;
  isArrivalCode: boolean;
  departureDate: string; // usando string como valor de los inputs date
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
    departureDate: today, // default en la fecha actual
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
        "El código de aeropuerto de salida debe tener 3 letras (IATA).";
    }

    if (formData.isArrivalCode && formData.arrivalAirportKeyword.length !== 3) {
      newErrors.arrivalAirportKeyword =
        "El código de aeropuerto de llegada debe tener 3 letras (IATA).";
    }


    const todayDate = new Date(today);
    const departure = new Date(formData.departureDate);
    const arrival = formData.arrivalDate ? new Date(formData.arrivalDate) : null;

    if (departure < todayDate) {
      newErrors.departureDate =
        "La fecha de salida no puede ser anterior a hoy.";
    }

    if (arrival && arrival < departure) {
      newErrors.arrivalDate =
        "La fecha de llegada no puede ser anterior a la de salida.";
    }

    // Validación numAdults
    if (formData.numAdults < 1) {
      newErrors.numAdults = "Debe haber al menos 1 adulto.";
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
