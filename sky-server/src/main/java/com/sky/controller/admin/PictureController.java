package com.sky.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntFunction;

@Slf4j
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
        log.info("返回照片{}", resource.getDescription());
        MediaType mediaType = getMediaType(resource.getFilename());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    
    
    private Resource[] getImageResourceFromClasspath() {
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
    
    public MediaType getMediaType(String filename) {
        
        String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        switch (suffix) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.IMAGE_JPEG;
        }
    }
}