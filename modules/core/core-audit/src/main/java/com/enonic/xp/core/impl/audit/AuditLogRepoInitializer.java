package com.enonic.xp.core.impl.audit;

import com.enonic.xp.context.Context;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;

import static java.util.Objects.requireNonNull;

public class AuditLogRepoInitializer
    extends ExternalInitializer
{
    private final InternalRepositoryService repositoryService;

    private final Context adminContext;

    protected AuditLogRepoInitializer( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
        this.adminContext = AuditLogContext.createAdminContext();
    }

    @Override
    protected boolean isInitialized()
    {
        return this.adminContext.callWith( () -> repositoryService.isInitialized( AuditLogConstants.AUDIT_LOG_REPO_ID ) );
    }

    @Override
    protected void doInitialize()
    {
        this.adminContext.runWith( this::initializeRepository );
    }

    private void initializeRepository()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
            .repositoryId( AuditLogConstants.AUDIT_LOG_REPO_ID )
            .rootPermissions( AuditLogConstants.AUDIT_LOG_REPO_DEFAULT_ACL )
            .build();

        this.repositoryService.initializeRepository( createRepositoryParams );
    }

    @Override
    protected String getInitializationSubject()
    {
        return AuditLogConstants.AUDIT_LOG_REPO_ID + " repo";
    }

    public static Builder create()
    {
        return new Builder();
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

        public AuditLogRepoInitializer build()
        {
            validate();
            return new AuditLogRepoInitializer( this );
        }
    }
}
