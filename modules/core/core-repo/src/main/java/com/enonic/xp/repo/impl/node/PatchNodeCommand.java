package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
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
        verifyPermissionsOnCurrentBrunch();

        ContextBuilder.from( ContextAccessor.current() ).branch( this.branches.first() ).build().runWith( this::doPatchNode );

        refresh( params.getRefresh() );

        return results.build();
    }

    private void verifyPermissionsOnCurrentBrunch()
    {

        final Context context = ContextAccessor.current();
        Preconditions.checkState( this.branches.contains( context.getBranch() ),
                                  "Current(source) branch '%s' is not in the list of branches for patch: %s",
                                  ContextAccessor.current().getBranch(), this.branches );

        final Node persistedNode = getPersistedNode( context.getBranch() );

        final InternalContext internalContext = InternalContext.create( context ).build();

        requirePermission( internalContext, Permission.MODIFY, persistedNode );
        if ( this.branches.getSize() > 1 )
        {
            requirePermission( internalContext, Permission.PUBLISH, persistedNode );
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
        final Map<Branch, NodeVersionId> activeNodeMap = getActiveNodes();

        final Map<NodeVersionId, NodeVersionData> patchedVersions = new HashMap<>(); // old version id -> new version data

        for ( Branch targetBranch : this.branches )
        {
            final NodeVersionData nodeVersionData =
                Optional.ofNullable( activeNodeMap.get( targetBranch ) ).map( patchedVersions::get ).orElse( null );

            final NodeVersionData updatedTargetNode = patchNodeInBranch( nodeVersionData, targetBranch );

            if ( updatedTargetNode != null )
            {
                patchedVersions.put( activeNodeMap.get( targetBranch ), updatedTargetNode );
                results.nodeId( updatedTargetNode.node().id() );
            }

            results.addResult( targetBranch, updatedTargetNode != null ? updatedTargetNode.node() : null );
        }

    }

    private NodeVersionData patchNodeInBranch( final NodeVersionData patchedNode, final Branch branch )
    {
        final Node persistedNode = getPersistedNode( branch );

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        if ( persistedNode == null || !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), Permission.MODIFY,
                                                                              persistedNode.getPermissions() ) )
        {
            return null;
        }

        if ( patchedNode != null )
        {
            this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( patchedNode.metadata() ), this.branches.first(),
                                          internalContext );

            return patchedNode;
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

            return this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode, params.getVersionAttributes() ),
                                                  internalContext );
        }
    }

    private Map<Branch, NodeVersionId> getActiveNodes()
    {
        final Map<Branch, NodeVersionId> activeNodeMap = new HashMap<>();

        for ( Branch branch : this.branches )
        {
            final Node persistedNode = this.getPersistedNode( branch );
            if ( persistedNode != null )
            {
                activeNodeMap.put( branch, persistedNode.getNodeVersionId() );
            }
        }

        if ( activeNodeMap.isEmpty() )
        {
            throw new NodeNotFoundException( "No active node found" );
        }

        return activeNodeMap;
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
