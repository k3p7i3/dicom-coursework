import { useEffect, useState } from "react";
import DicomService from "../../api/DicomService";


export default function UploadFileForm({ dirPath, setModalVisible }) {

  const [selectedFile, setSelectedFile] = useState(null);
  const [fileName, setFileName] = useState('');

  const onFileSelect = (e) => {
    setSelectedFile(e.target.files[0]);
  }

  useEffect(() => {
    if (selectedFile) {
      setFileName(selectedFile.name);
    }
  }, [selectedFile])

  const uploadFile = async (e) => {
    e.preventDefault();

    const filePath = `${dirPath}/${fileName}`;
    await DicomService.uploadDicom({ filePath: filePath, file: selectedFile });

    setModalVisible(false);
    setSelectedFile(null);
    setFileName('');
    window.location.reload();
  }

  return (
    <form className="form">
      <h3>Загрузуть файл</h3>

      <label htmlFor='upload-file-form'>Выберите файл</label>
      <input className="file-input"
        name="upload-file-form"
        accept=".dcm"
        type="file"
        onChange={onFileSelect}
      />

      <label htmlFor='upload-file-name'>Название файла</label>
      <input className="input"
        name="upload-file-name-form"
        type="text" 
        value={fileName} 
        onChange={(e) => setFileName(e.target.value)}
        placeholder="Введите имя файла"
      />

      <button className="button" onClick={uploadFile}>Загрузить</button>
    </form>
  );
}