package com.enonic.xp.repo.impl.repository;

import java.util.Objects;

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
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
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
                repositoryId( SystemConstants.SYSTEM_REPO_ID ).
                rootChildOrder( ChildOrder.name() ).
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
            callWith( () -> this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) &&
                this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                             InternalContext.from( ContextAccessor.current() ) ) != null );
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

    @Override
    protected boolean readyToInitialize()
    {
        return indexServiceInternal.waitForYellowStatus();
    }

    private void initRepositoryFolder()
    {
        final Node node = Node.create( new NodeId() ).
            childOrder( ChildOrder.defaultOrder() ).
            parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getParentPath() ).
            name( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getName() ).
            permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
            build();

        this.nodeStorageService.store( StoreNodeParams.newVersion( node ), InternalContext.from( ContextAccessor.current() ) );
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( SUPER_USER ).login( SUPER_USER.getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.create().
            branch( SecurityConstants.BRANCH_SECURITY ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            authInfo( authInfo ).build();
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
            Objects.requireNonNull( indexServiceInternal );
            Objects.requireNonNull( repositoryService );
            Objects.requireNonNull( nodeStorageService );
        }

        public SystemRepoInitializer build()
        {
            validate();
            return new SystemRepoInitializer( this );
        }
    }
}
