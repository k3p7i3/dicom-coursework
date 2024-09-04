package hse.cs.se.study.storage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DicomStorageApplication

fun main(args: Array<String>) {
	runApplication<DicomStorageApplication>(*args)
}
