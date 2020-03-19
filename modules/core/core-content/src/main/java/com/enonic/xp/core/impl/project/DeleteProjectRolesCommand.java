package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.PrincipalKey;

public class DeleteProjectRolesCommand
    extends AbstractProjectRolesCommand
{
    private ProjectName projectName;

    private DeleteProjectRolesCommand( final Builder builder )
    {
        super( builder );
        this.projectName = builder.projectName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        doDeleteRoles( projectName );
    }

    private void doDeleteRoles( final ProjectName projectName )
    {
        for ( ProjectRoles projectRole : ProjectRoles.values() )
        {
            final PrincipalKey roleKey = projectRole.getRoleKey( projectName );
            if ( securityService.getRole( roleKey ).isPresent() )
            {
                securityService.deletePrincipal( PrincipalKey.ofRole( roleKey.getId() ) );
            }
        }
    }

    public static final class Builder
        extends AbstractProjectRolesCommand.Builder<Builder>
    {
        private ProjectName projectName;

        private Builder()
        {
        }

        public Builder projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.projectName, "Project name is required" );
            Preconditions.checkArgument( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ), "Default project has no roles" );
        }

        public DeleteProjectRolesCommand build()
        {
            validate();
            return new DeleteProjectRolesCommand( this );
        }

    }
}
