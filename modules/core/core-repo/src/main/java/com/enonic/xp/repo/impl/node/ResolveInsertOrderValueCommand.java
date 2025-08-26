package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;

public class ResolveInsertOrderValueCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final boolean last;

    private ResolveInsertOrderValueCommand( final Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
        last = builder.last;
    }

    public long execute()
    {
        refresh( RefreshMode.SEARCH );

        final ChildOrder childOrder = last ? ChildOrder.reverseManualOrder() : ChildOrder.manualOrder();

        final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query( NodeQuery.create()
                                                                                    .size( 1 )
                                                                                    .setOrderExpressions( childOrder.getOrderExpressions() )
                                                                                    .accurateScoring( true )
                                                                                    .parent( parentPath )
                                                                                    .build(),
                                                                                SingleRepoSearchSource.from( ContextAccessor.current() ) )
                                                      .getIds() );

        if ( childrenIds.isEmpty() )
        {
            return NodeManualOrderValueResolver.first();
        }
        else
        {
            final Node node = doGetById( childrenIds.first() );
            final Long manualOrderValue = node.getManualOrderValue();
            if ( manualOrderValue == null )
            {
                throw new IllegalArgumentException( "Expected that node " + node.id() +
                                                        " should have manualOrderValue since parent childOrder = manualOrderValue, but value was null" );
            }

            return last ? NodeManualOrderValueResolver.after( manualOrderValue ) : NodeManualOrderValueResolver.before( manualOrderValue );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private boolean last;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder parentPath( final NodePath val )
        {
            parentPath = val;
            return this;
        }

        public Builder last( final boolean val )
        {
            last = val;
            return this;
        }

        public ResolveInsertOrderValueCommand build()
        {
            return new ResolveInsertOrderValueCommand( this );
        }
    }
}
