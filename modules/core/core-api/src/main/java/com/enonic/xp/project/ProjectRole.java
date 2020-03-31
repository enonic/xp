package com.enonic.xp.project;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum ProjectRole
{
    OWNER( "Owner" ), EDITOR( "Editor" ), AUTHOR( "Author" ), CONTRIBUTOR( "Contributor" ), VIEWER( "Viewer" );

    private String value;

    ProjectRole( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
