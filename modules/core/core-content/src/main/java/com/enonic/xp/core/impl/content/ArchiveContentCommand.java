package com.enonic.xp.core.impl.content;

import java.util.UUID;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentListener;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;

final class ArchiveContentCommand
    extends AbstractArchiveCommand
    implements MoveNodeListener
{
    private final ArchiveContentParams params;

    private final ArchiveContentListener archiveContentListener;

    private ArchiveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.archiveContentListener = builder.archiveContentListener;
    }

    public static Builder create( final ArchiveContentParams params )
    {
        return new Builder( params );
    }

    ArchiveContentsResult execute()
    {
        params.validate();

        try
        {
            final ArchiveContentsResult archivedContents = doExecute();
            this.nodeService.refresh( RefreshMode.ALL );
            return archivedContents;
        }
        catch ( MoveNodeException e )
        {
            throw new ArchiveContentException( e.getMessage(), ContentPath.from( e.getPath().toString() ) );
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

    private ArchiveContentsResult doExecute()
    {
        final Node contentToArchive = nodeService.getById( NodeId.from( params.getContentId() ) );

        final Node container = nodeService.create( containerParams( contentToArchive ) );

        final MoveNodeParams.Builder builder = MoveNodeParams.create().
            nodeId( NodeId.from( params.getContentId() ) ).
            parentNodePath( container.path() ).
            moveListener( this );

        final Node movedNode = nodeService.move( builder.build() );

        final Content movedContent = translator.fromNode( movedNode, true );

        return ArchiveContentsResult.create().
            addArchived( movedContent.getId() ).
            build();
    }

    private CreateNodeParams containerParams( final Node contentToArchive )
    {
        final String uniqueName = UUID.randomUUID().toString();

        final PropertyTree data = new PropertyTree();
        data.setString( "oldParentPath", contentToArchive.parentPath().toString() );

        return CreateNodeParams.create().parent( ArchiveConstants.ARCHIVE_ROOT_PATH ).
            name( uniqueName ).
            setNodeId( NodeId.from( uniqueName ) ).
            nodeType( ArchiveConstants.ARCHIVE_NODE_TYPE ).
            data( data ).
            build();
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( archiveContentListener != null )
        {
            archiveContentListener.contentArchived( count );
        }
    }

    public static class Builder
        extends AbstractArchiveCommand.Builder<Builder>
    {
        private final ArchiveContentParams params;

        private ArchiveContentListener archiveContentListener;

        private Builder( final ArchiveContentParams params )
        {
            this.params = params;
        }

        public Builder archiveListener( final ArchiveContentListener archiveContentListener )
        {
            this.archiveContentListener = archiveContentListener;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public ArchiveContentCommand build()
        {
            validate();
            return new ArchiveContentCommand( this );
        }
    }

}
