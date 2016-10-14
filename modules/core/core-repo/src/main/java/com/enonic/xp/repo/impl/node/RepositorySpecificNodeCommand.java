package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.acl.Permission;

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
        return !repositoryService.get( repositoryId ).
            getSettings().
            getValidationSettings().
            isCheckExists();
    }

    protected boolean skipParentNodeExistsVerification()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return !repositoryService.get( repositoryId ).
            getSettings().
            getValidationSettings().
            isCheckParentExists();
    }

    protected boolean skipPermissionsVerification()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        return !repositoryService.get( repositoryId ).
            getSettings().
            getValidationSettings().
            isCheckParentExists();
    }

    protected void requireContextUserPermission( final Permission permission, final Node node )
    {
        if ( skipPermissionsVerification() )
        {
            return;
        }
        NodePermissionsResolver.requireContextUserPermission( permission, node );
    }

    protected void requireContextUserPermissionOrAdmin( final Permission permission, final Node node )
    {
        if ( skipPermissionsVerification() )
        {
            return;
        }
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( permission, node );
    }

    protected boolean contextUserHasPermissionOrAdmin( final Permission permission, final Node node )
    {
        if ( skipPermissionsVerification() )
        {
            return true;
        }
        return NodePermissionsResolver.contextUserHasPermissionOrAdmin( permission, node );
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
