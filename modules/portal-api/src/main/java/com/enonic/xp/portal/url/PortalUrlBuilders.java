package com.enonic.xp.portal.url;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.xp.portal.PortalContext;

public final class PortalUrlBuilders
{
    private final PortalContext context;

    public PortalUrlBuilders( final PortalContext context )
    {
        this.context = context;
    }

    private ContentPath getContentPath()
    {
        return this.context.getContentPath();
    }

    private <T extends PortalUrlBuilder> T defaults( final T builder )
    {
        builder.baseUri( this.context.getBaseUri() );
        builder.workspace( this.context.getWorkspace() );
        builder.contentPath( getContentPath() );
        return builder;
    }

    public AttachmentUrlBuilder attachmentUrl()
    {
        return defaults( new AttachmentUrlBuilder() );
    }
}
