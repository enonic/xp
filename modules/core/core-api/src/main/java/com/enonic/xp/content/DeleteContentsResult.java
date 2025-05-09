package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DeleteContentsResult
{
    private final ContentIds deletedContents;

    private final ContentIds unpublishedContents;

    private DeleteContentsResult( Builder builder )
    {
        this.deletedContents = builder.deletedContents.build();
        this.unpublishedContents = builder.unpublishedContents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getDeletedContents()
    {
        return deletedContents;
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public static final class Builder
    {
        private final ContentIds.Builder deletedContents = ContentIds.create();

        private final ContentIds.Builder unpublishedContents = ContentIds.create();

        private Builder()
        {
        }

        public Builder addDeleted( final ContentId deletedContent )
        {
            this.deletedContents.add( deletedContent );
            return this;
        }

        public Builder addDeleted( final ContentIds deletedContents )
        {
            this.deletedContents.addAll( deletedContents );
            return this;
        }

        public Builder addUnpublished( final ContentId unpublishedContent )
        {
            this.unpublishedContents.add( unpublishedContent );
            return this;
        }

        public Builder addUnpublished( final ContentIds unpublishedContents )
        {
            this.unpublishedContents.addAll( unpublishedContents );
            return this;
        }

        public DeleteContentsResult build()
        {
            return new DeleteContentsResult( this );
        }
    }
}
