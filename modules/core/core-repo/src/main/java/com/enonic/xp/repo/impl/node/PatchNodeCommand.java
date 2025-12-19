package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public final class PatchNodeCommand
    extends AbstractNodeCommand
{
    private final PatchNodeParams params;

    private final BinaryService binaryService;

    private final Branches branches;

    private final PatchNodeResult.Builder results;

    private record PatchedVersionData( Branch originBranch, NodeVersionData versionData ) {}

    private PatchNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.binaryService = builder.binaryService;
        this.results = PatchNodeResult.create().nodeId( params.getId() );
        this.branches = params.getBranches().isEmpty() ? Branches.from( ContextAccessor.current().getBranch() ) : params.getBranches();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PatchNodeResult execute()
    {
        final Context context = this.branches.getSize() == 1
            ? ContextBuilder.from( ContextAccessor.current() ).branch( this.branches.first() ).build()
            : ContextAccessor.current();

        context.runWith( () -> {
            verifyBranch();
            verifyPermissions();
            doPatchNode();
        } );

        refresh( params.getRefresh() );

        return results.build();
    }

    private void verifyBranch()
    {
        Preconditions.checkState( this.branches.contains( ContextAccessor.current().getBranch() ),
                                  "Current(source) branch '%s' is not in the list of branches for patch: %s",
                                  ContextAccessor.current().getBranch(), this.branches );
    }

    private void verifyPermissions()
    {
        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).build();
        final Branch firstBranch = this.branches.first();
        final Node persistedNode = getPersistedNode( firstBranch );

        if ( this.branches.getSize() == 1 )
        {
            requirePermission( internalContext, Permission.MODIFY, persistedNode );
            return;
        }

        final Map<Branch, NodeVersionId> activeNodeMap = getActiveNodes( this.branches );

        for ( Branch branch : this.branches )
        {
            Permission requiredPermission;

            if ( branch.equals( firstBranch ) ||
                !activeNodeMap.get( firstBranch ).equals( persistedNode.getNodeVersionId() ) )
            {
                requiredPermission = Permission.MODIFY;
            }
            else
            {
                requiredPermission = Permission.PUBLISH;
            }

            requirePermission( internalContext, requiredPermission, persistedNode );
        }
    }

    private void requirePermission( final InternalContext internalContext, final Permission permission, final Node node )
    {
        if ( node == null )
        {
            throw new NodeNotFoundException( "Node not found." );
        }
        if ( !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), permission, node.getPermissions() ) )
        {
            throw new NodeAccessException( ContextAccessor.current().getAuthInfo().getUser(), node.path(), permission );
        }
    }

    private void doPatchNode()
    {
        final Map<Branch, NodeVersionId> activeNodeMap = getActiveNodes( this.branches );

        final Map<NodeVersionId, PatchedVersionData> patchedVersions = new HashMap<>(); // old version id -> new version data with origin branch

        for ( Branch targetBranch : this.branches )
        {
            final PatchedVersionData patchedData =
                Optional.ofNullable( activeNodeMap.get( targetBranch ) ).map( patchedVersions::get ).orElse( null );

            final NodeVersionData updatedTargetNode = patchNodeInBranch( patchedData, targetBranch );

            if ( updatedTargetNode != null )
            {
                patchedVersions.put( activeNodeMap.get( targetBranch ), new PatchedVersionData( targetBranch, updatedTargetNode ) );
                results.nodeId( updatedTargetNode.node().id() );
            }

            results.addResult( targetBranch, updatedTargetNode != null ? updatedTargetNode.node() : null );
        }

    }

    private NodeVersionData patchNodeInBranch( final PatchedVersionData patchedData, final Branch branch )
    {
        final Node persistedNode = getPersistedNode( branch );

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        if ( persistedNode == null || !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), Permission.MODIFY,
                                                                              persistedNode.getPermissions() ) )
        {
            return null;
        }

        if ( patchedData != null )
        {
            this.nodeStorageService.push( NodeBranchEntry.fromNodeVersionMetadata( patchedData.versionData().metadata() ),
                                          patchedData.originBranch(), internalContext );

            return patchedData.versionData();
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

            return this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode, params.getVersionAttributes() ), internalContext );
        }
    }

    private Map<Branch, NodeVersionId> getActiveNodes( final Branches branches )
    {
        final Map<Branch, NodeVersionId> result = new HashMap<>();

        for ( Branch branch : branches )
        {
            final Node persistedNode = this.getPersistedNode( branch );
            if ( persistedNode != null )
            {
                result.put( branch, persistedNode.getNodeVersionId() );
            }
        }

        if ( result.isEmpty() ) {
            throw new NodeNotFoundException( "No active node found" );
        }

        return result;
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
