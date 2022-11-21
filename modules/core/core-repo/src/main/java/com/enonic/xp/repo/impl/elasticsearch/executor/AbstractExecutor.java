package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

abstract class AbstractExecutor
{
    static final TimeValue DEFAULT_SCROLL_TIME = new TimeValue( 60, TimeUnit.SECONDS );

    final Client client;

    AbstractExecutor( final Builder builder )
    {
        client = builder.client;
    }

    void clearScroll( final String scrollId )
    {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId( scrollId );

        client.clearScroll( clearScrollRequest ).actionGet();
    }

    public static class Builder<B extends Builder>
    {
        private final Client client;

        Builder( final Client client )
        {
            this.client = client;
        }
    }
}
