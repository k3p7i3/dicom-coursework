package hse.cs.se.study.storage.utils

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)