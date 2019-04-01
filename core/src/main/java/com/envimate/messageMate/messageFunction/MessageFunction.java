/*
 * Copyright (c) 2018 envimate GmbH - https://envimate.com/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.envimate.messageMate.messageFunction;

import com.envimate.messageMate.internal.autoclosable.NoErrorAutoClosable;
import com.envimate.messageMate.messageBus.EventType;

/**
 * {@code MessageFunctions} simplify the execution of request-reply based communications over an asynchronous {@code MessageBus}.
 *
 * @see <a href="https://github.com/envimate/message-mate#message-function">Message Mate Documentation</a>
 */
public interface MessageFunction extends NoErrorAutoClosable {

    /**
     * Sends the given request over the {@code MessageBus}.
     *
     * <p>The returned {@code ResponseFuture} fulfills, when a response is received or an exception during the transport of
     * request or the reply is thrown.</p>
     *
     * @param request the request to send
     * @return a {@code ResponseFuture} that can be queried for the result
     */
    ResponseFuture request(EventType eventType, Object request);

    /**
     * Closes the {@code MessageFunction}. This does not cancel any pending {@code ResponseFutures}.
     */
    @Override
    void close();
}
