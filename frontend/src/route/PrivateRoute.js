import { Outlet, Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider.js"

const PrivateRoute = ({children}) => {
    const auth = useAuth();

    return (
        auth.autoLoginLoaded 
        ? (
            (auth.user == null)
            ? <Navigate to="/login"/>
            : <Outlet />
        )
        : <div>Loading...</div>
    );
}

export default PrivateRoute;