package com.enonic.xp.repo.impl.node;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.VersionAttributesResolver;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static java.util.Objects.requireNonNull;

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

    @Nullable Node doGetById( final @NonNull NodeId id )
    {
        return doGetById( id, InternalContext.from( ContextAccessor.current() ) );
    }

    @Nullable Node doGetById( final @NonNull NodeId id, final @NonNull InternalContext context )
    {
        return this.nodeStorageService.get( id, context );
    }

    @Nullable Node doGetByPath( final NodePath path )
    {
        return GetNodeByPathCommand.create( this ).nodePath( path ).build().execute();
    }

    void refresh( final RefreshMode refreshMode )
    {
        if ( refreshMode != null )
        {
            RefreshCommand.create().refreshMode( refreshMode ).indexServiceInternal( this.indexServiceInternal ).build().execute();
        }
    }

    static Attributes resolveVersionAttributes( final VersionAttributesResolver resolver, final Node originalNode, final Node editedNode,
                                                final Branch branch, final Attributes originalAttributes )
    {
        return resolver != null ? resolver.resolve( Node.create( editedNode ).build(),
                                                    originalNode != null ? Node.create( originalNode ).build() : null, branch,
                                                    originalAttributes ) : null;
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
            requireNonNull( indexServiceInternal );
            requireNonNull( nodeStorageService );
            requireNonNull( nodeSearchService );
        }
    }
}
