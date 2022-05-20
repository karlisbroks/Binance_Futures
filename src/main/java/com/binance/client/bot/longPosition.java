package com.binance.client.bot;

import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SyncRequestClient;
import com.binance.client.constant.PrivateConfig;
import com.binance.client.model.enums.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class LongPosition {
    private static double actualPrice;
    int closePrice;
    int takeLoss;
    int leverage=20;
    int random = ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    int random1= ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    int random2 = ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    String orderId=Integer.toString(random);
    String orderIdProfit =Integer.toString(random1);
    String orderIdLoss=Integer.toString(random2);
    private static boolean isFound;
    boolean tradeProfit;
    boolean tradeLoss;
    String amount = "0.1";
    int counter;

    public void StartLong() throws InterruptedException {
        while (true) {
            counter=0;
            RequestOptions options = new RequestOptions();
            SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                    options);
            SubscriptionClient client = SubscriptionClient.create();

            //GET Latest Price
            client.subscribeMarkPriceEvent("ethusdt", ((event) -> {
                actualPrice = event.getMarkPrice().doubleValue();
                //            System.out.println(actualPrice);
                //            client.unsubscribeAll();
            }), null);
            while (actualPrice == 0) {
                TimeUnit.SECONDS.sleep(1);
            }
            closePrice = (int) (actualPrice + actualPrice / leverage * PrivateConfig.takeProfitValue);
            takeLoss = (int) (actualPrice - actualPrice / leverage * PrivateConfig.takeLossValue);
            //LONG
            String postOrderRs = (syncRequestClient.postOrder("ETHUSDT", OrderSide.BUY, PositionSide.LONG, OrderType.LIMIT, TimeInForce.GTC,
                    amount,
//                  "1500",
                    String.format("%.2f", actualPrice),
                    null, orderId, null, null, NewOrderRespType.RESULT)).toString();
            //      is order OK
            //Check order status
            while (isFound != true) {
                String orderRs = syncRequestClient.getOrder("ETHUSDT",
                        (long) random,
                        //                    858143724L,
                        null).toString();
                isFound = orderRs.contains("FILLED");
                System.out.println("NEW "+counter);
                TimeUnit.SECONDS.sleep(1);
                counter=counter+1;
                if (counter>=20){
                    System.out.print(syncRequestClient.cancelAllOpenOrder("ETHUSDT")+" ");
                    break;
                }
            }
            if (counter>=20) continue;
            //
            //      //Set TAKE PROFFIT
            System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                    amount, null, null, orderIdProfit, Integer.toString(closePrice), null, NewOrderRespType.RESULT));
            //SET TAKE LOSS
            System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.STOP_MARKET, TimeInForce.GTC,
                    amount, null, null, orderIdLoss, Integer.toString(takeLoss), null, NewOrderRespType.RESULT));
            //Report
//            System.out.println("Position created!");
            System.out.println("Position: " + actualPrice);
            System.out.println("TAKE PROFIT: " + closePrice);
            System.out.println("STOP MARKET: " + takeLoss);
//            System.out.println("Order filled: " + isFound);
            System.out.println("Long order ID: " + orderId);
            System.out.println("TAKE PROFIT ID: " + orderIdProfit);
            System.out.println("STOP MARKET ID " + orderIdLoss);

            while (true) {
                String orderRs = (syncRequestClient.getOpenOrders("ETHUSDT")).toString();
                tradeProfit = !orderRs.contains(orderIdProfit);
                tradeLoss = !orderRs.contains(orderIdLoss);
                if (tradeProfit == true) {
                    break;
                }
                if (tradeLoss == true) {
                    break;
                }
//                System.out.println("Position still open");
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println("------TRADE COMPLETE------");
            syncRequestClient.cancelAllOpenOrder("ETHUSDT");
//            System.exit(0);
            break;
        }

    }
}
