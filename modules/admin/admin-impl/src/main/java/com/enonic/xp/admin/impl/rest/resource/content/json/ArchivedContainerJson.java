package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.archive.ArchivedContainer;

public class ArchivedContainerJson
{
    private final Set<String> contentIds;

    private final String id;

    private final String parent;

    private final Instant archiveTime;

    public ArchivedContainerJson( final ArchivedContainer archivedContainer )
    {
        this.contentIds = archivedContainer.getContentIds().asStrings();
        this.id = archivedContainer.getId().toString();
        this.parent = archivedContainer.getParent() != null ? archivedContainer.getParent().toString() : null;
        this.archiveTime = archivedContainer.getArchiveTime();
    }

    public Set<String> getContentIds()
    {
        return contentIds;
    }

    public String getId()
    {
        return id;
    }

    public String getParent()
    {
        return parent;
    }

    public Instant getArchiveTime()
    {
        return archiveTime;
    }
}
