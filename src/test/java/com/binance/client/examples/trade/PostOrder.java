package com.binance.client.examples.trade;

import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SyncRequestClient;

import com.binance.client.examples.constants.PrivateConfig;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.Order;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PostOrder {
    public static double actualPrice;
    public static int closePrice;
    public static int leverage=20;
    public static int random = ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    public static String orderId=Integer.toString(random);
    private static boolean isFound;
    public static String amount = "0.1";


    public static void main(String[] args) throws InterruptedException {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        SubscriptionClient client = SubscriptionClient.create();

        //GET Latest Price
        client.subscribeMarkPriceEvent("ethusdt", ((event) -> {
            actualPrice=event.getMarkPrice().doubleValue();
            System.out.println(actualPrice);
//            client.unsubscribeAll();
        }), null);
        while (actualPrice==0){
            TimeUnit.SECONDS.sleep(1);
        }
        closePrice = (int) (actualPrice+actualPrice/leverage/10);
        //LONG
        String postOrderRs=(syncRequestClient.postOrder("ETHUSDT", OrderSide.BUY, PositionSide.LONG, OrderType.LIMIT, TimeInForce.GTC,
                amount,
//                "1500",
                String.format("%.2f", actualPrice),
                null, orderId, null, null, NewOrderRespType.RESULT)).toString();
//      is order OK
        //Check order status
        while (isFound!=true){
            String orderRs=syncRequestClient.getOrder("ETHUSDT",
                    (long) random,
//                    858143724L,
                    null).toString();
            isFound = orderRs.contains("FILLED");
          System.out.println("NEW");
            TimeUnit.SECONDS.sleep(1);
        }
//
//      //Set TAKE PROFFIT
        String takeProffitOrder = (syncRequestClient.postOrder("ETHUSDT",OrderSide.SELL, PositionSide.LONG, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                amount, null, null, null, Integer.toString(closePrice), null, NewOrderRespType.RESULT)).toString();

        //Report
        System.out.println("Position created!");
        System.out.println("Actual price: "+actualPrice);
        System.out.println("Order filled: "+ isFound);
        System.out.println("Order ID: "+orderId);
        System.out.println("Take Profit set!");
        System.out.println("Close price: "+closePrice);
    }
}