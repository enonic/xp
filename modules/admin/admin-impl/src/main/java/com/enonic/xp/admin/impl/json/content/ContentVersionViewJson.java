package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ContentVersion;

public class ContentVersionViewJson
    extends ContentVersionJson
{
    private final ImmutableList<String> workspaces;

    public ContentVersionViewJson( final ContentVersion contentVersion, final ContentPrincipalsResolver resolver,
                                   final List<String> workspaces )
    {
        super( contentVersion, resolver );
        this.workspaces = ImmutableList.copyOf( workspaces );
    }

    @SuppressWarnings("UnusedDeclaration")
    public ImmutableList<String> getWorkspaces()
    {
        return workspaces;
    }
}
