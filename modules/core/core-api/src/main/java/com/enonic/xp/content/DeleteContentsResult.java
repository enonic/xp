package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class DeleteContentsResult
{
    private final ContentIds deletedContents;

    private final ContentIds pendingContents;

    private final ContentIds failedContents;

    private DeleteContentsResult( Builder builder )
    {
        this.pendingContents = ContentIds.from( builder.pendingContents );
        this.deletedContents = ContentIds.from( builder.deletedContents );
        this.failedContents = ContentIds.from( builder.failedContents );
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

    public ContentIds getFailedContents()
    {
        return failedContents;
    }

    public static final class Builder
    {
        private List<ContentId> pendingContents = Lists.newArrayList();

        private List<ContentId> deletedContents = Lists.newArrayList();

        private List<ContentId> failedContents = Lists.newArrayList();

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

        public Builder addFailed( final ContentId failedContent )
        {
            this.failedContents.add( failedContent );
            return this;
        }

        public Builder addFailed( final ContentIds failedContents )
        {
            this.failedContents.addAll( failedContents.getSet() );
            return this;
        }

        public Builder merge( final DeleteContentsResult.Builder result )
        {
            this.pendingContents.addAll( result.pendingContents );
            this.deletedContents.addAll( result.deletedContents );
            this.failedContents.addAll( result.failedContents );

            return this;
        }

        public DeleteContentsResult build()
        {
            return new DeleteContentsResult( this );
        }
    }
}