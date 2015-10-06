/*
Copyright 2012 Artem Stasuk

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.terma.javaniotcpserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Handler for all incoming client connection.
 *
 * @see TcpServerHandlerFactory
 */
public interface TcpServerHandler {

    /**
     * Called when worker get handler from queue.
     * 
     * You can use this method for register it channel on
     * selector.
     * 
     * This method called only one time.
     *
     * @param selector - selector which will support this handler.
     */
    void register(Selector selector);

    /**
     * Called when selector receive IO event
     * it try to get attached handler from key and call
     * this method.
     *
     * @param key - event from IO
     */
    void process(SelectionKey key);

    /**
     * Called when workers were stopped and server should
     * close all not processed channels.
     * 
     * Called only one time.
     */
    void destroy();

}
