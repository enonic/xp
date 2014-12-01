package com.enonic.wem.api.content;

import com.google.common.base.Objects;

import com.enonic.wem.api.event.Event;

public final class ContentChildOrderUpdatedEvent
    implements Event
{
    private final ContentId contentId;

    public ContentChildOrderUpdatedEvent( final ContentId contentId )
    {
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "contentId", this.contentId ).
            omitNullValues().
            toString();
    }

}
