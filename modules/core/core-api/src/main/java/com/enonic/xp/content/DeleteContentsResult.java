package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DeleteContentsResult
{
    private final ContentIds deletedContents;

    private final ContentIds unpublishedContents;

    private DeleteContentsResult( Builder builder )
    {
        this.deletedContents = ContentIds.from( builder.deletedContents );
        this.unpublishedContents = ContentIds.from( builder.unpublishedContents );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
    public ContentIds getPendingContents()
    {
        return ContentIds.empty();
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
        private final List<ContentId> deletedContents = new ArrayList<>();

        private final List<ContentId> unpublishedContents = new ArrayList<>();

        private Builder()
        {
        }

        @Deprecated
        public Builder addPending( final ContentId pendingContent )
        {
            return this;
        }

        @Deprecated
        public Builder addPending( final ContentIds pendingContents )
        {
            return this;
        }

        public Builder addDeleted( final ContentId deletedContent )
        {
            this.deletedContents.add( deletedContent );
            return this;
        }

        public Builder addDeleted( final ContentIds deletedContents )
        {
            this.deletedContents.addAll( deletedContents.getSet() );
            return this;
        }

        public Builder addUnpublished( final ContentId unpublishedContent )
        {
            this.unpublishedContents.add( unpublishedContent );
            return this;
        }

        public Builder addUnpublished( final ContentIds unpublishedContents )
        {
            this.unpublishedContents.addAll( unpublishedContents.getSet() );
            return this;
        }

        public DeleteContentsResult build()
        {
            return new DeleteContentsResult( this );
        }
    }
}
