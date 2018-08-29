package com.enonic.xp.admin.impl.json.content;

import java.util.List;
import java.util.Objects;

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
