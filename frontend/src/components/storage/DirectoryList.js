import DirectoryItem from "./DicrectoryItem.js"

function  DirectoryList({ dirs }) {

  return (
    <div>
      <h3>Папки:</h3>
      <div className="storage-objects-box">
        {dirs.map(dir => 
          <DirectoryItem dirPath={dir} key={dir}/>
        )}
      </div>
    </div>

  );
}

export default DirectoryList;