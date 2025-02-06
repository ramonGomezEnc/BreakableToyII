import './App.css'
import { useNavigate } from "react-router-dom";
import { FlightSearchCard } from "./components/FlightSearchCard";

function App() {
  const navigate = useNavigate();
  const handleSearch = (params: Record<string, any>) => {
    const queryParams = new URLSearchParams(params).toString();
    navigate(`/search?${queryParams}`);
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-400 to-blue-600">
      <h1 className="text-white text-4xl font-bold mb-8">fligh-search.io</h1>
      <FlightSearchCard onSearch={handleSearch} />
    </div>
  );
}

export default App;

