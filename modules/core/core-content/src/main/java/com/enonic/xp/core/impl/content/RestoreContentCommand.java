package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentListener;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;

final class RestoreContentCommand
    extends AbstractArchiveCommand
    implements MoveNodeListener
{
    private final RestoreContentParams params;

    private final RestoreContentListener restoreContentListener;

    private final NameResolver nameResolver;

    private RestoreContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.restoreContentListener = builder.restoreContentListener;
        this.nameResolver = new NameResolver();
    }

    public static Builder create( final RestoreContentParams params )
    {
        return new Builder( params );
    }

    RestoreContentsResult execute()
    {
        params.validate();

        try
        {
            final RestoreContentsResult restoredContents = doExecute();
            this.nodeService.refresh( RefreshMode.ALL );
            return restoredContents;
        }
        catch ( MoveNodeException e )
        {
            throw new RestoreContentException( e.getMessage(), ContentPath.from( e.getPath().toString() ) );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new ContentAlreadyExistsException( ContentPath.from( e.getNode().toString() ), e.getRepositoryId(), e.getBranch() );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private RestoreContentsResult doExecute()
    {
        final Node nodeToRestore = nodeService.getById( NodeId.from( params.getContentId() ) );

        if ( !ArchiveConstants.ARCHIVE_ROOT_NAME.equals( nodeToRestore.path().getElementAsString( 0 ) ) )
        {
            if ( ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeToRestore.getNodeType() ) )
            {
                throw new RestoreContentException( String.format( "Content [%s] is not archived", nodeToRestore.id().toString() ) );
            }
            else
            {
                throw new ContentNotFoundException( params.getContentId(), ContextAccessor.current().getBranch() );
            }
        }

        NodePath parentPathToRestore;
        String originalSourceName;
        final boolean isRootContent = nodeToRestore.path().asAbsolute().elementCount() == 2;

        if ( isRootContent )
        {
            originalSourceName = nodeToRestore.data().getString( ArchiveConstants.ORIGINAL_NAME_PROPERTY_NAME );
            final String originalSourceParentPath = nodeToRestore.data().getString( ArchiveConstants.ORIGINAL_PARENT_PATH_PROPERTY_NAME );

            parentPathToRestore = params.getPath() != null
                ? NodePath.create( ContentConstants.CONTENT_ROOT_PATH, params.getPath().toString() ).build()
                : !Strings.nullToEmpty( originalSourceParentPath ).isBlank() &&
                    nodeService.nodeExists( NodePath.create( originalSourceParentPath ).build() ) ? NodePath.create(
                    originalSourceParentPath ).build() : ContentConstants.CONTENT_ROOT_PATH;

        }
        else
        {
            originalSourceName = nodeToRestore.name().toString();
            parentPathToRestore = ContentConstants.CONTENT_ROOT_PATH;
        }
        final RestoreContentsResult.Builder result = RestoreContentsResult.create();

        final NodeName newNodeName = nameResolver.buildName( parentPathToRestore, originalSourceName, nodeToRestore );

        if ( !newNodeName.equals( nodeToRestore.name() ) )
        {
            nodeService.rename( RenameNodeParams.create().nodeId( nodeToRestore.id() ).nodeName( newNodeName ).build() );
        }

        final MoveNodeParams.Builder builder =
            MoveNodeParams.create().nodeId( nodeToRestore.id() ).parentNodePath( parentPathToRestore ).moveListener( this );

        final Node movedNode = nodeService.move( builder.build() );

        if ( originalSourceName != null && !originalSourceName.equals( movedNode.name().toString() ) )
        {
            nodeService.rename( RenameNodeParams.create()
                                    .nodeId( movedNode.id() )
                                    .nodeName( nameResolver.buildName( movedNode.parentPath(), originalSourceName, movedNode ) )
                                    .build() );
        }

        if ( isRootContent )
        {
            nodeService.update( UpdateNodeParams.create().id( movedNode.id() ).editor( toBeEdited -> {
                toBeEdited.data.removeProperties( ArchiveConstants.ORIGINAL_NAME_PROPERTY_NAME );
                toBeEdited.data.removeProperties( ArchiveConstants.ORIGINAL_PARENT_PATH_PROPERTY_NAME );
            } ).build() );


        }
        nodeService.refresh( RefreshMode.ALL );

        result.addRestored( ContentId.from( movedNode.id().toString() ) );

        return result.build();
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( restoreContentListener != null )
        {
            restoreContentListener.contentRestored( count );
        }
    }

    public static class Builder
        extends AbstractArchiveCommand.Builder<Builder>
    {
        private final RestoreContentParams params;

        private RestoreContentListener restoreContentListener;

        private Builder( final RestoreContentParams params )
        {
            this.params = params;
        }

        public Builder restoreListener( final RestoreContentListener restoreContentListener )
        {
            this.restoreContentListener = restoreContentListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public RestoreContentCommand build()
        {
            validate();
            return new RestoreContentCommand( this );
        }
    }

    private final class NameResolver
    {
        private NodeName buildName( final NodePath newParentPath, final String name, final Node node )
        {
            String newName = null;

            boolean nameAlreadyExist;

            do
            {
                newName = newName != null ? NameValueResolver.name( newName ) : name;

                final NodePath targetPath = NodePath.create( newParentPath, newName ).build();

                nameAlreadyExist = nodeService.nodeExists( targetPath ) && !nodeService.getByPath( targetPath ).id().equals( node.id() );
                if ( !newParentPath.equals( node.parentPath() ) )
                {
                    nameAlreadyExist = nameAlreadyExist ||
                        ( nodeService.nodeExists( NodePath.create( node.parentPath(), newName ).build() ) &&
                            !node.name().toString().equals( newName ) );
                }

            }
            while ( nameAlreadyExist );

            return NodeName.from( newName );
        }


    }
}
