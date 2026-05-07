package com.hotel.controller;

import com.hotel.common.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    /**
     * 上传文件访问URL前缀
     */
    private static final String ACCESS_URL = "/uploads/";

    /**
     * 上传文件存储的绝对路径（项目根目录下）
     */
    private String uploadAbsolutePath;

    @PostConstruct
    public void init() {
        // 使用项目所在根目录下的 uploads 文件夹
        uploadAbsolutePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File dir = new File(uploadAbsolutePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        log.info("图片上传目录: {}", uploadAbsolutePath);
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @Operation(summary = "上传图片", description = "上传图片文件，支持jpg、png、gif、webp格式")
    @PostMapping("/image")
    public RestResult<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return RestResult.error("上传文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return RestResult.error("只能上传图片文件");
        }

        // 检查文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return RestResult.error("图片大小不能超过5MB");
        }

        try {
            // 生成文件名：日期目录 + UUID + 原始扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 创建目标目录
            String fullDir = uploadAbsolutePath + datePath;
            File dir = new File(fullDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            File destFile = new File(fullDir + File.separator + newFilename);
            file.transferTo(destFile.getAbsoluteFile());

            // 返回访问URL
            String fileUrl = ACCESS_URL + datePath + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);

            log.info("图片上传成功: {} -> {}", fileUrl, destFile.getAbsolutePath());
            return RestResult.success("上传成功", result);

        } catch (IOException e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            return RestResult.error("图片上传失败: " + e.getMessage());
        }
    }
}
