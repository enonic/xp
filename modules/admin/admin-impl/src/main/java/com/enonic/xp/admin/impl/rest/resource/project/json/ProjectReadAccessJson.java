package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Set;

import com.enonic.xp.project.ProjectReadAccess;

public class ProjectReadAccessJson
{
    private String type;

    private Set<String> principals;

    public ProjectReadAccessJson()
    {
    }

    public ProjectReadAccessJson( final ProjectReadAccess readAccess )
    {
        this.type = readAccess.getType().toString();
        this.principals = readAccess.getPrincipals().asStrings();
    }

    public String getType()
    {
        return type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public Set<String> getPrincipals()
    {
        return principals;
    }

    public void setPrincipals( final Set<String> principals )
    {
        this.principals = principals;
    }
}
