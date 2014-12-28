package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.repo.internal.index.query.QueryService;

public class DuplicateNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final BlobService blobService;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.blobService = builder.blobService;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId, false );

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams builder = CreateNodeParams.from( existingNode ).
            name( newNodeName ).
            build();

        final Node duplicatedNode = doCreateNode( builder, this.blobService );

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
                build(), blobService );

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

        private BlobService blobService;

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

        public Builder blobService( final BlobService blobService )
        {
            this.blobService = blobService;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( id );
            Preconditions.checkNotNull( blobService );
        }
    }

}
