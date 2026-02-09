package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class PushNodesCommand
    extends AbstractNodeCommand
{
    private final PushNodeParams params;

    private final PushNodesListener pushListener;

    private PushNodesCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.pushListener = Objects.requireNonNullElse( params.getPushListener(), c -> {
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PushNodesResult execute()
    {
        refresh( RefreshMode.ALL );

        final InternalContext internalContext =
            InternalContext.create( ContextAccessor.current() ).searchPreference( SearchPreference.PRIMARY ).build();

        NodeIds.Builder allIdsBuilder = NodeIds.create().addAll( params.getIds() );

        final NodeComparisons comparisons = getNodeComparisons( params.getIds() );

        final SingleRepoSearchSource targetSearchSource = SingleRepoSearchSource.from( targetContext() );
        for ( NodeComparison comparison : comparisons )
        {
            if ( comparison.getCompareStatus() == NodeCompareStatus.MOVED )
            {
                final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query( NodeQuery.create()
                                                                                            .query( QueryExpr.from( CompareExpr.like(
                                                                                                FieldExpr.from( NodeIndexPath.PATH ),
                                                                                                ValueExpr.string(
                                                                                                    comparison.getTargetPath() +
                                                                                                        "/*" ) ) ) )
                                                                                            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                                            .build(), targetSearchSource ).getIds() );
                allIdsBuilder.addAll( childrenIds );
            }
        }

        final NodeIds allIds = allIdsBuilder.build();
        final NodeComparisons allComparisons = getNodeComparisons( allIds );

        final NodeBranchEntries nodeBranchEntries = this.nodeStorageService.getBranchNodeVersions( allIds, internalContext );

        final PushNodesResult.Builder builder = PushNodesResult.create();
        List<SuccessfulPush> toPushEntries = new ArrayList<>();

        final List<NodeBranchEntry> list =
            nodeBranchEntries.stream().sorted( Comparator.comparing( NodeBranchEntry::getNodePath ) ).collect( Collectors.toList() );

        final Set<NodePath> alreadyAdded = new HashSet<>();
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        for ( final NodeBranchEntry branchEntry : list )
        {
            final NodeComparison comparison = allComparisons.get( branchEntry.getNodeId() );

            if ( !authInfo.hasRole( RoleKeys.ADMIN ) )
            {
                final AccessControlList nodePermissions =
                    this.nodeStorageService.getNodePermissions( branchEntry.getNodeVersionKey(), internalContext );
                if ( !NodePermissionsResolver.userHasPermission( authInfo, Permission.PUBLISH, nodePermissions ) )
                {
                    builder.add(
                        PushNodeResult.failure( branchEntry.getNodeId(), branchEntry.getNodePath(), PushNodeResult.Reason.ACCESS_DENIED ) );
                    pushListener.nodesPushed( 1 );
                    continue;
                }
            }

            final NodeCompareStatus compareStatus = comparison.getCompareStatus();

            if ( compareStatus == NodeCompareStatus.EQUAL )
            {
                final NodeBranchEntry processed = processBeforePush( branchEntry, internalContext );
                toPushEntries.add( new SuccessfulPush( processed, comparison.getTargetPath() ) );
                alreadyAdded.add( branchEntry.getNodePath() );
                builder.add( PushNodeResult.success( processed.getNodeId(), processed.getVersionId(), processed.getNodePath(),
                                                     comparison.getTargetPath() ) );

                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( ( NodeCompareStatus.NEW == compareStatus || NodeCompareStatus.MOVED == compareStatus ) &&
                targetAlreadyExists( branchEntry.getNodePath(), comparisons ) )
            {
                builder.add(
                    PushNodeResult.failure( branchEntry.getNodeId(), branchEntry.getNodePath(), PushNodeResult.Reason.ALREADY_EXIST ) );
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( !alreadyAdded.contains( branchEntry.getNodePath().getParentPath() ) && !targetParentExists( branchEntry.getNodePath() ) )
            {
                builder.add(
                    PushNodeResult.failure( branchEntry.getNodeId(), branchEntry.getNodePath(), PushNodeResult.Reason.PARENT_NOT_FOUND ) );

                pushListener.nodesPushed( 1 );
                continue;
            }

            final NodeBranchEntry processed = processBeforePush( branchEntry, internalContext );
            toPushEntries.add( new SuccessfulPush( processed, comparison.getTargetPath() ) );
            alreadyAdded.add( branchEntry.getNodePath() );
            builder.add( PushNodeResult.success( processed.getNodeId(), processed.getVersionId(), processed.getNodePath(),
                                                 comparison.getTargetPath() ) );
        }

        final PushNodesResult result = builder.build();

        final InternalContext targetContext =
            InternalContext.create( internalContext ).branch( params.getTarget() ).build();

        for ( SuccessfulPush toPush : toPushEntries )
        {
            this.nodeStorageService.push( toPush.entry, internalContext.getBranch(), targetContext );
            if ( toPush.originalPath != null && !toPush.originalPath.equals( toPush.entry.getNodePath() ) )
            {
                this.nodeStorageService.invalidatePath( toPush.originalPath, targetContext );
            }
            pushListener.nodesPushed( 1 );
        }
        refresh( RefreshMode.ALL );

        return result;
    }

    private NodeComparisons getNodeComparisons( final NodeIds nodeIds )
    {
        return CompareNodesCommand.create()
            .nodeIds( nodeIds )
            .storageService( this.nodeStorageService )
            .target( params.getTarget() )
            .build()
            .execute();
    }

    private NodeBranchEntry processBeforePush( NodeBranchEntry nbe, InternalContext internalContext )
    {
        if ( params.getProcessor() == null )
        {
            return nbe;
        }
        final NodeStoreVersion version = this.nodeStorageService.getNodeVersion( nbe.getNodeVersionKey(), internalContext );

        final PropertyTree processedData = params.getProcessor().process( version.getData(), nbe.getNodePath() );
        if ( processedData.equals( version.getData() ) )
        {
            return nbe;
        }

        final Node changedNode = NodeFactory.create( version )
            .name( nbe.getNodePath().getName() )
            .parentPath( nbe.getNodePath().getParentPath() )
            .data( processedData )
            .timestamp( Instant.now( CLOCK ) )
            .build();

        final NodeVersionData stored = this.nodeStorageService.store( StoreNodeParams.newVersion( changedNode ), internalContext );

        return NodeBranchEntry.fromNodeVersionMetadata( stored.metadata() );
    }

    private boolean targetAlreadyExists( final NodePath nodePath, final NodeComparisons comparisons )
    {
        //Checks if a node exist
        final NodeBranchEntry parentNodeBranchEntry = targetContext().callWith(
            () -> this.nodeStorageService.getBranchNodeVersion( nodePath, InternalContext.from( ContextAccessor.current() ) ) );

        //If the node does not exist, returns false
        if ( parentNodeBranchEntry == null )
        {
            return false;
        }

        //Else, if the existing node is being moved during the current push, returns false
        final NodeComparison nodeComparison = comparisons.get( parentNodeBranchEntry.getNodeId() );
        return nodeComparison == null || NodeCompareStatus.MOVED != nodeComparison.getCompareStatus();
    }

    private boolean targetParentExists( final NodePath nodePath )
    {
        if ( nodePath.isRoot() || nodePath.getParentPath().isRoot() )
        {
            return true;
        }

        return targetContext().callWith( CheckNodeExistsCommand.create( this ).nodePath( nodePath.getParentPath() ).build()::execute );
    }

    private Context targetContext()
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .branch( params.getTarget() )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();
    }

    private record SuccessfulPush(NodeBranchEntry entry, NodePath originalPath)
    {
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private PushNodeParams params;

        Builder()
        {
            super();
        }

        public Builder params( final PushNodeParams params )
        {
            this.params = params;
            return this;
        }

        public PushNodesCommand build()
        {
            validate();
            return new PushNodesCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannon be null" );
        }
    }
}
