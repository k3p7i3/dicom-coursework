import { useState } from "react";
import { useNavigate } from "react-router-dom";
import DataBlock from "./DataBlock";
import { patientSchema, studySchema, seriesSchema, physicianSchema, equipmentSchema, dicomInstanceSchema } from "./schemas";
import arrowLeft from "../../icon/arrow-left.svg"

export function DicomData({ dicomData }) {
  const navigate = useNavigate();
  const filePath = dicomData.dicomInstance.s3DicomFilePath
  const fileTitle = filePath.split('/').slice(-1)[0];

  const goBackReference = filePath.substring(0, filePath.lastIndexOf('/'));

  const goBack = () => {
    console.log(goBackReference);
    const url = "/storage/search/" + btoa(goBackReference);
    navigate(url);
  }

  const [patientDisplay, setPatientDisplay] = useState(false);
  const [studyDisplay, setStudyDisplay] = useState(false);
  const [seriesDisplay, setSeriesDisplay] = useState(false);
  const [physicianDisplay, setPhysicianDisplay] = useState(false);
  const [equipmentDisplay, setEquipmentDisplay] = useState(false);
  const [dicomInstanceDisplay, setDicomInstanceDisplay] = useState(false);

  const patientData = {...dicomData.patient, ...dicomData.study.patientStudy};

  console.log(dicomData)
  return <div className="dicom-info-block">

    <div className="dicom-info-header-wrapper">
      {goBackReference && <img src={arrowLeft} style={{ width: "20px", height: "auto"}} onClick={goBack} />}
      <h3>{fileTitle}</h3>
    </div>
    
    
    <span className="dicom-info-title" onClick={() => {setPatientDisplay(!patientDisplay)}}>
      Пациент
    </span>
    {patientDisplay && 
      <DataBlock schema={patientSchema} entity={patientData} />}

    <span className="dicom-info-title" onClick={() => {setStudyDisplay(!studyDisplay)}}>
      Исследование
    </span>
    {studyDisplay && 
      <DataBlock schema={studySchema} entity={dicomData.study} />}

    <span className="dicom-info-title" onClick={() => {setSeriesDisplay(!seriesDisplay)}}>
      Серия
    </span>
    {seriesDisplay && 
      <DataBlock schema={seriesSchema} entity={dicomData.series} />}

    <span className="dicom-info-title" onClick={() => {setPhysicianDisplay(!physicianDisplay)}}>
      Лечащий врач
    </span>
    {physicianDisplay && 
      <DataBlock schema={physicianSchema} entity={{...dicomData.study.physician}} />}


    <span className="dicom-info-title" onClick={() => {setEquipmentDisplay(!equipmentDisplay)}}>
      Оборудование
    </span>
    {equipmentDisplay && 
      <DataBlock schema={equipmentSchema} entity={{...dicomData.series.equipment}} />}


    <span className="dicom-info-title" onClick={() => {setDicomInstanceDisplay(!dicomInstanceDisplay)}}>
      Другое
    </span>
    {dicomInstanceDisplay && 
      <DataBlock schema={dicomInstanceSchema} entity={{...dicomData.dicomInstance}} />}
  </div>
}