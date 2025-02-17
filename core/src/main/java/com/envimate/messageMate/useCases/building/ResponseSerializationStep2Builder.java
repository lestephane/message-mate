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

package com.envimate.messageMate.useCases.building;

import com.envimate.messageMate.mapping.Mapifier;

import java.util.Map;

/**
 * Based on the conditions defined in the previous {@link ResponseSerializationStep1Builder}, this step defines how to
 * map the matching from object into a {@link Map}.
 *
 * @param <T> the type to serialize into a {@code Map}
 */
public interface ResponseSerializationStep2Builder<T> {

    /**
     * Uses the given {@code Mapifier}, when the previous condition triggers.
     *
     * @param mapifier the {@code Mapifier} to use
     * @return the next step in the fluent builder interface
     */
    ResponseSerializationStep1Builder using(Mapifier<T> mapifier);
}
