package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

@PublicApi
public final class ReprocessContentParams
{
    private final ContentId contentId;

    private ReprocessContentParams( Builder builder )
    {
        contentId = requireNonNull( builder.contentId );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ReprocessContentParams ) )
        {
            return false;
        }
        final ReprocessContentParams that = (ReprocessContentParams) o;
        return Objects.equals( this.contentId, that.contentId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.contentId );
    }

    public static final class Builder
    {
        private ContentId contentId;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public ReprocessContentParams build()
        {
            return new ReprocessContentParams( this );
        }
    }
}
