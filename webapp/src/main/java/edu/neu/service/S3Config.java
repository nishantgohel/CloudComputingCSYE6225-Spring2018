package edu.neu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {


    @Bean
    public AmazonS3 s3client() {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();

        return s3Client;
    }
}
