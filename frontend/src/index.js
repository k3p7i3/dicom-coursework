import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './style/nullify.css';
import { AuthProvider } from './auth/AuthProvider';

import './style/nullify.css';
import './style/fonts.css';
import './style/color.css';
import './style/style.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <AuthProvider>
        <App />
    </AuthProvider>
);