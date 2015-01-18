package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class ActiveContentVersionEntry
    implements Comparable<ActiveContentVersionEntry>
{
    private final Workspace workspace;

    private final ContentVersion contentVersion;

    public final static ActiveContentVersionEntry from( final Workspace workspace, final ContentVersion contentVersion )
    {
        return new ActiveContentVersionEntry( workspace, contentVersion );
    }

    private ActiveContentVersionEntry( final Workspace workspace, final ContentVersion contentVersion )
    {
        this.workspace = workspace;
        this.contentVersion = contentVersion;
    }

    public ContentVersion getContentVersion()
    {
        return contentVersion;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public int compareTo( final ActiveContentVersionEntry o )
    {
        if ( this.contentVersion.equals( o.contentVersion ) )
        {
            return this.workspace.getName().compareTo( o.workspace.getName() );
        }

        return this.contentVersion.compareTo( o.contentVersion );
    }

}
