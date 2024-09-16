import { useState } from "react"
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";
import UserService from "../api/UserService";

const RegistryForm = ({ setIsLogin }) => {
  
  const [name, setName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorLabel, setErrorLabel] = useState('');

  const auth = useAuth()
  const navigate = useNavigate()

  const submitFormAction = async () => {
    setErrorLabel('')

    try {
      await UserService.createUser({
        userData: {
          firstName: name,
          lastName: lastName,
          email: email,
          password: password
        }
      })
    } catch (err) {
      setErrorLabel("Ошибка регистрации");
      return;
    }
      

    const res = await auth.loginAction({ email: email, password: password })
    if (!res) {
      navigate('/profile');
      return;
    }

    setErrorLabel(`Ошибка входа в систему: ${res.message}`)
  };

  const redirectToLogin = () => {
    setIsLogin(true);
  }

  return (
    <div className="login-form">
      <h2>Регистрация в DicomDisk</h2>

      <label>{errorLabel}</label>
      <label htmlFor="login-name">Имя</label>
      <input 
        className="input"
        name="login-name"
        value={name}
        placeholder="Введите имя"
        onChange={(e) => setName(e.target.value)}
      />

      <label htmlFor="login-lastname">Фамилия</label>
      <input 
        className="input"
        name="login-lastname"
        value={lastName}
        placeholder="Введите фамилию"
        onChange={(e) => setLastName(e.target.value)}
      />

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
      <button className="button" onClick={submitFormAction}>Зарегистрироваться</button>
      <button className="button" onClick={redirectToLogin}>Уже есть аккаунт? Войти</button>
    </div>
  );
}

export default RegistryForm;