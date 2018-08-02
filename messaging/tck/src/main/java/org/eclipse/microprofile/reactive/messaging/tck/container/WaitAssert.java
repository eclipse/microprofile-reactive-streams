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

package org.eclipse.microprofile.reactive.messaging.tck.container;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WaitAssert {

    private WaitAssert() {
    }

    public static void waitUntil(Duration timeout, Runnable block) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (true) {
            try {
                block.run();
                break;
            }
            catch (Throwable t) {
                if (System.currentTimeMillis() >= deadline) {
                    throw new AssertionError("Action was not completed in " + timeout.toMillis() + "ms", t);
                }
            }

        }
    }

    public static <T> T await(CompletionStage<T> future, TestEnvironment environment) {
        try {
            return future.toCompletableFuture().get(environment.receiveTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e) {
            throw new AssertionError("Action was not completed in " + environment.receiveTimeout().toMillis() + "ms", e);
        }
        catch (Exception e) {
            throw new AssertionError("Action failed", e);
        }
    }
}
