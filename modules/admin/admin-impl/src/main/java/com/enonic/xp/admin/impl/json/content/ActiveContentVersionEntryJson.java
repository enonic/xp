package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ActiveContentVersionEntry;

public class ActiveContentVersionEntryJson
{
    private final String branch;

    private final ContentVersionJson contentVersion;

    public ActiveContentVersionEntryJson( final ActiveContentVersionEntry activeContentVersion,
                                          final ContentPrincipalsResolver principalsResolver )
    {
        this.branch = activeContentVersion.getBranch().getName();
        this.contentVersion = new ContentVersionJson( activeContentVersion.getContentVersion(), principalsResolver.findPrincipal(
            activeContentVersion.getContentVersion().getModifier() ) );
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
