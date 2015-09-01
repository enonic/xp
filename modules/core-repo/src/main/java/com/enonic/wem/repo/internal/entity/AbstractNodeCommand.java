package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexServiceInternal;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

abstract class AbstractNodeCommand
{
    static final OrderExpressions DEFAULT_ORDER_EXPRESSIONS =
        OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) );

    final IndexServiceInternal indexServiceInternal;

    final NodeDao nodeDao;

    final BranchService branchService;

    final VersionService versionService;

    final QueryService queryService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexServiceInternal = builder.indexServiceInternal;
        this.nodeDao = builder.nodeDao;
        this.branchService = builder.branchService;
        this.versionService = builder.versionService;
        this.queryService = builder.queryService;
    }

    Node updateNodeMetadata( final Node node )
    {
        return StoreNodeCommand.create( this ).
            node( node ).
            updateMetadataOnly( true ).
            build().
            execute();
    }

    Node doStoreNode( final Node node )
    {
        return StoreNodeCommand.create( this ).
            node( node ).
            build().
            execute();
    }

    Node doMoveNode( final NodePath newParentPath, final NodeName nodeName, final NodeId nodeId )
    {
        return MoveNodeCommand.create( this ).
            id( nodeId ).
            newParent( newParentPath ).
            newNodeName( nodeName ).
            build().
            execute();
    }

    Node doGetByPath( final NodePath path, final boolean resolveHasChild )
    {
        return GetNodeByPathCommand.create( this ).
            nodePath( path ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }

    Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create( this ).
            id( id ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }

    Nodes doGetByIds( final NodeIds ids, final OrderExpressions orderExprs, final boolean resolveHasChild )
    {
        return GetNodesByIdsCommand.create( this ).
            ids( ids ).
            orderExpressions( orderExprs ).
            resolveHasChild( resolveHasChild ).
            build().
            execute();
    }


    Node doCreateNode( final CreateNodeParams params, final BlobStore binaryBlobStore )
    {
        return CreateNodeCommand.create( this ).
            params( params ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doCreateNode( final CreateNodeParams params, final BlobStore binaryBlobStore, final Instant timestamp )
    {
        return CreateNodeCommand.create( this ).
            params( params ).
            timestamp( timestamp ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }


    Node doUpdateNode( final UpdateNodeParams params, final BlobStore binaryBlobStore )
    {
        return UpdateNodeCommand.create( this ).
            params( params ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create( this ).
            nodeId( nodeId ).
            build().
            execute();
    }

    FindNodesByParentResult doFindNodesByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create( this ).
            params( params ).
            build().
            execute();
    }

    protected PrincipalKey getCurrentPrincipalKey()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        return authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();
    }

    AccessControlList evaluatePermissions( final NodePath parentPath, final boolean inheritPermissions,
                                           final AccessControlList permissions )
    {
        if ( !inheritPermissions )
        {
            return permissions;
        }
        else
        {
            final Node node = NodeHelper.runAsAdmin( () -> doGetByPath( parentPath, false ) );
            if ( node == null || node.getPermissions().isEmpty() )
            {
                throw new RuntimeException( "Could not evaluate permissions for node [" + parentPath.toString() + "]" );
            }
            return node.getPermissions();
        }
    }

    protected boolean canRead( final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return node.getPermissions().isAllowedFor( authInfo.getPrincipals(), Permission.READ );
    }

    public static abstract class Builder<B extends Builder>
    {
        IndexServiceInternal indexServiceInternal;

        NodeDao nodeDao;

        BranchService branchService;

        VersionService versionService;

        QueryService queryService;

        Builder()
        {
        }

        Builder( final AbstractNodeCommand source )
        {
            this.indexServiceInternal = source.indexServiceInternal;
            this.nodeDao = source.nodeDao;
            this.branchService = source.branchService;
            this.queryService = source.queryService;
            this.versionService = source.versionService;
        }

        @SuppressWarnings("unchecked")
        public B indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B branchService( final BranchService branchService )
        {
            this.branchService = branchService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( indexServiceInternal, "indexService not set" );
            Preconditions.checkNotNull( versionService, "versionService not set" );
            Preconditions.checkNotNull( nodeDao, "nodeDao not set" );
            Preconditions.checkNotNull( branchService, "branchService not set" );
            Preconditions.checkNotNull( queryService, "queryService not set" );
        }
    }
}
