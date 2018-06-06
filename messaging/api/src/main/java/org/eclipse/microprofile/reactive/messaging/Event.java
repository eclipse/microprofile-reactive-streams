/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.eclipse.microprofile.reactive.messaging;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * An abstract event envelope, modeled after the
 * <a href="https://github.com/cloudevents/spec/blob/master/spec.md">CNCF CloudEvent spec</a>.
 *
 */
public interface Event<T> extends Message<T> {
    /**
     * Type of occurrence which has happened. Often this property is used for routing, observability, policy enforcement, etc.
     */
    String getEventType();

    /**
     * The version of the CloudEvents specification which the event uses. This enables the interpretation of the context.
     */
    String getCloudEventsVersion();

    /**
     * This describes the event producer. Often this will include information such as the type of the event source,
     * the organization publishing the event, and some unique identifiers. The exact syntax and semantics behind the data
     * encoded in the URI is event producer defined.
     */
    URI getSource();

    /**
     * ID of the event. The semantics of this string are explicitly undefined to ease the implementation of producers.
     * Enables deduplication.
     */
    String getEventID();

    /**
     * The version of the eventType. This enables the interpretation of data by eventual consumers, requires the
     * consumer to be knowledgeable about the producer.
     */
    Optional<String> getEventTypeVersion();
    /**
     * Timestamp of when the event happened.
     */
    Optional<ZonedDateTime> getEventTime();

    /**
     * A link to the schema that the data attribute adheres to.
     */
    Optional<URI> getSchemaURL();

    /**
     * Describe the data encoding format
     */
    Optional<String> getContentType();

    /**
     * This is for additional metadata and this does not have a mandated structure. This enables a place for custom
     * fields a producer or middleware might want to include and provides a place to test metadata before
     * adding them to the CloudEvents specification.
     */
    Optional<Map<?, ?>> getExtensions();
}
