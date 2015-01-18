package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.ActiveContentVersionEntry;

public class ActiveContentVersionEntryJson
{
    private final String workspace;

    private final ContentVersionJson contentVersion;

    public ActiveContentVersionEntryJson( final ActiveContentVersionEntry activeContentVersion )
    {
        this.workspace = activeContentVersion.getWorkspace().getName();
        this.contentVersion = new ContentVersionJson( activeContentVersion.getContentVersion() );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getWorkspace()
    {
        return workspace;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ContentVersionJson getContentVersion()
    {
        return contentVersion;
    }
}
