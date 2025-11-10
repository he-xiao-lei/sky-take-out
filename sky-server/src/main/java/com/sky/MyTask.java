package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
@Slf4j
public class MyTask {

    @Scheduled(cron = "0/2 * * * * ?")
    public void taskDemo(){
        log.info("定时任务执行了{}",new Date());
        float f = 1.3f;
        dolt(f);
    }

    void dolt(Float f){
        System.out.println("float");
    }
}
