package com.enonic.xp.repo.impl.node;

import java.util.stream.Collectors;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

public final class FindInternalDependenciesCommand
    extends AbstractNodeCommand
{
    private final NodeIds nodeIds;

    private FindInternalDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes execute()
    {
        final Nodes originalNodes = GetNodesByIdsCommand.create( this ).
            ids( nodeIds ).
            build().
            execute();

        final NodeIds dependenciesIds = FindNodesDependenciesCommand.create( this ).
            nodeIds( nodeIds ).
            recursive( true ).
            build().execute();

        final Nodes dependencies = GetNodesByIdsCommand.create( this ).
            ids( dependenciesIds ).
            build().
            execute();

        final Nodes internalDependencies = Nodes.from( dependencies.stream().filter(
            dependency -> originalNodes.stream().anyMatch( node -> dependency.path().isChildOf( node.path() ) ) ).collect(
            Collectors.toSet() ) );

        final Nodes parentNodes = getParentNodes( originalNodes, internalDependencies );

        return Nodes.create().
            addAll( parentNodes ).
            addAll( internalDependencies ).
            build();
    }

    private Nodes getParentNodes( final Nodes originalNodes, final Nodes internalDependencies )
    {

        final Nodes.Builder result = Nodes.create();

        internalDependencies.forEach( internalDependency -> {

            NodePath currentAncestorPath = internalDependency.parentPath();
            while ( !currentAncestorPath.isRoot() )
            {
                Boolean originNodeReached = false;

                for ( Node originalNode : originalNodes )
                {
                    if ( originalNode.path().equals( currentAncestorPath ) )
                    {
                        originNodeReached = true;
                        break;
                    }
                }
                if ( originNodeReached )
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
        private NodeIds nodeIds;

        Builder()
        {
            super();
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public FindInternalDependenciesCommand build()
        {
            validate();
            return new FindInternalDependenciesCommand( this );
        }

    }

}
