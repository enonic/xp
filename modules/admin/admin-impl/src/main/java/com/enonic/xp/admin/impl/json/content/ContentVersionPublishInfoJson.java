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

    private final ContentPublishInfoJson contentPublishInfo;

    public ContentVersionPublishInfoJson( final ContentVersionPublishInfo versionPublishInfo,
                                          final ContentPrincipalsResolver principalsResolver )
    {
        this.timestamp = versionPublishInfo.getTimestamp();
        this.message = versionPublishInfo.getMessage();

        final Principal publisher = principalsResolver.findPrincipal( versionPublishInfo.getPublisher() );

        this.publisher = versionPublishInfo.getPublisher().toString();
        this.publisherDisplayName = publisher != null ? publisher.getDisplayName() : "";

        this.contentPublishInfo = versionPublishInfo.getContentPublishInfo() != null
            ? new ContentPublishInfoJson( versionPublishInfo.getContentPublishInfo() )
            : null;
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

    public ContentPublishInfoJson getContentPublishInfo()
    {
        return contentPublishInfo;
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
            Objects.equals( message, that.message ) && Objects.equals( publisherDisplayName, that.publisherDisplayName ) &&
            Objects.equals( contentPublishInfo, that.contentPublishInfo );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( publisher, timestamp, message, publisherDisplayName, contentPublishInfo );
    }
}
