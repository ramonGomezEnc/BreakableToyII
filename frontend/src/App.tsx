/* eslint-disable @typescript-eslint/no-explicit-any */
import "./App.css";
import { useNavigate } from "react-router-dom";
import { FlightSearchCard } from "./components/FlightSearchCard";

/**
 * Main application component. Displays a quick flight search card and
 * redirects to the search results page with query parameters.
 */
function App() {
  const navigate = useNavigate();

  /**
   * Callback when the user submits the search form in FlightSearchCard.
   * Builds a query string and navigates to /search.
   */
  const handleSearch = (params: Record<string, any>) => {
    const queryParams = new URLSearchParams(params).toString();
    navigate(`/search?${queryParams}`);
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-400 to-blue-600">
      <h1 className="text-white text-4xl font-bold mb-8">flight-search.io</h1>
      <FlightSearchCard onSearch={handleSearch} />
    </div>
  );
}

export default App;
