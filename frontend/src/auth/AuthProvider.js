import { createContext, useState, useContext, useEffect } from "react";

import { setAuthToken } from "../api/client.js";
import { redirect } from "react-router-dom";
import UserService from "../api/UserService.js"

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {

  const [user, setUser] = useState(null);
  const [authHeader, setAuthHeader] = useState(null);
  const [autoLoginLoaded, setAutoLoginLoaded] = useState(false);

  const loginAction = async (data) => {
      try {
        const token = btoa(`${data.email}:${data.password}`)
        setAuthToken(token)

        const userResponse = (
          await UserService.getUser(data)
            .then(res => { return res.data })
        )

        if (userResponse) {
          setUser(userResponse);
          setAuthHeader(token);
          localStorage.setItem("authToken", token);
          setAutoLoginLoaded(true);
          return;
        }

        throw Error("Непредвиденная ошибка")

      } catch (err) {
        setAuthToken(null);
        setAutoLoginLoaded(true);
        console.error(err)


        if (err.status === 401) {
          return Error("Неверный email и/или пароль")
        }

        return err;
      }
  };

  const cachedLoginAction = async () => {
    const token = localStorage.getItem("authToken")

    if (token) {
      const [username, password] = atob(token).split(':')
      await loginAction({ email: username, password: password })
    }
  }

  useEffect(() => {
    async function tryLogin() {
      await cachedLoginAction()
    };
    tryLogin();
  }, [])

  const logOut = () => {
      setUser(null);
      setAuthHeader(null);
      setAuthToken(null);
      localStorage.removeItem("authToken");
      return redirect("/login");
  };

  return (
    <AuthContext.Provider value={{ user, authHeader, autoLoginLoaded, loginAction, logOut }}>
        {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext)
};

export const useUser = () => {
  return useContext(AuthContext).user
}