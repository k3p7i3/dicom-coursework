

import { useState } from "react";
import UserService from "../api/UserService.js";
import { useAuth, useUser } from "../auth/AuthProvider.js"


export default function EditUserForm({ setModalVisible }) {

  const auth = useAuth();
  const user = useUser();

  const [name, setName] = useState(user.firstName)
  const [lastName, setLastName] = useState(user.lastName)
  const [email, setEmail] = useState(user.email)

  const editUser = async (e) => {
    e.preventDefault()

    let userData = {}
    
    if (name && name !== user.firstName) {
      userData.firstName = name
    } 
    if (lastName && lastName !== user.lastName) {
      userData.lastName = lastName
    }
    if (email && email !== email.lastName) {
      userData.email = email
    }

    const newUser = await UserService.editUser({ email: user.email, userNewData: userData})
      .then(response => response.data)
    
    if (email !== user.email) {
      const password = atob(auth.authHeader).split(':')[1]
      await auth.loginAction({ email: email, password: password })
    }

    setModalVisible(false);
    window.location.reload();
  }

  return (
    <form className='form user-form'>
        <h3>Редактировать данные</h3>

        <label htmlFor='edit-user-form-name'>Имя</label>
        <input className='input'
          name='edit-user-form-name'
          value={name}
          onChange={e => setName(e.target.value)}
          type='text'
          placeholder='Введите имя'
        />

        <label htmlFor='edit-user-form-last-name'>Фамилия</label>
        <input className='input'
          name='edit-user-form-last-name'
          value={lastName}
          onChange={e => setLastName(e.target.value)}
          type='text'
          placeholder='Введите фамилию'
        />

        <label htmlFor='edit-user-form-email'>Email</label>
        <input className='input'
          name='edit-user-form-email'
          value={email}
          onChange={e => setEmail(e.target.value)}
          type='text'
          placeholder='Введите email'
        />


        <button className='button' onClick={editUser}>Сохранить</button>
    </form>
  );
}