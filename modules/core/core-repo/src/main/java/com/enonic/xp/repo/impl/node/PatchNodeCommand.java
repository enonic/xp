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

        final Map<Branch, Node> activeNodeMap = getActiveNodes( this.branches );

        for ( Branch branch : this.branches )
        {
            Permission requiredPermission;

            if ( firstBranch.equals( branch ) ||
                !activeNodeMap.get( firstBranch ).getNodeVersionId().equals( persistedNode.getNodeVersionId() ) )
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
        final Map<Branch, Node> activeNodeMap = getActiveNodes( this.branches );

        final Map<NodeVersionId, NodeVersionData> patchedVersions = new HashMap<>(); // old version id -> new version data

        for ( Branch targetBranch : this.branches )
        {

            final NodeVersionData updatedTargetNode = patchNodeInBranch( Optional.ofNullable( activeNodeMap.get( targetBranch ) )
                                                                             .map( activeNode -> patchedVersions.get(
                                                                                 activeNode.getNodeVersionId() ) )
                                                                             .orElse( null ), targetBranch );

            if ( updatedTargetNode != null )
            {
                patchedVersions.put( activeNodeMap.get( targetBranch ).getNodeVersionId(), updatedTargetNode );
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
            this.nodeStorageService.push( List.of( NodeBranchEntry.create()
                                                       .nodeVersionId( patchedNode.node().getNodeVersionId() )
                                                       .nodePath( patchedNode.node().path() )
                                                       .nodeVersionKey( patchedNode.metadata().getNodeVersionKey() )
                                                       .nodeId( patchedNode.node().id() )
                                                       .timestamp( patchedNode.node().getTimestamp() )
                                                       .build() ), branch, l -> {
            }, internalContext );

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

            return this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode ), internalContext );
        }
    }

    private Map<Branch, Node> getActiveNodes( final Branches branches )
    {
        final Map<Branch, Node> result = new HashMap<>();

        branches.forEach( branch -> result.put( branch, this.getPersistedNode( branch ) ) );

        result.values()
            .stream()
            .filter( Objects::nonNull )
            .findAny()
            .orElseThrow( () -> new NodeNotFoundException( "No active node found" ) );

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
