package com.enonic.xp.repo.impl.node;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PushNodesCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodeIds ids;

    private final PushNodesListener pushListener;

    private PushNodesCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.ids = builder.ids;
        this.pushListener = Objects.requireNonNullElse( builder.pushListener, c -> {
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PushNodesResult execute()
    {
        refresh( RefreshMode.ALL );

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final Context context = ContextAccessor.current();

        NodeIds.Builder allIdsBuilder = NodeIds.create().addAll( ids );

        final NodeComparisons comparisons = getNodeComparisons( ids );

        for ( NodeComparison comparison : comparisons )
        {
            if ( comparison.getCompareStatus() == CompareStatus.MOVED )
            {
                final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query( NodeQuery.create()
                                                                                            .query( QueryExpr.from( CompareExpr.like(
                                                                                                FieldExpr.from( NodeIndexPath.PATH ),
                                                                                                ValueExpr.string(
                                                                                                    comparison.getTargetPath() +
                                                                                                        "/*" ) ) ) )
                                                                                            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                                            .build(),
                                                                                        SingleRepoSearchSource.from( targetContext() ) )
                                                              .getIds() );
                allIdsBuilder.addAll( childrenIds );
            }
        }

        final NodeIds allIds = allIdsBuilder.build();
        final NodeComparisons allComparisons = getNodeComparisons( allIds );

        final NodeBranchEntries nodeBranchEntries = this.nodeStorageService.getBranchNodeVersions( allIds, internalContext );

        final PushNodesResult.Builder builder = PushNodesResult.create();

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
                    builder.addFailed( branchEntry, PushNodesResult.Reason.ACCESS_DENIED );
                    pushListener.nodesPushed( 1 );
                    continue;
                }
            }

            final CompareStatus compareStatus = comparison.getCompareStatus();

            if ( compareStatus == CompareStatus.EQUAL )
            {
                builder.addSuccess( branchEntry, comparison.getTargetPath() );
                alreadyAdded.add( branchEntry.getNodePath() );
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( ( CompareStatus.NEW == compareStatus || CompareStatus.MOVED == compareStatus ) &&
                targetAlreadyExists( branchEntry.getNodePath(), comparisons ) )
            {
                builder.addFailed( branchEntry, PushNodesResult.Reason.ALREADY_EXIST );
                pushListener.nodesPushed( 1 );
                continue;
            }

            if ( !alreadyAdded.contains( branchEntry.getNodePath().getParentPath() ) && !targetParentExists( branchEntry.getNodePath() ) )
            {
                builder.addFailed( branchEntry, PushNodesResult.Reason.PARENT_NOT_FOUND );
                pushListener.nodesPushed( 1 );
                continue;
            }

            builder.addSuccess( branchEntry, comparison.getTargetPath() );
            alreadyAdded.add( branchEntry.getNodePath() );
        }

        final PushNodesResult result = builder.build();

        this.nodeStorageService.push( result.getSuccessfulEntries(), target, pushListener, InternalContext.create( context ).build() );

        refresh( RefreshMode.ALL );

        return result;
    }

    private NodeComparisons getNodeComparisons( final NodeIds nodeIds )
    {
        return CompareNodesCommand.create()
            .nodeIds( nodeIds )
            .storageService( this.nodeStorageService )
            .target( this.target )
            .build()
            .execute();
    }

    private boolean targetAlreadyExists( final NodePath nodePath, final NodeComparisons comparisons )
    {
        //Checks if a node exist
        final NodeId nodeId =
            targetContext().callWith( () -> GetNodeIdByPathCommand.create( this ).nodePath( nodePath ).build().execute() );

        //If the node does not exist, returns false
        if ( nodeId == null )
        {
            return false;
        }

        //Else, if the existing node is being moved during the current push, returns false
        final NodeComparison nodeComparison = comparisons.get( nodeId );
        return nodeComparison == null || CompareStatus.MOVED != nodeComparison.getCompareStatus();
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
            .branch( target )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branch target;

        private NodeIds ids;

        private PushNodesListener pushListener;

        Builder()
        {
            super();
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder ids( final NodeIds nodeIds )
        {
            this.ids = nodeIds;
            return this;
        }

        public Builder pushListener( final PushNodesListener pushListener )
        {
            this.pushListener = pushListener;
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
            Objects.requireNonNull( target, "target is required" );
            Objects.requireNonNull( ids, "ids is required" );
        }
    }
}
