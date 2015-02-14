package com.enonic.xp.admin.impl.json.content;

import com.enonic.wem.api.content.ActiveContentVersionEntry;

public class ActiveContentVersionEntryJson
{
    private final String branch;

    private final ContentVersionJson contentVersion;

    public ActiveContentVersionEntryJson( final ActiveContentVersionEntry activeContentVersion )
    {
        this.branch = activeContentVersion.getBranch().getName();
        this.contentVersion = new ContentVersionJson( activeContentVersion.getContentVersion() );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getBranch()
    {
        return branch;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ContentVersionJson getContentVersion()
    {
        return contentVersion;
    }
}
