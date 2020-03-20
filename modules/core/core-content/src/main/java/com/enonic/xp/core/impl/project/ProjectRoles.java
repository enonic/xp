package com.enonic.xp.core.impl.project;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.PrincipalKey;

public enum ProjectRoles
{
    OWNER( "owner" ), EDITOR( "editor" ), AUTHOR( "author" ), CONTRIBUTOR( "contributor" ), VIEWER( "viewer" );

    private String value;

    ProjectRoles( final String value )
    {
        this.value = value;
    }

    public PrincipalKey getRoleKey( final ProjectName projectName )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + value;
        return PrincipalKey.ofRole( roleName );
    }
}
