package com.enonic.xp.core.impl.project;

import java.util.Objects;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;

public final class CreateProjectRolesCommand
    extends AbstractProjectRolesCommand
{
    private CreateProjectRolesCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        doCreateRoles();
    }

    private void doCreateRoles()
    {
        for ( ProjectRole projectRole : ProjectRole.values() )
        {
            final PrincipalKey roleKey = ProjectAccessHelper.createRoleKey( projectName, projectRole );
            final String roleDisplayName = createRoleDisplayName( projectRole );

            if ( securityService.getRole( roleKey ).isEmpty() )
            {
                doCreateRole( roleKey, roleDisplayName );
            }
        }
    }

    private Role doCreateRole( final PrincipalKey roleKey, final String displayName )
    {
        return securityService.createRole( CreateRoleParams.create().
            roleKey( roleKey ).
            displayName( displayName ).
            build() );
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
            Objects.requireNonNull( this.projectDisplayName, "Project display name is required" );
        }

        public CreateProjectRolesCommand build()
        {
            validate();
            return new CreateProjectRolesCommand( this );
        }

    }
}
