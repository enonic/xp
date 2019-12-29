package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DuplicateContentsResult
{
    private final ContentIds duplicatedContents;

    private final String contentName;

    private final ContentPath contentPath;

    private DuplicateContentsResult( Builder builder )
    {
        this.duplicatedContents = ContentIds.from( builder.duplicatedContents );
        this.contentName = builder.contentName;
        this.contentPath = builder.contentPath;
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

    public ContentPath getSourceContentPath()
    {
        return contentPath;
    }

    public static final class Builder
    {
        private List<ContentId> duplicatedContents = new ArrayList<>();

        private String contentName;

        private ContentPath contentPath;

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

        public Builder setSourceContentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public DuplicateContentsResult build()
        {
            return new DuplicateContentsResult( this );
        }
    }
}
