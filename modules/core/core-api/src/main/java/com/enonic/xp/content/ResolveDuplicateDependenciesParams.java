package com.enonic.xp.content;

import java.util.Map;

import com.google.common.annotations.Beta;

@Beta
public class ResolveDuplicateDependenciesParams
{
    private final Map<ContentId, ContentPath> contentIds;

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

    public Map<ContentId, ContentPath> getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public static final class Builder
    {
        private Map<ContentId, ContentPath> contentIds;

        private ContentIds excludeChildrenIds;

        private Builder()
        {
        }

        public Builder contentIds( Map<ContentId, ContentPath> contentIds )
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
