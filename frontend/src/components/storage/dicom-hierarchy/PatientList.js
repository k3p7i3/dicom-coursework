import { useEffect, useState } from "react"
import DicomService from "../../../api/DicomService"
import { useNavigate } from "react-router-dom"
import StorageItem from "./StorageItem";
import folderIcon from "../../../icon/folder.png";

export default function PatientList({ domain }) {
  const navigate = useNavigate();

  const [patients, setPatients] = useState([]);

  const fetchPatients = async () => {
    const fetchedPatients = (
      await DicomService.getPatients({ userEmail: domain })
      .then( response => response.data )
    );

    setPatients(fetchedPatients.patients);
  }

  useEffect(() => {
   fetchPatients();
  }, [domain])


  const onSelectPatient = (patient) => {
    const url = '/storage/patient/' + btoa(patient.patientUid);
    navigate(url);
  }

  return (
    <div className="storage-page">
      <h3>Пациенты: </h3>

      <div className="storage-objects-box">
        {patients.map(patient => 
          <StorageItem 
            objectId={patient.patientName ? patient.patientName : patient.patientId}
            item={patient} 
            doubleClickCallback={onSelectPatient}
            iconSource={folderIcon}
            key={patient.patientUid}
          />
        )}
      </div>

    </div>
  );
};