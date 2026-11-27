package com.enonic.xp.portal.impl.idprovider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.jspecify.annotations.NullMarked;

/**
 * Adds the resource metadata URL to the bearer challenges of a response on a connector without
 * interactive login, where a 401 goes to the client as it is.
 */
@NullMarked
final class BearerChallengeResponseWrapper
    extends HttpServletResponseWrapper
{
    private final HttpServletRequest request;

    BearerChallengeResponseWrapper( final HttpServletRequest request, final HttpServletResponse response )
    {
        super( response );
        this.request = request;
    }

    @Override
    public void setHeader( final String name, final String value )
    {
        super.setHeader( name, challenge( name, value ) );
    }

    @Override
    public void addHeader( final String name, final String value )
    {
        super.addHeader( name, challenge( name, value ) );
    }

    private String challenge( final String name, final String value )
    {
        return BearerChallenge.HEADER.equalsIgnoreCase( name ) && BearerChallenge.isBearer( value )
            ? BearerChallenge.withResourceMetadata( value, request )
            : value;
    }
}
