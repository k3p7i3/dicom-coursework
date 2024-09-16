import { useState } from "react";
import DicomService from "../../api/DicomService";


export default function RenameForm({ objectPath, isFile, setModalVisible }) {

  const oldPath = objectPath;
  const pathPrefix = objectPath.split('/').slice(0, -1).join('');
  const shortName = isFile 
    ? objectPath.split('/').slice(-1)[0].split('.')[0]
    : objectPath.split('/').slice(-1)[0];

  const [objectName, setObjectName] = useState(shortName);


  const renameObject = async (e) => {
    e.preventDefault();

    if (objectName.length !== 0) {
      const newFullPath = `${pathPrefix}/${objectName}${isFile ? ".dcm" : ""}`

      isFile
      ? await DicomService.renameDicom({ oldFilePath: oldPath, newFilePath: newFullPath })
      : await DicomService.renameDirectory({ oldPath: oldPath, newPath: newFullPath })

      setModalVisible(false);
      window.location.reload();
    }
  }


  return (
    <form className='form'>
        <h3>Переименовать { isFile ? "файл" : "директорию" }</h3>

        <label htmlFor='rename-form-name'>Название { isFile ? "файла" : "директории" }</label>
        <input className='input'
          name='rename-form-name'
          value={objectName}
          onChange={e => setObjectName(e.target.value)}
          type='text'
          placeholder='Отредактируйте название'
        />

        <button className='button' onClick={renameObject}>Переименовать</button>
    </form>
  );
};