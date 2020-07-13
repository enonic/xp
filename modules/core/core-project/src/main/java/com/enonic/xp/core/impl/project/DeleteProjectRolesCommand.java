package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;

public final class DeleteProjectRolesCommand
    extends AbstractProjectRolesCommand
{
    private DeleteProjectRolesCommand( final Builder builder )
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
            final PrincipalKey roleKey = ProjectAccessHelper.createRoleKey( projectName, projectRole );
            if ( securityService.getRole( roleKey ).isPresent() )
            {
                securityService.deletePrincipal( PrincipalKey.ofRole( roleKey.getId() ) );
            }
        }
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public DeleteProjectRolesCommand build()
        {
            validate();
            return new DeleteProjectRolesCommand( this );
        }

    }
}
