import React from 'react';
import { useState } from 'react';
import { useUser, useAuth } from '../auth/AuthProvider';
import EditUserForm from '../components/EditUserForm';
import Modal from '../components/ui/Modal';
import ChangePasswordForm from '../components/ChangePasswordForm';

const ProfilePage = () => {

  const auth = useAuth()
  const user = useUser()

  const info = ['Имя','Фамилия','Email']
  const userInfo = {
    'Имя': user.firstName,
    'Фамилия': user.lastName,
    'Email': user.email
  }

  const [editUserFormVisible, setEditUserFormVisible] = useState(false);
  const [changePasswordFormVisible, setChangePasswordFormVisible] = useState(false);

  const editUserFormComponent = <Modal 
    children={<EditUserForm setModalVisible={setEditUserFormVisible}/>} 
    visible={editUserFormVisible} 
    setVisible={setEditUserFormVisible} 
  />;
  


  const changePasswordFormComponent = <Modal 
    children={<ChangePasswordForm setModalVisible={setChangePasswordFormVisible}/>} 
    visible={changePasswordFormVisible} 
    setVisible={setChangePasswordFormVisible} 
  />;

  return (
    <div className="content-container">

      <div className='side-bar'>
        <button className='button' onClick={() => setEditUserFormVisible(true)}>Изменить данные</button>
        <button className='button' onClick={() => setChangePasswordFormVisible(true)}>Изменить пароль</button>
        <button className='button' onClick={auth.logOut}>Выйти из аккаунта</button>
      </div>
      
      <div className='content-page'>

        <div className='user-page'>
          <h2>Аккаунт пользователя</h2>
          <div className='user-data'>
            {info.map((i) => {
              return <div className='user-info'>
                <div className='user-info__tag'>{i}</div>
                <div className='user-info__value'>{userInfo[i]}</div>
              </div>
            })}
          </div>


          {editUserFormComponent}
          {changePasswordFormComponent}

        </div>  
      </div>
    </div>


  )

}

export default ProfilePage;