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

package com.envimate.messageMate.useCases.givenWhenThen;

import com.envimate.messageMate.processingContext.EventType;
import com.envimate.messageMate.shared.givenWhenThen.TestAction;
import com.envimate.messageMate.shared.environment.TestEnvironment;
import com.envimate.messageMate.useCases.shared.TestUseCase;
import lombok.RequiredArgsConstructor;

import static com.envimate.messageMate.shared.environment.TestEnvironmentProperty.EXCEPTION;
import static com.envimate.messageMate.shared.properties.SharedTestProperties.EVENT_TYPE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class Then {
    private final UseCaseInvocationSetupBuilder setupBuilder;
    private final UseCaseInvocationActionBuilder actionBuilder;

    public void then(final UseCaseInvocationValidationBuilder validationBuilder) {
        final UseCaseInvocationSetup setup = setupBuilder.build();
        final TestEnvironment testEnvironment = setup.getTestEnvironment();
        final TestAction<TestUseCase> testAction = actionBuilder.build();
        final TestUseCase testUseCase = setup.getTestUseCase();
        final EventType eventType = testUseCase.getEventType();
        testEnvironment.setPropertyIfNotSet(EVENT_TYPE, eventType);
        try {
            testAction.execute(testUseCase, testEnvironment);
        } catch (final Exception e) {
            testEnvironment.setProperty(EXCEPTION, e);
        }
        final UseCaseInvocationValidationBuilder.UseCaseAdapterTestValidation validation = validationBuilder.build();
        validation.validate(testUseCase, testEnvironment);
    }
}
