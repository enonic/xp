package com.enonic.xp.project;

public enum ProjectRoles
{
    OWNER( "owner" ), EDITOR( "editor" ), AUTHOR( "author" ), CONTRIBUTOR( "contributor" ), VIEWER( "viewer" );

    private String value;

    ProjectRoles( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
