package com.enonic.xp.content;

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

    public void validate()
    {
        Preconditions.checkNotNull( ids, "ids must be specified" );
    }
}
