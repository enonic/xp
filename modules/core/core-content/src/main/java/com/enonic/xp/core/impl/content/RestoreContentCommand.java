package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.archive.RestoreContentException;
import com.enonic.xp.archive.RestoreContentListener;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;

import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_BY;
import static com.enonic.xp.content.ContentPropertyNames.ARCHIVED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_NAME;
import static com.enonic.xp.content.ContentPropertyNames.ORIGINAL_PARENT_PATH;
import static com.google.common.base.Strings.nullToEmpty;

final class RestoreContentCommand
    extends AbstractContentCommand
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
            throw new ContentAccessException( e );
        }
    }

    private RestoreContentsResult doExecute()
    {
        final Node nodeToRestore = nodeService.getById( NodeId.from( params.getContentId() ) );

        validateLocation( nodeToRestore );

        final NodePath parentPathToRestore = getParentPathToRestore( nodeToRestore );
        final NodeName originalSourceName = getOriginalSourceName( nodeToRestore );

        final RestoreContentsResult.Builder result = RestoreContentsResult.create();

        final MoveNodeResult moveNodeResult = rename( nodeToRestore, parentPathToRestore, originalSourceName );

        commit( moveNodeResult.getMovedNodes().stream().map( MoveNodeResult.MovedNode::getNode ).collect( Nodes.collector() ) );

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

    private MoveNodeResult rename( final Node nodeToRestore, final NodePath parentPathToRestore, final NodeName originalSourceName )
    {
        final NodeName newNodeName = buildName( parentPathToRestore, originalSourceName, nodeToRestore );

        if ( !newNodeName.equals( nodeToRestore.name() ) )
        {
            nodeService.rename( RenameNodeParams.create().nodeId( nodeToRestore.id() ).nodeName( newNodeName ).build() );
        }
        final MoveNodeParams.Builder moveParams =
            MoveNodeParams.create().nodeId( nodeToRestore.id() ).parentNodePath( parentPathToRestore ).moveListener( this );

        final var processors = CompositeNodeDataProcessor.create().add( updateProperties() );
        if ( this.params.stopInherit() )
        {
            processors.add( InheritedContentDataProcessor.ALL );
        }
        moveParams.processor( processors.build() );

        final MoveNodeResult movedNodeResult = nodeService.move( moveParams.build() );
        final Node firstNode = movedNodeResult.getMovedNodes().getFirst().getNode();

        if ( originalSourceName != null && !originalSourceName.equals( firstNode.name() ) )
        {
            return nodeService.rename( RenameNodeParams.create()
                                             .nodeId( firstNode.id() )
                                             .nodeName( buildName( firstNode.parentPath(), originalSourceName, firstNode ) )
                                             .build() );
        }
        else
        {
            return movedNodeResult;
        }
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

    private void commit( final Nodes nodes )
    {
        final RoutableNodeVersionIds routableNodeVersionIds = nodes.stream()
            .map( n -> RoutableNodeVersionId.from( n.id(), n.getNodeVersionId() ) )
            .collect( RoutableNodeVersionIds.collector() );

        nodeService.commit( NodeCommitEntry.create().message( ContentConstants.RESTORE_COMMIT_PREFIX ).build(), routableNodeVersionIds );
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

    @Override
    public void nodesMoved( final int count )
    {
        if ( restoreContentListener != null )
        {
            restoreContentListener.contentRestored( count );
        }
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
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
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public RestoreContentCommand build()
        {
            validate();
            return new RestoreContentCommand( this );
        }
    }

    private NodeName buildName( final NodePath newParentPath, final NodeName name, final Node node )
    {
        NodeName newName = null;

        boolean nameAlreadyExist;

        do
        {
            newName = newName != null ? NodeName.from( NameValueResolver.name( newName.toString() ) ) : name;

            final NodePath targetPath = new NodePath( newParentPath, NodeName.from( newName ) );

            nameAlreadyExist = nodeService.nodeExists( targetPath ) && !nodeService.getByPath( targetPath ).id().equals( node.id() );
            if ( !newParentPath.equals( node.parentPath() ) )
            {
                nameAlreadyExist = nameAlreadyExist ||
                    !node.name().equals( newName ) && nodeService.nodeExists( new NodePath( node.parentPath(), NodeName.from( newName ) ) );
            }

        }
        while ( nameAlreadyExist );

        return NodeName.from( newName );
    }
}
