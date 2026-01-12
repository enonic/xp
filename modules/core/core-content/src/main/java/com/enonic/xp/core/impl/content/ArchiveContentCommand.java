package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.ArchiveContentException;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;

final class ArchiveContentCommand
    extends AbstractContentCommand
{
    private final ArchiveContentParams params;

    private final PathResolver pathResolver;

    private final boolean stopInherit;

    private ArchiveContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.stopInherit = builder.stopInherit;
        this.pathResolver = new PathResolver();
    }

    public static Builder create( final ArchiveContentParams params )
    {
        return new Builder( params );
    }

    ArchiveContentsResult execute()
    {
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
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private ArchiveContentsResult doExecute()
    {
        final ContentId contentId = params.getContentId();
        final NodeId nodeId = NodeId.from( contentId );
        final Node originalNode = nodeService.getById( nodeId );

        validateLocation( originalNode );

        final NodeIds descendants =
            nodeService.findByParent( FindNodesByParentParams.create().recursive( true ).parentPath( originalNode.path() ).build() )
                .getNodeIds();

        final ContentIds descendantContents = ContentNodeHelper.toContentIds( descendants );

        final ContentIds unpublishedContents = unpublish( contentId, descendantContents );

        final MoveNodeResult moveNodeResult = rename( originalNode );

        commit( moveNodeResult.getMovedNodes().stream().map( MoveNodeResult.MovedNode::getNode ).collect( Nodes.collector() ) );

        this.nodeService.refresh( RefreshMode.SEARCH );

        return ArchiveContentsResult.create()
            .addArchived( contentId )
            .addArchived( descendantContents )
            .addUnpublished( unpublishedContents )
            .build();
    }

    private ContentIds unpublish( final ContentId contentId, final ContentIds descendants )
    {
        return UnpublishContentCommand.create()
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .eventPublisher( eventPublisher )
            .params(
                UnpublishContentParams.create().contentIds( ContentIds.create().addAll( descendants ).add( contentId ).build() ).build() )
            .build()
            .execute()
            .getUnpublishedContents();
    }

    private NodeDataProcessor updateProperties( final ContentPath originalPath )
    {
        final Instant now = Instant.now();
        final String archivedBy = getCurrentUserKey().toString();

        return ( data, nodePath ) -> {
            var toBeEdited = data.copy();
            if ( nodePath.getParentPath().equals( ArchiveConstants.ARCHIVE_ROOT_PATH ) )
            {
                toBeEdited.setString( ORIGINAL_PARENT_PATH, originalPath.getParentPath().toString() );
                toBeEdited.setString( ORIGINAL_NAME, originalPath.getName().toString() );
            }
            toBeEdited.setInstant( ARCHIVED_TIME, now );
            toBeEdited.setString( ARCHIVED_BY, archivedBy );
            return toBeEdited;
        };
    }

    private void commit( final Nodes nodes )
    {
        final String commitEntryMessage = params.getMessage() == null
            ? ContentConstants.ARCHIVE_COMMIT_PREFIX
            : String.join( ContentConstants.ARCHIVE_COMMIT_PREFIX_DELIMITER, ContentConstants.ARCHIVE_COMMIT_PREFIX, params.getMessage() );

        nodeService.commit( CommitNodeParams.create()
                                .nodeCommitEntry( NodeCommitEntry.create().message( commitEntryMessage ).build() )
                                .nodeVersionIds( nodes.stream().map( Node::getNodeVersionId ).collect( NodeVersionIds.collector() ) )
                                .build() );
    }

    private MoveNodeResult rename( final Node node )
    {
        final ContentPath originalPath = ContentNodeHelper.translateNodePathToContentPath( node.path() );

        final NodePath newPath = pathResolver.buildArchivedPath( node.path() );

        final MoveNodeParams.Builder moveParams =
            MoveNodeParams.create().nodeId( node.id() ).newParentPath( ArchiveConstants.ARCHIVE_ROOT_PATH ).refresh( RefreshMode.ALL );

        if ( params.getArchiveContentListener() != null )
        {
            moveParams.moveListener( this.params.getArchiveContentListener()::contentArchived );
        }

        if ( !newPath.getName().equals( node.name() ) )
        {
            moveParams.newName( newPath.getName() );
        }

        final var processors = CompositeNodeDataProcessor.create().add( updateProperties( originalPath ) );
        if ( stopInherit )
        {
            processors.add( InheritedContentDataProcessor.ALL );
        }
        moveParams.processor( processors.build() );

        moveParams.versionAttributes( ContentAttributesHelper.versionHistoryAttrNoVacuum( ContentAttributesHelper.ARCHIVE_ATTR ) );

        return nodeService.move( moveParams.build() );
    }

    private void validateLocation( final Node node )
    {
        if ( ContentNodeHelper.inArchive( node.path() ) )
        {
            throw new ArchiveContentException( String.format( "content [%s] is archived already", node.id() ),
                                               ContentNodeHelper.translateNodePathToContentPath( node.path() ) );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ArchiveContentParams params;

        private boolean stopInherit = true;

        private Builder( final ArchiveContentParams params )
        {
            this.params = params;
        }

        public Builder stopInherit( final boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public ArchiveContentCommand build()
        {
            validate();
            return new ArchiveContentCommand( this );
        }
    }

    private final class PathResolver
    {
        private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern( "HH-mm-ss-SSS" ).withZone( ZoneId.systemDefault() );

        private static final String DELIMITER = "...";

        private static final int MAX_NAME_SIZE = 50;

        private NodePath buildArchivedPath( final NodePath nodePath )
        {
            final String currentNodeName = nodePath.getName().toString();

            NodePath newPath;
            String newName = null;
            do
            {
                newName = newName == null ? currentNodeName : currentNodeName + " " + DATE_TIME_FORMATTER.format( Instant.now() );
                newPath = new NodePath( ArchiveConstants.ARCHIVE_ROOT_PATH, abbreviate( newName ) );
            }
            while ( nodeService.nodeExists( newPath ) || !newName.equals( currentNodeName ) &&
                nodeService.nodeExists( new NodePath( newPath.getParentPath(), NodeName.from( newName ) ) ) );

            return newPath;
        }

        private static NodeName abbreviate( final String value )
        {
            String name = value;
            if ( name.length() > MAX_NAME_SIZE )
            {
                name = value.substring( 0, MAX_NAME_SIZE - 7 - DELIMITER.length() + 1 ) + DELIMITER + value.substring( value.length() - 7 );
            }
            return NodeName.from( name );
        }
    }

}
