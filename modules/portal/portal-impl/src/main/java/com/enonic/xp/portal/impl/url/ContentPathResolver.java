package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ContentOutOfScopeException;

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
     * @return the content path as it appears after the base URL of the level it belongs to:
     * relative to that level, empty when the content is the level itself, and the full content
     * path when the level is the root of the project
     * @throws ContentOutOfScopeException if the content is outside the level
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

        if ( !contentPath.isChildOf( anchorPath ) )
        {
            // the base URL of the level does not lead to this content, so no URL can be built:
            // appending the content path to that base would address something that is not there
            throw new ContentOutOfScopeException(
                String.format( "Content [%s] is not inside [%s]", contentPath, anchorPath ) );
        }

        return contentPath.toString().substring( anchorPath.toString().length() );
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
