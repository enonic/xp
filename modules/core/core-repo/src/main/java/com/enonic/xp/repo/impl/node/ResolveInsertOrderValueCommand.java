package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.InsertManualStrategy;
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

    private final InsertManualStrategy insertManualStrategy;

    private ResolveInsertOrderValueCommand( final Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
        insertManualStrategy = builder.insertManualStrategy;
    }

    public Long execute()
    {
        refresh( RefreshMode.SEARCH );

        final ChildOrder childOrder =
            InsertManualStrategy.LAST.equals( insertManualStrategy ) ? ChildOrder.reverseManualOrder() : ChildOrder.manualOrder();

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
            return NodeManualOrderValueResolver.START_ORDER_VALUE;
        }
        else
        {
            final Node first = doGetById( childrenIds.first() );
            if ( first.getManualOrderValue() == null )
            {
                throw new IllegalArgumentException( "Expected that node " + first +
                                                        " should have manualOrderValue since parent childOrder = manualOrderValue, but value was null" );
            }

            if ( InsertManualStrategy.LAST.equals( insertManualStrategy ) )
            {
                return first.getManualOrderValue() - NodeManualOrderValueResolver.ORDER_SPACE;
            }
            else
            {
                return first.getManualOrderValue() + NodeManualOrderValueResolver.ORDER_SPACE;
            }
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

        private InsertManualStrategy insertManualStrategy;

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

        public Builder insertManualStrategy( final InsertManualStrategy val )
        {
            insertManualStrategy = val;
            return this;
        }

        public ResolveInsertOrderValueCommand build()
        {
            return new ResolveInsertOrderValueCommand( this );
        }
    }
}
