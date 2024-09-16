import React from 'react';

import { Outlet } from "react-router-dom";
import { NavBar } from '../components/NavBar.js';



function Layout() {
    
    return (
        <div id="layout">
            <NavBar/>
            <div id="detail">
                <Outlet />
            </div>
        </div>
    )
}

export default Layout;