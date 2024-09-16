import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PatientList from '../components/storage/dicom-hierarchy/PatientList.js';
import StudyList from '../components/storage/dicom-hierarchy/StudyList.js';
import SeriesList from '../components/storage/dicom-hierarchy/SeriesList.js';
import DicomList from '../components/storage/dicom-hierarchy/DicomList.js';
import { useUser } from '../auth/AuthProvider.js';

const DicomHierarchyPage = ({ level }) => {
  const navigate = useNavigate();
  const params = useParams();
  const user = useUser();
  const uid = atob(params.uid);

  const componentMap = {
    "patient": (domain) => <PatientList domain={domain}/>,
    "study": (patientUid) => <StudyList patientUid={patientUid}/>,
    "series": (studyUid) => <SeriesList studyUid={studyUid}/>,
    "dicom": (seriesUid) => <DicomList seriesUid={seriesUid}/>
  }

  const contentComponent = (componentMap[level])(uid)

  const redirectToSearchPage = () => {
    navigate('/storage/search/' + btoa(user.domain));
  }

  return (
    <div className='content-container'>
      <div className='side-bar'>
        <button className='button' onClick={redirectToSearchPage}>Файловая структура</button>
      </div>

      <div className='content-page'>
        {contentComponent}
      </div>
    </div>
  )
};


export default DicomHierarchyPage;