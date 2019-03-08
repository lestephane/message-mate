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

package com.envimate.messageMate.messageBus;

import com.envimate.messageMate.channel.Channel;
import com.envimate.messageMate.messageBus.statistics.MessageBusStatistics;
import com.envimate.messageMate.subscribing.Subscriber;

import java.util.List;
import java.util.Map;

/**
 * Gives access to the {@code MessageBus'} statistics and all of its currently registered {@code Subscribers}.
 *
 * @see <a href="https://github.com/envimate/message-mate#messagebus-statistics">Message Mate Documentation</a>
 * @see <a href="https://github.com/envimate/message-mate#querying-subscriber">Message Mate Documentation</a>
 */
public interface MessageBusStatusInformation {

    /**
     * Collects the message statistics for this point in time.
     *
     * @return {@code MessageBusStatistics} with a timestamp
     */
    MessageBusStatistics getCurrentMessageStatistics();

    /**
     * Returns the list of all {@code Subscribers}.
     *
     * @return list of all {@code Subscribers}
     */
    List<Subscriber<?>> getAllSubscribers();

    /**
     * Returns all {@code Subscribers} grouped by their subscribed classes.
     *
     * @return map of classes and their {@code Subscribers}
     */
    Map<Class<?>, List<Subscriber<?>>> getSubscribersPerType();

    /**
     * Returns the class-specific {@code Channel} for the given class or {@code null} if the class has not yet been
     * sent or subscribed.
     *
     * @param messageClass the class of interest
     * @param <T>          the type of the class
     * @return the {@code Channel} of the class or {@code null}
     */
    <T> Channel<T> getChannelFor(Class<T> messageClass);
}
