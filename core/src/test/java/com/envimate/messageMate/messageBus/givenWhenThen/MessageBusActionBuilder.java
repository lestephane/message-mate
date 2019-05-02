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

package com.envimate.messageMate.messageBus.givenWhenThen;

import com.envimate.messageMate.channel.Channel;
import com.envimate.messageMate.filtering.Filter;
import com.envimate.messageMate.messageBus.MessageBus;
import com.envimate.messageMate.messageBus.MessageBusStatusInformation;
import com.envimate.messageMate.messageBus.exception.MessageBusExceptionListener;
import com.envimate.messageMate.processingContext.EventType;
import com.envimate.messageMate.processingContext.ProcessingContext;
import com.envimate.messageMate.shared.environment.TestEnvironment;
import com.envimate.messageMate.shared.givenWhenThen.TestAction;
import com.envimate.messageMate.shared.pipeChannelMessageBus.testActions.MessageBusSutActions;
import com.envimate.messageMate.shared.pipeMessageBus.givenWhenThen.PipeMessageBusSutActions;
import com.envimate.messageMate.shared.subscriber.BlockingTestSubscriber;
import com.envimate.messageMate.shared.subscriber.TestException;
import com.envimate.messageMate.shared.testMessages.TestMessage;
import com.envimate.messageMate.shared.utils.ShutdownTestUtils;
import com.envimate.messageMate.subscribing.Subscriber;
import com.envimate.messageMate.subscribing.SubscriptionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestActions.sendASingleMessage;
import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestActions.sendSeveralMessages;
import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestActions.*;
import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestActionsOld.messageBusTestActions;
import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestProperties.CORRELATION_SUBSCRIPTION_ID;
import static com.envimate.messageMate.messageBus.givenWhenThen.MessageBusTestProperties.EVENT_TYPE;
import static com.envimate.messageMate.shared.environment.TestEnvironmentProperty.RESULT;
import static com.envimate.messageMate.shared.eventType.TestEventType.testEventType;
import static com.envimate.messageMate.shared.pipeMessageBus.givenWhenThen.PipeChannelMessageBusSharedTestProperties.*;
import static com.envimate.messageMate.shared.pipeMessageBus.givenWhenThen.PipeMessageBusTestActions.*;
import static com.envimate.messageMate.shared.pipeMessageBus.givenWhenThen.TestFilter.anErrorThrowingFilter;
import static com.envimate.messageMate.shared.polling.PollingUtils.pollUntilEquals;
import static com.envimate.messageMate.shared.subscriber.BlockingTestSubscriber.blockingTestSubscriber;
import static com.envimate.messageMate.shared.subscriber.ExceptionThrowingTestSubscriber.exceptionThrowingTestSubscriber;
import static com.envimate.messageMate.shared.utils.AsynchronousSendingTestUtils.addABlockingSubscriberAndThenSendXMessagesInEachThread;
import static com.envimate.messageMate.shared.utils.ShutdownTestUtils.sendMessagesBeforeShutdownAsynchronously;

public final class MessageBusActionBuilder {
    private List<TestAction<MessageBus>> actions = new ArrayList<>();

    private MessageBusActionBuilder(final TestAction<MessageBus> action) {
        this.actions.add(action);
    }

