package com.enonic.xp.core.impl.content;

import java.util.EnumSet;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentListener;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;
import static com.google.common.base.Strings.nullToEmpty;

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
            throw new RestoreContentException( e.getMessage(), ContentNodeHelper.translateNodePathToContentPath( e.getPath() ) );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private RestoreContentsResult doExecute()
    {
        final Node nodeToRestore = nodeService.getById( NodeId.from( params.getContentId() ) );

        validateLocation( nodeToRestore );

        final boolean isRootContent = nodeToRestore.path().asAbsolute().elementCount() == 2;
        final NodePath parentPathToRestore = getParentPathToRestore( nodeToRestore, isRootContent );
        final String originalSourceName = getOriginalSourceName( nodeToRestore, isRootContent );

        final RestoreContentsResult.Builder result = RestoreContentsResult.create();

        rename( nodeToRestore, parentPathToRestore, originalSourceName );

        final MoveNodeParams.Builder builder =
            MoveNodeParams.create().nodeId( nodeToRestore.id() ).parentNodePath( parentPathToRestore ).moveListener( this );

        stopInherit( builder );

        final Node movedNode = move( builder.build(), originalSourceName );

        updateProperties( movedNode, isRootContent );

        commitNode( movedNode.id(), ContentConstants.RESTORE_COMMIT_PREFIX );

        result.addRestored( ContentId.from( movedNode.id().toString() ) )
            .parentPath( ContentNodeHelper.translateNodePathToContentPath( parentPathToRestore ) );

        return result.build();
    }

    private void validateLocation( final Node node )
    {
        if ( !ArchiveConstants.ARCHIVE_ROOT_NAME.equals( node.path().getElementAsString( 0 ) ) )
        {
            if ( ContentConstants.CONTENT_NODE_COLLECTION.equals( node.getNodeType() ) )
            {
                throw new RestoreContentException( String.format( "Content [%s] is not archived", node.id().toString() ),
                                                   ContentNodeHelper.translateNodePathToContentPath( node.path() ) );
            }
            else
            {
                throw new ContentNotFoundException( params.getContentId(), ContextAccessor.current().getBranch() );
            }
        }
    }

    private void rename( final Node nodeToRestore, final NodePath parentPathToRestore, final String originalSourceName )
    {
        final NodeName newNodeName = nameResolver.buildName( parentPathToRestore, originalSourceName, nodeToRestore );

        if ( !newNodeName.equals( nodeToRestore.name() ) )
        {
            nodeService.rename( RenameNodeParams.create().nodeId( nodeToRestore.id() ).nodeName( newNodeName ).build() );
        }
    }

    private void stopInherit( final MoveNodeParams.Builder builder )
    {

        if ( params.stopInherit() )
        {
            builder.processor( new ContentDataProcessor()
            {
                @Override
                protected EnumSet<ContentInheritType> getTypesToProceed()
                {
                    return EnumSet.of( ContentInheritType.CONTENT, ContentInheritType.PARENT );
                }
            } );
        }
    }

    private Node move( final MoveNodeParams params, final String originalSourceName )
    {
        final Node movedNode = nodeService.move( params );

        if ( originalSourceName != null && !originalSourceName.equals( movedNode.name().toString() ) )
        {
            return nodeService.rename( RenameNodeParams.create()
                                           .nodeId( movedNode.id() )
                                           .nodeName( nameResolver.buildName( movedNode.parentPath(), originalSourceName, movedNode ) )
                                           .build() );
        }
        return movedNode;
    }

    private void updateProperties( final Node node, final boolean isRootContent )
    {
        final FindNodesByParentResult childrenToRestore =
            nodeService.findByParent( FindNodesByParentParams.create().size( -1 ).recursive( true ).parentId( node.id() ).build() );

        childrenToRestore.getNodeIds().forEach( id -> nodeService.update( UpdateNodeParams.create().id( id ).editor( toBeEdited -> {
            toBeEdited.data.removeProperties( ARCHIVED_TIME );
            toBeEdited.data.removeProperties( ARCHIVED_BY );
        } ).build() ) );

        if ( isRootContent )
        {
            nodeService.update( UpdateNodeParams.create().id( node.id() ).editor( toBeEdited -> {
                toBeEdited.data.removeProperties( ORIGINAL_PARENT_PATH );
                toBeEdited.data.removeProperties( ORIGINAL_NAME );
                toBeEdited.data.removeProperties( ARCHIVED_TIME );
                toBeEdited.data.removeProperties( ARCHIVED_BY );
            } ).build() );
        }
    }

    private NodePath getParentPathToRestore( final Node node, final boolean isRootContent )
    {
        if ( isRootContent )
        {
            final Property originalParentPathProperty = node.data().getProperty( ORIGINAL_PARENT_PATH );

            if ( originalParentPathProperty != null )
            {
                final String originalParentPath = originalParentPathProperty.getString();

                if ( params.getParentPath() != null )
                {
                    return NodePath.create( ContentConstants.CONTENT_ROOT_PATH, params.getParentPath().toString() ).build();
                }
                else if ( !nullToEmpty( originalParentPath ).isBlank() )
                {
                    final NodePath parentPath =
                        NodePath.create( ContentConstants.CONTENT_ROOT_PATH, originalParentPathProperty.getValue().asString() ).build();

                    if ( nodeService.nodeExists( parentPath ) )
                    {
                        return parentPath;
                    }

                }
            }

        }

        return ContentConstants.CONTENT_ROOT_PATH;
    }

    private String getOriginalSourceName( final Node node, final boolean isRootContent )
    {
        if ( isRootContent )
        {
            final Property originalNameProperty = node.data().getProperty( ORIGINAL_NAME );
            if ( originalNameProperty != null )
            {
                return originalNameProperty.getString();
            }
        }
        return node.name().toString();
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
