import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import DicomService from "../../api/DicomService.js"
import DirectoryList from "./DirectoryList.js";
import FileList from "./FileList.js";
import arrowLeft from "../../icon/arrow-left.svg"

function StorageContent({ dirPath }) {
  const navigate = useNavigate();
  const [dirList, setDirList] = useState([]);
  const [fileList, setFileList] = useState([]);
  

  const dirPathToRender = dirPath.split('/').slice(1).slice(-1)[0];
  const goBackReference = dirPath.substring(0, dirPath.lastIndexOf('/'));

  const fecthDirContent = async (dirPath) => {

    const dirContent = (
      await DicomService.getDirectoryContents({dir: dirPath})
        .then( response => response.data )
    )

    setDirList(dirContent.dirs.map(dir => dir.slice(0, -1)))
    setFileList(dirContent.files)
  };

  const goBack = () => {
    console.log(goBackReference);
    const url = "/storage/search/" + btoa(goBackReference);
    navigate(url);
  }

  useEffect(() => {
    fecthDirContent(dirPath)
  }, [dirPath])

  return (
    <div className="storage-page">
      <div className="storage-page-header-wrapper">
        {goBackReference && <img src={arrowLeft} style={{ width: "20px", height: "auto"}} onClick={goBack} />}
        <h2>Мой диск{(dirPathToRender) ? `: ${dirPathToRender}` : ''}</h2>
      </div>
      

      {dirList.length !== 0 
        ? <DirectoryList dirs={dirList} />
        : <h3>Директорий нет</h3>
      }
      {fileList.length !== 0
        ? <FileList files={fileList} />
        : <h3>Файлов нет</h3>
      }
    </div>
  )

}

export default StorageContent;