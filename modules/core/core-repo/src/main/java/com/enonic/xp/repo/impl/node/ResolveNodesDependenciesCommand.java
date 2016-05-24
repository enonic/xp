package com.enonic.xp.repo.impl.node;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class ResolveNodesDependenciesCommand
    extends AbstractNodeCommand
{
    private final NodeIds nodeIds;

    private final Set<NodeId> processed = Sets.newHashSet();

    private boolean recursive;

    private ResolveNodesDependenciesCommand( final Builder builder )
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
        return resolveDependencies( this.nodeIds );
    }

    private NodeIds resolveDependencies( final NodeIds nodeIds )
    {
        final Set<NodeId> toBeProcessed = nodeIds.stream().
            filter( ( nodeId ) -> !this.processed.contains( nodeId ) ).
            collect( Collectors.toSet() );

        return doResolveDependencies( NodeIds.from( toBeProcessed ) );
    }

    private NodeIds doResolveDependencies( final NodeIds nodeIds )
    {
        this.processed.addAll( nodeIds.getSet() );

        final NodeIds children = GetOutgoingNodesReferencesCommand.create( this ).
            nodeId( nodeIds ).
            build().
            execute();

        if ( !recursive )
        {
            return children;
        }

        if ( children.isEmpty() )
        {
            return children;
        }

        final NodeIds.Builder builder = NodeIds.create();

        builder.addAll( children );
        builder.addAll( resolveDependencies( children ) );

        return builder.build();
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

        public ResolveNodesDependenciesCommand build()
        {
            return new ResolveNodesDependenciesCommand( this );
        }
    }
}
