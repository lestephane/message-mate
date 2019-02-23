package com.envimate.messageMate.messageBus.givenWhenThen;

import com.envimate.messageMate.filtering.Filter;
import com.envimate.messageMate.messageBus.MessageBus;
import com.envimate.messageMate.messageBus.MessageBusStatusInformation;
import com.envimate.messageMate.messageBus.statistics.MessageBusStatistics;
import com.envimate.messageMate.pipe.statistics.PipeStatistics;
import com.envimate.messageMate.qcec.shared.TestEnvironment;
import com.envimate.messageMate.shared.pipeMessageBus.givenWhenThen.PipeMessageBusSutActions;
import com.envimate.messageMate.shared.testMessages.TestMessage;
import com.envimate.messageMate.subscribing.Subscriber;
import com.envimate.messageMate.subscribing.SubscriptionId;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.envimate.messageMate.qcec.shared.TestEnvironmentProperty.RESULT;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class MessageBusTestActions implements PipeMessageBusSutActions {
    private final MessageBus messageBus;

    public static MessageBusTestActions messageBusTestActions(final MessageBus messageBus) {
        return new MessageBusTestActions(messageBus);
    }

    @Override
    public boolean isShutdown(final TestEnvironment testEnvironment) {
        return messageBus.isShutdown();
    }

    @Override
    public List<?> getFilter(final TestEnvironment testEnvironment) {
        return messageBus.getFilter();
    }

    @Override
    public <R> void subscribe(final Class<R> messageClass, final Subscriber<R> subscriber) {
        messageBus.subscribe(messageClass, subscriber);
    }

    @Override
    public void close(final boolean finishRemainingTasks) {
        messageBus.close(finishRemainingTasks);
    }

    @Override
    public boolean awaitTermination(final int timeout, final TimeUnit timeUnit) throws InterruptedException {
        return messageBus.awaitTermination(timeout, timeUnit);
    }

    @Override
    public List<?> getFilter() {
        return messageBus.getFilter();
    }

    @Override
    public void unsubscribe(final SubscriptionId subscriptionId) {
        messageBus.unsubcribe(subscriptionId);
    }

    @Override
    public void send(final TestMessage message) {
        messageBus.send(message);
    }

    @Override
    public PipeStatistics getMessageStatistics() {
        throw new UnsupportedOperationException();
    }

    public MessageBusStatistics getMessageStatistics_real() {
        final MessageBusStatusInformation statusInformation = messageBus.getStatusInformation();
        return statusInformation.getCurrentMessageStatistics();
    }

    public void queryTheNumberOfAcceptedMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getAcceptedMessages);
    }

    public void queryTheNumberOfAcceptedMessagesAsynchronously(final TestEnvironment testEnvironment) {
        final Semaphore semaphore = new Semaphore(0);
        new Thread(() -> {
            queryMessageStatistics(testEnvironment, MessageBusStatistics::getAcceptedMessages);
            semaphore.release();
        }).start();
        try {
            semaphore.acquire();
        } catch (final InterruptedException e) {
            //not necessary to do anything here
        }
    }

    public void queryTheNumberOfQueuedMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getQueuedMessages);
    }

    public void queryTheNumberOfSuccessfulDeliveredMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getSuccessfulMessages);
    }

    public void queryTheNumberOfFailedDeliveredMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getFailedMessages);
    }

    public void queryTheNumberOfBlockedMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getBlockedMessages);
    }

    public void queryTheNumberOfReplacedMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getReplacedMessages);
    }

    public void queryTheNumberOfForgottenMessages(final TestEnvironment testEnvironment) {
        queryMessageStatistics(testEnvironment, MessageBusStatistics::getForgottenMessages);
    }

    public void queryTheTimestampOfTheMessageStatistics(final TestEnvironment testEnvironment) {
        final MessageBusStatistics messageBusStatistics = getMessageStatistics_real();
        final Date timestamp = messageBusStatistics.getTimestamp();
        testEnvironment.setProperty(RESULT, timestamp);
    }

    private void queryMessageStatistics(final TestEnvironment testEnvironment,
                                        final MessageBusStatisticsQuery query) {
        final MessageBusStatistics messageBusStatistics = getMessageStatistics_real();
        final BigInteger statistic = query.query(messageBusStatistics);
        final long longValueExact = statistic.longValueExact();
        testEnvironment.setProperty(RESULT, longValueExact);
    }

    @Override
    public Object removeAFilter() {
        final List<Filter<Object>> filters = messageBus.getFilter();
        final int indexToRemove = (int) (Math.random() * filters.size());
        final Filter<Object> filter = filters.get(indexToRemove);
        messageBus.remove(filter);
        return filter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addFilter(final Filter<?> filter) {
        final Filter<Object> objectFilter = (Filter<Object>) filter;
        messageBus.add(objectFilter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addFilter(final Filter<?> filter, final int position) {
        final Filter<Object> objectFilter = (Filter<Object>) filter;
        messageBus.add(objectFilter, position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Subscriber<?>> getAllSubscribers() {
        final MessageBusStatusInformation statusInformation = messageBus.getStatusInformation();
        final List<Subscriber<?>> allSubscribers = statusInformation.getAllSubscribers();
        return allSubscribers;
    }

    private interface MessageBusStatisticsQuery {
        BigInteger query(MessageBusStatistics messageBusStatistics);
    }
}
