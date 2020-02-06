package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.security.acl.Permission;

public class ContentIdsPermissionsJson
{
    private ContentIds contentIds;

    private List<Permission> permissions;

    @JsonCreator
    public ContentIdsPermissionsJson( @JsonProperty("contentIds") final List<String> contentIds,
                                      @JsonProperty("permissions") final List<String> permissions )
    {
        this.contentIds = ContentIds.from( contentIds );
        this.permissions = permissions.stream().map( Permission::valueOf ).collect( Collectors.toList() );
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public List<Permission> getPermissions()
    {
        return permissions;
    }
}
