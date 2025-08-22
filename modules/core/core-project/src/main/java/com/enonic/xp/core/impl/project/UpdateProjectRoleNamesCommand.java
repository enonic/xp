package com.enonic.xp.core.impl.project;

import java.util.Objects;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.UpdateRoleParams;

public final class UpdateProjectRoleNamesCommand
    extends AbstractProjectRolesCommand
{
    private UpdateProjectRoleNamesCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        doUpdateRoles();
    }

    private void doUpdateRoles()
    {
        for ( ProjectRole projectRole : ProjectRole.values() )
        {
            final PrincipalKey roleKey = ProjectAccessHelper.createRoleKey( projectName, projectRole );
            final String newRoleDisplayName = createRoleDisplayName( projectRole );

            securityService.getRole( roleKey ).
                filter( role -> !newRoleDisplayName.equals( role.getDisplayName() ) ).
                ifPresent( ( roleValue ) -> doUpdateRoleDisplayName( roleValue, newRoleDisplayName ) );
        }
    }

    private Role doUpdateRoleDisplayName( final Role role, final String newDisplayName )
    {
        return securityService.updateRole( UpdateRoleParams.create().
            roleKey( role.getKey() ).
            displayName( newDisplayName ).
            description( role.getDescription() ).
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

        public UpdateProjectRoleNamesCommand build()
        {
            validate();
            return new UpdateProjectRoleNamesCommand( this );
        }

    }
}
