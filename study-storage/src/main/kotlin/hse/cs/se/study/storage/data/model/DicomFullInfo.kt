package hse.cs.se.study.storage.data.model

data class DicomFullInfo(
    val patient: Patient,
    val study: Study,
    val series: Series,
    val dicomInstance: DicomInstance
)
