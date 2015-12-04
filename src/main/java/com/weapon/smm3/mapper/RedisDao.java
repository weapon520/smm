package com.weapon.smm3.mapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.weapon.smm3.common.SerializeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class RedisDao {

    @Resource
    private RedisTemplate<Serializable, Serializable> redisTemplateMaster1;

    @Resource
    private RedisTemplate<Serializable, Serializable> redisTemplateMaster2;

    public RedisTemplate<Serializable, Serializable> getRedisTemplate(String key) {
        //目前只用一个redis服务器
/*        if(StringUtils.isNotBlank(key)){
            if(this.checkUsageOfMaster2(key)){ 
                return redisTemplateMaster2;
            }
        }*/
        return redisTemplateMaster1;
    }
    
    /**
     * 判断是否使用master2服务器
     * @param key
     * @return
     */
    private boolean checkUsageOfMaster2(String key){
    	boolean checkFlag = StringUtils.contains(key, ":login:");
    	checkFlag = checkFlag || StringUtils.startsWith(key, "pos:access:ip:");
    	checkFlag = checkFlag || StringUtils.startsWith(key, "pos:analysis:");
    	return checkFlag;
    }

    /**
     * 给外部调用的main方法使用
     * @param redisTemplate
     */
    public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {
        this.redisTemplateMaster1 = redisTemplate;
        this.redisTemplateMaster2 = redisTemplate;
    }

    /**
     * 判断Redis key是否存在
     * @param key Key
     * @return 存在返回true，否则返回false
     */
    public Boolean isKeyExist(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            return connection.exists(getRedisTemplate(key).getStringSerializer().serialize(key));
        });

    }

    /**
     * 
    * 获取Redis key的生存期
    * @param @param key
    * @param @return    设定文件 
    * @return Long    返回类型 
    * @throws
     */
    public Long getKeyExpireTime(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<Long>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            return connection.ttl(redisKey);
        });
    }

    /**
     * 设置Redis key的生存期
     * @param key Key
     * @param seconds 存活期（秒）
     * @return 成功设置返回true，否则返回false
     */
    public Boolean setKeyExpire(final String key, final long seconds) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                return connection.expire(redisKey, seconds);
            }
            return false;

        });
    }

    public Long del(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<Long>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            return connection.del(redisKey);
        });
    }

    /**
     * 将内容写入Redis，保存类型为String
     * @param key Key
     * @param value 内容
     */
    public void writeToString(final String key, final String value) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.del(redisKey);
            connection.set(redisKey, getRedisTemplate(key).getStringSerializer().serialize(value));
            return null;
        });
    }

    /**
     * 将内容从Redis的String类型中读取
     * @param key Key
     * @return 内容
     */
    public String readFromString(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<String>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            return getRedisTemplate(key).getStringSerializer().deserialize(connection.get(redisKey));
        });
    }

    /**
     * 将值对象写入Redis，保存类型为Hash（哈希表）
     * @param key Key
     * @param vo 值对象的实体，需带待写入的值
     * @param <T> 值对象泛型声明
     * @return 成功写入返回true，否则返回false
     */
    public <T> Boolean writeRedisVoToHash(final String key, final T vo) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            Boolean returnFlag = true;
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.del(redisKey);
            BeanWrapper wrapper = new BeanWrapperImpl(vo);
            for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors()) {
                if (!StringUtils.equalsIgnoreCase("class", propertyDescriptor.getName())) {
                    returnFlag = returnFlag && connection.hSet(
                            redisKey,
                            getRedisTemplate(key).getStringSerializer().serialize(propertyDescriptor.getName()),
                            getRedisTemplate(key).getStringSerializer().serialize(ObjectUtils.toString(wrapper.getPropertyValue(propertyDescriptor.getName())))
                    );
                }
            }
            return returnFlag;
        });
    }

    /**
     * 将值对象从Redis的Hash（哈希表）中读取
     * @param key Key
     * @param vo 值对象的实体，存放读取出来的内容，传参时传一个new出来的空内容对象即可
     * @param <T> 值对象泛型声明
     * @return 返回对应的值对象
     */
    public <T> T readRedisVoFromHash(final String key, final T vo) {
        return getRedisTemplate(key).execute((RedisCallback<T>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                BeanWrapper wrapper = new BeanWrapperImpl(vo);
                for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors()) {
                    if (!StringUtils.equalsIgnoreCase("class", propertyDescriptor.getName())) {
                        wrapper.setPropertyValue(
                                propertyDescriptor.getName(),
                                getRedisTemplate(key).getStringSerializer().deserialize(connection.hGet(redisKey, getRedisTemplate(key).getStringSerializer().serialize(propertyDescriptor.getName())))
                        );
                    }
                }
                return vo;
            } else {
                return null;
            }
        });
    }

    /**
     * 将内容插入到Redis，保存类型为List
     * key不存在的话就创建，存在的话就append（lpush）
     * @param key Key
     * @param value 待写入的值
     * @return 成功写入返回true，否则返回false
     */
    public Boolean writeToList(final String key, final String value) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] redisValue = getRedisTemplate(key).getStringSerializer().serialize(value);
            return connection.lPush(redisKey, redisValue) == 1;
        });
    }

    /**
     * 将内容插入到Redis，保存类型为List
     * 此方法只针对key不存在的情况，将list全部写入（所以用rpush的方法）
     * 如果key已经存在了，返回false
     * @param key Key
     * @param list 待写入的列表值
     * @return 成功写入返回true，否则返回false
     */
    public Boolean writeToList(final String key, final List<String> list) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                return false;
            } else {
                int count = 0;
                if (CollectionUtils.isNotEmpty(list)) {
                    for (String str : list) {
                        count += connection.rPush(redisKey, getRedisTemplate(key).getStringSerializer().serialize(str));
                    }
                }
                return count == CollectionUtils.size(list);
            }
        });
    }
    
    /**
     * 将字符串移出REDIS
     * 移出KEY对应的LIST里表中，所有的值等于VALUE的元素
     * @param key
     * @param value
     * @return
     */
    public Boolean removeFromList(final String key, final String value){
    	return getRedisTemplate(key).execute((RedisCallback<Boolean>)(connection)-> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] redisVlaue = getRedisTemplate(key).getStringSerializer().serialize(value);
            return connection.lRem(redisKey, 0, redisVlaue) == 1;
        });
    }
    
    /**
     * 将对象列表写入REDIS
     * 如果KEY已经存在，先删除，然后再写入
     * @param key
     * @param list
     * @return
     */
    public <T> Boolean writeObjectToList(final String key, final List<T> list) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.del(redisKey);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(t -> {
                    connection.rPush(redisKey, SerializeUtil.serialize(t));
                });
            }
            return true;
        });
    }
    
    /**
     * 将对象写入KEY对应的LIST对象列表
     * @param key
     * @param t
     * @return
     */
    public <T> Boolean writeObjectToList(final String key, final T t) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.rPush(redisKey, SerializeUtil.serialize(t));
            return true;
        });
    }
    
    /**
     * 从左向右读取对象列表
     * @param key
     * @return
     */
    public <T> List<T> readObjectFromList(final String key, final Class<T> cla){
    	return readObjectFromList(key, cla, 0, -1);
    }
    
    /**
     * 从左向右读取对象列表，索引位置错误不返回异常，返回列表为空
     * @param key
     * @param begin
     * 			索引开始位置[0：第一个元素]
     * @param end
     * 			索引结束位置[-1：最后一个元素，-2：倒数第2个元素...]
     * @return
     */
    public <T> List<T> readObjectFromList(final String key, final Class<T> cla, final int begin, final int end) {
        return getRedisTemplate(key).execute((RedisCallback<List<T>>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                List<byte[]> redisList = connection.lRange(redisKey, begin, end);
                List<T> voList = Lists.newArrayList();
                for (byte[] obj : redisList) {
                    T t = (T) SerializeUtil.unserialize(obj, cla);
                    voList.add(t);
                }
                return voList;
            } else {
                return null;
            }
        });
    }

    /**
     * 将内容从Redis的List中读取
     * @param key Key
     * @return 返回对应的列表
     */
    public List<String> readFromList(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<List<String>>)(connection)->{
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if(connection.exists(redisKey)) {
                List<byte[]> redisList = connection.lRange(redisKey,0,-1);
                List<String> voList = Lists.newArrayList();
                for(byte[] obj : redisList) {
                    String voJson = getRedisTemplate(key).getStringSerializer().deserialize(obj);
                    voList.add(voJson);
                }
                return voList;
            } else {
                return null;
            }
        });
    }

    /**
     * 将内容写入到Redis，保存类型为Set
     * @param key Key
     * @param value 写入Set的值
     */
    public void writeToSet(final String key, final String value) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.sAdd(redisKey, getRedisTemplate(key).getStringSerializer().serialize(value));
            return null;
        });
    }

    public void writeToSet(final String key, final List<String> list) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.del(redisKey);
            if (CollectionUtils.isNotEmpty(list)) {
                for (String value : list) {
                    byte[] redisVlaue = getRedisTemplate(key).getStringSerializer().serialize(value);
                    connection.sAdd(redisKey, redisVlaue);
                }
            }
            return null;
        });
    }
    
    /**
     *  将内容从Redis的Set中读取
     * @param key Key
     * @return Redis中保存的Set内容
     */
    public Set<String> readFromSet(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<Set<String>>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            Set<String> strSet = Sets.newHashSet();
            Set<byte[]> redisSet = connection.sMembers(redisKey);

            if (CollectionUtils.isNotEmpty(redisSet)) {
                for (byte[] obj : redisSet) {
                    strSet.add(getRedisTemplate(key).getStringSerializer().deserialize(obj));
                }
            }
            return strSet;
        });
    }

    /**
     * 将内容写入到Redis，保存类型为Map
     * @param key
     * @param map
     */
    public <T> void writeObjectToMap(final String key, final Map<String, T> map) {
        getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (MapUtils.isNotEmpty(map)) {
                map.entrySet().forEach(entry -> {
                    byte[] fieldKey = getRedisTemplate(key).getStringSerializer().serialize(entry.getKey());
                    byte[] fieldVal = SerializeUtil.serialize(entry.getValue());
                    connection.hSet(redisKey, fieldKey, fieldVal);
                });
            }
            return true;
        });
    }

    public <T> void writeObjectToMap(final String key, final String mapkey, final T mapVal) {
        getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] fieldKey = getRedisTemplate(key).getStringSerializer().serialize(mapkey);
            byte[] fieldVal = SerializeUtil.serialize(mapVal);
            return connection.hSet(redisKey, fieldKey, fieldVal);
        });
    }

    /**
     * 从Redis中读取Map结构数据并封装到Map对象返回
     * @param key
     * @param cla
     * @return
     */
    public <T> Map<String, T> readObjectFromMap(final String key, final Class<T> cla) {
        return getRedisTemplate(key).execute((RedisCallback<Map<String, T>>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            Map<String, T> map = Maps.newHashMap();
            Map<byte[], byte[]> byteMap = connection.hGetAll(redisKey);

            if (MapUtils.isNotEmpty(byteMap)) {
                byteMap.entrySet().forEach(entry -> {
                    String fieldKey = getRedisTemplate(key).getStringSerializer().deserialize(entry.getKey());
                    T t = (T) SerializeUtil.unserialize(entry.getValue(), cla);
                    map.put(fieldKey, t);
                });
            }
            return map;
        });
    }
    
    /**
     * 从Redis中读取Map结构中某一个key的数据并返回
     * @param key
     * @param mapKey
     * @param cla
     * @return
     */
    public <T> T readObjectFromMap(final String key, final String mapKey, final Class<T> cla) {
        return getRedisTemplate(key).execute((RedisCallback<T>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] fieldKey = getRedisTemplate(key).getStringSerializer().serialize(mapKey);
            byte[] fieldVal = connection.hGet(redisKey, fieldKey);
            return (T) SerializeUtil.unserialize(fieldVal, cla);
        });
    }

    /**
     * 从Redis的Map中移除某个key及value映射
     * @param key
     * @param mapKey
     * @return
     */
    public Boolean removeFromMap(final String key, final String mapKey) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] fieldKey = getRedisTemplate(key).getStringSerializer().serialize(mapKey);
            return connection.hDel(redisKey, fieldKey) == 0 ? true : false;
        });
    }

    /**
     * 判断某个值是否存在在Redis的Set中
     * @param key Key
     * @param value 需要判断的值
     * @return 存在返回True，否则返回False
     */
    public Boolean isInSet(final String key, final String value) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] redisValue = getRedisTemplate(key).getStringSerializer().serialize(value);
            if (connection.exists(redisKey)) {
                return connection.sIsMember(redisKey, redisValue);
            } else {
                return false;
            }
        });
    }

    /**
     * 从Redis的Set中移除某个值
     * @param key
     * @param value
     * @return
     */
    public Boolean removeFromSet(final String key, final String value) {
        return getRedisTemplate(key).execute((RedisCallback<Boolean>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            byte[] redisValue = getRedisTemplate(key).getStringSerializer().serialize(value);
            if (connection.exists(redisKey)) {
                return connection.sRem(redisKey, redisValue) == 0 ? true : false;
            } else {
                return false;
            }
        });
    }

    /**
     * 按长度将Redis的List限制到指定大小
     * @param key Key
     * @param length 长度
     */
    public void trimList(final String key, final Integer length) {
        trimList(key, 0, length);
    }
    
    /**
     * 从左到右保留LIST中begin到end的元素[另外一重含义：删除不在范围内的元素]
     * @param key
     * @param begin
     * 			索引开始位置[0：第一个元素]
     * @param end
     * 			索引结束位置[-1：最后一个元素，-2：倒数第2个元素...]
     */
    public void trimList(final String key, final int begin, final int end) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            connection.lTrim(redisKey, begin, end);
            return null;
        });
    }

    /**
     * 将内容推入队列，队列是类型为SortedSet的Redis数据结构
     * 用zAdd的方法
     * @param key Key
     * @param list 待写入的列表值
     */
    public void zAdd(final String key, final List<String> list) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (CollectionUtils.isNotEmpty(list)) {
                for (String str : list) {
                    connection.zAdd(redisKey, 0, getRedisTemplate(key).getStringSerializer().serialize(str));
                }
            }
            return null;

        });
    }

    /**
     * 返回有序集sortedSet中，指定数量的元素列表
     * @param key
     * @param count
     * 			元素数量
     * @return
     */
    public List<String> zRange(final String key, final long count) {
        return zRange(key, 0, count - 1);
    }

    public List<String> zRange(final String key, final long begin, final long end) {
        return getRedisTemplate(key).execute((RedisCallback<List<String>>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            Set<byte[]> items = connection.zRange(redisKey, begin, end);
            List<String> list = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(items)) {
                for (byte[] item : items) {
                    list.add(getRedisTemplate(key).getStringSerializer().deserialize(item));
                }
            }
            return list;
        });
    }
    
    /**
     * 返回有序集合sortedSet中元素的个数
     * @param key
     * @return
     */
    public Long zCard(final String key) {
        return getRedisTemplate(key).execute((RedisCallback<Long>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            return connection.zCard(redisKey);

        });
    }

    /**
     * 从Set中移除n条内容
     * @param key
     * @param valueList
     * @return
     */
    public void sRem(final String key, final List<String> valueList) {
        getRedisTemplate(key).execute((RedisCallback<Long>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                if (CollectionUtils.isNotEmpty(valueList)) {
                    for (String value : valueList) {
                        connection.sRem(redisKey, getRedisTemplate(key).getStringSerializer().serialize(value));
                    }
                }
            }
            return null;
        });
    }

    /**
     * 从SortedSet中移除n条内容
     * @param key
     * @param valueList
     * @return
     */
    public void zRem(final String key, final List<String> valueList) {
        getRedisTemplate(key).execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                if (CollectionUtils.isNotEmpty(valueList)) {
                    for (String value : valueList) {
                        connection.zRem(redisKey, getRedisTemplate(key).getStringSerializer().serialize(value));
                    }
                }
            }
            return null;
        });
    }

    /**
     * 删除并返回LIST中的首个元素
     * @param key
     * @return
     */
    public <T> T popObjectFromList(final String key, final Class<T> cla) {
        return getRedisTemplate(key).execute((RedisCallback<T>) (connection) -> {
            byte[] redisKey = getRedisTemplate(key).getStringSerializer().serialize(key);
            if (connection.exists(redisKey)) {
                byte[] redisValue = connection.lPop(redisKey);
                return SerializeUtil.unserialize(redisValue, cla);
            } else {
                return null;
            }
        });
    }
}
