package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.archive.ArchivedContainerLayer;

public class ArchivedContainerLayerJson
{
    private final Set<String> contentIds;

    private final String id;

    private final String parent;

    private final Instant archiveTime;

    public ArchivedContainerLayerJson( final ArchivedContainerLayer archivedContainerLayer )
    {
        this.contentIds = archivedContainerLayer.getContentIds().asStrings();
        this.id = archivedContainerLayer.getId().toString();
        this.parent = archivedContainerLayer.getParent() != null ? archivedContainerLayer.getParent().toString() : null;
        this.archiveTime = archivedContainerLayer.getArchiveTime();
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
