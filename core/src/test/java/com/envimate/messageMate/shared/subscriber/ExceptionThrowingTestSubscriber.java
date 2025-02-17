/*
 * Copyright (c) 2019 envimate GmbH - https://envimate.com/.
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

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionThrowingTestSubscriber<T> implements TestSubscriber<T> {
    private final SubscriptionId subscriptionId = SubscriptionId.newUniqueId();

    public static <T> ExceptionThrowingTestSubscriber<T> exceptionThrowingTestSubscriber() {
        return new ExceptionThrowingTestSubscriber<>();
    }

    @Override
    public AcceptingBehavior accept(final T message) {
        throw new TestException();
    }

    @Override
    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public List<T> getReceivedMessages() {
        return Collections.emptyList();
    }

}
