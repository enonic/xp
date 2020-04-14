package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.security.PrincipalKey;

public class ProjectPermissionsJson
{
    private final ProjectPermissions permissions;

    @JsonCreator
    public ProjectPermissionsJson( @JsonProperty("owner") final List<String> ownerList,
                                   @JsonProperty("editor") final List<String> editorList,
                                   @JsonProperty("author") final List<String> authorList,
                                   @JsonProperty("contributor") final List<String> contributorList,
                                   @JsonProperty("viewer") final List<String> viewerList )
    {
        final ProjectPermissions.Builder builder = ProjectPermissions.create();

        Optional.ofNullable( ownerList ).ifPresent( list -> list.stream().
            map( PrincipalKey::from ).
            forEach( builder::addOwner ) );
        Optional.ofNullable( editorList ).ifPresent( list -> list.stream().
            map( PrincipalKey::from ).
            forEach( builder::addEditor ) );
        Optional.ofNullable( authorList ).ifPresent( list -> list.stream().
            map( PrincipalKey::from ).
            forEach( builder::addAuthor ) );
        Optional.ofNullable( contributorList ).ifPresent( list -> list.stream().
            map( PrincipalKey::from ).
            forEach( builder::addContributor ) );
        Optional.ofNullable( viewerList ).ifPresent( list -> list.stream().
            map( PrincipalKey::from ).
            forEach( builder::addViewer ) );

        this.permissions = builder.build();
    }

    public ProjectPermissionsJson( final ProjectPermissions permissions )
    {
        this.permissions = permissions;
    }

    public Set<String> getOwner()
    {
        return this.permissions.getOwner().asStrings();
    }

    public Set<String> getEditor()
    {
        return this.permissions.getEditor().asStrings();
    }

    public Set<String> getAuthor()
    {
        return this.permissions.getAuthor().asStrings();
    }

    public Set<String> getContributor()
    {
        return this.permissions.getContributor().asStrings();
    }

    @JsonIgnore
    public ProjectPermissions getPermissions()
    {
        return permissions;
    }
}
