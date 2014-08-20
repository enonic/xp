package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.index.query.QueryService;

public abstract class AbstractFindNodeCommand
    extends AbstractNodeCommand
{
    protected final QueryService queryService;

    protected AbstractFindNodeCommand( Builder builder )
    {
        super( builder );
        queryService = builder.queryService;
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private QueryService queryService;

        public Builder( final Context context )
        {
            super( context );
        }

        @SuppressWarnings("unchecked")
        public B queryService( QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }

    }
}
