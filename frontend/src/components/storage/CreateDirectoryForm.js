import { useState } from "react";
import DicomService from "../../api/DicomService";


export default function CreateDirectoryForm({ parentDir, setModalVisible }) {

  const [directoryName, setDirectoryName] = useState('')

  const createDirectory = async (e) => {
    e.preventDefault()

    if (directoryName.length !== 0) {
      await DicomService.createDirectory({ dirPath: `${parentDir}/${directoryName}` });
      setModalVisible(false);
      setDirectoryName('');
      window.location.reload();
    }
  }

  return (
    <form className='form'>
        <h3>Создать директорию</h3>

        <label htmlFor='create-dir-form-name'>Название директории</label>
        <input className='input'
          name='create-dir-form-name'
          value={directoryName}
          onChange={e => setDirectoryName(e.target.value)}
          type='text'
          placeholder='Введите название'
        />

        <button className='button' onClick={createDirectory}>Создать</button>
    </form>
  );
}