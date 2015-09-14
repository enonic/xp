package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexServiceInternal;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
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

    final StorageService storageService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexServiceInternal = builder.indexServiceInternal;
        this.nodeDao = builder.nodeDao;
        this.branchService = builder.branchService;
        this.versionService = builder.versionService;
        this.queryService = builder.queryService;
        this.storageService = builder.storageService;
    }

    Node doGetById( final NodeId id, final boolean resolveHasChild )
    {
        return GetNodeByIdCommand.create( this ).
            id( id ).
            resolveHasChild( resolveHasChild ).
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
            final Node node = NodeHelper.runAsAdmin( () -> GetNodeByPathCommand.create( this ).
                nodePath( parentPath ).
                resolveHasChild( false ).
                build().
                execute() );

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

        StorageService storageService;

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
            this.storageService = source.storageService;
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

        public B storageService( final StorageService storageService )
        {
            this.storageService = storageService;
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
            Preconditions.checkNotNull( storageService, "storageService not set" );
        }
    }
}
