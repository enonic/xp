package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class GetPublishStatusesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private GetPublishStatusesParams( final ContentIds contentIds )
    {
        this.contentIds = contentIds;
        this.target = null;
    }

    @Deprecated
    public GetPublishStatusesParams( final ContentIds contentIds, final Branch target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static class Builder
    {

        private ContentIds contentIds;

        public Builder contentId( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public GetPublishStatusesParams build()
        {
            return new GetPublishStatusesParams( this.contentIds );
        }
    }
}
