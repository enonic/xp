package com.enonic.xp.project;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public enum ProjectRole
{
    OWNER( "owner" ), EDITOR( "editor" ), AUTHOR( "author" ), CONTRIBUTOR( "contributor" ), VIEWER( "viewer" );

    private String value;

    ProjectRole( final String value )
    {
        this.value = value;
    }

    public PrincipalKey getRoleKey( final ProjectName projectName )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + value;
        return PrincipalKey.ofRole( roleName );
    }
}
