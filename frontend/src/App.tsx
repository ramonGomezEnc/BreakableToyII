import './App.css'
import { FlightSearchCard } from "./components/FlightSearchCard";

function App() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-400 to-blue-600">
      <h1 className="text-white text-4xl font-bold mb-8">
        flight-search.io
      </h1>
      <FlightSearchCard />
    </div>
  );
}

export default App;