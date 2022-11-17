package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodeBranchEntriesByIdCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private FindNodeBranchEntriesByIdCommand( final Builder builder )
    {
        super( builder );
        ids = builder.ids;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeBranchEntries execute()
    {
        final NodeIds nodeIds = getNodeIds();

        return this.nodeStorageService.getBranchNodeVersions( nodeIds, InternalContext.from( ContextAccessor.current() ) );
    }

    private NodeIds getNodeIds()
    {
        if ( this.ids.isEmpty() )
        {
            return NodeIds.empty();
        }

        final NodeQuery.Builder queryBuilder = NodeQuery.create().
            addQueryFilters( Filters.create().
                add( IdFilter.create().
                    fieldName( NodeIndexPath.ID.getPath() ).
                    values( this.ids ).
                    build() ).
                build() ).
            from( 0 ).
            size( ids.getSize() );

        final SearchResult result =
            this.nodeSearchService.query( queryBuilder.build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        return NodeIds.from( result.getIds() );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder ids( final NodeIds val )
        {
            ids = val;
            return this;
        }

        public FindNodeBranchEntriesByIdCommand build()
        {
            return new FindNodeBranchEntriesByIdCommand( this );
        }
    }
}
