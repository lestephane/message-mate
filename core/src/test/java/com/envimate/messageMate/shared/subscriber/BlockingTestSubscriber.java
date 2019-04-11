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

package com.envimate.messageMate.shared.subscriber;

import com.envimate.messageMate.subscribing.AcceptingBehavior;
import com.envimate.messageMate.subscribing.SubscriptionId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import static com.envimate.messageMate.subscribing.AcceptingBehavior.MESSAGE_ACCEPTED;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockingTestSubscriber<T> implements TestSubscriber<T> {
    private final SubscriptionId subscriptionId = SubscriptionId.newUniqueId();
    private final Semaphore semaphoreToWaitUntilExecutionIsDone;
    private final List<T> receivedMessages = new CopyOnWriteArrayList<>();

    public static <T> BlockingTestSubscriber<T> blockingTestSubscriber(final Semaphore semaphore) {
        return new BlockingTestSubscriber<>(semaphore);
    }

    @Override
    public AcceptingBehavior accept(final T message) {
        try {
            semaphoreToWaitUntilExecutionIsDone.acquire();
            receivedMessages.add(message);
        } catch (final InterruptedException ignored) {
            receivedMessages.add(message);
        }
        return MESSAGE_ACCEPTED;
    }

    @Override
    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public List<T> getReceivedMessages() {
        return receivedMessages;
    }
}
