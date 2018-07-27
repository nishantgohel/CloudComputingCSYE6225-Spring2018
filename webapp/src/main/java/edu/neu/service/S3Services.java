package edu.neu.service;

import java.io.File;

public interface S3Services {
    public void uploadFile(String keyName, File file);
    public void deleteFile(String keyName);
}
