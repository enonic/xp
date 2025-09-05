package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ReorderChildContentParams
{
    private final ContentId contentToMove;

    private final ContentId contentToMoveBefore;

    private ReorderChildContentParams( final Builder builder )
    {
        contentToMove = builder.contentToMove;
        contentToMoveBefore = builder.contentToMoveBefore;
    }

    public ContentId getContentToMove()
    {
        return contentToMove;
    }

    public ContentId getContentToMoveBefore()
    {
        return contentToMoveBefore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentId contentToMove;

        private ContentId contentToMoveBefore;

        private Builder()
        {
        }

        public Builder contentToMove( ContentId contentToMove )
        {
            this.contentToMove = contentToMove;
            return this;
        }

        public Builder contentToMoveBefore( ContentId contentToMoveBefore )
        {
            this.contentToMoveBefore = contentToMoveBefore;
            return this;
        }

        public ReorderChildContentParams build()
        {
            Objects.requireNonNull( contentToMove, "contentToMove is required" );
            Preconditions.checkArgument( !contentToMove.equals( contentToMoveBefore ),
                                         "contentToMove and contentToMoveBefore must be different" );
            return new ReorderChildContentParams( this );
        }
    }
}
