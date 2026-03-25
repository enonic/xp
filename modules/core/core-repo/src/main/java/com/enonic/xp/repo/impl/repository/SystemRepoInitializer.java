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
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SystemRepoInitializer
    extends Initializer
{
    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final IndexMigrator indexMigrator;

    private final RepositoryCreator repositoryCreator;

    private SystemRepoInitializer( final Builder builder )
    {
        super( builder );
        this.indexServiceInternal = Objects.requireNonNull( builder.indexServiceInternal );
        this.nodeStorageService = Objects.requireNonNull( builder.nodeStorageService );
        this.indexMigrator = new IndexMigrator( Objects.requireNonNull( builder.repositoryEntryService ),
                                                Objects.requireNonNull( builder.nodeRepositoryService ), this.indexServiceInternal );
        this.repositoryCreator =
            new RepositoryCreator( builder.nodeRepositoryService, builder.nodeStorageService, builder.repositoryEntryService );
    }

    @Override
    public void doInitialize()
    {
        createAdminContext().runWith( () -> {
            repositoryCreator.createSystemRepository( RepositorySettings.create().build() );
            initRepositoryFolder();

            indexMigrator.migrate();
        } );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().callWith( () -> this.repositoryCreator.isInitialized( SystemConstants.SYSTEM_REPO_ID ) &&
            this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                         InternalContext.from( ContextAccessor.current() ) ) != null ) && !indexMigrator.needMigrate();
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
        final Node existing = this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                                           InternalContext.from( ContextAccessor.current() ) );
        if ( existing == null )
        {
            final Node node = Node.create()
                .id( new NodeId() )
                .parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getParentPath() )
                .name( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getName() )
                .permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL )
                .childOrder( ChildOrder.defaultOrder() )
                .build();

            this.nodeStorageService.store( StoreNodeParams.newVersion( node ), InternalContext.from( ContextAccessor.current() ) );
        }
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authInfo )
            .build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Initializer.Builder<Builder>
    {
        private IndexServiceInternal indexServiceInternal;

        private NodeStorageService nodeStorageService;

        private RepositoryEntryService repositoryEntryService;

        private NodeRepositoryService nodeRepositoryService;

        public Builder setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public Builder setNodeStorageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return this;
        }

        public Builder setRepositoryEntryService( final RepositoryEntryService repositoryEntryService )
        {
            this.repositoryEntryService = repositoryEntryService;
            return this;
        }

        public Builder setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
        {
            this.nodeRepositoryService = nodeRepositoryService;
            return this;
        }


        public SystemRepoInitializer build()
        {
            return new SystemRepoInitializer( this );
        }
    }
}
