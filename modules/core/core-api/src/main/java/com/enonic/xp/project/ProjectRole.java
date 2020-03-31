package com.enonic.xp.project;

import com.enonic.xp.security.PrincipalKey;

public enum ProjectRole
{
    OWNER( "Owner" ), EDITOR( "Editor" ), AUTHOR( "Author" ), CONTRIBUTOR( "Contributor" ), VIEWER( "Viewer" );

    private String value;

    ProjectRole( final String value )
    {
        this.value = value;
    }

    public PrincipalKey getRoleKey( final ProjectName projectName )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + value.toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }

    public String getRoleDisplayName( final String projectDisplayName )
    {
        return projectDisplayName + " - " + value;
    }
}
