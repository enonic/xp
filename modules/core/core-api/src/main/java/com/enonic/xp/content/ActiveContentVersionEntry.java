package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class ActiveContentVersionEntry
    implements Comparable<ActiveContentVersionEntry>
{
    private final Branch branch;

    private final ContentVersion contentVersion;

    public static ActiveContentVersionEntry from( final Branch branch, final ContentVersion contentVersion )
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
            return this.branch.getValue().compareTo( o.branch.getValue() );
        }

        return this.contentVersion.compareTo( o.contentVersion );
    }

}
