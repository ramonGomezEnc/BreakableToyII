/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchFlights } from "../api/flightSearchApi"; // la función que llama a Axios
import { useFlightSearchForm } from "../hooks/useFlightSearchForm";
import { FlightCard } from "../components/FlightCard";
import { FlightDetailModal } from "../components/FlightDetailModal";

export const SearchPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { formData, setFormData, errors, handleChange, validateForm } = useFlightSearchForm();

  const [flights, setFlights] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  // Aquí guardamos el total de resultados que regresa el backend
  const [totalCount, setTotalCount] = useState<number>(0);

  // PageSize fijo de ejemplo (cantidad de resultados por página)
  const PAGE_SIZE = 10;

  // Modal states
  const [selectedFlightId, setSelectedFlightId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  // Página actual (convertimos string a number)
  const currentPage = parseInt(searchParams.get("page") || "0", 10);

  // Calculamos cuántas páginas hay
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  // 1. Al montar, inicializamos formData con los valores de la URL
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

  // 2. Cada vez que cambien los searchParams, llamamos a la API
  useEffect(() => {
    fetchFlights();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  // Llamada a la API
  const fetchFlights = async () => {
    setLoading(true);
    try {
      // Construimos el objeto de parámetros a partir de la query
      const params: any = {};
      for (const [key, val] of searchParams.entries()) {
        params[key] = val;
      }

      // Asegura que el backend reciba el size
      params.size = PAGE_SIZE;

      // Llamada a tu endpoint: { counter, data: [] }
      const response = await searchFlights(params);

      // Guardamos la data de vuelos y el total
      setFlights(response.data);
      setTotalCount(response.counter);

      // Imprimimos en consola el valor real que vino del backend
      console.log("Total Count del Backend:", response.counter);
    } catch (error) {
      console.error("Error fetching flights:", error);
    } finally {
      setLoading(false);
    }
  };

  // Manejo de submit del formulario
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    const updatedParams: Record<string, any> = {};

    Object.keys(formData).forEach((key) => {
      updatedParams[key] = formData[key as keyof typeof formData];
    });

    // Cuando se hace una nueva búsqueda, reseteamos la página a 0
    updatedParams.page = "0";

    setSearchParams(updatedParams);
  };

  // Ordenamiento
  const handleSort = (sortBy: string, order: string) => {
    const updatedParams = Object.fromEntries(searchParams.entries());
    updatedParams.sortBy = sortBy;
    updatedParams.order = order;
    updatedParams.page = "0";

    setSearchParams(updatedParams);
  };

  // Navegar a otra página
  const handlePageChange = (newPage: number) => {
    if (newPage < 0) return; // no bajar de 0
    if (newPage >= totalPages) return; // no pasar de la última página

    const updatedParams = Object.fromEntries(searchParams.entries());
    // Aseguramos pasarlo como string
    updatedParams.page = newPage.toString();

    setSearchParams(updatedParams);
  };

  // Abrir modal
  const handleOpenModal = (flightId: string) => {
    setSelectedFlightId(flightId);
    setShowModal(true);
  };

  // Cerrar modal
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedFlightId(null);
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Modal para detalles del vuelo */}
      {showModal && selectedFlightId && (
        <FlightDetailModal flightId={selectedFlightId} onClose={handleCloseModal} />
      )}

      {/* Barra superior con el formulario */}
      <div className="bg-white px-4 py-6 shadow">
        <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-4">
          {/* ... Tus inputs de aeropuerto, fechas, etc. ... */}
          {/* Salida */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Aeropuerto Salida</label>
            <input
              type="text"
              name="departureAirportKeyword"
              value={formData.departureAirportKeyword}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
              placeholder="Ej: MEX"
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
            <label className="text-sm">Usar código IATA</label>
          </div>

          {/* Llegada */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Aeropuerto Llegada</label>
            <input
              type="text"
              name="arrivalAirportKeyword"
              value={formData.arrivalAirportKeyword}
              onChange={handleChange}
              className="p-2 border border-gray-300 rounded"
              placeholder="Ej: CAN"
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
            <label className="text-sm">Usar código IATA</label>
          </div>

          {/* Fecha de salida */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Fecha de Salida</label>
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

          {/* Fecha de llegada */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Fecha de Llegada</label>
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

          {/* # adultos */}
          <div className="flex flex-col">
            <label className="text-sm font-medium"># Adultos</label>
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

          {/* Moneda */}
          <div className="flex flex-col">
            <label className="text-sm font-medium">Moneda</label>
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
            <label className="text-sm">Solo vuelos directos</label>
          </div>

          <button
            type="submit"
            className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition-colors h-fit"
          >
            Buscar
          </button>
        </form>
      </div>

      {/* sorting */}
      <div className="max-w-6xl mx-auto mt-4 flex gap-2 justify-end px-4">
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("price", "ASC")}
        >
          Ordenar por Precio (ASC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("price", "DES")}
        >
          Ordenar por Precio (DESC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("duration", "ASC")}
        >
          Ordenar por Duración (ASC)
        </button>
        <button
          className="bg-gray-300 hover:bg-gray-400 text-sm py-2 px-4 rounded"
          onClick={() => handleSort("duration", "DES")}
        >
          Ordenar por Duración (DESC)
        </button>
      </div>

      {/* results */}
      <div className="max-w-6xl mx-auto py-6 px-4">
        {loading && <p className="text-gray-700">Cargando vuelos...</p>}

        {!loading && flights.length === 0 && (
          <p className="text-red-500">
            No se encontró información de vuelos. Intenta otra búsqueda.
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

        {/* Paginación */}
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
              Anterior
            </button>
            <span className="font-medium">
              Página {currentPage + 1} de {totalPages}
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
              Siguiente
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
