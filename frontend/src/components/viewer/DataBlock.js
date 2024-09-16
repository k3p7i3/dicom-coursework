
const fieldMapper = (field, value) => {
  switch (field) {
    case "patientSex":
      switch (value) {
        case "FEMALE":
          return 'Женщина';
        case "MALE":
          return 'Мужчина';
        default:
          return 'Другой';  
      }
    
    case "smokingStatus":
      switch (value) {
        case "YES":
          return 'Курит';
        case "NO":
          return 'Не курит';
        default:
          return null;
      }

    default:
      return value;
  }
}

export default function DataBlock({ schema, entity }) {
  return (
    <div className="data">
      {
        schema.map((fieldPair) => {
              var value = fieldMapper(
                fieldPair.field, 
                entity[fieldPair.field]
              );

              return (
                  value && value.length !== 0 && value[0] &&
                  <p id={fieldPair.field}>
                      <span className="title-font">{fieldPair.text}: </span>
                      <span className="dicom-value">{value}</span>
                  </p>
              )
          })
      }
    </div>
  );
}