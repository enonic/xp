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
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.User;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;

final class ArchiveContentCommand
    extends AbstractContentCommand
    implements MoveNodeListener
{
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern( "HH-mm-ss-SSS" ).withZone( ZoneId.systemDefault() );

    private final ArchiveContentParams params;

    private final PathResolver pathResolver;

    private ArchiveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
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
            return doExecute();
        }
        catch ( MoveNodeException e )
        {
            throw new ArchiveContentException( e.getMessage(), ContentNodeHelper.translateNodePathToContentPath( e.getPath() ) );
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private ArchiveContentsResult doExecute()
    {
        validateLocation();

        final ArchiveContentsResult.Builder result = ArchiveContentsResult.create();

        final ContentId contentId = params.getContentId();
        final NodeId nodeId = NodeId.from( contentId );

        final NodeIds descendants = nodeService.findByParent(
                FindNodesByParentParams.create().recursive( true ).parentId( nodeId ).build() )
            .getNodeIds();

        final ContentIds descendantContents = ContentNodeHelper.toContentIds( descendants );

        final ContentIds unpublishedContents = unpublish( contentId, descendantContents );
        result.addUnpublished( unpublishedContents );

        final Node originalNode = nodeService.getById( nodeId );

        rename( originalNode );
        move( nodeId );

        updatePropertiesAndCommit( nodeId, originalNode.parentPath(), originalNode.name(), descendants );

        this.nodeService.refresh( RefreshMode.SEARCH );

        return result.addArchived( contentId ).addArchived( descendantContents ).build();
    }

    @Override
    public void nodesMoved( final int count )
    {
        if ( params.getArchiveContentListener() != null )
        {
            params.getArchiveContentListener().contentArchived( count );
        }
    }

    private ContentIds unpublish( final ContentId contentId, final ContentIds descendants )
    {
        return UnpublishContentCommand.create()
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .params( UnpublishContentParams.create()
                         .contentIds( ContentIds.create()
                                          .addAll( descendants )
                                          .add( contentId )
                                          .build() )
                         .build() )
            .build()
            .execute()
            .getUnpublishedContents();
    }

    private void updatePropertiesAndCommit( final NodeId nodeId, final NodePath originalParent, final NodeName originalName,
                                            NodeIds descendants )
    {
        final RoutableNodeVersionIds.Builder routableNodeVersionIds = RoutableNodeVersionIds.create();

        final Instant now = Instant.now();
        final String archivedBy = getCurrentUser().getKey().toString();

        descendants.forEach( id -> {
            final Node updated = nodeService.update( UpdateNodeParams.create().id( id ).editor( toBeEdited -> {
                toBeEdited.data.setInstant( ARCHIVED_TIME, now );
                toBeEdited.data.setString( ARCHIVED_BY, archivedBy );
            } ).build() );
            routableNodeVersionIds.add( RoutableNodeVersionId.from( updated.id(), updated.getNodeVersionId() ) );
        } );

        final Node updated = nodeService.update( UpdateNodeParams.create().id( nodeId ).editor( toBeEdited -> {
            toBeEdited.data.setString( ORIGINAL_PARENT_PATH,
                                       ContentNodeHelper.translateNodePathToContentPath( originalParent ).toString() );
            toBeEdited.data.setString( ORIGINAL_NAME, originalName.toString() );
            toBeEdited.data.setInstant( ARCHIVED_TIME, now );
            toBeEdited.data.setString( ARCHIVED_BY, archivedBy );
        } ).build() );
        routableNodeVersionIds.add( RoutableNodeVersionId.from( updated.id(), updated.getNodeVersionId() ) );

        final String commitEntryMessage = params.getMessage() == null
            ? ContentConstants.ARCHIVE_COMMIT_PREFIX
            : String.join( ContentConstants.ARCHIVE_COMMIT_PREFIX_DELIMITER, ContentConstants.ARCHIVE_COMMIT_PREFIX, params.getMessage() );
        nodeService.commit( NodeCommitEntry.create().message( commitEntryMessage ).build(),
                            routableNodeVersionIds.build() );

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
            builder.processor( new InheritedContentDataProcessor()
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

    private void validateLocation()
    {
        final Node node = nodeService.getById( NodeId.from( params.getContentId() ) );
        if ( ContentNodeHelper.getContentRootName( node.path() ).equals( ArchiveConstants.ARCHIVE_ROOT_NAME ) )
        {
            throw new ArchiveContentException( String.format( "content [%s] is archived already", params.getContentId() ),
                                               ContentNodeHelper.translateNodePathToContentPath( node.path() ) );
        }
    }

    private User getCurrentUser()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ArchiveContentParams params;

        private Builder( final ArchiveContentParams params )
        {
            this.params = params;
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

        private static final int MAX_NAME_SIZE = 50;

        private NodePath buildArchivedPath( final Node node )
        {
            NodePath newPath;
            String newName = null;
            do
            {
                newName = newName == null ? node.name().toString() : node.name() + " " + DATE_TIME_FORMATTER.format( Instant.now() );
                newPath = new NodePath( ArchiveConstants.ARCHIVE_ROOT_PATH, NodeName.from( trim( newName ) ) );

            }
            while ( nodeService.nodeExists( newPath ) || !newName.equals( node.name().toString() ) &&
                nodeService.nodeExists( new NodePath( node.parentPath(), NodeName.from( newName ) ) ) );

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
