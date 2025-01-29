package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public final class PatchNodeCommand
    extends AbstractNodeCommand
{
    private final PatchNodeParams params;

    private PatchNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PatchNodeResult execute()
    {
        return doPatchNode( params.getId() );
    }

    private PatchNodeResult doPatchNode( final NodeId nodeId )
    {
        final PatchNodeResult.Builder result = PatchNodeResult.create().nodeId( nodeId );

        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( nodeId, params.getBranches() );

        final Map<NodeVersionId, NodeVersionMetadata> patchedVersions = new HashMap<>(); // old version id -> new version metadata

        activeVersionMap.keySet().forEach( targetBranch -> {

            final NodeVersionData updatedTargetNode =
                patchNodeInBranch( nodeId, patchedVersions.get( activeVersionMap.get( targetBranch ).getNodeVersionId() ), targetBranch );

            if ( updatedTargetNode != null )
            {
                patchedVersions.put( activeVersionMap.get( targetBranch ).getNodeVersionId(), updatedTargetNode.nodeVersionMetadata() );
            }

            result.addResult( targetBranch, updatedTargetNode != null ? updatedTargetNode.node() : null );
        } );

        return result.build();
    }

    private NodeVersionData patchNodeInBranch( final NodeId nodeId, final NodeVersionMetadata patchedVersionMetadata, final Branch branch )
    {
        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        final Node persistedNode = nodeStorageService.get( nodeId, internalContext );

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

            //TODO: refresh ?

            return new NodeVersionData( nodeStorageService.get( nodeId, internalContext ), patchedVersionMetadata );
        }
        else
        {
            final EditableNode toBeEdited = new EditableNode( persistedNode );

            params.getEditor().edit( toBeEdited );

            final Node patchedNode = Node.create( toBeEdited.build() ).timestamp( Instant.now( CLOCK ) ).build();

            return this.nodeStorageService.store( patchedNode, internalContext );
        }
    }

    private Map<Branch, NodeVersionMetadata> getActiveNodeVersions( final NodeId nodeId, final Branches branches )
    {
        return params.getBranches() != null ? GetActiveNodeVersionsCommand.create( this )
            .nodeId( nodeId )
            .branches( branches )
            .build()
            .execute()
            .getNodeVersions() : Map.of();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private PatchNodeParams params;

        private Builder()
        {
            super();
        }

        public Builder params( final PatchNodeParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public PatchNodeCommand build()
        {
            this.validate();
            return new PatchNodeCommand( this );
        }
    }
}
