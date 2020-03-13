package com.enonic.xp.project;

import com.enonic.xp.security.PrincipalKeys;

public class ProjectReadAccess
{
    private final ProjectReadAccessType type;

    private final PrincipalKeys principals;

    public ProjectReadAccess( final ProjectReadAccessType type )
    {
        this.type = type;
        this.principals = PrincipalKeys.empty();
    }

    public ProjectReadAccess( final ProjectReadAccessType type, final PrincipalKeys principals )
    {
        this.type = type;
        this.principals = principals;
    }

    public ProjectReadAccessType getType()
    {
        return type;
    }

    public PrincipalKeys getPrincipals()
    {
        return principals;
    }

}
