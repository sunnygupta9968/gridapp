package com.gridapp.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private RedisTemplate redisTemplate;

    public <T> T get(String  key,Class<T> postClass){
        try{
            Object b= redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(b.toString(), postClass);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void  set(String  key,Object o, Long ttl){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
