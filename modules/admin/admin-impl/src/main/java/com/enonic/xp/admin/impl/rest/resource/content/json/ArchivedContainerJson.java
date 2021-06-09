package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

import com.enonic.xp.archive.ArchivedContainer;

public class ArchivedContainerJson
{
    private final Set<String> contentIds;

    private final String id;

    public ArchivedContainerJson( final ArchivedContainer archivedContainer )
    {
        this.contentIds = archivedContainer.getContentIds().asStrings();
        this.id = archivedContainer.getId().toString();
    }

    public Set<String> getContentIds()
    {
        return contentIds;
    }

    public String getId()
    {
        return id;
    }
}
