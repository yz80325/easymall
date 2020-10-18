package jp.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 2021 on 2019/10/18.
 */
public interface IFileService {
    String upload(MultipartFile multipartFile, String path);
}
