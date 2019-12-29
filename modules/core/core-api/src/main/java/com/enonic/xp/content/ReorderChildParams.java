package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ReorderChildParams
{
    private final ContentId contentToMove;

    private final ContentId contentToMoveBefore;

    private ReorderChildParams( final Builder builder )
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ReorderChildParams ) )
        {
            return false;
        }

        final ReorderChildParams that = (ReorderChildParams) o;
        return Objects.equals( contentToMove, that.contentToMove ) && Objects.equals( contentToMoveBefore, that.contentToMoveBefore );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentToMove, contentToMoveBefore );
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

        public ReorderChildParams build()
        {
            return new ReorderChildParams( this );
        }
    }
}
