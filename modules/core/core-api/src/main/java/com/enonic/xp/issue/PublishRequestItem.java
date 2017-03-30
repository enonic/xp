package com.enonic.xp.issue;

import com.enonic.xp.content.ContentId;

public class PublishRequestItem
{
    private final ContentId id;

    private Boolean includeChildren;

    public ContentId getId()
    {
        return id;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    private PublishRequestItem( Builder builder )
    {
        this.id = builder.id;
        this.includeChildren = builder.includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId id;

        private Boolean includeChildren;

        public Builder()
        {
        }

        public Builder id( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren)
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public PublishRequestItem build()
        {
            return new PublishRequestItem( this );
        }
    }
}
