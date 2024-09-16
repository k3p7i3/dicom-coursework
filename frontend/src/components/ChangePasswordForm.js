

import { useState } from "react";
import UserService from "../api/UserService.js";
import { useAuth, useUser } from "../auth/AuthProvider.js"


export default function ChangePasswordForm({ setModalVisible }) {

  const auth = useAuth();
  const user = useUser();

  const [oldPassword, setOldPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [errorLabel, setErrorLabel] = useState('')

  const changePassword = async (e) => {
    e.preventDefault()

    try {
      const newUser = await UserService.changePassword({
        userData: {
          email: user.email, 
          oldPassword: oldPassword, 
          newPassword: newPassword
        } 

      })
        .then(response => response.data)
    } catch (err) {
      console.log(err)
      setErrorLabel("Не получилось сменить пароль");
      return;
    }

    await auth.loginAction({ email: user.email, password: newPassword })
  
    setErrorLabel('');
    setModalVisible(false);
    window.location.reload();
  }

  return (
    <form className='form user-form'>
        <h3>Сменить пароль</h3>

        <label>{errorLabel}</label>
        
        <label htmlFor='old-password'>Старый пароль</label>
        <input className='input'
          name='old-password'
          value={oldPassword}
          onChange={e => setOldPassword(e.target.value)}
          type='password'
          placeholder='Введите старый пароль'
        />

        <label htmlFor='new-password'>Новый пароль</label>
        <input className='input'
          name='new-password'
          value={newPassword}
          onChange={e => setNewPassword(e.target.value)}
          type='password'
          placeholder='Введите новый пароль'
        />

        <button className='button' onClick={changePassword}>Сохранить</button>
    </form>
  );
}