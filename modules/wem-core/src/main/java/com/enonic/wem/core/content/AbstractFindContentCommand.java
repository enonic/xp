package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.index.query.QueryService;

abstract class AbstractFindContentCommand
    extends AbstractContentCommand
{
    private final QueryService queryService;

    AbstractFindContentCommand( final Builder builder )
    {
        super( builder );
        this.queryService = builder.queryService;
    }

    public static class Builder<B extends Builder>
        extends AbstractContentCommand.Builder<B>
    {
        private QueryService queryService;

        @SuppressWarnings("unchecked")
        public B queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( queryService );
        }

    }

}



