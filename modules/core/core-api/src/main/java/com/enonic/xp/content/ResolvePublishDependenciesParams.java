package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ResolvePublishDependenciesParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeDescendantsOf;

    private ResolvePublishDependenciesParams( Builder builder )
    {
        contentIds = Objects.requireNonNullElse( builder.contentIds, ContentIds.empty() );
        excludeDescendantsOf = Objects.requireNonNullElse( builder.excludeDescendantsOf, ContentIds.empty() );
        excludedContentIds = Objects.requireNonNullElse( builder.excludedContentIds, ContentIds.empty() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludedContentIds()
    {
        return excludedContentIds;
    }

    public ContentIds getExcludeDescendantsOf()
    {
        return excludeDescendantsOf;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeDescendantsOf;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeDescendantsOf( ContentIds excludeDescendantsOf )
        {
            this.excludeDescendantsOf = excludeDescendantsOf;
            return this;
        }

        public ResolvePublishDependenciesParams build()
        {
            return new ResolvePublishDependenciesParams( this );
        }
    }
}
