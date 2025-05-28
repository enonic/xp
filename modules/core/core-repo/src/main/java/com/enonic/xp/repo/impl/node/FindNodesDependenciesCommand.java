package com.enonic.xp.repo.impl.node;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

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

    private final NodeIds excludedIds;

    private final Set<NodeId> processed = new HashSet<>();

    private final Function<NodeIds, NodeIds> filter;

    private FindNodesDependenciesCommand( final Builder builder )
    {
        super( builder );
        filter = builder.filter != null ? builder.filter : Function.identity();
        nodeIds = builder.nodeIds.build();
        excludedIds = builder.excludedIds.build();
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
        final NodeIds nonProcessedNodes = NodeIds.from( nodeIds.getSet().stream().
            filter( ( nodeId ) -> !processed.contains( nodeId ) ).
            collect( ImmutableSet.toImmutableSet() ) );

        if ( nonProcessedNodes.isEmpty() )
        {
            return NodeIds.empty();
        }

        final SearchResult result = getReferences( nonProcessedNodes );

        this.processed.addAll( nonProcessedNodes.getSet() );

        if ( result.isEmpty() )
        {
            return NodeIds.empty();
        }

        final NodeIds.Builder builder = NodeIds.create();

        final NodeIds directDependencies = filter.apply( getNodeIdsFromReferenceReturnValues( result ) );

        builder.addAll( directDependencies );
        builder.addAll( resolveDependencies( directDependencies ) );

        return builder.build();
    }

    private SearchResult getReferences( final NodeIds nonProcessedNodes )
    {
        return this.nodeSearchService.query( NodeQuery.create().
            addQueryFilter( ExistsFilter.create().
                fieldName( NodeIndexPath.REFERENCE.getPath() ).
                build() ).
            addQueryFilter( IdFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                values( nonProcessedNodes ).
                build() ).
            from( 0 ).
            size( nonProcessedNodes.getSize() ).
            build(), ReturnFields.from( NodeIndexPath.REFERENCE ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

    }

    private NodeIds getNodeIdsFromReferenceReturnValues( final SearchResult result )
    {
        final NodeIds.Builder builder = NodeIds.create();
        for ( SearchHit hit : result.getHits() )
        {
            final ReturnValue returnValue = hit.getReturnValues().get( NodeIndexPath.REFERENCE.getPath() );

            if ( returnValue == null || returnValue.getValues().isEmpty() )
            {
                continue;
            }

            returnValue.getValues()
                .stream()
                .map( NodeId::from )
                .filter( ( value ) -> !processed.contains( value ) )
                .filter( ( value ) -> !excludedIds.contains( value ) )
                .forEach( builder::add );
        }

        return builder.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private final NodeIds.Builder nodeIds = NodeIds.create();

        private final NodeIds.Builder excludedIds = NodeIds.create();

        private Function<NodeIds, NodeIds> filter = null;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds.addAll( val );
            return this;
        }

        public Builder excludedIds( final NodeIds val )
        {
            excludedIds.addAll( val );
            return this;
        }

        public Builder filter( final Function<NodeIds, NodeIds> val )
        {
            this.filter = val;
            return this;
        }

        public FindNodesDependenciesCommand build()
        {
            return new FindNodesDependenciesCommand( this );
        }
    }
}
