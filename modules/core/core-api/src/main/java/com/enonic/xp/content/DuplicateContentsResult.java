package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class DuplicateContentsResult
{
    private final ContentIds duplicatedContents;

    private final String contentName;

    private DuplicateContentsResult( Builder builder )
    {
        this.duplicatedContents = ContentIds.from( builder.duplicatedContents );
        this.contentName = builder.contentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getDuplicatedContents()
    {
        return duplicatedContents;
    }

    public String getContentName()
    {
        return contentName;
    }

    public static final class Builder
    {
        private List<ContentId> duplicatedContents = Lists.newArrayList();

        private String contentName;

        private Builder()
        {
        }

        public Builder addDuplicated( final ContentId contentId )
        {
            this.duplicatedContents.add( contentId );
            return this;
        }

        public Builder addDuplicated( final ContentIds contentIds )
        {
            this.duplicatedContents.addAll( contentIds.getSet() );
            return this;
        }

        public Builder setContentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public DuplicateContentsResult build()
        {
            return new DuplicateContentsResult( this );
        }
    }
}
