import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import DicomService from "../../../api/DicomService";
import StorageItem from "./StorageItem";
import folderIcon from "../../../icon/folder.png";

export default function StudyList({ patientUid }) {

  const navigate = useNavigate();

  const [studies, setStudies] = useState([])

  const fetchStudies = async () => {
    const fetchedStudies = await DicomService.getStudies({ patientUid: patientUid })
      .then( response => response.data )

    setStudies(fetchedStudies.studies)
  }

  useEffect(() => {
    fetchStudies();
  }, [patientUid])


  const onSelectStudy = (study) => {
    const url = '/storage/study/' + btoa(study.studyUid);
    navigate(url);
  }

  return (
    <div className="storage-page">
      <h3>Исследования:</h3>

      <div className="storage-objects-box">
        {studies.map(study => 
          <StorageItem 
            objectId={study.studyId ? study.studyId : study.studyUid}
            item={study} 
            doubleClickCallback={onSelectStudy}
            iconSource={folderIcon}
            key={study.studyUid}
          />
        )}
      </div>

    </div>
  );
}