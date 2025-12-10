package com.enonic.xp.repo.impl.repository;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
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
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Version;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class SystemRepoInitializer
    extends Initializer
{
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
            if (!this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID )) {
                this.repositoryService.createRepository( CreateRepositoryParams.create().
                    repositoryId( SystemConstants.SYSTEM_REPO_ID ).
                    rootChildOrder( ChildOrder.name() ).
                    rootPermissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL ).
                    build() );
            }

            Node repoFolder = initRepositoryFolder();
            if ( !repoVersionMatch( repoFolder ) )
            {
                new Xp8IndexMigrator( this.repositoryService, this.indexServiceInternal ).migrate();

                final PropertyTree data = new PropertyTree();
                data.addString( "version", Version.valueOf( "8.0.0.pre1" ).toString() );

                final Node updatedRepoFolder =
                    Node.create( repoFolder ).data( data ).timestamp( Instant.now( CLOCK ) ).build();

                this.nodeStorageService.store( StoreNodeParams.newVersion( updatedRepoFolder ), InternalContext.from( ContextAccessor.current() ) );
            }
        } );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> {
            if ( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) )
            {
                final Node node = this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                                               InternalContext.from( ContextAccessor.current() ) );
                return repoVersionMatch( node );
            }
            return false;
        } );
    }

    private static boolean repoVersionMatch( final Node node )
    {
        return Optional.ofNullable( node )
            .map( Node::data )
            .map( pt -> pt.getString( "version" ) )
            .map( Version::parseVersion )
            .orElse( Version.emptyVersion )
            .equals( Version.parseVersion( "8.0.0.pre1" ) );
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

    private Node initRepositoryFolder()
    {
        final Node existing = this.nodeStorageService.get( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH,
                                                           InternalContext.from( ContextAccessor.current() ) );
        if ( existing == null )
        {
            final PropertyTree data = new PropertyTree();
            data.addString( "version", Version.valueOf( "8.0.0.pre1" ).toString() );

            final Node node = Node.create( new NodeId() )
                .childOrder( ChildOrder.defaultOrder() )
                .parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getParentPath() )
                .name( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH.getName() )
                .permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL )
                .data( data )
                .build();

            return this.nodeStorageService.store( StoreNodeParams.newVersion( node ), InternalContext.from( ContextAccessor.current() ) )
                .node();
        }
        else
        {
            return existing;
        }
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.create().
            branch( SystemConstants.BRANCH_SYSTEM ).
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
