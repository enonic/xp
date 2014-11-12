package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByParentParams;
import com.enonic.wem.repo.FindNodesByParentResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodePath;

public class DuplicateNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId, false );

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams builder = CreateNodeParams.from( existingNode ).
            name( newNodeName ).
            build();

        final Node duplicatedNode = doCreateNode( builder );

        storeChildNodes( existingNode, duplicatedNode );

        return duplicatedNode;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent )
    {
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( originalParent.path() ).
            from( 0 ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node node : findNodesByParentResult.getNodes() )
        {
            final Node newChildNode = this.doCreateNode( CreateNodeParams.from( node ).
                parent( newParent.path() ).
                build() );

            storeChildNodes( node, newChildNode );
        }
    }

    private String resolveNewNodeName( final Node existingNode )
    {
        String newNodeName = DuplicateValueResolver.name( existingNode.name() );

        boolean resolvedUnique = false;

        while ( !resolvedUnique )
        {
            final NodePath checkIfExistsPath = NodePath.newNodePath( existingNode.parent(), newNodeName ).build();
            Node foundNode = this.doGetByPath( checkIfExistsPath, false );

            if ( foundNode == null )
            {
                resolvedUnique = true;
            }
            else
            {
                newNodeName = DuplicateValueResolver.name( newNodeName );
            }
        }

        return newNodeName;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        Builder()
        {
            super();
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public DuplicateNodeCommand build()
        {
            validate();
            return new DuplicateNodeCommand( this );
        }

        void validate()
        {
            Preconditions.checkNotNull( id );
        }
    }

}
