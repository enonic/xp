package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationRepoConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;

import static java.util.Objects.requireNonNull;

/**
 * Creates the application repository ({@link ApplicationRepoConstants#APP_REPO_ID}). The applications are children of its root,
 * nothing else needs to be laid out.
 */
public class ApplicationRepoInitializer
    extends ExternalInitializer
{
    private final InternalRepositoryService repositoryService;

    private final Context adminContext;

    private ApplicationRepoInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.adminContext = ApplicationHelper.createAdminContext();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected boolean isInitialized()
    {
        return this.adminContext.callWith( () -> repositoryService.isInitialized( ApplicationRepoConstants.APP_REPO_ID ) );
    }

    @Override
    protected void doInitialize()
    {
        this.adminContext.runWith( () -> repositoryService.initializeRepository( CreateRepositoryParams.create()
                                                                                    .repositoryId( ApplicationRepoConstants.APP_REPO_ID )
                                                                                    .rootPermissions(
                                                                                        ApplicationRepoConstants.APP_REPO_DEFAULT_ACL )
                                                                                    .build() ) );
    }

    @Override
    protected String getInitializationSubject()
    {
        return ApplicationRepoConstants.APP_REPO_ID + " repo";
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

        public ApplicationRepoInitializer build()
        {
            validate();
            return new ApplicationRepoInitializer( this );
        }
    }
}
