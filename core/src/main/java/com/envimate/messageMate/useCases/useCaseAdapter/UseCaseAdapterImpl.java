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

package com.envimate.messageMate.useCases.useCaseAdapter;

import com.envimate.messageMate.mapping.Deserializer;
import com.envimate.messageMate.mapping.ExceptionSerializer;
import com.envimate.messageMate.mapping.Serializer;
import com.envimate.messageMate.messageBus.MessageBus;
import com.envimate.messageMate.serializedMessageBus.SerializedMessageBus;
import com.envimate.messageMate.useCases.useCaseAdapter.parameterInjecting.ParameterInjector;
import com.envimate.messageMate.useCases.useCaseAdapter.usecaseInstantiating.UseCaseInstantiator;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static com.envimate.messageMate.internal.enforcing.NotNullEnforcer.ensureNotNull;
import static com.envimate.messageMate.serializedMessageBus.SerializedMessageBus.aSerializedMessageBus;
import static com.envimate.messageMate.useCases.useCaseAdapter.UseCaseRequestExecutingSubscriber.useCaseRequestExecutingSubscriber;
import static lombok.AccessLevel.PRIVATE;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
final class UseCaseAdapterImpl implements UseCaseAdapter {
    private final List<UseCaseCallingInformation<?>> useCaseCallingInformations;
    private final UseCaseInstantiator useCaseInstantiator;
    private final Deserializer requestDeserializer;
    private final Serializer responseSerializer;
    private final ExceptionSerializer exceptionSerializer;
    private final ParameterInjector parameterInjector;

    static UseCaseAdapter useCaseAdapterImpl(final List<UseCaseCallingInformation<?>> useCaseCallingInformations,
                                             final UseCaseInstantiator useCaseInstantiator,
                                             final Deserializer requestDeserializer,
                                             final Serializer responseSerializer,
                                             final ExceptionSerializer exceptionSerializer,
                                             final ParameterInjector parameterInjector) {
        ensureNotNull(useCaseCallingInformations, "useCaseCallingInformations");
        ensureNotNull(useCaseInstantiator, "useCaseInstantiator");
        ensureNotNull(requestDeserializer, "requestDeserializer");
        ensureNotNull(responseSerializer, "responseSerializer");
        return new UseCaseAdapterImpl(useCaseCallingInformations, useCaseInstantiator, requestDeserializer,
                responseSerializer, exceptionSerializer, parameterInjector);
    }

    @Override
    public SerializedMessageBus attachAndEnhance(final MessageBus messageBus) {
        final SerializedMessageBus serializedMessageBus = aSerializedMessageBus(messageBus, requestDeserializer,
                responseSerializer);
        attachTo(serializedMessageBus);
        return serializedMessageBus;
    }

    @Override
    public void attachTo(final SerializedMessageBus serializedMessageBus) {
        useCaseCallingInformations.forEach(callingInformation -> {
            final UseCaseRequestExecutingSubscriber useCaseRequestSubscriber = useCaseRequestExecutingSubscriber(
                    callingInformation, useCaseInstantiator, requestDeserializer, responseSerializer, exceptionSerializer,
                    parameterInjector);
            useCaseRequestSubscriber.attachTo(serializedMessageBus);
        });
    }
}
