package com.binance.client.examples.trade;

import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SyncRequestClient;

import com.binance.client.examples.constants.PrivateConfig;
import com.binance.client.model.enums.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class PostOrder {
    public static BigDecimal actualPrice;
    public static BigDecimal closePrice;
    public static int leverage=20;


    public static void main(String[] args) throws InterruptedException {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        SubscriptionClient client = SubscriptionClient.create();
//        System.out.println(syncRequestClient.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
//                "1", "1", null, null, null, null));

        // place dual position side order.
        // Switch between dual or both position side, call: com.binance.client.examples.trade.ChangePositionSide

        //GET Latest Price
        client.subscribeMarkPriceEvent("ethusdt", ((event) -> {
            actualPrice=event.getMarkPrice();
            System.out.println(actualPrice);
//            client.unsubscribeAll();
        }), null);
        while (actualPrice==null){
            TimeUnit.SECONDS.sleep(1);
        }
        closePrice = actualPrice.add(actualPrice.divide(BigDecimal.valueOf(leverage)));
        //LONG
        System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.BUY, PositionSide.LONG, OrderType.LIMIT, TimeInForce.GTC,
                "0.1", actualPrice.toString(), null, null, null, null, NewOrderRespType.RESULT));
//      //Set TAKE PROFFIT is order OK
        //Check order status

//        boolean isFound = text.contains("Java8");

        System.out.println(syncRequestClient.postOrder("ETHUSDT",OrderSide.SELL, PositionSide.LONG, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                "0.1", null, null, null, closePrice.toString(), null, NewOrderRespType.RESULT));

    }
}