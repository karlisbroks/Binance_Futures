package com.binance.client.bot;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.constant.PrivateConfig;

public class calls {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        String out= syncRequestClient.getAccountTrades("BTCUSDT", null, null, null, null).toString();
        out = out.replaceAll("],", "\n");
        System.out.println(out);
        System.out.println("Open Trades");
        System.out.println(syncRequestClient.getOpenOrders("BTCUSDT"));


    }
}
