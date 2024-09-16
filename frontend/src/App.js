
import { useEffect } from "react";
import { useAuth } from "./auth/AuthProvider.js";
import { Router } from "./route/Router.js";
import { initCornerstone } from "./ConfigCornerstone.js";

function App() {
  const authHeader = useAuth().authHeader

  useEffect(() => {
    initCornerstone(authHeader)
  }, [authHeader])

  return (
    <Router />
  );
}
  
export default App;