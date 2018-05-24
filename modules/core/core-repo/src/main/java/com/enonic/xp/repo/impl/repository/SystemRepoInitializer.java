package com.enonic.xp.repo.impl.repository;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.init.Initializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemRepoInitializer
    extends Initializer
{
    private static final PrincipalKey SUPER_USER = PrincipalKey.ofSuperUser();

    private final IndexServiceInternal indexServiceInternal;

    private final RepositoryService repositoryService;

    private final NodeStorageService nodeStorageService;


    private SystemRepoInitializer( final Builder builder )
    {
        super( builder );
        this.indexServiceInternal = builder.indexServiceInternal;
        this.repositoryService = builder.repositoryService;
        this.nodeStorageService = builder.nodeStorageService;
    }

    @Override
    public void doInitialize()
    {
        createAdminContext().runWith( () -> {
            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                rootChildOrder( ChildOrder.from( "_name ASC" ) ).
                rootPermissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                build();

            this.repositoryService.createRepository( createRepositoryParams );

            initRepositoryFolder();
        } );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> {
                if ( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() ) )
                {
                    final Context currentContext = ContextAccessor.current();
                    final Node repositoryNode = this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                                                             InternalContext.from( currentContext ) );
                    if ( repositoryNode != null )
                    {
                        return true;
                    }
                }
                return false;
            } );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "System-repo";
    }

    @Override
    protected boolean isMaster()
    {
        return indexServiceInternal.isMaster();
    }

    private void initRepositoryFolder()
    {
        final Context currentContext = ContextAccessor.current();
        final Node node = Node.create( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getParentPath() ).
            name( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getName() ).
            permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
            build();

        this.nodeStorageService.store( node, InternalContext.from( currentContext ) );
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( SUPER_USER ).login( SUPER_USER.getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Initializer.Builder<Builder>
    {
        private IndexServiceInternal indexServiceInternal;

        private RepositoryService repositoryService;

        private NodeStorageService nodeStorageService;

        public Builder setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public Builder setRepositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder setNodeStorageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( indexServiceInternal );
            Preconditions.checkNotNull( repositoryService );
            Preconditions.checkNotNull( nodeStorageService );
        }

        public SystemRepoInitializer build()
        {
            validate();
            return new SystemRepoInitializer( this );
        }
    }
}
