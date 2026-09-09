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

    /**
     * @return the content path as it appears after the base URL of the anchor: relative to the
     * anchor when the content is inside it, and the full content path when the anchor is the
     * root of the project
     */
    static String relativeToAnchor( final ContentPath contentPath, final ContentPath anchorPath )
    {
        if ( anchorPath.isRoot() )
        {
            return contentPath.toString();
        }

        if ( contentPath.equals( anchorPath ) )
        {
            return "";
        }

        // a content outside the anchor cannot be addressed relative to it: its full path is kept
        return contentPath.isChildOf( anchorPath )
            ? contentPath.toString().substring( anchorPath.toString().length() )
            : contentPath.toString();
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
