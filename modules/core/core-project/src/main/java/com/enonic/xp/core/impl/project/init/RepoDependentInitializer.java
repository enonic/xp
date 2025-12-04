package com.enonic.xp.core.impl.project.init;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

public abstract class RepoDependentInitializer
    extends ExternalInitializer
{
    public static final AuthenticationInfo SUPER_USER_AUTH = AuthenticationInfo.create()
        .principals( RoleKeys.ADMIN )
        .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
        .build();

    protected final NodeService nodeService;

    protected final RepositoryId repositoryId;

    protected final AccessControlList accessControlList;

    protected RepoDependentInitializer( final Builder builder )
    {
        super( builder );

        this.nodeService = Objects.requireNonNull( builder.nodeService );
        this.repositoryId = Objects.requireNonNull( builder.repositoryId );
        this.accessControlList = builder.accessControlList;
    }

    protected Context createAdminContext( Branch branch )
    {
        return createAdminContext( branch, repositoryId );
    }

    static Context createAdminContext( Branch branch, RepositoryId repositoryId )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .branch( branch )
            .repositoryId( repositoryId )
            .authInfo( SUPER_USER_AUTH )
            .build();
    }

    public static class Builder<T extends Builder>
        extends ExternalInitializer.Builder<T>
    {
        private NodeService nodeService;

        private RepositoryId repositoryId;

        private AccessControlList accessControlList;

        public T setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (T) this;
        }

        public T repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return (T) this;
        }

        public T accessControlList( final AccessControlList accessControlList )
        {
            this.accessControlList = accessControlList;
            return (T) this;
        }
    }
}
