import { useEffect, useState } from 'react';
import StackViewport from './StackViewport';
import DicomService from '../../api/DicomService.js'
import { DicomData } from './DicomData.js';

function Viewer({ filePaths }) {
  const [dicomInfo, setDicomInfo] = useState(null)
  const [frameNumbers, setFrameNumbers] = useState([])
  const [imageIds, setImageIds] = useState([])
  const [imageIdsLoaded, setImageIdsLoaded] = useState(false)

  const url = 'http://localhost:8080/api/v1/dicom/get-frame'

  useEffect(() => {
    const loadInfo = async () => {
      await DicomService.getDicomInfo({ filePath: filePaths[0]})
        .then(info => {
          setDicomInfo(info.data)
          setFrameNumbers(
            info.data.dicomInstance.hasImageData
            ? [info.data.dicomInstance.numberOfFrames]
            : [0]
          )
        })
    }

    setImageIdsLoaded(false);
    if (filePaths.length === 0) {
      return;
    }

    loadInfo();
  }, [filePaths]);

  useEffect(() => {
    setImageIds(
      filePaths
      .map((filePath, i) => {
        return [...Array(frameNumbers[i]).keys()]
          .map(frame => { return url + `?filePath=${filePath}&frame=${frame + 1}`});
      })
      .flat()
    )
    if (frameNumbers.length != 0) { 
      setImageIdsLoaded(true);
    }
  }, [frameNumbers]);


  return (imageIdsLoaded &&
    <div className="viewer-container">
      <div className='viewer-sidebar'>
        <DicomData dicomData={dicomInfo} />
      </div>
      <div className='viewer'>           
        <div className="viewports">
          <StackViewport imageIds={imageIds}/>
        </div>
      </div>
    </div>
  )
}

export default Viewer;