package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PatchVersionParams;
import com.enonic.xp.node.PatchVersionResult;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchVersionFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StoreNodeBranchParams;
import com.enonic.xp.repo.impl.storage.StoreNodeVersionParams;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.requireContextUserPermissionOrAdmin;

public class PatchVersionCommand
    extends AbstractNodeCommand
{
    private final PatchVersionParams params;

    private final List<PatchVersionResult> results;

    private PatchVersionCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
        results = new ArrayList<>();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<PatchVersionResult> execute()
    {
        patchVersion( params.getVersionId() );

        return results;
    }

    private void patchVersion( final NodeVersionId versionId )
    {
        final PatchVersionResult patchVersionResult = onlyPatchVersion( versionId );

        // if need to patch children (moved or node permissions changed)
        // patchVersion and patchBranches for node children

        patchVersionResult.getNodeBranchEntries().forEach( ( branch, nodeBranchEntry ) -> {
            final SearchResult result = this.nodeSearchService.query( NodeBranchQuery.create()
                                                                          .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                          .query( QueryExpr.from( CompareExpr.create(
                                                                              FieldExpr.from( BranchIndexPath.PATH.getPath() ),
                                                                              CompareExpr.Operator.LIKE, ValueExpr.string(
                                                                                  nodeBranchEntry.getNodePath().toString() + "/*" ) ) ) )
                                                                          .addOrderBy(
                                                                              FieldOrderExpr.create( BranchIndexPath.PATH.getPath(),
                                                                                                     OrderExpr.Direction.ASC ) )
                                                                          .build(), ContextAccessor.current().getRepositoryId() );

            result.getHits()
                .forEach( searchHit -> patchVersion(
                    NodeVersionId.from( searchHit.getReturnValues().getSingleValue( BranchIndexPath.VERSION_ID.getPath() ) ) ) );

        } );
    }

    private PatchVersionResult onlyPatchVersion( final NodeVersionId versionId )
    {
        final Node persistedNode = nodeStorageService.get( params.getVersionId(), InternalContext.from( ContextAccessor.current() ) );

        Preconditions.checkArgument( persistedNode != null, "Node version was not found: " + params.getVersionId() );

        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );

        checkPermissions( persistedNode, editableNode );

        final Node editedNode = editableNode.build();

        final NodeVersionKey nodeVersionKey = patchVersion( editedNode );
        final Map<Branch, NodeBranchEntry> nodeBranchEntries = patchBranches( nodeVersionKey );

        results.add( PatchVersionResult.create()
                         .nodePath( editedNode.path() )
                         .nodeVersionKey( nodeVersionKey )
                         .nodeBranchEntries( nodeBranchEntries )
                         .build() );
    }

    private void checkPermissions( final Node persistedNode, final EditableNode editableNode )
    {
        requireContextUserPermissionOrAdmin( Permission.MODIFY, persistedNode );

        if ( editableNode.inheritPermissions != persistedNode.inheritsPermissions() ||
            !persistedNode.getPermissions().equals( editableNode.permissions ) )
        {
            requireContextUserPermissionOrAdmin( Permission.WRITE_PERMISSIONS, persistedNode );
        }
    }

    private NodeVersionKey patchVersion( final Node editedNode )
    {
        final NodeVersion editedNodeVersion = NodeVersion.from( editedNode );

        return nodeStorageService.storeVersion( StoreNodeVersionParams.create()
                                                    .nodeVersion( editedNodeVersion )
                                                    .nodeVersionId( params.getVersionId() )
                                                    .nodeId( editedNode.id() )
                                                    .nodePath( editedNode.path() )
//                                             .timestamp(  )
//                                             .nodeCommitId( node ).
                                                    .build(), InternalContext.from( ContextAccessor.current() ) );
    }

    private Map<Branch, NodeBranchEntry> patchBranches( final NodeVersionKey nodeVersionKey )
    {
        final CompareExpr compareExpr = CompareExpr.create( FieldExpr.from( BranchIndexPath.VERSION_ID.getPath() ), CompareExpr.Operator.EQ,
                                                            ValueExpr.string( params.getVersionId().toString() ) );

        final SearchResult searchResult = this.nodeSearchService.query(
            NodeBranchQuery.create().query( QueryExpr.from( compareExpr ) ).size( NodeSearchService.GET_ALL_SIZE_FLAG ).build(),
            ContextAccessor.current().getRepositoryId() );

        return searchResult.getHits()
            .stream()
            .map( hit -> {
                final NodeBranchEntry nodeBranchEntry = NodeBranchVersionFactory.create( hit.getReturnValues() );

                final String branch = hit.getReturnValues().getSingleValue( BranchIndexPath.BRANCH_NAME.getPath() ).toString();

                final StoreNodeBranchParams storeNodeBranchParams = StoreNodeBranchParams.create()
                    .nodeVersionKey( nodeVersionKey )
                    .nodeVersionId( params.getVersionId() )
                    .timestamp( nodeBranchEntry.getTimestamp() )
                    .nodePath( nodeBranchEntry.getNodePath() )
                    .nodeId( nodeBranchEntry.getNodeId() )
                    .build();

                nodeStorageService.storeBranch( storeNodeBranchParams, InternalContext.create( ContextAccessor.current() )
                    .branch( Branch.from( branch ) )
                    .build() );

                return Map.entry( Branch.from( branch ), storeNodeBranchParams );
            } )
            .collect( Collectors.toMap( Map.Entry::getKey, entry -> NodeBranchEntry.create()
                .nodeVersionKey( entry.getValue().getNodeVersionKey() )
                .nodeVersionId( entry.getValue().getNodeVersionId() )
                .timestamp( entry.getValue().getTimestamp() )
                .nodePath( entry.getValue().getNodePath() )
                .nodeId( entry.getValue().getNodeId() )
                .build() ) );
    }

    private void verifyNodeProperties( final Node node )
    {
        Preconditions.checkArgument( node.id() != null, "NodeId must be set when loading node" );
        Preconditions.checkArgument( node.name() != null, "Node name must be set when loading node" );
        Preconditions.checkArgument( node.isRoot() || node.parentPath() != null, "Node parentPath must be set when loading node" );
        Preconditions.checkArgument( node.getTimestamp() != null, "Node timestamp must be set when loading node" );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private PatchVersionParams params;

        private Builder()
        {
        }

        public Builder params( final PatchVersionParams val )
        {
            params = val;
            return this;
        }

        public PatchVersionCommand build()
        {
            return new PatchVersionCommand( this );
        }
    }
}
