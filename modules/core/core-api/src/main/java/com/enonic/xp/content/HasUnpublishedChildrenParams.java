package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class HasUnpublishedChildrenParams
{
    private final ContentId contentId;

    private HasUnpublishedChildrenParams( final ContentId contentId )
    {
        this.contentId = contentId;
    }

    @Deprecated
    public HasUnpublishedChildrenParams( final ContentId contentId, final Branch target )
    {
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Deprecated
    public Branch getTarget()
    {
        return ContentConstants.BRANCH_MASTER;
    }

    @Deprecated
    public void validate()
    {
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId contentId;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public HasUnpublishedChildrenParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new HasUnpublishedChildrenParams( this.contentId );
        }
    }
}
