package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.security.PrincipalKey;

public class ContentVersionPublishInfo
{
    private final PrincipalKey publisher;

    private final Instant timestamp;

    private final String message;

    private final ContentPublishInfo contentPublishInfo;

    private ContentVersionPublishInfo( Builder builder )
    {
        publisher = builder.publisher;
        timestamp = builder.timestamp;
        message = builder.message;
        contentPublishInfo = builder.contentPublishInfo;
    }

    public PrincipalKey getPublisher()
    {
        return publisher;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getMessage()
    {
        return message;
    }

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ContentVersionPublishInfo that = (ContentVersionPublishInfo) o;
        return Objects.equals( publisher, that.publisher ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( message, that.message ) && Objects.equals( contentPublishInfo, that.contentPublishInfo );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( publisher, timestamp, message, contentPublishInfo );
    }

    public static class Builder
    {
        private PrincipalKey publisher;

        private Instant timestamp;

        private String message;

        private ContentPublishInfo contentPublishInfo;

        public Builder publisher( final PrincipalKey publisher )
        {
            this.publisher = publisher;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder contentPublishInfo( final ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        public ContentVersionPublishInfo build()
        {
            return new ContentVersionPublishInfo( this );
        }
    }
}
