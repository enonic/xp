package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.MoveContentException;
import com.enonic.xp.content.MoveContentListener;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

final class MoveContentCommand
    extends AbstractContentCommand
    implements MoveNodeListener
{
    private final MoveContentParams params;

    private final MoveContentListener moveContentListener;

    private MoveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.moveContentListener = builder.moveContentListener;
    }

    public static Builder create( final MoveContentParams params )
    {
        return new Builder( params );
    }

    MoveContentsResult execute()
    {
        params.validate();

        try
        {
            final MoveContentsResult movedContents = doExecute();
            this.nodeService.refresh( RefreshMode.ALL );
            return movedContents;
        }
        catch ( MoveNodeException e )
        {
            throw new MoveContentException( e.getMessage(), ContentPath.from( e.getPath().toString() ) );
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

    private MoveContentsResult doExecute()
    {
        final ContentId contentId = params.getContentId();
        final Content sourceContent = getContent( contentId );

        if ( sourceContent.getParentPath().equals( params.getParentContentPath() ) )
        {
            throw new ContentAlreadyMovedException(
                String.format( "Content with id [%s] is already a child of [%s]", params.getContentId(), params.getParentContentPath() ),
                sourceContent.getPath() );
        }

        validateParentChildRelations( params.getParentContentPath(), sourceContent.getType() );
        validateArchive( sourceContent );

        final NodePath nodePath =
            NodePath.create( ContentConstants.CONTENT_ROOT_PATH ).elements( params.getParentContentPath().toString() ).build();
        final NodeId sourceNodeId = NodeId.from( contentId );

        final MoveNodeParams.Builder builder =
            MoveNodeParams.create().nodeId( sourceNodeId ).parentNodePath( nodePath ).moveListener( this );

        if ( params.stopInherit() )
        {
            builder.processor( new MoveContentProcessor() );
        }

        final Node movedNode = nodeService.move( builder.build() );

        final Content movedContent = translator.fromNode( movedNode, true );

        return MoveContentsResult.create().setContentName( movedContent.getDisplayName() ).addMoved( movedContent.getId() ).build();
    }

    private void validateArchive( final Content sourceContent )
    {
            if ( ArchiveConstants.ARCHIVE_ROOT_CONTENT_PATH.equals( sourceContent.getPath() ) )
            {
                throw new ArchiveContentException( "Archive node cannot be moved" );
            }

            if ( ArchiveConstants.ARCHIVE_ROOT_CONTENT_PATH.equals( sourceContent.getParentPath() ) )
            {
                throw new ArchiveContentException( "Archive container cannot be moved" );
            }

            if ( ArchiveConstants.ARCHIVE_ROOT_CONTENT_PATH.equals( params.getParentContentPath() ) )
            {
                throw new ArchiveContentException( "Content cannot be moved directly to the archive" );
            }
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( moveContentListener != null )
        {
            moveContentListener.contentMoved( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final MoveContentParams params;

        private MoveContentListener moveContentListener;

        Builder( final MoveContentParams params )
        {
            this.params = params;
        }

        public Builder moveListener( final MoveContentListener moveContentListener )
        {
            this.moveContentListener = moveContentListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public MoveContentCommand build()
        {
            validate();
            return new MoveContentCommand( this );
        }
    }

}
