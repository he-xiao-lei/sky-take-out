package com.sky.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping
@Api(tags = "返回随机照片")
public class PictureController {
    
    
    @GetMapping("/random/picture")
    @ApiOperation("返回照片")
    public ResponseEntity<Resource> randomPicture() {
        Resource[] resources = getImageResourceFromClasspath();
        
        Random random = new Random();
        
        Resource resource = resources[random.nextInt(resources.length)];
        MediaType mediaType = getMediaType(resource.getFilename());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    
    /**
     * 根据类路径获取照片
     * @return 返回资源集合
     */
    private Resource[] getImageResourceFromClasspath(){
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = patternResolver.getResources("classpath:picture/*.{jpg,jpeg,png,bmp}");
            return Arrays.stream(resources)
                    .filter(element -> {
                        String filename = element.getFilename();
                        return filename != null && !filename.toLowerCase().contains("thumb");
                    }).toArray(Resource[]::new);
        } catch (Exception e) {
            return new Resource[0];
        }
    }
    
    /**
     * 根据文件后缀名判断照片类型
     * @param filename 获取文件后缀名
     * @return 返回照片类型
     */
    private MediaType getMediaType(String filename) {
        if (filename == null) return MediaType.IMAGE_JPEG;
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg": case "jpeg": return MediaType.IMAGE_JPEG;
            case "png": return MediaType.IMAGE_PNG;
            case "gif": return MediaType.IMAGE_GIF;
            case "bmp": return MediaType.valueOf("image/bmp");
            default: return MediaType.IMAGE_JPEG;
        }
    }
}