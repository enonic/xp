package com.enonic.xp.repo.impl.node;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
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

    private final Branch sourceBranch;

    private PatchNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.sourceBranch = ContextAccessor.current().getBranch();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PatchNodeResult execute()
    {
        if ( params.getBranches().contains( this.sourceBranch ) )
        {
            throw new IllegalArgumentException( "Branches cannot contain current branch" );
        }

        final Node persistedNode = doGetById( params.getId() );

        if ( persistedNode == null )
        {
            return PatchNodeResult.create().nodeId( params.getId() ).addResult( this.sourceBranch, null ).build();
        }

        return doPatchNode( persistedNode );
    }

    private PatchNodeResult doPatchNode( final Node node )
    {
        final PatchNodeResult.Builder result = PatchNodeResult.create().nodeId( node.id() );

        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( node.id(),
                                                                                         Streams.concat( params.getBranches().stream(),
                                                                                                         Stream.of( this.sourceBranch ) )
                                                                                             .collect( Branches.collecting() ) );

        final NodeVersionData patchedOriginNode = patchNodeInBranch( node.id(), null, this.sourceBranch );

        if ( patchedOriginNode == null )
        {
            result.addResult( this.sourceBranch, null );
            return result.build();
        }

        result.addResult( this.sourceBranch, patchedOriginNode.node() );

        activeVersionMap.keySet().forEach( targetBranch -> {
            if ( targetBranch.equals( this.sourceBranch ) )
            {
                return;
            }

            final boolean isEqualToOrigin = activeVersionMap.get( targetBranch ).getNodeVersionId().equals( node.getNodeVersionId() );

            final NodeVersionData updatedTargetNode =
                patchNodeInBranch( node.id(), isEqualToOrigin ? patchedOriginNode.nodeVersionMetadata() : null, targetBranch );
            ;
            result.addResult( targetBranch,
                              isEqualToOrigin ? patchedOriginNode.node() : updatedTargetNode != null ? updatedTargetNode.node() : null );

        } );

        return result.build();
    }

    private NodeVersionData patchNodeInBranch( final NodeId nodeId, final NodeVersionMetadata patchedVersionMetadata, final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).branch( branch ).build().callWith( () -> {

            final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

            final Node persistedNode = nodeStorageService.get( nodeId, internalContext );

            if ( persistedNode == null || !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), Permission.MODIFY,
                                                                                  persistedNode.getPermissions() ) )
            {
                return null;
            }
            NodeVersionData result;

            if ( patchedVersionMetadata != null )
            {
                this.nodeStorageService.push( List.of( PushNodeEntry.create()
                                                           .nodeBranchEntry( NodeBranchEntry.create()
                                                                                 .nodeVersionId( patchedVersionMetadata.getNodeVersionId() )
                                                                                 .nodePath( patchedVersionMetadata.getNodePath() )
                                                                                 .nodeVersionKey(
                                                                                     patchedVersionMetadata.getNodeVersionKey() )
                                                                                 .nodeId( patchedVersionMetadata.getNodeId() )
                                                                                 .timestamp( patchedVersionMetadata.getTimestamp() )
                                                                                 .build() )
                                                           .build() ), branch, l -> {
                }, internalContext );
                return null;
            }
            else
            {
                final EditableNode toBeEdited = new EditableNode( persistedNode );

                params.getEditor().edit( toBeEdited );

                final Node patchedNode = Node.create( toBeEdited.build() ).timestamp( Instant.now( CLOCK ) ).build();

                result = this.nodeStorageService.store( patchedNode, internalContext );
            }
            return result;
        } );
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
