package com.enonic.xp.portal.impl.url;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.ContentService;
import com.enonic.xp.portal.PortalContext;

final class ContentIdResolver
{
    private PortalContext context;

    private ContentId id;

    private ContentPath path;

    private ContentService contentService;

    public ContentIdResolver context( final PortalContext context )
    {
        this.context = context;
        return this;
    }

    public ContentIdResolver id( final String value )
    {
        this.id = value != null ? ContentId.from( value ) : null;
        return this;
    }

    public ContentIdResolver path( final String value )
    {
        this.path = value != null ? ContentPath.from( value ) : null;
        return this;
    }

    public ContentIdResolver contentService( final ContentService value )
    {
        this.contentService = value;
        return this;
    }

    public ContentId resolve()
    {
        if ( this.id != null )
        {
            return this.id;
        }

        if ( this.path != null )
        {
            return resolveId( this.path );
        }

        return resolveId( ContentPath.from( "" ) );
    }

    private ContentPath resolvePath( final ContentPath path )
    {
        if ( path.isAbsolute() )
        {
            return path;
        }

        return ContentPath.from( this.context.getContentPath(), path );
    }

    private ContentId resolveId( final ContentPath path )
    {
        final ContentPath resolved = resolvePath( path );
        final Content content = this.contentService.getByPath( resolved );
        return content != null ? content.getId() : null;
    }
}
