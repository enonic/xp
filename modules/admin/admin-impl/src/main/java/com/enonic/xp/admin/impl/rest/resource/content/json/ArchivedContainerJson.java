package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.archive.ArchivedContainer;
import com.enonic.xp.content.ContentId;

public class ArchivedContainerJson
{
    private final List<String> contentIds;

    private final String containerId;

    public ArchivedContainerJson( final ArchivedContainer archived )
    {
        this.contentIds = archived.getContentIds().stream().map( ContentId::toString ).collect( Collectors.toList());
        this.containerId = archived.getId().toString();
    }

    public List<String> getContentIds()
    {
        return contentIds;
    }

    public String getContainerId()
    {
        return containerId;
    }
}
