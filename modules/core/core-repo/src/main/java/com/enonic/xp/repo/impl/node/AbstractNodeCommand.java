package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.security.PrincipalKey;
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

    Node doGetByPath( final NodePath path )
    {
        return GetNodeByPathCommand.create( this ).
            nodePath( path ).
            build().
            execute();
    }

    void refresh( final RefreshMode refreshMode )
    {
        if (refreshMode != null)
        {
            RefreshCommand.create().refreshMode( refreshMode ).indexServiceInternal( this.indexServiceInternal ).build().execute();
        }
    }

    PrincipalKey getCurrentPrincipalKey()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        return authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();
    }

    public abstract static class Builder<B extends Builder>
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
            Objects.requireNonNull( indexServiceInternal );
            Objects.requireNonNull( nodeStorageService );
            Objects.requireNonNull( nodeSearchService );
        }
    }
}
