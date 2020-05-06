package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.project.ProjectReadAccess;
import com.enonic.xp.admin.impl.rest.resource.project.ProjectReadAccessType;
import com.enonic.xp.security.PrincipalKey;

public class ProjectReadAccessJson
{
    private final ProjectReadAccessType type;

    private final List<PrincipalKey> principals;

    @JsonCreator
    public ProjectReadAccessJson( @JsonProperty("type") final String type )
    {
        this.type = ProjectReadAccessType.from( type );
        this.principals = ImmutableList.of();
    }

    public ProjectReadAccessJson( final ProjectReadAccessType type, final List<PrincipalKey> principals )
    {
        this.type = type;
        this.principals = ImmutableList.copyOf( principals );
    }

    public String getType()
    {
        return this.type.toString();
    }

    public List<String> getPrincipals()
    {
        return this.principals.stream().
            map( PrincipalKey::toString ).
            collect( Collectors.toList() );
    }

    @JsonIgnore
    public ProjectReadAccess getProjectReadAccess()
    {
        return ProjectReadAccess.create().addPrincipals( this.principals ).setType( this.type ).build();
    }
}
