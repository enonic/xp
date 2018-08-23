package com.enonic.xp.content;

import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ResolveDuplicateDependenciesParams that = (ResolveDuplicateDependenciesParams) o;
        return Objects.equals( contentIds, that.contentIds ) && Objects.equals( excludeChildrenIds, that.excludeChildrenIds );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentIds, excludeChildrenIds );
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
