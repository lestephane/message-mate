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

package com.envimate.messageMate.mapping;

import com.envimate.messageMate.internal.collections.predicatemap.PredicateMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.envimate.messageMate.internal.enforcing.NotNullEnforcer.ensureNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Serializer {

    private final PredicateMap<Object, Mapifier<Object>> mapifiers;

    public static Serializer responseSerializer(final PredicateMap<Object, Mapifier<Object>> mapifierPredicateMap) {
        ensureNotNull(mapifierPredicateMap, "mapifiers");
        return new Serializer(mapifierPredicateMap);
    }

    public Map<String, Object> serialize(final Object value) {
        final Mapifier<Object> mapifier = mapifiers.get(value);
        return mapifier.map(value);
    }
}
