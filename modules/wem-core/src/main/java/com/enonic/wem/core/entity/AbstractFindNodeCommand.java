package com.enonic.wem.core.entity;

import com.enonic.wem.core.index.query.QueryService;

abstract class AbstractFindNodeCommand
    extends AbstractNodeCommand
{
    final QueryService queryService;

    AbstractFindNodeCommand( Builder builder )
    {
        super( builder );
        queryService = builder.queryService;
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private QueryService queryService;

        public Builder()
        {
            super();
        }

        @SuppressWarnings("unchecked")
        public B queryService( QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }

    }
}
