package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.security.Principal;

public class ContentVersionViewJson
    extends ContentVersionJson
{

    private ImmutableList<String> workspaces;

    public ContentVersionViewJson( final ContentVersion contentVersion, final Principal modifier, final List<String> workspaces )
    {
        super( contentVersion, modifier );
        this.workspaces = ImmutableList.copyOf( workspaces );
    }

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableList<String> getWorkspaces()
    {
        return workspaces;
    }
}
