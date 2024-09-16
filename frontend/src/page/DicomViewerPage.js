import { useParams } from 'react-router-dom';
import Viewer from '../components/viewer/Viewer.js'

const DicomViewerPage = () => {

  const params = useParams()

  return (
      <div className='content-container' style={{ margin: "0" }}>
          <Viewer filePaths={ [atob(params.path)] }/>
      </div>
  )
}

export default DicomViewerPage;