package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branches;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final Branches branches;

    private GetActiveContentVersionsParams( final Builder builder )
    {
        contentId = requireNonNull( builder.contentId, "contentId is required" );
        branches = requireNonNull( builder.branches, "branches is required" );
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable ContentId contentId;

        private @Nullable Branches branches;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public GetActiveContentVersionsParams build()
        {
            return new GetActiveContentVersionsParams( this );
        }
    }
}