    public static MessageBusActionBuilder aSingleMessageIsSend() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendASingleMessage(messageBus, testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder aMessageWithoutPayloadIsSend() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendTheMessageAsProcessingContext(messageBus, testEnvironment, null);
            return null;
        });
    }

    public static MessageBusActionBuilder aMessageWithoutEventType() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendTheMessageAsProcessingContext(messageBus, testEnvironment, null, null);
            return null;
        });
    }

    public static MessageBusActionBuilder theMessageIsSend(final TestMessage message) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendTheMessage(messageBus, testEnvironment, message);
            return null;
        });
    }

    public static MessageBusActionBuilder aMessageWithCorrelationIdIsSend() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendAMessageWithCorrelationId(messageBus, testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder severalMessagesAreSend(final int numberOfMessages) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendSeveralMessages(messageBus, testEnvironment, numberOfMessages);
            return null;
        });
    }

    public static MessageBusActionBuilder severalMessagesAreSendAsynchronously(final int numberOfSender,
                                                                               final int numberOfMessagesPerSender) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendMessagesAsynchronously(messageBus, testEnvironment, numberOfSender,
                    numberOfMessagesPerSender, true);
            return null;
        });
    }

    public static MessageBusActionBuilder severalMessagesAreSendAsynchronouslyButWillBeBlocked(final int numberOfMessages,
                                                                                               final int expectedBlockedThreads) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusSutActions sutActions = MessageBusSutActions.messageBusSutActions(messageBus);
            final Semaphore semaphore = new Semaphore(0);
            final BlockingTestSubscriber<TestMessage> subscriber = blockingTestSubscriber(semaphore);
            addABlockingSubscriberAndThenSendXMessagesInEachThread(sutActions, subscriber, numberOfMessages, 1,
                    testEnvironment, expectedBlockedThreads);
            return null;
        });
    }

    public static MessageBusActionBuilder severalMessagesAreSendAsynchronouslyButWillBeBlocked(final int numberOfMessages) {
        return severalMessagesAreSendAsynchronouslyButWillBeBlocked(numberOfMessages, numberOfMessages);
    }

    public static MessageBusActionBuilder sendSeveralMessagesBeforeTheBusIsShutdown(final int numberOfSender,
                                                                                    final boolean finishRemainingTasks,
                                                                                    final int expectedNumberOfBlockedThreads) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusSutActions sutActions = MessageBusSutActions.messageBusSutActions(messageBus);
            sendMessagesBeforeShutdownAsynchronously(sutActions, testEnvironment, numberOfSender, finishRemainingTasks, expectedNumberOfBlockedThreads);
            return null;
        });
    }
    public static MessageBusActionBuilder sendSeveralMessagesBeforeTheBusIsShutdown(final int numberOfSender,
                                                                                    final boolean finishRemainingTasks) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusSutActions sutActions = MessageBusSutActions.messageBusSutActions(messageBus);
            sendMessagesBeforeShutdownAsynchronously(sutActions, testEnvironment, numberOfSender, finishRemainingTasks);
            return null;
        });
    }

    public static MessageBusActionBuilder aSingleMessageWithErrorPayloadIsSend() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendMessageWithErrorPayloadIsSend(messageBus, testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder aSubscriberIsAdded(final EventType eventType) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            addASingleSubscriber(messageBus, testEnvironment, eventType);
            return null;
        });
    }

    public static MessageBusActionBuilder oneSubscriberUnsubscribesSeveralTimes(final int numberOfUnsubscriptions) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            unsubscribeASubscriberXTimes(sutActions, testEnvironment, numberOfUnsubscriptions);
            return null;
        });
    }

    public static MessageBusActionBuilder oneSubscriberUnsubscribes() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            unsubscribeASubscriberXTimes(sutActions, testEnvironment, 1);
            return null;
        });
    }

    public static MessageBusActionBuilder theSubscriberForTheCorrelationIdUnsubscribes() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final SubscriptionId subscriptionId = getUsedSubscriptionId(testEnvironment);
            messageBus.unsubcribe(subscriptionId);
            return null;
        });
    }

    private static SubscriptionId getUsedSubscriptionId(final TestEnvironment testEnvironment) {
        return testEnvironment.getPropertyAsType(CORRELATION_SUBSCRIPTION_ID, SubscriptionId.class);
    }

    public static MessageBusActionBuilder halfValidAndInvalidMessagesAreSendAsynchronously(final int numberOfSender,
                                                                                           final int numberOfMessagesPerSender) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendInvalidAndInvalidMessagesAsynchronously(messageBus, testEnvironment, numberOfSender, numberOfMessagesPerSender);
            return null;
        });
    }

    public static MessageBusActionBuilder severalInvalidMessagesAreSendAsynchronously(final int numberOfSender,
                                                                                      final int numberOfMessagesPerSender) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendOnlyInvalidMessagesAsynchronously(messageBus, testEnvironment, numberOfSender, numberOfMessagesPerSender);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfAcceptedMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusTestActionsOld testActions = messageBusTestActions(messageBus);
            pollUntilEquals(testActions::getTheNumberOfAcceptedMessages, testEnvironment.getProperty(NUMBER_OF_MESSAGES_SHOULD_BE_SEND));
            messageBusTestActions(messageBus).queryTheNumberOfAcceptedMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfQueuedMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            messageBusTestActions(messageBus).queryTheNumberOfQueuedMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfSuccessfulMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusTestActionsOld testActions = messageBusTestActions(messageBus);
            pollUntilEquals(testActions::getTheNumberOfSuccessfulDeliveredMessages, testEnvironment.getProperty(NUMBER_OF_MESSAGES_SHOULD_BE_SEND));
            messageBusTestActions(messageBus)
                    .queryTheNumberOfSuccessfulDeliveredMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfFailedMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final Object expectedNumberOfMessages = testEnvironment.getProperty(NUMBER_OF_MESSAGES_SHOULD_BE_SEND);
            pollUntilEquals(() -> testEnvironment.getPropertyAsType(MESSAGES_SEND, List.class).size(), expectedNumberOfMessages);
            messageBusTestActions(messageBus)
                    .queryTheNumberOfFailedDeliveredMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfBlockedMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusTestActionsOld testActions = messageBusTestActions(messageBus);
            pollUntilEquals(testActions::getTheNumberOfBlockedMessages, testEnvironment.getProperty(NUMBER_OF_MESSAGES_SHOULD_BE_SEND));
            messageBusTestActions(messageBus)
                    .queryTheNumberOfBlockedMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theNumberOfForgottenMessagesIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusTestActionsOld testActions = messageBusTestActions(messageBus);
            pollUntilEquals(testActions::getTheNumberOfForgottenMessages, testEnvironment.getProperty(NUMBER_OF_MESSAGES_SHOULD_BE_SEND));
            messageBusTestActions(messageBus)
                    .queryTheNumberOfForgottenMessages(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder theTimestampOfTheStatisticsIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            messageBusTestActions(messageBus)
                    .queryTheTimestampOfTheMessageStatistics(testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder aShortWaitIsDone(final long timeout, final TimeUnit timeUnit) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            performAShortWait(timeout, timeUnit);
            return null;
        });
    }

    public static MessageBusActionBuilder theSubscriberAreQueriedPerType() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusStatusInformation statusInformation = messageBus.getStatusInformation();
            final Map<EventType, List<Subscriber<?>>> subscribersPerType = statusInformation.getSubscribersPerType();
            testEnvironment.setProperty(RESULT, subscribersPerType);
            return null;
        });
    }

    public static MessageBusActionBuilder allSubscribersAreQueriedAsList() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final List<Subscriber<?>> allSubscribers = messageBus.getStatusInformation().getAllSubscribers();
            testEnvironment.setProperty(RESULT, allSubscribers);
            return null;
        });
    }

    public static MessageBusActionBuilder theChannelForTheTypeIsQueried(final EventType eventType) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final Channel<Object> channel = messageBus.getStatusInformation()
                    .getChannelFor(eventType);
            testEnvironment.setProperty(RESULT, channel);
            return null;
        });
    }

    public static MessageBusActionBuilder severalMessagesAreSendAsynchronouslyBeforeTheMessageBusIsShutdown(final int numberOfMessages) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            sendMessagesBeforeAShutdownAsynchronously(messageBus, testEnvironment, numberOfMessages);
            return null;
        });
    }

    public static MessageBusActionBuilder theMessageBusIsShutdownAsynchronouslyXTimes(final int numberOfThreads) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusSutActions sutActions = MessageBusSutActions.messageBusSutActions(messageBus);
            ShutdownTestUtils.shutdownTheSutAsynchronouslyXTimes(sutActions, numberOfThreads);
            return null;
        });
    }

    public static MessageBusActionBuilder theMessageBusIsShutdown() {
        return theMessageBusIsShutdown(true);
    }

    public static MessageBusActionBuilder theMessageBusIsShutdown(final boolean finishRemainingTasks) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            shutdownTheSut(sutActions, finishRemainingTasks);
            return null;
        });
    }

    public static MessageBusActionBuilder theMessageBusShutdownIsExpectedForTimeoutInSeconds(final int timeoutInSeconds) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            awaitTheShutdownTimeoutInSeconds(sutActions, testEnvironment, timeoutInSeconds);
            return null;
        });
    }

    public static MessageBusActionBuilder theListOfFiltersIsQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            queryTheListOfFilters(sutActions, testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder aFilterIsRemoved() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final PipeMessageBusSutActions sutActions = messageBusTestActions(messageBus);
            removeAFilter(sutActions, testEnvironment);
            return null;
        });
    }

    public static MessageBusActionBuilder anExceptionThrowingFilterIsAddedInChannelOf(final EventType eventType) {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final MessageBusStatusInformation statusInformation = messageBus.getStatusInformation();
            final Channel<Object> channel = statusInformation.getChannelFor(eventType);
            final RuntimeException exception = new TestException();
            final Filter<ProcessingContext<Object>> filter = anErrorThrowingFilter(exception);
            channel.addProcessFilter(filter);
            return null;
        });
    }

    public static MessageBusActionBuilder anExceptionThrowingSubscriberIsAdded() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final EventType eventType = testEnvironment.getPropertyOrSetDefault(EVENT_TYPE, testEventType());
            final Subscriber<Object> subscriber = exceptionThrowingTestSubscriber();
            addASingleSubscriber(messageBus, testEnvironment, eventType, subscriber);
            return null;
        });
    }

    public static MessageBusActionBuilder theDynamicExceptionHandlerToBeRemoved() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final SubscriptionId subscriptionId = testEnvironment.getPropertyAsType(USED_SUBSCRIPTION_ID, SubscriptionId.class);
            messageBus.unregisterExceptionListener(subscriptionId);
            return null;
        });
    }

    public static MessageBusActionBuilder allDynamicExceptionListenerAreQueried() {
        return new MessageBusActionBuilder((messageBus, testEnvironment) -> {
            final List<MessageBusExceptionListener> listeners = queryListOfDynamicExceptionListener(messageBus);
            testEnvironment.setPropertyIfNotSet(RESULT, listeners);
            return null;
        });
    }

    public MessageBusActionBuilder andThen(final MessageBusActionBuilder followUpBuilder) {
        actions.addAll(followUpBuilder.actions);
        return this;
    }

    public List<TestAction<MessageBus>> build() {
        return actions;
    }
}
