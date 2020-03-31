package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.UpdateRoleParams;

public final class CreateProjectRolesCommand
    extends AbstractProjectRolesCommand
{
    private final String projectDisplayName;

    private CreateProjectRolesCommand( final Builder builder )
    {
        super( builder );
        this.projectDisplayName = builder.projectDisplayName;
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
            final PrincipalKey roleKey = createRoleKey( projectRole );
            final String roleDisplayName = createRoleDisplayName( projectRole );

            securityService.getRole( roleKey ).
                ifPresentOrElse( ( roleValue ) -> doUpdateRoleDisplayName( roleValue, roleDisplayName ),
                                 () -> doCreateRole( roleKey, roleDisplayName ) );
        }
    }

    private Role doCreateRole( final PrincipalKey roleKey, final String displayName )
    {
        return securityService.createRole( CreateRoleParams.create().
            roleKey( roleKey ).
            displayName( displayName ).
            build() );
    }

    private Role doUpdateRoleDisplayName( final Role role, final String newDisplayName )
    {
        return !newDisplayName.equals( role.getDisplayName() ) ? securityService.updateRole( UpdateRoleParams.create().
            roleKey( role.getKey() ).
            displayName( newDisplayName ).
            description( role.getDescription() ).
            build() ) : role;
    }


    protected String createRoleDisplayName( final ProjectRole projectRole )
    {
        return this.projectDisplayName + " - " + projectRole.getValue();
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private String projectDisplayName;

        private Builder()
        {
        }

        public Builder projectDisplayName( final String projectDisplayName )
        {
            this.projectDisplayName = projectDisplayName;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.projectDisplayName, "Project display name is required" );
        }

        public CreateProjectRolesCommand build()
        {
            validate();
            return new CreateProjectRolesCommand( this );
        }

    }
}
