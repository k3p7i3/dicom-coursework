import FileItem from "./FileItem.js"

function  FileList({ files }) {

  return (
    <div>
      <h3>Файлы:</h3>
      <div className="storage-objects-box">
        {files.map(file => 
          <FileItem filePath={file} key={file}/>
        )}
      </div>
    </div>

  );
}

export default FileList;