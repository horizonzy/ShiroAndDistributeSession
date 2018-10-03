package com.zy.cache;

import com.zy.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import java.util.*;

/**
 * @author: Horizon
 * @time: 21:27 2018/10/3
 * Description:
 */
@Component
public class RedisCache<K, V> implements Cache<K, V> {

    private final String CACHE_PREFIX = "shiro-cache:";
    private final int EXPIRE_TIME = 600;

    private byte[] getKey(K k) {
        if (k instanceof String) {
            return (CACHE_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public V get(K k) throws CacheException {
        System.out.println("从redis中获取权限数据");
        byte[] key = getKey(k);
        byte[] vlaue = jedisUtil.get(key);
        jedisUtil.expire(key, EXPIRE_TIME);
        if (vlaue != null) {
            return (V) SerializationUtils.deserialize(vlaue);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key, value);
        jedisUtil.expire(key, EXPIRE_TIME);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V v = get(k);
        byte[] key = getKey(k);
        jedisUtil.del(key);
        return v;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        Set<K> keys = new HashSet<>();
        Set<byte[]> keysInRedis = jedisUtil.keys(CACHE_PREFIX);
        if (CollectionUtils.isEmpty(keysInRedis)) {
            return keys;
        }
        keysInRedis.forEach(key -> keys.add((K) SerializationUtils.deserialize(key)));
        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        Set<K> keys = keys();
        keys.forEach(key -> list.add(get(key)));
        return list;
    }
}
