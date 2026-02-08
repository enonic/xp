package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;
import static com.google.common.base.Strings.nullToEmpty;

final class RestoreContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final RestoreContentParams params;

    private RestoreContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final RestoreContentParams params )
    {
        return new Builder( params );
    }

    RestoreContentsResult execute()
    {
        try
        {
            return doExecute();
        }
        catch ( MoveNodeException e )
        {
            throw new RestoreContentException( e.getMessage(), ContentNodeHelper.translateNodePathToContentPath( e.getPath() ) );
        }
        catch ( NodeAccessException e )
        {
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    private RestoreContentsResult doExecute()
    {
        final Node nodeToRestore = nodeService.getById( NodeId.from( params.getContentId() ) );

        validateLocation( nodeToRestore );

        final NodePath parentPathToRestore = getParentPathToRestore( nodeToRestore );

        final RestoreContentsResult.Builder result = RestoreContentsResult.create();

        final MoveNodeResult moveNodeResult = rename( nodeToRestore, parentPathToRestore );

        final Nodes nodes = moveNodeResult.getMovedNodes().stream().map( MoveNodeResult.MovedNode::getNode ).collect( Nodes.collector() );
        nodeService.commit( CommitNodeParams.create()
                                .nodeCommitEntry( NodeCommitEntry.create().message( ContentConstants.RESTORE_COMMIT_PREFIX ).build() )
                                .nodeVersionIds( nodes.stream().map( Node::getNodeVersionId ).collect( NodeVersionIds.collector() ) )
                                .build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        result.addRestored( ContentId.from( nodeToRestore.id() ) )
            .parentPath( ContentNodeHelper.translateNodePathToContentPath( parentPathToRestore ) );

        return result.build();
    }

    private void validateLocation( final Node node )
    {
        if ( !ContentNodeHelper.inArchive( node.path() ) )
        {
            if ( ContentConstants.CONTENT_NODE_COLLECTION.equals( node.getNodeType() ) )
            {
                throw new RestoreContentException( String.format( "Content [%s] is not archived", node.id() ),
                                                   ContentNodeHelper.translateNodePathToContentPath( node.path() ) );
            }
            else
            {
                throw ContentNotFoundException.create()
                    .contentId( params.getContentId() )
                    .repositoryId( ContextAccessor.current().getRepositoryId() )
                    .branch( ContextAccessor.current().getBranch() )
                    .contentRoot( ContentNodeHelper.getContentRoot() )
                    .build();
            }
        }
    }

    private MoveNodeResult rename( final Node nodeToRestore, final NodePath parentPathToRestore )
    {
        final NodeName originalSourceName = getOriginalSourceName( nodeToRestore );

        final NodeName newNodeName = buildName( parentPathToRestore, originalSourceName );

        final Attributes versionAttributes = layersSync
            ? ContentAttributesHelper.layersSyncAttr()
            : ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.RESTORE_ATTR );
        final MoveNodeParams.Builder moveParams = MoveNodeParams.create()
            .nodeId( nodeToRestore.id() )
            .newParentPath( parentPathToRestore )
            .newName( newNodeName )
            .versionAttributes( versionAttributes )
            .childVersionAttributes( versionAttributes )
            .refresh( RefreshMode.ALL );

        if ( params.getRestoreContentListener() != null )
        {
            moveParams.moveListener( this.params.getRestoreContentListener()::contentRestored );
        }

        final var processors = CompositeNodeDataProcessor.create().add( updateProperties() );
        if ( !layersSync )
        {
            processors.add( InheritedContentDataProcessor.ALL );
        }
        moveParams.processor( processors.build() );

        return nodeService.move( moveParams.build() );
    }

    private NodeDataProcessor updateProperties()
    {
        return ( data, nodePath ) -> {
            var toBeEdited = data.copy();
            toBeEdited.removeProperties( ORIGINAL_PARENT_PATH );
            toBeEdited.removeProperties( ORIGINAL_NAME );
            toBeEdited.removeProperties( ARCHIVED_TIME );
            toBeEdited.removeProperties( ARCHIVED_BY );
            return toBeEdited;
        };
    }

    private NodePath getParentPathToRestore( final Node node )
    {
        if ( ArchiveConstants.ARCHIVE_ROOT_PATH.equals( node.parentPath() ) )
        {
            final Property originalParentPathProperty = node.data().getProperty( ORIGINAL_PARENT_PATH );

            if ( originalParentPathProperty != null )
            {
                final String originalParentPath = originalParentPathProperty.getString();

                if ( params.getParentPath() != null )
                {
                    return ContentNodeHelper.translateContentPathToNodePath( ContentConstants.CONTENT_ROOT_PATH, params.getParentPath() );
                }
                else if ( !nullToEmpty( originalParentPath ).isBlank() )
                {
                    final NodePath parentPath = ContentNodeHelper.translateContentPathToNodePath( ContentConstants.CONTENT_ROOT_PATH,
                                                                                                  ContentPath.from(
                                                                                                      originalParentPathProperty.getValue()
                                                                                                          .asString() ) );
                    if ( nodeService.nodeExists( parentPath ) )
                    {
                        return parentPath;
                    }

                }
            }

        }

        return ContentConstants.CONTENT_ROOT_PATH;
    }

    private NodeName getOriginalSourceName( final Node node )
    {
        if ( ArchiveConstants.ARCHIVE_ROOT_PATH.equals( node.parentPath() ) )
        {
            final Property originalNameProperty = node.data().getProperty( ORIGINAL_NAME );
            if ( originalNameProperty != null )
            {
                return NodeName.from( originalNameProperty.getString() );
            }
        }
        return node.name();
    }

    private NodeName buildName( final NodePath newParentPath, final NodeName name )
    {
        NodeName newName = name;

        while ( nodeService.nodeExists( new NodePath( newParentPath, newName ) ) )
        {
            newName = NodeName.from( NameValueResolver.name( newName.toString() ) );
        }

        return newName;
    }

    static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private final RestoreContentParams params;

        private Builder( final RestoreContentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        RestoreContentCommand build()
        {
            validate();
            return new RestoreContentCommand( this );
        }
    }
}
