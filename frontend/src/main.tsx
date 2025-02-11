import React from "react";
import ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider
} from "react-router-dom";
import App from "./App";
import { SearchPage } from "./pages/SearchPage";
import "./App.css";

const router = createBrowserRouter([
  { path: "/", element: <App /> },
  { path: "/search", element: <SearchPage /> },
]);

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
