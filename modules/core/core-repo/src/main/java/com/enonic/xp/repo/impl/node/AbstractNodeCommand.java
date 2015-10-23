package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

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
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.storage.StorageService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

abstract class AbstractNodeCommand
{
    static final OrderExpressions DEFAULT_ORDER_EXPRESSIONS =
        OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) );

    final IndexServiceInternal indexServiceInternal;

    final StorageService storageService;

    final SearchService searchService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexServiceInternal = builder.indexServiceInternal;
        this.storageService = builder.storageService;
        this.searchService = builder.searchService;
    }

    Node doGetById( final NodeId id )
    {
        return GetNodeByIdCommand.create( this ).
            id( id ).
            build().
            execute();
    }

    FindNodesByParentResult doFindNodesByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create( this ).
            params( params ).
            searchService( this.searchService ).
            build().
            execute();
    }

    PrincipalKey getCurrentPrincipalKey()
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
                build().
                execute() );

            if ( node == null || node.getPermissions().isEmpty() )
            {
                throw new RuntimeException( "Could not evaluate permissions for node [" + parentPath.toString() + "]" );
            }
            return node.getPermissions();
        }
    }

    public static abstract class Builder<B extends Builder>
    {
        IndexServiceInternal indexServiceInternal;

        StorageService storageService;

        SearchService searchService;

        Builder()
        {
        }

        Builder( final AbstractNodeCommand source )
        {
            this.indexServiceInternal = source.indexServiceInternal;
            this.storageService = source.storageService;
            this.searchService = source.searchService;
        }

        @SuppressWarnings("unchecked")
        public B indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageService( final StorageService storageService )
        {
            this.storageService = storageService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B searchService( final SearchService searchService )
        {
            this.searchService = searchService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( indexServiceInternal, "indexService not set" );
            Preconditions.checkNotNull( storageService, "storageService not set" );
            Preconditions.checkNotNull( searchService, "searchService not set" );
        }
    }
}
