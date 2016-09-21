package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

abstract class RepositorySpecificNodeCommand
    extends AbstractNodeCommand
{
    private final RepositoryService repositoryService;

    RepositorySpecificNodeCommand( final Builder builder )
    {
        super( builder );
        this.repositoryService = builder.repositoryService;
    }

    protected boolean skipNodeExistsVerification()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return !repositoryService.get( repositoryId ).getValidationSettings().isCheckExists();
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private RepositoryService repositoryService;

        Builder()
        {
        }

        Builder( final RepositorySpecificNodeCommand source )
        {
            super( source );
            this.repositoryService = source.repositoryService;
        }

        public B repositoryService( RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return (B) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( repositoryService );
        }
    }


}
