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

package org.eclipse.microprofile.reactive.messaging.tck.client;

import org.eclipse.microprofile.reactive.messaging.tck.container.Topics;
import org.eclipse.microprofile.reactive.messaging.tck.client.event.DeployTopics;
import org.eclipse.microprofile.reactive.messaging.tck.client.event.TopicEvent;
import org.eclipse.microprofile.reactive.messaging.tck.client.event.UnDeployTopics;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.event.container.AfterUnDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.container.test.impl.client.deployment.event.GenerateDeployment;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TckDeployListener {

    @Inject
    @ClassScoped
    private InstanceProducer<TopicDeploymentScenario> topicDeploymentScenario;

    @Inject
    private Event<TopicEvent> topicEvent;

    public void onGenerateDeployment(@Observes GenerateDeployment event) throws DeploymentException {

        List<String> topicNames = getTopicsUsedByTestClass(event.getTestClass());

        TopicDeploymentScenario scenario = new TopicDeploymentScenario(topicNames);

        topicDeploymentScenario.set(scenario);
    }

    public void onBeforeDeploy(@Observes BeforeDeploy event) {
        TopicDeploymentScenario scenario = topicDeploymentScenario.get();
        if (!scenario.isDeployed()) {
            try {
                topicEvent.fire(new DeployTopics(event.getDeployableContainer(), scenario.topics()));
            }
            finally {
                scenario.deploy();
            }
        }
    }

    public void onAfterUnDeploy(@Observes AfterUnDeploy event) throws DeploymentException {
        TopicDeploymentScenario scenario = topicDeploymentScenario.get();

        if (scenario.isDeployed()) {
            try {
                topicEvent.fire(new UnDeployTopics(event.getDeployableContainer(), scenario.topics()));
            }
            finally {
                scenario.undeploy();
            }
        }
    }

    private List<String> getTopicsUsedByTestClass(TestClass testClass) throws DeploymentException {
        Method[] topicsMethods = testClass.getMethods(Topics.class);
        List<String> topicNames = new ArrayList<>();
        for (Method method : topicsMethods) {
            Object topics;
            try {
                topics = method.invoke(null);
            }
            catch (Exception e) {
                throw new DeploymentException("Error reading topics from " + testClass.getName(), e);
            }
            if (topics instanceof Collection) {
                topicNames.addAll((Collection) topics);
            }
            else if (topics instanceof String[]) {
                topicNames.addAll(Arrays.asList((String[]) topics));
            }
            else if (topics instanceof String) {
                topicNames.add((String) topics);
            }
            else {
                throw new DeploymentException("Object of invalid type returned from @Topic annotated " + method + ", object was " + topics);
            }
        }
        return topicNames;
    }

}
