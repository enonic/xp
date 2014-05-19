package com.enonic.wem.portal.jaxrs;

import java.util.Set;

import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableSet;

public enum ExtendedStatus
    implements Response.StatusType
{
    METHOD_NOT_ALLOWED( 405, "Method not allowed", Response.Status.Family.CLIENT_ERROR );

    private final int code;

    private final String reason;

    private final Response.Status.Family family;

    private ExtendedStatus( final int code, final String reason, final Response.Status.Family family )
    {
        this.code = code;
        this.reason = reason;
        this.family = family;
    }

    @Override
    public int getStatusCode()
    {
        return this.code;
    }

    @Override
    public Response.Status.Family getFamily()
    {
        return this.family;
    }

    @Override
    public String getReasonPhrase()
    {
        return this.reason;
    }

    @Override
    public String toString()
    {
        return this.reason;
    }

    public static Set<Response.StatusType> all()
    {
        final ImmutableSet.Builder<Response.StatusType> builder = ImmutableSet.builder();
        builder.add( values() );
        builder.add( Response.Status.values() );
        return builder.build();
    }

    public static Response.StatusType fromCode( final int code )
    {
        for ( final Response.StatusType status : all() )
        {
            if ( status.getStatusCode() == code )
            {
                return status;
            }
        }

        return null;
    }
}
