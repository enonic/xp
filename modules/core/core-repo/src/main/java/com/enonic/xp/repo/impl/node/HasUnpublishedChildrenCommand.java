package com.enonic.xp.repo.impl.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class HasUnpublishedChildrenCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodeId parent;

    private HasUnpublishedChildrenCommand( final Builder builder )
    {
        super( builder );
        target = builder.target;
        parent = builder.parent;
    }

    public boolean execute()
    {
        final Node parentNode = doGetById( parent );

        final NodeVersionDiffResult result = nodeSearchService.query( NodeVersionDiffQuery.create().
            source( ContextAccessor.current().getBranch() ).
            target( target ).
            nodePath( parentNode.path() ).
            size( 0 ).
            excludes( ExcludeEntries.create().
                add( new ExcludeEntry( parentNode.path(), false ) ).
                build() ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        return result.getTotalHits() > 0;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branch target;

        private NodeId parent;

        private Builder()
        {
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
        }

        public Builder parent( final NodeId val )
        {
            parent = val;
            return this;
        }

        public HasUnpublishedChildrenCommand build()
        {
            return new HasUnpublishedChildrenCommand( this );
        }
    }
}
