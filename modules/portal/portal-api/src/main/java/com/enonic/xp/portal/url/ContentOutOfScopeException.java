package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Thrown when the content a URL addresses lies outside the scope the URL is asked to belong to. A URL exists only for
 * a content at or below the content of its scope.
 *
 * @see PageUrlPartsParams.Builder#setScope(PortalScope)
 */
@NullMarked
public class ContentOutOfScopeException
    extends RuntimeException
{
    /**
     * @param message names the content and the scope it lies outside of
     */
    public ContentOutOfScopeException( final String message )
    {
        super( message );
    }
}
