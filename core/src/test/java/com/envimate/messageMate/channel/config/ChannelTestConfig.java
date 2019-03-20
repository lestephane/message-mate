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

package com.envimate.messageMate.channel.config;

import com.envimate.messageMate.channel.ChannelType;
import com.envimate.messageMate.internal.pipe.configuration.AsynchronousConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.envimate.messageMate.channel.ChannelType.ASYNCHRONOUS;
import static com.envimate.messageMate.channel.ChannelType.SYNCHRONOUS;
import static com.envimate.messageMate.internal.pipe.configuration.AsynchronousConfiguration.constantPoolSizeAsynchronousPipeConfiguration;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ChannelTestConfig {
    public static final int ASYNCHRONOUS_CHANNEL_CONFIG_POOL_SIZE = 5;
    @Getter
    private final ChannelType type;
    @Getter
    private final AsynchronousConfiguration asynchronousConfiguration;
    @Getter
    private final long millisecondsSleepBetweenExecutionActionSteps;
    @Getter
    private final long millisecondsSleepAfterExecution;


    public static ChannelTestConfig synchronousChannelTestConfig() {
        return new ChannelTestConfig(SYNCHRONOUS, null, 0, 0);
    }

    public static ChannelTestConfig asynchronousChannelTestConfig() {
        final AsynchronousConfiguration asynchronousConfiguration = constantPoolSizeAsynchronousPipeConfiguration(ASYNCHRONOUS_CHANNEL_CONFIG_POOL_SIZE);
        return new ChannelTestConfig(ASYNCHRONOUS, asynchronousConfiguration, 5, 20);
    }
}
