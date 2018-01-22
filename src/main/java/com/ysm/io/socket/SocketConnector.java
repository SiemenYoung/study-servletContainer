/**
 * @Title: SocketConnector
 * @Package com.ysm.io.socket
 * @Description: TODO
 * Copyright: Copyright (c) 2016
 * Company: 成都壹柒互动科技有限公司
 * @author yangshiming.ysm
 * @date 2018/1/22 11:40
 * @version V1.0
 */
package com.ysm.io.socket;

import com.ysm.event.EventListener;
import com.ysm.io.Connector;
import com.ysm.io.ConnectorException;
import com.ysm.io.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @version 1.0.0
 * @Title: SocketConnector
 * @Package: com.ysm.io.socket
 * @Description: Copyright: Copyright (c) 2016
 * @Company: 成都壹柒互动科技有限公司
 * @author yangshiming.ysm
 * @date 2018/1/22 11:40
 */
public class SocketConnector extends Connector<Socket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketConnector.class);

    private final int PORT ;

    private ServerSocket serverSocket;

    private volatile boolean started = false;

    private final EventListener<Socket> eventListener;


    public SocketConnector(int port,EventListener<Socket> eventListener) {

        this.PORT = port;

        this.eventListener = eventListener;
    }


    @Override
    public void stop() {
        this.started = false;
        IoUtils.closeQuietly(this.serverSocket);
    }

    @Override
    protected void init() throws ConnectorException {

        try {
            this.serverSocket = new ServerSocket(this.PORT);

            started = true;

        } catch (IOException e) {
            throw new ConnectorException();
        }
    }

    @Override
    protected void acceptConnect() throws ConnectorException {

        new Thread(()->{

            while (true && started) {
                Socket socket = null;

                try {
                    socket = serverSocket.accept();

                    whenAccept(socket);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }finally {
                    IoUtils.closeQuietly(socket);
                }
            }

        }).start();
    }

    @Override
    protected void whenAccept(Socket socketConnect) throws ConnectorException {

        eventListener.onEvent(socketConnect);
    }
}
