package nl.stokpop.nettyclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.LastHttpContent.EMPTY_LAST_CONTENT;

public class DelayedMessageHandler extends ChannelInboundHandlerAdapter {

    private static volatile int overrideDelayMillis = -1;

    private final int originalDelayMillis;

    public DelayedMessageHandler(int delayMillis) {
        this.originalDelayMillis = delayMillis;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (EMPTY_LAST_CONTENT == msg) {
            int realDelayMillis = overrideDelayMillis >= 0 ? overrideDelayMillis : originalDelayMillis;

            log("READ_CHANNEL[" + Thread.currentThread().getName() + "] Delaying message by " + realDelayMillis + " ms " + msg);

            ctx.executor().schedule(() -> {
                ctx.fireChannelRead(msg); // Pass the original message downstream
            }, realDelayMillis, TimeUnit.MILLISECONDS);
        }
        else {
            log("READ_CHANNEL[" + Thread.currentThread().getName() + "] not delaying " + msg);
            ctx.fireChannelRead(msg); // Pass the original message downstream
        }
    }

    public static void changeDelay(int delayMillis) {
        log("Changing delay to " + delayMillis + " ms");
        overrideDelayMillis = delayMillis;
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}