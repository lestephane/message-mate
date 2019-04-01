package com.envimate.messageMate.useCaseAdapter.building;
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

import com.envimate.messageMate.useCaseAdapter.methodInvoking.UseCaseMethodInvoker;
import com.envimate.messageMate.useCaseAdapter.usecaseInvoking.Caller;
import com.envimate.messageMate.useCaseAdapter.usecaseInvoking.UseCaseInvocationInformation;
import com.envimate.messageMate.useCaseAdapter.usecaseInvoking.UseCaseInvoker;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.envimate.messageMate.useCaseAdapter.usecaseInvoking.ClassBasedUseCaseInvokerImpl.classBasedUseCaseInvoker;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public interface UseCaseAdapterCallingBuilder<USECASE> {

    default UseCaseAdapterStep1Builder calling(final BiFunction<USECASE, Object, Object> caller) {
        return callingBy((useCase, event, parameterValueMappings) -> {
            final Object returnValue = caller.apply(useCase, event);
            return ofNullable(returnValue);
        });
    }

    default UseCaseAdapterStep1Builder callingVoid(final BiConsumer<USECASE, Object> caller) {
        return callingBy((usecase, event, parameterValueMappings) -> {
            caller.accept(usecase, event);
            return empty();
        });
    }

    default UseCaseAdapterStep1Builder callingTheSingleUseCaseMethod() {
        return callingBy((usecase, event, parameterValueMappings) -> {
            final UseCaseInvoker invoker = classBasedUseCaseInvoker(usecase.getClass());
            final UseCaseInvocationInformation invocationInformation = invoker.getInvocationInformation();
            final UseCaseMethodInvoker methodInvoker = invocationInformation.getMethodInvoker();
            final Object returnValue = methodInvoker.invoke(usecase, event, parameterValueMappings); // TODO
            return ofNullable(returnValue);
        });
    }

    UseCaseAdapterStep1Builder callingBy(Caller<USECASE, Object> caller);
}
