import { useNavigate } from "react-router-dom";
import { useState } from "react";
import folderIcon from "../../icon/folder.png";
import ItemMenu from "./ItemMenu";
import menuIcon from "../../icon/menu-icon.svg"
import DicomService from "../../api/DicomService";
import RenameForm from "./RenameForm";
import Modal from '../ui/Modal.js';


function DirectoryItem({ dirPath }) {
  const dirName = dirPath.split('/').slice(-1)[0];
  const navigate = useNavigate();

  const [isMenuVisible, setIsMenuVisible] = useState(false);

  const redirectOnDoubleClick = () => {
    navigate('/storage/search/' + btoa(dirPath))
  };

  const [renameFormVisible, setRenameFormVisible] = useState(false)
  const renameFormComponent = <Modal 
    children={<RenameForm objectPath={dirPath} isFile={false} setModalVisible={setRenameFormVisible}/>} 
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

    await DicomService.deleteDirectory({ dirPath: dirPath });
    window.location.reload();
  };

  return (
    <div className="storage-object" 
        onDoubleClick={redirectOnDoubleClick}>

      <div className="storage-object-title">
        <div className="storage-object-name">{dirName}</div>

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

      <img className="storage-object-preview" src={folderIcon} />

      {renameFormComponent}

    </div>
  );
}

export default DirectoryItem;