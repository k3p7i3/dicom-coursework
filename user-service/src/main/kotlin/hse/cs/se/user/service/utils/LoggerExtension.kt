package hse.cs.se.user.service.utils

import org.slf4j.Logger

fun Logger.logTrace(msg: String) {
    if (this.isTraceEnabled) {
        this.trace(msg)
    }
}

fun Logger.logInfo(msg: String) {
    if (this.isInfoEnabled) {
        this.info(msg)
    }
}

fun Logger.logWarn(msg: String, throwable: Throwable? = null) {
    if (this.isWarnEnabled) {
        if (throwable != null) {
            this.warn(msg, throwable)
        } else {
            this.warn(msg)
        }
    }
}

fun Logger.logError(msg: String, throwable: Throwable? = null) {
    if (this.isErrorEnabled) {
        if (throwable != null) {
            this.error(msg, throwable)
        } else {
            this.error(msg)
        }
    }
}
