import { useState } from "react"
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";

const LoginForm = ({ setIsLogin }) => {
  

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorLabel, setErrorLabel] = useState('');

  const auth = useAuth()
  const navigate = useNavigate()

  const submitFormAction = async () => {
    setErrorLabel('')

    const login = auth.loginAction
    const res = await login({ email: email, password: password })

    if (!res) {
      navigate('/profile');
      return;
    }

    setErrorLabel(`Ошибка входа в систему: ${res.message}`)
  };

  const redirectToSignIn = () => {
    setIsLogin(false);
  }

  return (
    <div className="login-form">
      <h2>Вход в DicomDisk</h2>
      <label>{errorLabel}</label>

      <label htmlFor="login-email">Email</label>
      <input 
      className="input"
        name="login-email"
        value={email}
        placeholder="Enter your email"
        onChange={(e) => setEmail(e.target.value)}
      />
      <label htmlFor="login-password">Пароль</label>
      <input 
        type="password"
        className="input"
        name="login-password"
        value={password}
        placeholder="Enter your password"
        onChange={(e) => setPassword(e.target.value)}
      />
      <br/>
      <button className="button" onClick={submitFormAction}>Войти</button>
      <button className="button" onClick={redirectToSignIn}>Еще нет аккаунта? Зарегистрироваться</button>
    </div>
  );
}

export default LoginForm;