import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";

import Layout from "../page/Layout.js";
import LoginPage from "../page/LoginPage.js";
import ProfilePage from "../page/ProfilePage.js"
import StorageSearchPage from "../page/StorageSearchPage.js"
import DicomViewerPage from "../page/DicomViewerPage.js";
import PrivateRoute from "../route/PrivateRoute.js"
import ErrorPage from "../page/ErrorPage.js";
import { useUser } from "../auth/AuthProvider.js";
import DicomHierarchyPage from "../page/DicomHierarchyPage.js";

export function Router() {
  const user = useUser()

  return (
    <BrowserRouter>
      <Routes>

        <Route path='/login' element={<LoginPage />} />

        
        <Route element={<PrivateRoute />}>

          <Route element={<Layout />}>
          
            <Route path='/profile' element={<ProfilePage />} />

            <Route path='/storage/search/:path' element={<StorageSearchPage />} />

            <Route path='/storage/viewer/:path' element={<DicomViewerPage />} />

            <Route path='/storage/patients/:uid' element={<DicomHierarchyPage level={"patient"} />} />
            <Route path='/storage/patient/:uid' element={<DicomHierarchyPage level={"study"} />} />
            <Route path='/storage/study/:uid' element={<DicomHierarchyPage level={"series"} />} />
            <Route path='/storage/series/:uid' element={<DicomHierarchyPage level={"dicom"} />} />
          </Route>

        </Route>

        <Route path='*' element={<ErrorPage />} />
      </Routes>
    </BrowserRouter>
  )
}