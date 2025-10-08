package com.enonic.xp.lib.node;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.node.mapper.FindNodesByParentResultMapper;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;

public class FindChildrenNodeHandler
    extends AbstractNodeHandler
{
    private final NodeKey parentKey;

    private final ChildOrder childOrder;

    private final boolean recursive;

    private final boolean countOnly;

    private final Integer count;

    private final Integer start;

    private FindChildrenNodeHandler( final Builder builder )
    {
        super( builder );
        this.parentKey = builder.parentKey;
        this.childOrder = builder.childOrder;
        this.recursive = builder.recursive;
        this.countOnly = builder.countOnly;
        this.count = builder.count;
        this.start = builder.start;
    }

    @Override
    public Object execute()
    {
        final FindNodesByParentResult result = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( getNodeId( parentKey ) ).
            childOrder( childOrder ).
            recursive( recursive ).
            countOnly( countOnly ).
            size( count ).
            from( start ).
            build() );

        return new FindNodesByParentResultMapper( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey parentKey;

        private ChildOrder childOrder;

        private boolean recursive;

        private boolean countOnly;

        private Integer count;

        private Integer start;

        private Builder()
        {
        }

        public Builder parentKey( final NodeKey val )
        {
            parentKey = val;
            return this;
        }

        public Builder childOrder( final ChildOrder val )
        {
            childOrder = val;
            return this;
        }

        public Builder recursive( final boolean val )
        {
            recursive = val;
            return this;
        }

        public Builder countOnly( final boolean val )
        {
            countOnly = val;
            return this;
        }

        public Builder count( final Integer val )
        {
            count = val;
            return this;
        }

        public Builder start( final Integer val )
        {
            start = val;
            return this;
        }

        public FindChildrenNodeHandler build()
        {
            return new FindChildrenNodeHandler( this );
        }
    }
}
