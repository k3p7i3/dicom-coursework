import React, { useState } from 'react';
import LoginForm from '../components/LoginForm';
import RegistryForm from '../components/RegistryForm.js';

const LoginPage = () => {

    const [isLogin, setIsLogin] = useState(true);

    return (
        <div className='login-form-wrapper'>
            {isLogin 
                ? <LoginForm setIsLogin={setIsLogin}/>
                : <RegistryForm setIsLogin={setIsLogin}/>
            }
        </div>
    );
}

export default LoginPage;