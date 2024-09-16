
export default function StorageItem({ objectId, item, doubleClickCallback, iconSource }) {
  return (
    <div className="storage-object" 
        onDoubleClick={() => doubleClickCallback(item)}>

      <div className="storage-object-title">
        <div className="storage-object-name">{objectId}</div>
      </div>

      <img className="storage-object-preview" src={iconSource} />

    </div>
  );
}