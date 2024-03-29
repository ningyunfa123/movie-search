package com.movie.search.moviesearch.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl<HK, T> implements RedisService<HK, T> {

    private RedisTemplate<String, T> redisTemplate;
    private HashOperations<String, HK, T> hashOperations;
    private ListOperations<String, T> listOperations;
    private ZSetOperations<String, T> zSetOperations;
    private SetOperations<String, T> setOperations;
    private ValueOperations<String, T> valueOperations;

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, T> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.listOperations = redisTemplate.opsForList();
        this.zSetOperations = redisTemplate.opsForZSet();
        this.setOperations = redisTemplate.opsForSet();
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void hashSet(String key, HK hashKey, T domain) {
        hashOperations.put(key, hashKey, domain);
    }

    @Override
    public Map<HK, T> hashFindAll(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public T hashGet(String key, HK hashKey) {
        return hashOperations.get(key, hashKey);
    }

    @Override
    public void hashRemove(String key, HK hashKey) {
        hashOperations.delete(key, hashKey);
    }

    @Override
    public Long listPush(String key, T domain) {
        return listOperations.rightPush(key, domain);
    }

    @Override
    public Long listUnshift(String key, T domain) {
        return listOperations.leftPush(key, domain);
    }

    @Override
    public List<T> listFindAll(String key) {
        if (! redisTemplate.hasKey(key)){
            return null;
        }
        return listOperations.range(key, 0, listOperations.size(key));
    }

    @Override
    public T listLPop(String key) {
        return listOperations.leftPop(key);
    }

    @Override
    public void set(String key, T domain) {
        valueOperations.set(key, domain);
    }

    @Override
    public boolean set(String key, T domain, Long timeOut) {
        valueOperations.set(key, domain, timeOut);
        return expirse(key, timeOut);
    }

    @Override
    public T get(String key) {
        return valueOperations.get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean expirse(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }
}
