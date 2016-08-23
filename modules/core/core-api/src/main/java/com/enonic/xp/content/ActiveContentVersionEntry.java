package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchId;

@Beta
public class ActiveContentVersionEntry
    implements Comparable<ActiveContentVersionEntry>
{
    private final BranchId branchId;

    private final ContentVersion contentVersion;

    public final static ActiveContentVersionEntry from( final BranchId branchId, final ContentVersion contentVersion )
    {
        return new ActiveContentVersionEntry( branchId, contentVersion );
    }

    private ActiveContentVersionEntry( final BranchId branchId, final ContentVersion contentVersion )
    {
        this.branchId = branchId;
        this.contentVersion = contentVersion;
    }

    public ContentVersion getContentVersion()
    {
        return contentVersion;
    }

    public BranchId getBranchId()
    {
        return branchId;
    }

    @Override
    public int compareTo( final ActiveContentVersionEntry o )
    {
        if ( this.contentVersion.equals( o.contentVersion ) )
        {
            return this.branchId.getValue().compareTo( o.branchId.getValue() );
        }

        return this.contentVersion.compareTo( o.contentVersion );
    }

}
