package com.envimate.messageMate.qcec.querying.givenWhenThen;

import com.envimate.messageMate.internal.brokering.BrokerStrategyType;
import com.envimate.messageMate.messageBus.MessageBus;
import com.envimate.messageMate.qcec.queryresolving.Query;
import com.envimate.messageMate.qcec.queryresolving.QueryResolver;
import com.envimate.messageMate.qcec.queryresolving.QueryResolverFactory;
import com.envimate.messageMate.subscribing.SubscriptionId;

import java.util.Optional;
import java.util.function.Consumer;

import static com.envimate.messageMate.messageBus.MessageBusBuilder.aMessageBus;


public final class QueryResolverTestQueryResolver extends TestQueryResolver {
    private final QueryResolver queryResolver;

    private QueryResolverTestQueryResolver() {
        final MessageBus messageBus = aMessageBus()
                .withBrokerType(BrokerStrategyType.DELIVERY_TO_CLASS_AND_DIRECT_INHERITED_INTERFACES)
                .build();
        queryResolver = QueryResolverFactory.aQueryResolver(messageBus);
    }

    public static QueryResolverTestQueryResolver queryResolverTestQueryResolver() {
        return new QueryResolverTestQueryResolver();
    }

    @Override
    public <R> Optional<R> executeQuery(final Query<R> query) {
        return queryResolver.query(query);
    }

    @Override
    public <R> R executeRequiredQuery(final Query<R> query) {
        return queryResolver.queryRequired(query);
    }

    @Override
    public <T extends Query> SubscriptionId subscribing(final Class<T> queryClass, final Consumer<T> consumer) {
        final SubscriptionId subscriptionId = queryResolver.answer(queryClass, consumer);
        return subscriptionId;
    }

    @Override
    public <T extends Query> TestQueryResolver withASubscriber(final Class<T> queryClass, final Consumer<T> consumer) {
        subscribing(queryClass, consumer);
        return this;
    }

    @Override
    public void unsubscribe(final SubscriptionId subscriptionId) {
        queryResolver.unsubscribe(subscriptionId);
    }
}
