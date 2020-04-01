package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;

public final class DeleteProjectPermissionRolesCommand
    extends AbstractProjectPermissionsCommand
{
    private DeleteProjectPermissionRolesCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        doDeleteRoles();
    }

    private void doDeleteRoles()
    {
        for ( ProjectRole projectRole : ProjectRole.values() )
        {
            final PrincipalKey roleKey = createRoleKey( projectRole );
            if ( securityService.getRole( roleKey ).isPresent() )
            {
                securityService.deletePrincipal( PrincipalKey.ofRole( roleKey.getId() ) );
            }
        }
    }

    public static final class Builder
        extends AbstractProjectPermissionsCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        void validate()
        {
            super.validate();
        }

        public DeleteProjectPermissionRolesCommand build()
        {
            validate();
            return new DeleteProjectPermissionRolesCommand( this );
        }

    }
}
