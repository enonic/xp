package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Thrown when the content a URL addresses lies outside the site - or project - the URL is asked
 * to belong to. A URL exists only for a content inside the site or project it belongs to.
 *
 * @see PageUrlPartsParams.Builder#setBase(UrlBase)
 */
@NullMarked
public class ContentOutOfScopeException
    extends RuntimeException
{
    /**
     * @param message names the content and the site or project it lies outside of
     */
    public ContentOutOfScopeException( final String message )
    {
        super( message );
    }
}
