package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.security.Principal;

public class ContentVersionPublishInfoJson
{
    private final String publisher;

    private final Instant timestamp;

    private final String message;

    private final String publisherDisplayName;

    public ContentVersionPublishInfoJson( final ContentVersionPublishInfo publishInfo, final ContentPrincipalsResolver principalsResolver )
    {
        this.timestamp = publishInfo.getTimestamp();
        this.message = publishInfo.getMessage();

        final Principal publisher = principalsResolver.findPrincipal( publishInfo.getPublisher() );

        this.publisher = publishInfo.getPublisher().toString();
        this.publisherDisplayName = publisher != null ? publisher.getDisplayName() : "";
    }

    public String getPublisher()
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

    public String getPublisherDisplayName()
    {
        return publisherDisplayName;
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
        final ContentVersionPublishInfoJson that = (ContentVersionPublishInfoJson) o;
        return Objects.equals( publisher, that.publisher ) && Objects.equals( timestamp, that.timestamp ) &&
            Objects.equals( message, that.message ) && Objects.equals( publisherDisplayName, that.publisherDisplayName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( publisher, timestamp, message, publisherDisplayName );
    }
}
