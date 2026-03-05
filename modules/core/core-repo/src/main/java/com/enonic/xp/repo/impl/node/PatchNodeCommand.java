package com.enonic.xp.repo.impl.node;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.Permission;

public final class PatchNodeCommand
    extends AbstractNodeCommand
{
    private final PatchNodeParams params;

    private final BinaryService binaryService;

    private final Branches branches;

    private final PatchNodeResult.Builder results;

    private final NodePatchCache<Node> patchedVersionsCache = new NodePatchCache<>();

    private PatchNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
        this.results = PatchNodeResult.create();
        this.branches = params.getBranches().isEmpty() ? Branches.from( ContextAccessor.current().getBranch() ) : params.getBranches();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PatchNodeResult execute()
    {
        final Branch contextBranch = ContextAccessor.current().getBranch();
        Preconditions.checkState( this.branches.contains( contextBranch ),
                                  "Current (source) branch '%s' is not in the list of branches for patch: %s", contextBranch,
                                  this.branches );

        final Node persistedNode = params.getId() != null ? doGetById( params.getId() ) : doGetByPath( params.getPath() );
        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Node not found: " + Objects.requireNonNullElse( params.getId(), params.getPath() ) );
        }

        this.results.nodeId( persistedNode.id() );
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.MODIFY, persistedNode );
        if ( this.branches.getSize() > 1 )
        {
            NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.PUBLISH, persistedNode );
        }

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).build();

        doPatchNode( persistedNode.id(), internalContext );

        refresh( params.getRefresh() );

        return results.build();
    }

    private void doPatchNode( NodeId nodeId, InternalContext internalContext )
    {
        final Map<Branch, NodeVersion> activeNodeMap = getActiveNodes( nodeId );

        for ( Map.Entry<Branch, NodeVersion> versionEntry : activeNodeMap.entrySet() )
        {
            patchInBranch( versionEntry.getValue(),
                           InternalContext.create( internalContext ).branch( versionEntry.getKey() ).build() );
        }
    }

    private void patchInBranch( final NodeVersion activeNodeVersion, final InternalContext internalContext )
    {
        final NodeVersionId nodeVersionId = activeNodeVersion.getNodeVersionId();
        final NodeStoreVersion nodeStoreVersion =
            this.nodeStorageService.getNodeVersion( activeNodeVersion.getNodeVersionKey(), internalContext );

        final Node persistedNode = NodeFactory.create( nodeStoreVersion, activeNodeVersion );
        if ( !NodePermissionsResolver.hasPermission( internalContext.getPrincipalKeys(), Permission.MODIFY,
                                                                              persistedNode.getPermissions() ) )
        {
            return;
        }

        final NodePatchCache.Entry<Node> cachedNewVersion = patchedVersionsCache.get( nodeVersionId );

        if ( cachedNewVersion != null )
        {
            this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( cachedNewVersion.version() ),
                                          cachedNewVersion.originBranch(), internalContext );

            results.addResult( internalContext.getBranch(), Node.create( cachedNewVersion.data() ).build() );
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
                final NodeVersion existingVersion = this.nodeStorageService.getVersion( persistedNode.getNodeVersionId(), internalContext );
                results.addResult( internalContext.getBranch(), persistedNode );
                final NodeVersionData data = new NodeVersionData( persistedNode, existingVersion );
                patchedVersionsCache.put( nodeVersionId, internalContext.getBranch(), data.version(), data.node() );
            }
            else
            {
                final Node updatedNode = Node.create( editedNode ).timestamp( Millis.now() ).attachedBinaries( updatedBinaries ).build();
                final NodeVersionData storedData =
                    this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode, params.getVersionAttributes() ),
                                                   internalContext );
                results.addResult( internalContext.getBranch(), storedData.node() );
                patchedVersionsCache.put( nodeVersionId, internalContext.getBranch(), storedData.version(), storedData.node() );
            }
        }
    }

    private Map<Branch, NodeVersion> getActiveNodes( NodeId nodeId )
    {
        return GetActiveNodeVersionsCommand.create()
            .nodeId( nodeId )
            .branches( this.branches )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute()
            .getNodeVersions();
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
            Objects.requireNonNull( params, "params cannot be null" );
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
