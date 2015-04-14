package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class ActiveContentVersionEntry
    implements Comparable<ActiveContentVersionEntry>
{
    private final Branch branch;

    private final ContentVersion contentVersion;

    public final static ActiveContentVersionEntry from( final Branch branch, final ContentVersion contentVersion )
    {
        return new ActiveContentVersionEntry( branch, contentVersion );
    }

    private ActiveContentVersionEntry( final Branch branch, final ContentVersion contentVersion )
    {
        this.branch = branch;
        this.contentVersion = contentVersion;
    }

    public ContentVersion getContentVersion()
    {
        return contentVersion;
    }

    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public int compareTo( final ActiveContentVersionEntry o )
    {
        if ( this.contentVersion.equals( o.contentVersion ) )
        {
            return this.branch.getName().compareTo( o.branch.getName() );
        }

        return this.contentVersion.compareTo( o.contentVersion );
    }

}
