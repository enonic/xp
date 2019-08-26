package com.enonic.xp.admin.impl.json.content;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ContentVersion;

public class ContentVersionViewJson
    extends ContentVersionJson
{

    private ImmutableList<String> workspaces;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final ContentVersionViewJson that = (ContentVersionViewJson) o;
        return Objects.equals( workspaces, that.workspaces );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), workspaces );
    }
}
