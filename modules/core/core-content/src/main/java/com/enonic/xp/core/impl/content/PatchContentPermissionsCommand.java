package com.enonic.xp.core.impl.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.PatchPermissionsParams;
import com.enonic.xp.node.PatchPermissionsResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.security.acl.AccessControlList;

public class PatchContentPermissionsCommand
    extends AbstractContentCommand
{
    private final PatchPermissionsParams params;

    private PatchContentPermissionsCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    private static Context context( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).branch( branch ).build();
    }

    public List<PatchPermissionsResult> execute()
    {
        final List<PatchPermissionsResult> result = doPatchPermissions( params.getNodeId(), params.getPermissions() );

        commitNewMasterVersions( result );

        return result;
    }

    private void saveResult( Map<NodeId, PatchPermissionsResult.Builder> resultBuilder, NodeId nodeId, Branch branch, Boolean succeed )
    {
        resultBuilder.compute( nodeId, ( id, result ) -> result != null
            ? result.branchResult( branch, succeed )
            : PatchPermissionsResult.create().nodeId( id ).branchResult( branch, succeed ) );
    }

    private List<PatchPermissionsResult> doPatchPermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final Map<NodeId, PatchPermissionsResult.Builder> resultBuilder = new HashMap<>();

        final Map<NodeId, Map<Branch, NodeVersionMetadata>> activeVersionMap = getActiveNodeVersions( nodeId );

        final ApplyNodePermissionsResult appliedInMaster = context( ContentConstants.BRANCH_MASTER ).callWith(
            () -> nodeService.applyPermissions( ApplyNodePermissionsParams.create().nodeId( nodeId ).permissions( permissions ).build() ) );

        appliedInMaster.getSucceedNodes()
            .forEach( ( node ) -> resultBuilder.computeIfAbsent( node.id(), id -> PatchPermissionsResult.create()
                .nodeId( id )
                .branchResult( ContentConstants.BRANCH_MASTER, true ) ) );
        appliedInMaster.getSkippedNodes()
            .forEach( ( node ) -> resultBuilder.computeIfAbsent( node.id(), id -> PatchPermissionsResult.create()
                .nodeId( id )
                .branchResult( ContentConstants.BRANCH_MASTER, false ) ) );

        appliedInMaster.getSucceedNodes().forEach( node -> {
            if ( activeVersionMap.get( node.id() )
                .get( ContentConstants.BRANCH_MASTER )
                .getNodeVersionId()
                .equals( activeVersionMap.get( node.id() ).get( ContentConstants.BRANCH_DRAFT ).getNodeVersionId() ) )
            {
                final PushNodesResult pushedToDraft = context( ContentConstants.BRANCH_MASTER ).callWith(
                    () -> nodeService.push( NodeIds.from( node.id() ), ContentConstants.BRANCH_DRAFT ) );

                pushedToDraft.getSuccessfulEntries()
                    .forEach( ( pushedNode ) -> resultBuilder.compute( pushedNode.getNodeBranchEntry().getNodeId(),
                                                                       ( id, result ) -> result != null
                                                                           ? result.branchResult( ContentConstants.BRANCH_DRAFT, true )
                                                                           : PatchPermissionsResult.create()
                                                                               .nodeId( id )
                                                                               .branchResult( ContentConstants.BRANCH_DRAFT, true ) ) );
                pushedToDraft.getFailedEntries()
                    .forEach( ( pushedNode ) -> resultBuilder.compute( pushedNode.getNodeBranchEntry().getNodeId(),
                                                                       ( id, result ) -> result != null
                                                                           ? result.branchResult( ContentConstants.BRANCH_DRAFT, false )
                                                                           : PatchPermissionsResult.create()
                                                                               .nodeId( id )
                                                                               .branchResult( ContentConstants.BRANCH_DRAFT, false ) ) );
            }
            else
            {
                final ApplyNodePermissionsResult appliedToDraft = context( ContentConstants.BRANCH_DRAFT ).callWith(
                    () -> nodeService.applyPermissions(
                        ApplyNodePermissionsParams.create().nodeId( node.id() ).permissions( permissions ).build() ) );

                appliedToDraft.getSucceedNodes()
                    .forEach( ( pushedNode ) -> resultBuilder.compute( pushedNode.id(),
                                                                       ( id, result ) -> result != null
                                                                           ? result.branchResult( ContentConstants.BRANCH_DRAFT, true )
                                                                           : PatchPermissionsResult.create()
                                                                               .nodeId( id )
                                                                               .branchResult( ContentConstants.BRANCH_DRAFT, true ) ) );

                appliedToDraft.getSkippedNodes()
                    .forEach( ( pushedNode ) -> resultBuilder.compute( pushedNode.id(),
                                                                       ( id, result ) -> result != null
                                                                           ? result.branchResult( ContentConstants.BRANCH_DRAFT, false )
                                                                           : PatchPermissionsResult.create()
                                                                               .nodeId( id )
                                                                               .branchResult( ContentConstants.BRANCH_DRAFT, false ) ) );
            }
        } );

        return resultBuilder.values().stream().map( PatchPermissionsResult.Builder::build ).collect( Collectors.toList() );
    }

    private Map<NodeId, Map<Branch, NodeVersionMetadata>> getActiveNodeVersions( final NodeId nodeId )
    {
        final NodeIds children = context( ContentConstants.BRANCH_MASTER ).callWith(
            () -> nodeService.findByParent( FindNodesByParentParams.create().parentId( nodeId ).build() ) ).getNodeIds();

        return Stream.concat( Stream.of( nodeId ), children.stream() )
            .map( id -> Map.entry( id, nodeService.getActiveVersions( GetActiveNodeVersionsParams.create()
                                                                          .nodeId( id )
                                                                          .branches( Branches.from( ContentConstants.BRANCH_MASTER,
                                                                                                    ContentConstants.BRANCH_DRAFT ) )
                                                                          .build() ).getNodeVersions() ) )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
    }

    private void commitNewMasterVersions( final List<PatchPermissionsResult> result )
    {
        final List<NodeId> nodesToCommit = result.stream()
            .filter( r -> r.getBranchResult().get( ContentConstants.BRANCH_MASTER ) )
            .map( PatchPermissionsResult::getNodeId )
            .collect( Collectors.toList() );

        if ( !nodesToCommit.isEmpty() )
        {
            context( ContentConstants.BRANCH_MASTER ).runWith(
                () -> nodeService.commit( NodeCommitEntry.create().message( "Patch permissions" ).build(),
                                          NodeIds.from( nodesToCommit ) ) );
        }
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private PatchPermissionsParams params;

        private Builder()
        {
        }

        public Builder params( final PatchPermissionsParams val )
        {
            params = val;
            return this;
        }

        public PatchContentPermissionsCommand build()
        {
            return new PatchContentPermissionsCommand( this );
        }
    }
}
