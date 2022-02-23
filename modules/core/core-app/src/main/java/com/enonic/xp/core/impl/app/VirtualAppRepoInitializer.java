package com.enonic.xp.core.impl.app;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.issue.VirtualAppConstants;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryService;

public class VirtualAppRepoInitializer
    extends ExternalInitializer
{
    private final RepositoryService repositoryService;

    private final Context adminContext;

    private VirtualAppRepoInitializer( final Builder builder )
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

        this.repositoryService.createRepository( createRepositoryParams );
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

        public Builder setRepositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( repositoryService );
        }

        public VirtualAppRepoInitializer build()
        {
            validate();
            return new VirtualAppRepoInitializer( this );
        }
    }
}
