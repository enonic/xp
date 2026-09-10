package com.enonic.xp.portal.url;

/**
 * Thrown when a URL is asked for at a site - or project - that does not contain the content the
 * URL addresses. No such URL exists: the base URL of that site does not lead to the content, so
 * the URL is refused rather than assembled from a base and a path that do not belong together.
 */
public class ContentOutOfScopeException
    extends RuntimeException
{
    public ContentOutOfScopeException( final String message )
    {
        super( message );
    }
}
