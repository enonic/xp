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
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

final class RestoreContentCommand
    extends AbstractArchiveCommand
    implements MoveNodeListener
{
    private final RestoreContentParams params;

    private final RestoreContentListener restoreContentListener;

    private RestoreContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.restoreContentListener = builder.restoreContentListener;
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
                throw new RestoreContentException(
                    String.format( "Content [%s] is not archived", nodeToRestore.id().toString() ) );
            }
            else
            {
                throw new ContentNotFoundException( params.getContentId(), ContextAccessor.current().getBranch() );
            }
        }

        final Node container = nodeService.getById( NodeId.from( nodeToRestore.path().asAbsolute().getElementAsString( 1 ) ) );

        final String oldSourceParentPath = container.data().getString( "oldParentPath" );

        final NodePath oldParentPath = params.getPath() != null
            ? ContentNodeHelper.translateContentPathToNodePath( params.getPath() )
            : !Strings.nullToEmpty( oldSourceParentPath ).isBlank()
                ? NodePath.create( oldSourceParentPath ).build()
                : ContentNodeHelper.translateContentPathToNodePath( ContentPath.ROOT );

        final NodeIds contentsToRestore =
            nodeService.findByParent( FindNodesByParentParams.create().parentPath( container.path() ).size( -1 ).build() ).getNodeIds();

        final RestoreContentsResult.Builder result = RestoreContentsResult.create();

        for ( final NodeId contentToRestore : contentsToRestore )
        {

            final MoveNodeParams.Builder builder =
                MoveNodeParams.create().nodeId( contentToRestore ).parentNodePath( oldParentPath ).moveListener( this );

            final Node movedNode = nodeService.move( builder.build() );

            result.addRestored( ContentId.from( movedNode.id().toString() ) );
        }

        nodeService.deleteById( container.id() );

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

}
