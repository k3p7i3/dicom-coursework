import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import DicomService from "../../../api/DicomService";
import StorageItem from "./StorageItem";
import folderIcon from "../../../icon/folder.png";

export default function SeriesList({ studyUid }) {

  const navigate = useNavigate();

  const [series, setSeries] = useState([])

  const fetchSeries = async () => {
    const fetchedSeries = await DicomService.getSeries({ studyUid: studyUid })
      .then( response => response.data )

    setSeries(fetchedSeries.series)
  }

  useEffect(() => {
    fetchSeries();
  }, [studyUid])


  const onSelectSeries = (series) => {
    const url = '/storage/series/' + btoa(series.seriesUid);
    navigate(url);
  }

  return (
    <div className="storage-page">
      <h3>Серии: </h3>

      <div className="storage-objects-box">
        {series.map(ser => 
          <StorageItem 
            objectId={ser.seriesNumber ? ser.seriesNumber : ser.seriesUid}
            item={ser} 
            doubleClickCallback={onSelectSeries}
            iconSource={folderIcon}
            key={ser.seriesUid}
          />
        )}
      </div>

    </div>
  );
}