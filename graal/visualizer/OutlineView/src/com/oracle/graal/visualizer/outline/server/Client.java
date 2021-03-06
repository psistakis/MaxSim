/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.visualizer.outline.server;

import com.sun.hotspot.igv.data.serialization.Parser;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Client implements Runnable {

    private Socket socket;
    private ServerCallback callback;

    public Client(Socket socket, ServerCallback callback) {
        this.callback = callback;
        this.socket = socket;
    }

    @Override
    public void run() {
        callback.connectionOpened(socket.getInetAddress());
        try {
            InputStream inputStream = new BufferedInputStream(socket.getInputStream());
            InputSource is = new InputSource(inputStream);

            try {
                Parser parser = new Parser(callback);
                parser.parse(is, null);
            } catch (SAXException ex) {
                throw new IOException(ex);
            }
        } catch (IOException ex) {
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
            }
            callback.connectionClosed();
        }
    }
}