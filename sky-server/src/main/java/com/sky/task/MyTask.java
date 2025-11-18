package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author sky
 * @version 1.0
 * 自定义定时任务
 */
@Component
@Slf4j
public class MyTask {

//    @Scheduled(cron = "0/2 * * * * ?")
    public void taskDemo(){
        log.info("定时任务执行了{}",new Date());
    }

}
