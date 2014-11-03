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

    FindNodesByParentResult doFindNodesByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
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
