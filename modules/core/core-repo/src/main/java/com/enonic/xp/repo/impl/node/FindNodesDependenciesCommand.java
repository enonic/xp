package com.enonic.xp.repo.impl.node;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodesDependenciesCommand
    extends AbstractNodeCommand
{
    private final NodeIds nodeIds;

    private final Set<NodeId> processed = Sets.newHashSet();

    private final boolean recursive;

    private FindNodesDependenciesCommand( final Builder builder )
    {
        super( builder );
        recursive = builder.recursive;
        nodeIds = builder.nodeIds;
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
        return resolveDependencies( nodeIds );
    }

    private NodeIds resolveDependencies( final NodeIds nodeIds )
    {
        Set<NodeId> nonProcessedNodes = nodeIds.getSet().stream().
            filter( ( nodeId ) -> !processed.contains( nodeId ) ).
            collect( Collectors.toSet() );

        if ( nonProcessedNodes.isEmpty() )
        {
            return NodeIds.empty();
        }

        final SearchResult result = getReferences( nonProcessedNodes );

        this.processed.addAll( nonProcessedNodes );

        final NodeIds.Builder builder = NodeIds.create();

        if ( result.isEmpty() )
        {
            return NodeIds.empty();
        }

        addNodeIdsFromReferenceReturnValues( result, builder );

        if ( this.recursive )
        {
            final NodeIds currentLevelDependencies = builder.build();
            builder.addAll( resolveDependencies( currentLevelDependencies ) );
        }

        return builder.build();
    }

    private SearchResult getReferences( final Set<NodeId> nonProcessedNodes )
    {
        return this.nodeSearchService.query( NodeQuery.create().
            addQueryFilter( ExistsFilter.create().
                fieldName( NodeIndexPath.REFERENCE.getPath() ).
                build() ).
            addQueryFilter( IdFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                values( NodeIds.from( nonProcessedNodes ) ).
                build() ).
            from( 0 ).
            size( nonProcessedNodes.size() ).
            build(), ReturnFields.from( NodeIndexPath.REFERENCE ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

    }

    private void addNodeIdsFromReferenceReturnValues( final SearchResult result, final NodeIds.Builder builder )
    {
        for ( SearchHit hit : result.getHits() )
        {
            final ReturnValue returnValue = hit.getReturnValues().get( NodeIndexPath.REFERENCE.getPath() );

            if ( returnValue == null || returnValue.getValues().isEmpty() )
            {
                continue;
            }

            returnValue.getValues().stream().
                filter( ( value ) -> !processed.contains( NodeId.from( value.toString() ) ) ).
                forEach( ( value ) -> builder.add( NodeId.from( value ) ) );
        }
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        private boolean recursive;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds = val;
            return this;
        }

        public Builder recursive( final boolean val )
        {
            recursive = val;
            return this;
        }

        public FindNodesDependenciesCommand build()
        {
            return new FindNodesDependenciesCommand( this );
        }
    }
}
