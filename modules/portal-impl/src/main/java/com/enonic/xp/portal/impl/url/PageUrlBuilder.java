package com.enonic.xp.portal.impl.url;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.xp.portal.url.PageUrlParams;

final class PageUrlBuilder
    extends PortalUrlBuilder<PageUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        final ContentPath resolved = resolvePath();
        appendPart( url, resolved.toString() );
    }

    private ContentPath resolvePath()
    {
        final ContentId id = this.params.getId();
        final ContentPath path = this.params.getPath();

        Preconditions.checkArgument( id != null || path != null, "Id or path is required" );
        if ( ( id == null ) && ( path != null ) )
        {
            return resolvePath( path );
        }

        return resolvePath( id );
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
        return ContentPath.from( this.context.getContentPath(), path );
    }
}
