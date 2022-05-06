package edu.nus.iss.sg.myrecipe.services;

import java.io.IOException;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AmazonS3Service {

    @Autowired
    private AmazonS3 s3;
    
    public String upload(final MultipartFile file, String uploaderName) {
        String objId = UUID.randomUUID().toString().substring(0, 8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.addUserMetadata("uploader", uploaderName);

        try{
            PutObjectRequest putReq = new PutObjectRequest("dumpbucket", "myrecipe/images/%s".formatted(objId), file.getInputStream(), metadata);
            putReq.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putReq);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return objId;
    }

    public void delete(final String key) {
        DeleteObjectRequest deleteReq = new DeleteObjectRequest("dumpBucket", "myrecipe/images/%s".formatted(key));
        s3.deleteObject(deleteReq);
    }
}
