package com.enonic.xp.core.impl.app;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;

public class VirtualAppInitializer
    extends ExternalInitializer
{
    private final RepositoryService repositoryService;

    private final SecurityService securityService;

    private static final Logger LOG = LoggerFactory.getLogger( VirtualAppInitializer.class );

    private final Context adminContext;

    private VirtualAppInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.securityService = builder.securityService;
        this.adminContext = VirtualAppContext.createAdminContext();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected boolean isInitialized()
    {
        return this.adminContext.callWith( () -> repositoryService.isInitialized( VirtualAppConstants.VIRTUAL_APP_REPO_ID ) &&
            securityService.getRole( RoleKeys.SCHEMA_ADMIN ).isPresent() );
    }

    @Override
    protected void doInitialize()
    {
        this.adminContext.runWith( this::initializeRepository );
        this.adminContext.runWith( this::initializeRole );
    }

    private void initializeRepository()
    {
        if ( repositoryService.get( VirtualAppConstants.VIRTUAL_APP_REPO_ID ) == null )
        {
            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
                .repositoryId( VirtualAppConstants.VIRTUAL_APP_REPO_ID )
                .rootPermissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                .build();

            this.repositoryService.createRepository( createRepositoryParams );
        }
    }

    private void initializeRole()
    {
        try
        {
            if ( securityService.getRole( RoleKeys.SCHEMA_ADMIN ).isEmpty() )
            {
                final CreateRoleParams createRoleParams =
                    CreateRoleParams.create().roleKey( RoleKeys.SCHEMA_ADMIN ).displayName( "Schema Admin" ).build();

                securityService.createRole( createRoleParams );

                LOG.info( "Role created: " + createRoleParams.getKey().toString() );
            }
        }
        catch ( final Exception t )
        {
            LOG.error( "Unable to initialize role: " + RoleKeys.SCHEMA_ADMIN, t );
        }
    }

    @Override
    protected String getInitializationSubject()
    {
        return VirtualAppConstants.VIRTUAL_APP_REPO_ID + " repo";
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private RepositoryService repositoryService;

        private SecurityService securityService;

        public Builder setRepositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder setSecurityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Objects.requireNonNull( repositoryService );
        }

        public VirtualAppInitializer build()
        {
            validate();
            return new VirtualAppInitializer( this );
        }
    }
}
