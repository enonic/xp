package com.enonic.xp.core.impl.app;

import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;

import static java.util.Objects.requireNonNull;

public class NamespaceAppInitializer
    extends ExternalInitializer
{
    private final InternalRepositoryService repositoryService;

    private final Context adminContext;

    private NamespaceAppInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.adminContext = NamespaceAppContext.createAdminContext();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected boolean isInitialized()
    {
        return this.adminContext.callWith( () -> repositoryService.isInitialized( NamespaceAppConstants.NAMESPACE_APP_REPO_ID ) );
    }

    @Override
    protected void doInitialize()
    {
        this.adminContext.runWith( this::initializeRepository );
    }

    private void initializeRepository()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
            .repositoryId( NamespaceAppConstants.NAMESPACE_APP_REPO_ID )
            .rootPermissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
            .build();

        this.repositoryService.initializeRepository( createRepositoryParams );
    }

    @Override
    protected String getInitializationSubject()
    {
        return NamespaceAppConstants.NAMESPACE_APP_REPO_ID + " repo";
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
            requireNonNull( repositoryService );
        }

        public NamespaceAppInitializer build()
        {
            validate();
            return new NamespaceAppInitializer( this );
        }
    }
}
