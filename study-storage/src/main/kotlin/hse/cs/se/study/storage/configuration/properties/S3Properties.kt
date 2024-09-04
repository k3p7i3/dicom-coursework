package hse.cs.se.study.storage.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix="s3")
data class S3Properties(
    val credentials: AwsCredentials,
    val endpointUrl: String,
    val region: String,
    val buckets: Buckets,
    val advanced: AdvancedSettings? = null
) {

    data class AwsCredentials(
        val accessKey: String? = null,
        val secretKey: String? = null,
        val profileName: String? = null
    )

    data class Buckets(
        val dicomBucket: String,
        val imageBucket: String
    )

    data class AdvancedSettings(
        val connectionTimeout: Int? = null,
        val requestTimeout: Int? = null,
        val clientExecutionTimeout: Int? = null
    )
}
