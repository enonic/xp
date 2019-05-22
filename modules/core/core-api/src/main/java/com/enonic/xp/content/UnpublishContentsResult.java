package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class UnpublishContentsResult
{
    private final ContentIds unpublishedContents;

    private final ContentIds deletedContents;

    private final ContentPath contentPath;

    private UnpublishContentsResult( Builder builder )
    {
        this.unpublishedContents = ContentIds.from( builder.unpublishedContents );
        this.deletedContents = ContentIds.from( builder.deletedContents );
        this.contentPath = builder.contentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public ContentIds getDeletedContents()
    {
        return deletedContents;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public static final class Builder
    {
        private List<ContentId> unpublishedContents = Lists.newArrayList();

        private List<ContentId> deletedContents = Lists.newArrayList();

        private ContentPath contentPath;

        private Builder()
        {
        }

        public Builder addUnpublished( final ContentId contentId )
        {
            this.unpublishedContents.add( contentId );
            return this;
        }

        public Builder addUnpublished( final ContentIds contentIds )
        {
            this.unpublishedContents.addAll( contentIds.getSet() );
            return this;
        }

        public Builder addDeleted( final ContentId contentId )
        {
            this.deletedContents.add( contentId );
            return this;
        }

        public Builder addDeleted( final ContentIds contentIds )
        {
            this.deletedContents.addAll( contentIds.getSet() );
            return this;
        }

        public Builder setContentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public UnpublishContentsResult build()
        {
            return new UnpublishContentsResult( this );
        }
    }
}
