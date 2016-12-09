package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

abstract class AbstractNodeCommand
{
    final IndexServiceInternal indexServiceInternal;

    final NodeStorageService nodeStorageService;

    final NodeSearchService nodeSearchService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexServiceInternal = builder.indexServiceInternal;
        this.nodeStorageService = builder.nodeStorageService;
        this.nodeSearchService = builder.nodeSearchService;
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
            searchService( this.nodeSearchService ).
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

        NodeStorageService nodeStorageService;

        NodeSearchService nodeSearchService;

        Builder()
        {
        }

        Builder( final AbstractNodeCommand source )
        {
            this.indexServiceInternal = source.indexServiceInternal;
            this.nodeStorageService = source.nodeStorageService;
            this.nodeSearchService = source.nodeSearchService;
        }

        @SuppressWarnings("unchecked")
        public B indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( indexServiceInternal, "indexService not set" );
            Preconditions.checkNotNull( nodeStorageService, "storageService not set" );
            Preconditions.checkNotNull( nodeSearchService, "searchService not set" );
        }
    }
}
