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
    private static int closePrice;
    public static int takeLoss;
    private static int leverage=20;
    private static int random = ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    private static int random1= ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    private static int random2 = ThreadLocalRandom.current().nextInt(100000000, 900000000 + 1);
    private static String orderId=Integer.toString(random);
    private static String orderIdProffit=Integer.toString(random1);
    private static String orderIdLoss=Integer.toString(random2);
    private static boolean isFound;
    private static boolean tradeProffit;
    public static boolean tradeLoss;
    private static String amount = "0.1";
    private static String run;
    private static int counter;
    public LongPosition() throws InterruptedException {
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
            closePrice = (int) (actualPrice + actualPrice / leverage / 50);
            takeLoss = (int) (actualPrice - actualPrice / leverage / 50);
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
                    amount, null, null, orderIdProffit, Integer.toString(closePrice), null, NewOrderRespType.RESULT));
            //SET TAKE LOSS
            System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.STOP_MARKET, TimeInForce.GTC,
                    amount, null, null, orderIdLoss, Integer.toString(takeLoss), null, NewOrderRespType.RESULT));
            //Report
            System.out.println("Position created!");
            System.out.println("Actual price: " + actualPrice);
            System.out.println("Close price: " + closePrice);
            System.out.println("Order filled: " + isFound);
            System.out.println("Order ID: " + orderId);
            System.out.println("Take PROFFIT ID: " + orderIdProffit);
            System.out.println("Take LOSS ID " + orderIdLoss);

            while (true) {
                String orderRs = (syncRequestClient.getOpenOrders("ETHUSDT")).toString();
                tradeProffit = !orderRs.contains(orderIdProffit);
                tradeLoss = !orderRs.contains(orderIdLoss);
                if (tradeProffit == true) {
                    break;
                }
                if (tradeLoss == true) {
                    break;
                }
                System.out.println("Position still open");
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println("Trade done!!!!");
            System.out.println(syncRequestClient.cancelAllOpenOrder("ETHUSDT"));
            System.exit(0);
        }
    }
}
