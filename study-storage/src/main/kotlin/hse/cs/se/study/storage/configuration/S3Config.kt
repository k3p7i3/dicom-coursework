package hse.cs.se.study.storage.configuration

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import hse.cs.se.study.storage.configuration.properties.S3Properties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3Config(
    private val s3Properties: S3Properties
) {

    @Bean
    fun amazonS3Client(): AmazonS3 =
        AmazonS3ClientBuilder
            .standard()
            .withCredentials(createCredentialsProvider())
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    s3Properties.endpointUrl,
                    s3Properties.region
                )
            )
            .withPathStyleAccessEnabled(true)
            .withClientConfiguration(createClientConfiguration())
            .build()

    private fun createCredentialsProvider() =
        when (
            !s3Properties.credentials.accessKey.isNullOrEmpty() &&
                    !s3Properties.credentials.secretKey.isNullOrEmpty()
        ) {
            true -> AWSStaticCredentialsProvider(
                BasicAWSCredentials(
                    s3Properties.credentials.accessKey,
                    s3Properties.credentials.secretKey
                )
            )

            false -> when (s3Properties.credentials.profileName.isNullOrEmpty()) {
                true -> ProfileCredentialsProvider()
                false -> ProfileCredentialsProvider(s3Properties.credentials.profileName)
            }
        }

    private fun createClientConfiguration(): ClientConfiguration {
        val clientConfiguration = ClientConfiguration()

        s3Properties.advanced?.connectionTimeout?.let {
            clientConfiguration.connectionTimeout = it
        }

        s3Properties.advanced?.requestTimeout?.let {
            clientConfiguration.requestTimeout = it
        }

        s3Properties.advanced?.clientExecutionTimeout?.let {
            clientConfiguration.clientExecutionTimeout = it
        }

        return clientConfiguration
    }
}