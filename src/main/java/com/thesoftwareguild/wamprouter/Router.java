/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesoftwareguild.wamprouter;

import java.io.IOException;
import static java.lang.System.exit;
import java.net.URI;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.WampRouter;
import ws.wamp.jawampa.WampRouterBuilder;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;
import ws.wamp.jawampa.transport.netty.SimpleWampWebsocketListener;

/**
 *
 * @author ruckbeard
 */
public class Router {
    public static void main(String[] args) {
        new Router().start();
        
    }
    
    Subscription addProcSubscription;
    Subscription eventPublication;
    Subscription eventSubscription;
    
    public void start() {
        WampRouterBuilder routerBuilder = new WampRouterBuilder();
        WampRouter router;
        
        try {
            routerBuilder.addRealm("realm1");
            router = routerBuilder.build();
        } catch(ApplicationError e) {
            e.printStackTrace();
            return;
        }
        
        URI serverUri = URI.create("ws://127.0.0.1:8080/ws1");
        SimpleWampWebsocketListener server;
        
        IWampConnectorProvider connectionProvider = new NettyWampClientConnectorProvider();
        
        try {
            server = new SimpleWampWebsocketListener(router, serverUri, null);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        waitUntilKeypressed();
        System.out.println("Closing router");
        router.close().toBlocking().last();
        server.stop();
        exit(0);
    }
    
    private void waitUntilKeypressed() {
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
