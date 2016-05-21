package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;

public class FindNodeIdsByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodeIdsByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeIds execute()
    {
        NodePath parentPath = params.getParentPath();

        if ( parentPath == null )
        {
            Node parent = GetNodeByIdCommand.create( this ).
                id( params.getParentId() ).
                build().
                execute();

            if ( parent == null )
            {
                return NodeIds.empty();
            }

            parentPath = parent.path();
        }

        final ChildOrder order = NodeChildOrderResolver.create( this ).
            nodePath( parentPath ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQueryResult nodeQueryResult = this.searchService.query( NodeQuery.create().
            parent( parentPath ).
            from( params.getFrom() ).
            size( params.getSize() ).
            searchMode( params.isCountOnly() ? SearchMode.COUNT : SearchMode.SEARCH ).
            setOrderExpressions( order.getOrderExpressions() ).
            accurateScoring( true ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        if ( nodeQueryResult.getHits() == 0 )
        {
            return NodeIds.empty();
        }

        return nodeQueryResult.getNodeIds();
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public FindNodeIdsByParentCommand build()
        {
            this.validate();
            return new FindNodeIdsByParentCommand( this );
        }
    }
}
