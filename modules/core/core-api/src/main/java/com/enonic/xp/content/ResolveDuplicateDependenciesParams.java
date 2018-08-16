package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ResolveDuplicateDependenciesParams
{
    private final ContentIds contentIds;

    private final ContentIds excludeChildrenIds;


    private ResolveDuplicateDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
        excludeChildrenIds = builder.excludeChildrenIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludeChildrenIds;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludeChildrenIds( ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public ResolveDuplicateDependenciesParams build()
        {
            return new ResolveDuplicateDependenciesParams( this );
        }
    }
}
