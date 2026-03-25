package com.enonic.xp.core.impl.app;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;

public class VirtualAppInitializer
    extends ExternalInitializer
{
    private final InternalRepositoryService repositoryService;

    private static final Logger LOG = LoggerFactory.getLogger( VirtualAppInitializer.class );

    private final Context adminContext;

    private VirtualAppInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.adminContext = VirtualAppContext.createAdminContext();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected boolean isInitialized()
    {
        return this.adminContext.callWith( () -> repositoryService.isInitialized( VirtualAppConstants.VIRTUAL_APP_REPO_ID ) );
    }

    @Override
    protected void doInitialize()
    {
        this.adminContext.runWith( this::initializeRepository );
    }

    private void initializeRepository()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
            .repositoryId( VirtualAppConstants.VIRTUAL_APP_REPO_ID )
            .rootPermissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
            .build();

        this.repositoryService.initializeRepository( createRepositoryParams );
    }

    @Override
    protected String getInitializationSubject()
    {
        return VirtualAppConstants.VIRTUAL_APP_REPO_ID + " repo";
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private InternalRepositoryService repositoryService;

        public Builder setRepositoryService( final InternalRepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
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
