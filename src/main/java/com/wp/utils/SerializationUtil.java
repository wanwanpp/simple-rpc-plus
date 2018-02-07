package com.wp.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王萍
 * @date 2018/1/5 0004
 */
public class SerializationUtil {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    //    创建objenesis对象创建器
    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil() {}

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            //根据Class构建对应的Schema
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
//        获得对象的Class对象
        Class<T> cls = (Class<T>) obj.getClass();
        //实质上就是分配一个byte数组,这个byte数组被封装成了一个对象,
        // 而且LinkedBuffer还存着下一个LinkedBuffer的引用,类似一个链表
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
//            构建类的Schema
            Schema<T> schema = getSchema(cls);
//            使用给定的Schema将对象序列化成一个byte数组
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }

    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
//            使用objenesis创建对象实例
            T object = (T) objenesis.newInstance(cls);
//            构建类对应的Schema
            Schema<T> schema = getSchema(cls);
//            使用给定的Schema将创建的实例对象与含有对象信息的byte数组进行合并得到序列化后的对象
            ProtostuffIOUtil.mergeFrom(data, object, schema);
//            返回反序列化好的对象
            return object;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
