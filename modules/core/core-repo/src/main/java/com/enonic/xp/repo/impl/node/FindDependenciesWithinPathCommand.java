package com.enonic.xp.repo.impl.node;

import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

public final class FindDependenciesWithinPathCommand
    extends AbstractNodeCommand
{
    private final Map<NodeId, NodePath> nodeIds;

    private final Boolean skipChildren;

    private FindDependenciesWithinPathCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
        this.skipChildren = builder.skipChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes execute()
    {
        final Nodes.Builder result = Nodes.create();

        final Nodes originalNodes = GetNodesByIdsCommand.create( this ).
            ids( NodeIds.from( nodeIds.keySet() ) ).
            build().
            execute();

        originalNodes.forEach( originalNode -> {

            final NodeIds dependenciesIds = FindNodesDependenciesCommand.create( this ).
                nodeIds( NodeIds.from( originalNode.id() ) ).
                recursive( true ).
                build().execute();

            final Nodes dependencies = GetNodesByIdsCommand.create( this ).
                ids( dependenciesIds ).
                build().
                execute();

            final NodePath dependenciesLookingPath =
                nodeIds.get( originalNode.id() ) != null ? nodeIds.get( originalNode.id() ) : originalNode.path();

            final Nodes internalDependencies = Nodes.from( dependencies.stream().
                filter( dependency -> dependency.path().isChildOf( dependenciesLookingPath ) ).
                filter( dependency -> !skipChildren || !dependency.path().isChildOf( originalNode.path() ) ).
                collect( Collectors.toSet() ) );

            final Nodes parentNodes = getParentNodes( originalNode, internalDependencies );

            result.addAll( internalDependencies ).addAll( parentNodes );
        } );

        return result.build();
    }

    private Nodes getParentNodes( final Node originalNode, final Nodes internalDependencies )
    {

        final Nodes.Builder result = Nodes.create();

        internalDependencies.forEach( internalDependency -> {

            NodePath currentAncestorPath = internalDependency.parentPath();
            while ( !currentAncestorPath.isRoot() )
            {

                if ( originalNode.path().equals( currentAncestorPath ) || originalNode.path().isChildOf( currentAncestorPath ) )
                {
                    break;
                }

                result.add( GetNodeByPathCommand.create( this ).nodePath( currentAncestorPath ).build().execute() );

                currentAncestorPath = currentAncestorPath.getParentPath();
            }
        } );

        return result.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Map<NodeId, NodePath> nodeIds;

        private Boolean skipChildren = false;

        Builder()
        {
            super();
        }

        public Builder nodeIds( final Map<NodeId, NodePath> nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public Builder skipChildren( final Boolean skipChildren )
        {
            this.skipChildren = skipChildren;
            return this;
        }

        public FindDependenciesWithinPathCommand build()
        {
            validate();
            return new FindDependenciesWithinPathCommand( this );
        }

    }

}
