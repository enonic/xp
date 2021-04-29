package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

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
}
