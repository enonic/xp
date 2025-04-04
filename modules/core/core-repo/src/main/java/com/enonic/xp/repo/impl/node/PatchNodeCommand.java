package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public final class PatchNodeCommand
    extends AbstractNodeCommand
{
    private final PatchNodeParams params;

    private final BinaryService binaryService;

    private PatchNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public PatchNodeResult execute()
    {
        return doPatchNode();
    }

    private PatchNodeResult doPatchNode()
    {
        final PatchNodeResult.Builder result = PatchNodeResult.create().nodeId( params.getId() );

        final Branches branches =
            params.getBranches().isEmpty() ? Branches.from( ContextAccessor.current().getBranch() ) : params.getBranches();

        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( branches );

        final Map<NodeVersionId, NodeVersionMetadata> patchedVersions = new HashMap<>(); // old version id -> new version metadata

        activeVersionMap.keySet().forEach( targetBranch -> {

            final NodeVersionData updatedTargetNode =
                patchNodeInBranch( patchedVersions.get( activeVersionMap.get( targetBranch ).getNodeVersionId() ), targetBranch );

            if ( updatedTargetNode != null )
            {
                patchedVersions.put( activeVersionMap.get( targetBranch ).getNodeVersionId(), updatedTargetNode.nodeVersionMetadata() );
            }

            result.addResult( targetBranch, updatedTargetNode != null ? updatedTargetNode.node() : null );
            if ( updatedTargetNode != null )
            {
                result.nodeId( updatedTargetNode.node().id() );
            }
        } );

        return result.build();
    }

    private NodeVersionData patchNodeInBranch( final NodeVersionMetadata patchedVersionMetadata, final Branch branch )
    {
        final Node persistedNode = getPersistedNode( branch );

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        if ( persistedNode == null || !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), Permission.MODIFY,
                                                                              persistedNode.getPermissions() ) )
        {
            return null;
        }

        if ( patchedVersionMetadata != null )
        {
            this.nodeStorageService.push( List.of( PushNodeEntry.create()
                                                       .nodeBranchEntry( NodeBranchEntry.create()
                                                                             .nodeVersionId( patchedVersionMetadata.getNodeVersionId() )
                                                                             .nodePath( patchedVersionMetadata.getNodePath() )
                                                                             .nodeVersionKey( patchedVersionMetadata.getNodeVersionKey() )
                                                                             .nodeId( patchedVersionMetadata.getNodeId() )
                                                                             .timestamp( patchedVersionMetadata.getTimestamp() )
                                                                             .build() )
                                                       .build() ), branch, l -> {
            }, internalContext );

            refresh( params.getRefresh() );

            return new NodeVersionData( nodeStorageService.get( persistedNode.id(), internalContext ), patchedVersionMetadata );
        }
        else
        {
            final EditableNode editableNode = new EditableNode( persistedNode );
            params.getEditor().edit( editableNode );

            final AttachedBinaries updatedBinaries = UpdatedAttachedBinariesResolver.create()
                .editableNode( editableNode )
                .persistedNode( persistedNode )
                .binaryAttachments( this.params.getBinaryAttachments() )
                .binaryService( this.binaryService )
                .build()
                .resolve();

            final Node editedNode = editableNode.build();

            if ( editedNode.equals( persistedNode ) && updatedBinaries.equals( persistedNode.getAttachedBinaries() ) )
            {
                return new NodeVersionData( persistedNode,
                                            this.nodeStorageService.getVersion( persistedNode.getNodeVersionId(), internalContext ) );
            }

            final Node updatedNode =
                Node.create( editedNode ).timestamp( Instant.now( CLOCK ) ).attachedBinaries( updatedBinaries ).build();

            final NodeVersionData result = this.nodeStorageService.store( updatedNode, internalContext );

            refresh( params.getRefresh() );

            return result;
        }
    }

    private Map<Branch, NodeVersionMetadata> getActiveNodeVersions( final Branches branches )
    {
        final NodeId nodeId = branches.stream().<NodeId>mapMulti( ( branch, consumer ) -> {
            Node node = getPersistedNode( branch );
            if ( node != null )
            {
                consumer.accept( node.id() );
            }
        } ).findFirst().orElseThrow( () -> new NodeNotFoundException( "Node not found" ) );

        return GetActiveNodeVersionsCommand.create( this ).nodeId( nodeId ).branches( branches ).build().execute().getNodeVersions();
    }

    private Node getPersistedNode( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).branch( branch ).build().callWith( () -> {
            final Node persistedNode;
            if ( params.getId() != null )
            {
                persistedNode = doGetById( params.getId() );
            }
            else
            {
                persistedNode = doGetByPath( params.getPath() );
            }
            return persistedNode;
        } );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private PatchNodeParams params;

        private BinaryService binaryService;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( final PatchNodeParams params )
        {
            this.params = params;
            return this;
        }

        public Builder binaryService( final BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
            Preconditions.checkArgument( this.params.getBranches().getSize() <= 1 || this.params.getPath() == null,
                                         "Only one branch is allowed with path" );
        }

        public PatchNodeCommand build()
        {
            this.validate();
            return new PatchNodeCommand( this );
        }
    }

}
