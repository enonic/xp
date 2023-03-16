package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;

final class ContentResolver
{
    private PortalRequest portalRequest;

    private ContentId id;

    private ContentPath path;

    private ContentService contentService;

    public ContentResolver portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return this;
    }

    public ContentResolver id( final String value )
    {
        this.id = value != null ? ContentId.from( value ) : null;
        return this;
    }

    public ContentResolver path( final String value )
    {
        this.path = value != null ? ContentPath.from( value ) : null;
        return this;
    }

    public ContentResolver contentService( final ContentService value )
    {
        this.contentService = value;
        return this;
    }

    public Content resolve()
    {
        if ( this.id != null )
        {
            return this.contentService.getById( this.id );
        }

        if ( this.path == null )
        {
            return this.portalRequest.getContent();
        }

        final ContentPath contentPath;
        if ( this.path.isAbsolute() )
        {
            contentPath = this.path;
        }
        else
        {
            contentPath = ContentPath.from( this.portalRequest.getContentPath(), this.path );
        }

        return this.contentService.getByPath( contentPath );
    }
}
