package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;

final class ContentPathResolver
{
    private PortalRequest portalRequest;

    private ContentId id;

    private ContentPath path;

    private ContentService contentService;

    public ContentPathResolver portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return this;
    }

    public ContentPathResolver id( final String value )
    {
        this.id = value != null ? ContentId.from( value ) : null;
        return this;
    }

    public ContentPathResolver path( final String value )
    {
        this.path = value != null ? ContentPath.from( value ) : null;
        return this;
    }

    public ContentPathResolver contentService( final ContentService value )
    {
        this.contentService = value;
        return this;
    }

    public ContentPath resolve()
    {
        if ( this.id != null )
        {
            return resolvePath( this.id );
        }

        if ( this.path != null )
        {
            return resolvePath( this.path );
        }

        return this.portalRequest.getContentPath();
    }

    private ContentPath resolvePath( final ContentId id )
    {
        final Content content = this.contentService.getById( id );
        return content.getPath();
    }

    private ContentPath resolvePath( final ContentPath path )
    {
        if ( path.isAbsolute() )
        {
            return path;
        }

        return ContentPath.from( this.portalRequest.getContentPath(), path );
    }
}
