import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import DicomService from "../../../api/DicomService";
import StorageItem from "./StorageItem";
import fileIcon from "../../../icon/file.png";

export default function DicomList({ seriesUid }) {

  const navigate = useNavigate();

  const [dicoms, setDicoms] = useState([])

  const fetchDicoms = async () => {
    const fetchedDicoms = await DicomService.getDicoms({ seriesUid: seriesUid })
      .then( response => response.data )

    setDicoms(fetchedDicoms.dicoms);
  }

  useEffect(() => {
    fetchDicoms();
  }, [seriesUid])


  const onSelectDicom = (dicom) => {
    const url = '/storage/viewer/' + btoa(dicom.s3DicomFilePath);
    navigate(url);
  }

  return (
    <div className="storage-page">
      <h3>Файлы: </h3>

      <div className="storage-objects-box">
        {dicoms.map(dicom => 
          <StorageItem 
            objectId={dicom.s3DicomFilePath.split('/').slice(-1)[0]}
            item={dicom} 
            doubleClickCallback={onSelectDicom}
            iconSource={fileIcon}
            key={dicom.dicomUID}
          />
        )}
      </div>

    </div>
  );
}