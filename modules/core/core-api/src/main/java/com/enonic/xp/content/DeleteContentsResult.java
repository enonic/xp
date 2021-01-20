package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DeleteContentsResult
{
    private final ContentIds deletedContents;

    private final ContentIds pendingContents;

    private final ContentIds unpublishedContents;

    private DeleteContentsResult( Builder builder )
    {
        this.pendingContents = ContentIds.from( builder.pendingContents );
        this.deletedContents = ContentIds.from( builder.deletedContents );
        this.unpublishedContents = ContentIds.from( builder.unpublishedContents );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getPendingContents()
    {
        return pendingContents;
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
        private List<ContentId> pendingContents = new ArrayList<>();

        private List<ContentId> deletedContents = new ArrayList<>();

        private List<ContentId> unpublishedContents = new ArrayList<>();

        private Builder()
        {
        }

        public Builder addPending( final ContentId pendingContent )
        {
            this.pendingContents.add( pendingContent );
            return this;
        }

        public Builder addPending( final ContentIds pendingContents )
        {
            this.pendingContents.addAll( pendingContents.getSet() );
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