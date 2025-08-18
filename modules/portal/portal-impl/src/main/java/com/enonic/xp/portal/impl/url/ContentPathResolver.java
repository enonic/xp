package com.enonic.xp.portal.impl.url;

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
            return this.contentService.getById( this.id ).getPath();
        }

        if ( path == null )
        {
            return this.portalRequest.getContentPath();
        }

        return this.path.isAbsolute()
            ? this.path
            : ContentPath.create().addAll( this.portalRequest.getContentPath() ).addAll( this.path ).build();
    }
}
