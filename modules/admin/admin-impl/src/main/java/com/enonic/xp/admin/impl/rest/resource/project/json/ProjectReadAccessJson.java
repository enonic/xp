package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.security.PrincipalKey;

@SuppressWarnings("UnusedDeclaration")
public class ProjectReadAccessJson
{
    private final ProjectReadAccess projectReadAccess;

    @JsonCreator
    public ProjectReadAccessJson( @JsonProperty("type") final String type, @JsonProperty("principals") final List<String> principals )
    {
        final ProjectReadAccess.Builder readAccess = ProjectReadAccess.create().
            setType( ProjectReadAccessType.from( type ) );

        if ( principals != null )
        {
            readAccess.addPrincipals( principals.stream().
                map( PrincipalKey::from ).
                collect( Collectors.toList() ) );
        }

        this.projectReadAccess = readAccess.build();
    }

    public ProjectReadAccessJson( final ProjectReadAccess projectReadAccess )
    {
        this.projectReadAccess = projectReadAccess;
    }

    public String getType()
    {
        return this.projectReadAccess.getType().toString();
    }

    public List<String> getPrincipals()
    {
        return this.projectReadAccess.getPrincipals().stream().
            map( PrincipalKey::toString ).
            collect( Collectors.toList() );
    }

    @JsonIgnore
    public ProjectReadAccess getProjectReadAccess()
    {
        return projectReadAccess;
    }
}
