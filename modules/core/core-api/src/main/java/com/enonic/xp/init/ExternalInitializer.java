package com.enonic.xp.init;

import java.util.Objects;

import com.enonic.xp.index.IndexService;

public abstract class ExternalInitializer
    extends Initializer
{
    protected final IndexService indexService;

    protected ExternalInitializer( final Builder builder )
    {
        super( builder );
        this.indexService = builder.indexService;
    }

    @Override
    protected boolean isMaster()
    {
        return indexService.isMaster();
    }

    @Override
    protected boolean readyToInitialize()
    {
        return indexService.waitForYellowStatus();
    }

    public static class Builder<T extends Builder>
        extends Initializer.Builder<T>
    {
        protected IndexService indexService;

        @SuppressWarnings("unchecked")
        public T setIndexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (T) this;
        }

        protected void validate()
        {
            Objects.requireNonNull( indexService );
        }
    }
}
