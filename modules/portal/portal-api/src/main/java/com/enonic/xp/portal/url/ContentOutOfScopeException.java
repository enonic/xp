package com.enonic.xp.portal.url;

/**
 * Thrown when a URL is asked for at a site - or project - that does not contain the content the
 * URL addresses. The base URL of that site does not lead to the content, so no such URL exists.
 */
public class ContentOutOfScopeException
    extends RuntimeException
{
    public ContentOutOfScopeException( final String message )
    {
        super( message );
    }
}
