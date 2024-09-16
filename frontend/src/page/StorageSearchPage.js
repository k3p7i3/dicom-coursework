import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import StorageContent from '../components/storage/StorageContent.js';
import Modal from '../components/ui/Modal.js';
import CreateDirectoryForm from '../components/storage/CreateDirectoryForm.js';
import UploadFileForm from '../components/storage/UploadFileForm.js';
import { useUser } from '../auth/AuthProvider.js';

const StorageSearchPage = () => {
  const navigate = useNavigate()

  const domain = btoa(useUser().email);
  const params = useParams();
  const dirPath = atob(params.path);

  const [createDirFormVisible, setCreateDirFormVisible] = useState(false)
  const [uploadFileFormVisible, setUploadFileFormVisible] = useState(false)

  const createDirFormComponent = <Modal 
    children={<CreateDirectoryForm parentDir={dirPath} setModalVisible={setCreateDirFormVisible}/>} 
    visible={createDirFormVisible} 
    setVisible={setCreateDirFormVisible} 
  />;
  
  const uploadFileFormComponent = <Modal 
    children={<UploadFileForm dirPath={dirPath} setModalVisible={setUploadFileFormVisible}/>} 
    visible={uploadFileFormVisible} 
    setVisible={setUploadFileFormVisible} 
  />;

  return (
    <div className='content-container'>
      <div className='side-bar'>
        <button className='button' onClick={() => setCreateDirFormVisible(true)}>Создать папку</button>
        <button className='button' onClick={() => setUploadFileFormVisible(true)}>Загрузить файл</button>

        <button className='button' onClick={() => navigate('/storage/patients/' + domain)}>DICOM-структура</button>
      </div>

      <div className='content-page'>
        <StorageContent dirPath={dirPath}/>
      </div>

      {createDirFormComponent}
      {uploadFileFormComponent}

    </div>
  )
}

export default StorageSearchPage;