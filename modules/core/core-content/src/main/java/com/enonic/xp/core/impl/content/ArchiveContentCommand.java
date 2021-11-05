package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentListener;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UnpublishContentParams;
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

import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;

final class ArchiveContentCommand
    extends AbstractArchiveCommand
    implements MoveNodeListener
{
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern( "HH-mm-ss-SSS" ).withZone( ZoneId.systemDefault() );

    private final ArchiveContentParams params;

    private final ArchiveContentListener archiveContentListener;

    private final PathResolver pathResolver;

    private ArchiveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.archiveContentListener = builder.archiveContentListener;
        this.pathResolver = new PathResolver();
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
        unpublish();

        final Node nodeToArchive = updateProperties( NodeId.from( params.getContentId() ) );

        rename( nodeToArchive );
        move( nodeToArchive.id() );

        commitNode( nodeToArchive.id(), ContentConstants.ARCHIVE_COMMIT_PREFIX );

        return ArchiveContentsResult.create().addArchived( params.getContentId() ).build();
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( archiveContentListener != null )
        {
            archiveContentListener.contentArchived( count );
        }
    }

    private void unpublish()
    {
        UnpublishContentCommand.create()
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .params( UnpublishContentParams.create()
                         .contentIds( ContentIds.from( params.getContentId() ) )
                         .unpublishBranch( ContentConstants.BRANCH_MASTER )
                         .includeChildren( true )
                         .build() )
            .build()
            .execute();

        nodeService.refresh( RefreshMode.ALL );
    }

    private Node updateProperties( final NodeId nodeId )
    {
        return nodeService.update( UpdateNodeParams.create().id( nodeId ).editor( toBeEdited -> {
            toBeEdited.data.setString( ORIGINAL_PARENT_PATH,
                                       ContentNodeHelper.translateNodePathToContentPath( toBeEdited.source.parentPath() ).toString() );
            toBeEdited.data.setString( ORIGINAL_NAME, toBeEdited.source.name().toString() );
        } ).build() );
    }

    private Node rename( final Node node )
    {
        final NodePath newPath = pathResolver.buildArchivedPath( node );
        if ( !newPath.getName().equals( node.name().toString() ) )
        {
            return nodeService.rename(
                RenameNodeParams.create().nodeId( node.id() ).nodeName( NodeName.from( newPath.getName() ) ).build() );
        }

        return node;
    }

    private Node move( final NodeId nodeId )
    {
        final MoveNodeParams.Builder builder =
            MoveNodeParams.create().nodeId( nodeId ).parentNodePath( ArchiveConstants.ARCHIVE_ROOT_PATH ).moveListener( this );

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

        return nodeService.move( builder.build() );
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

    private final class PathResolver
    {
        private static final String DELIMITER = "...";

        private static final int MAX_NAME_SIZE = 30;

        private NodePath buildArchivedPath( final Node node )
        {
            NodePath newPath;
            String newName = null;
            do
            {
                newName = newName == null ? node.name().toString() : node.name() + " " + DATE_TIME_FORMATTER.format( Instant.now() );
                newPath = NodePath.create( ArchiveConstants.ARCHIVE_ROOT_PATH, trim( newName ) ).build();

            }
            while ( nodeService.nodeExists( newPath ) ||
                ( nodeService.nodeExists( NodePath.create( node.parentPath(), newName ).build() ) &&
                    !newName.equals( node.name().toString() ) ) );

            return newPath;
        }

        private String trim( final String value )
        {
            if ( value.length() > MAX_NAME_SIZE )
            {
                return value.substring( 0, MAX_NAME_SIZE - 7 - DELIMITER.length() + 1 ) + DELIMITER + value.substring( value.length() - 7 );
            }
            return value;
        }
    }

}
