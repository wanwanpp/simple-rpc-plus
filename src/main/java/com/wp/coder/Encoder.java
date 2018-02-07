package com.wp.coder;

import com.wp.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 王萍
 * @date 2018/1//5 0004
 * 继承自netty的MessageToByteEncoder
 */
public class Encoder  extends MessageToByteEncoder{
    private Class<?> genericClass;

    public Encoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
//            使用序列化工具序列化in对象
            byte[] data = SerializationUtil.serialize(in);
//            向ByteBuf中写入data的长度
            out.writeInt(data.length);
//            写入data数据
            out.writeBytes(data);
        }
    }
}
