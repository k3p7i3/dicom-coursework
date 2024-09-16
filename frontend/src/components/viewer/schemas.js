
export const patientSchema = [
  { field: "patientId", text: "Идентификатор" },
  { field: "patientName", text: "Имя" },
  { field: "patientBirthDate", text: "Дата рождения" },
  { field: "patientSex", text: "Пол" },
  { field: "patientComments", text: "Комментарии"},
  { field: "patientAge", text: "Возраст"},
  { field: "patientSize", text: "Рост"},
  { field: "patientWeight", text: "Вес"},
  { field: "medicalAlerts", text: "Медицинские противопоказания"},
  { field: "occupation", text: "Занятость"},
  { field: "smokingStatus", text: "Статус курения"},
  { field: "additionalPatientHistory", text: "Доп. история пациента"},
  { field: "patientState", text: "Состояние"},
  { field: "patientAddress", text: "Адрес"},
  { field: "patientPhoneNumbers", text: "Номер телефона"},
  { field: "patientCountry", text: "Страна"},
  { field: "patientRegion", text: "Регион"}
]

export const studySchema = [
  { field: "studyUid", text: "Уникальный индентификатор" },
  { field: "studyId", text: "Индентификатор" },
  { field: "studyDate", text: "Дата" },
  { field: "studyTime", text: "Время" },
  { field: "studyDescription", text: "Описание" }
]

export const seriesSchema = [
  { field: "seriesUid", text: "Уникальный индентификатор" },
  { field: "seriesNumber", text: "Номер серии" },
  { field: "modality", text: "Модальность" },
  { field: "seriesDate", text: "Дата" },
  { field: "seriesTime", text: "Время" },
  { field: "seriesDescription", text: "Описание" },
  { field: "performingPhysicianName", text: "Исполняющий врач" },
  { field: "operatorName", text: "Имя оператора" },
  { field: "bodyPartExamined", text: "Исследуемая часть тела" }
]

export const physicianSchema = [
  { field: "physicianName", text: "Имя" },
  { field: "institutionName", text: "Организация" },
  { field: "institutionAddress", text: "Адрес организации" },
  { field: "departmentName", text: "Отделение" },
  { field: "physicianAddress", text: "Адрес врача" },
  { field: "physicianTelephone", text: "Телефон врача" }
]

export const equipmentSchema = [
  { field: "stationName", text: "Название оборудования" },
  { field: "manufacturer", text: "Производитель" },
  { field: "institutionName", text: "Организация" },
  { field: "institutionAddress", text: "Адрес организации" },
  { field: "institutionalDepartmentName", text: "Отделение" }
]

export const dicomInstanceSchema = [
  { field: "SOPInstanceUID", text: "SOP идентификатор" },
  { field: "instanceNumber", text: "Порядковый номер файла" },
  { field: "anatomicRegion", text: "Анатомическая область" },
  { field: "anatomicRegion", text: "Анатомическая область" },
  { field: "windowCenter", text: "Window center" },
  { field: "windowWidth", text: "Window width" },
  { field: "photometric", text: "Тип изображения" }
]