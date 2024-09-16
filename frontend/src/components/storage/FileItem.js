import { useState } from "react";
import { useNavigate } from "react-router-dom"
import fileIcon from "../../icon/file.png";
import ItemMenu from "./ItemMenu";
import menuIcon from "../../icon/menu-icon.svg";
import DicomService from "../../api/DicomService";
import Modal from "../ui/Modal";
import RenameForm from "./RenameForm";

function FileItem({ filePath }) {
  const fileName = filePath.split('/').slice(-1)[0]

  const navigate = useNavigate()

  const [isMenuVisible, setIsMenuVisible] = useState(false)

  const redirectOnDoubleClick = () => {
    navigate('/storage/viewer/' + btoa(filePath))
  } 


  const [renameFormVisible, setRenameFormVisible] = useState(false)
  const renameFormComponent = <Modal 
    children={<RenameForm objectPath={filePath} isFile={true} setModalVisible={setRenameFormVisible}/>} 
    visible={renameFormVisible} 
    setVisible={setRenameFormVisible} 
  />;

  const renameCallback = (e) => {
    e.preventDefault();
    setIsMenuVisible(false);
    setRenameFormVisible(true);
  }

  const deleteCallback = async (e) => {
    e.preventDefault();

    await DicomService.deleteDicom({ filePath: filePath });
    window.location.reload();
  };

  return (
    <div className="storage-object" 
        onDoubleClick={redirectOnDoubleClick}>

      <div className="storage-object-title">
        <div className="storage-object-name">{fileName}</div>

        <div className="storage-object-menu-wrapper">
          <button className="button storage-object-menu-icon" onClick={() => setIsMenuVisible(true)}>
            <img style={{ height: "10px"}} src={menuIcon}/>
          </button>
          <ItemMenu 
            isVisible={isMenuVisible} 
            setVisible={setIsMenuVisible}
            deleteCallback={deleteCallback}
            renameCallback={renameCallback}
          />
        </div>

      </div>

      <img className="storage-object-preview" src={fileIcon} />

      {renameFormComponent}

    </div>
  )
}

export default FileItem;