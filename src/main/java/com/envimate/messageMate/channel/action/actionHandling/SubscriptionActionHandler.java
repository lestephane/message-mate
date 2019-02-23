package com.envimate.messageMate.channel.action.actionHandling;

import com.envimate.messageMate.channel.ProcessingContext;
import com.envimate.messageMate.channel.action.Subscription;
import com.envimate.messageMate.subscribing.Subscriber;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class SubscriptionActionHandler<T> implements ActionHandler<Subscription<T>, T> {

    public static <T> SubscriptionActionHandler<T> subscriptionActionHandler() {
        return new SubscriptionActionHandler<>();
    }

    @Override
    public void handle(final Subscription<T> subscription, final ProcessingContext<T> processingContext) {
        final T payload = processingContext.getPayload();
        final Set<Subscriber<T>> subscribers = subscription.getSubscribers();
        for (final Subscriber<T> subscriber : subscribers) {
            subscriber.accept(payload);
        }
    }
}
