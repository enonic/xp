package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.node.AttachedBinaries;

@Beta
public final class RepositoryAttachments
{
    private final AttachedBinaries attachedBinaries;

    private RepositoryAttachments( AttachedBinaries attachedBinaries )
    {
        this.attachedBinaries = attachedBinaries;
    }

    public AttachedBinaries getAttachedBinaries()
    {
        return attachedBinaries;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        RepositoryAttachments that = (RepositoryAttachments) o;
        return Objects.equals( attachedBinaries, that.attachedBinaries );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( attachedBinaries );
    }

    public static RepositoryAttachments from( AttachedBinaries attachedBinaries )
    {
        return new RepositoryAttachments( attachedBinaries );
    }

    public static RepositoryAttachments empty()
    {
        return new RepositoryAttachments( AttachedBinaries.empty() );
    }
}
