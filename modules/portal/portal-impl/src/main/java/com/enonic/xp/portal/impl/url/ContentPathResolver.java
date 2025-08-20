package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;

final class ContentPathResolver
{
    private PortalRequest portalRequest;

    private String id;

    private String path;

    private ContentService contentService;

    public ContentPathResolver portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return this;
    }

    public ContentPathResolver id( final String value )
    {
        this.id = value;
        return this;
    }

    public ContentPathResolver path( final String value )
    {
        this.path = value;
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
            return this.contentService.getById( ContentId.from( this.id ) ).getPath();
        }

        if ( path == null )
        {
            return this.portalRequest.getContentPath();
        }

        if ( path.startsWith( "/" ) )
        {
            return ContentPath.from( path );
        }
        else
        {
            return ContentPath.create().addAll( this.portalRequest.getContentPath() ).addAll( ContentPath.from( path ) ).build();
        }
    }
}
