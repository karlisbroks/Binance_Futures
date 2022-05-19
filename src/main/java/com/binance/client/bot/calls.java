package com.binance.client.bot;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.constant.PrivateConfig;
import com.binance.client.model.enums.*;

public class calls {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                "0.1", null, null, null, "2200", null, NewOrderRespType.RESULT);
    }
}
