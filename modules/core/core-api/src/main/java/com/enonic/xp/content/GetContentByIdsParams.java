package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetContentByIdsParams
{
    private final ContentIds ids;

    private boolean getChildrenIds = false;

    public GetContentByIdsParams( final ContentIds ids )
    {
        this.ids = ids;
    }

    public ContentIds getIds()
    {
        return this.ids;
    }

    public GetContentByIdsParams setGetChildrenIds( final boolean getChildrenIds )
    {
        this.getChildrenIds = getChildrenIds;
        return this;
    }

    public boolean doGetChildrenIds()
    {
        return getChildrenIds;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentByIdsParams ) )
        {
            return false;
        }

        final GetContentByIdsParams that = (GetContentByIdsParams) o;
        return Objects.equals( this.ids, that.ids );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.ids );
    }

    public void validate()
    {
        Preconditions.checkNotNull( ids, "ids must be specified" );
    }
}
