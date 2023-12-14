package com.oio.chatservice.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@NoArgsConstructor
public class S3Service {
    private AmazonS3 s3Client;
    /*lombok 패키지가 아닌, org.springframework.beans.factory.annotation 패키지임에 유의*/
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("oio-bucket")
    private String bucket;

    @Value("ap-northeast-2")
    private String region;

    /*자격증명이란 accessKey, secretKey를 의미하는데, 의존성 주입 시점에는 @Value 어노테이션의 값이 설정되지 않아서 @PostConstruct를 사용*/
    //의존성 주입이 이루어진 후 초기화를 수행하는 메서드이며, bean이 한 번만 초기화될 수 있도록 해줌.

    @PostConstruct
    public void setS3Client() {
        //accessKey와 secretKey를 이용하여 자격증명 객체를 얻음.
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                //자격증명을 통해 S3 Client를 가져옴
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        //업로드를 하기 위해 사용되는 함수
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                //외부에 공개할 이미지이므로, 해당 파일에 public read 권한을 추가
                .withCannedAcl(CannedAccessControlList.PublicRead));
        //업로드를 한 후, 해당 URL을 DB에 저장할 수 있도록 컨트롤러로 URL을 반환
        return s3Client.getUrl(bucket, fileName).toString();
    }
}

