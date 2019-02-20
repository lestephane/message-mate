package com.envimate.messageMate.pipe;

import com.envimate.messageMate.pipe.config.AsynchronousPipeConfigurationProvider;
import com.envimate.messageMate.pipe.config.PipeTestConfig;
import com.envimate.messageMate.pipe.transport.PipeWaitingQueueIsFullException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.envimate.messageMate.pipe.config.PipeTestConfig.anAsynchronousBoundedPipe;
import static com.envimate.messageMate.pipe.givenWhenThen.Given.given;
import static com.envimate.messageMate.pipe.givenWhenThen.PipeActionBuilder.*;
import static com.envimate.messageMate.pipe.givenWhenThen.PipeSetupBuilder.aConfiguredPipe;
import static com.envimate.messageMate.pipe.givenWhenThen.PipeValidationBuilder.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ExtendWith(AsynchronousPipeConfigurationProvider.class)
public class AsynchronousPipeSpecs implements PipeSpecs {

    //send

    @Test
    public void testPipe_doesNotFailForFullWaitingQueue() throws Exception {
        final int completeCapacity = PipeTestConfig.ASYNCHRONOUS_QUEUED_BOUND + PipeTestConfig.ASYNCHRONOUS_POOL_SIZE;
        given(aConfiguredPipe(anAsynchronousBoundedPipe())
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSend(completeCapacity))
                .then(expectNoException());
    }

    @Test
    public void testPipe_failsWhenBoundedQueueOverflows() throws Exception {
        final int completeCapacity = PipeTestConfig.ASYNCHRONOUS_QUEUED_BOUND + PipeTestConfig.ASYNCHRONOUS_POOL_SIZE;
        final int messagesSend = completeCapacity + 1;
        given(aConfiguredPipe(anAsynchronousBoundedPipe())
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSend(messagesSend))
                .then(expectTheException(PipeWaitingQueueIsFullException.class));
    }

    //statistics
    @Test
    public void testPipe_withBlockingSubscriber_whenNumberOfSuccessfulDeliveredMessagesIsQueried_returnsZero(final PipeTestConfig testConfig) throws Exception {
        given(aConfiguredPipe(testConfig)
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSendAsynchronouslyButWillBeBlocked(3, 5)
                        .andThen(theNumberOfSuccessfulMessagesIsQueried()))
                .then(expectResultToBe(0));
    }

    @Test
    public void testPipe_withBlockingSubscriber_whenNumberOfFailedDeliveredMessagesIsQueried_returnsZero(final PipeTestConfig testConfig) throws Exception {
        given(aConfiguredPipe(testConfig)
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSendAsynchronouslyButWillBeBlocked(3, 5)
                        .andThen(theNumberOfFailedMessagesIsQueried()))
                .then(expectResultToBe(0));
    }

    @Test
    public void testPipe_withBlockingSubscriber_whenNumberOfAcceptedMessagesIsQueried_returnsAll(final PipeTestConfig testConfig) throws Exception {
        final int numberOfParallelSender = 3;
        final int numberOfMessagesPerSender = 5;
        final int expectedAcceptedMessages = numberOfParallelSender * numberOfMessagesPerSender;
        given(aConfiguredPipe(testConfig)
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSendAsynchronouslyButWillBeBlocked(numberOfParallelSender, numberOfMessagesPerSender)
                        .andThen(aShortWaitIsDone(10, MILLISECONDS))
                        .andThen(theNumberOfAcceptedMessagesIsQueriedAsynchronously()))
                .then(expectResultToBe(expectedAcceptedMessages));
    }

    @Test
    public void testPipe_withBlockingSubscriber_whenNumberOfQueuedMessagesIsQueried(final PipeTestConfig testConfig) throws Exception {
        final int numberOfParallelSender = 3;
        final int numberOfMessagesPerSender = 5;
        final int sumOfMessages = numberOfParallelSender * numberOfMessagesPerSender;
        final int expectedQueuedMessages = sumOfMessages - PipeTestConfig.ASYNCHRONOUS_POOL_SIZE;
        given(aConfiguredPipe(testConfig)
                .withASubscriberThatBlocksWhenAccepting())
                .when(severalMessagesAreSendAsynchronouslyButWillBeBlocked(numberOfParallelSender, numberOfMessagesPerSender)
                        .andThen(aShortWaitIsDone(10, MILLISECONDS))
                        .andThen(theNumberOfQueuedMessagesIsQueried()))
                .then(expectResultToBe(expectedQueuedMessages));
    }

    //shutdown
    @Test
    public void testPipe_whenShutdown_deliversRemainingMessagesButNoNewAdded(final PipeTestConfig testConfig) throws Exception {
        given(aConfiguredPipe(testConfig))
                .when(thePipeIsShutdownAfterHalfOfTheMessagesWereDelivered(10))
                .then(expectXMessagesToBeDelivered_despiteTheChannelClosed(5));
    }

    @Test
    public void testPipe_whenShutdownWithoutFinishingRemainingTasksIsCalled_noTasksAreFinished(final PipeTestConfig testConfig) throws Exception {
        final int numberOfParallelSendMessage = 5;
        given(aConfiguredPipe(testConfig))
                .when(thePipeIsShutdownAfterHalfOfTheMessagesWereDelivered_withoutFinishingRemainingTasks(10))
                .then(expectXMessagesToBeDelivered_despiteTheChannelClosed(numberOfParallelSendMessage));
    }

    //await
    @Test
    public void testPipe_awaitsFailsWhenAllTasksCouldBeDone(final PipeTestConfig testConfig) throws Exception {
        final int numberOfMessagesSend = PipeTestConfig.ASYNCHRONOUS_POOL_SIZE + 3;
        given(aConfiguredPipe(testConfig))
                .when(awaitIsCalledWithoutExpectingTasksToFinish(numberOfMessagesSend))
                .then(expectTheAwaitToBeTerminatedWithFailure());
    }

}
