package com.enonic.wem.portal.controller;

import java.util.Map;

import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.util.Series;

import com.google.gson.Gson;

public final class JsHttpResponseSerializer2
{
    private final JsHttpResponse from;

    public JsHttpResponseSerializer2( final JsHttpResponse from )
    {
        this.from = from;
    }

    public Representation serialize()
    {
        final Representation body = convert( this.from.getBody() );

        final MediaType type = MediaType.valueOf( this.from.getContentType() );
        body.setMediaType( type );

        return body;
    }

    public void serializeTo( final Response response )
    {
        response.setStatus( Status.valueOf( this.from.getStatus() ) );
        response.setEntity( serialize() );

        final Series<Header> headers = response.getHeaders();
        for ( final Map.Entry<String, String> header : this.from.getHeaders().entrySet() )
        {
            headers.set( header.getKey(), header.getValue() );
        }
    }

    private Representation convert( final Object value )
    {
        if ( value instanceof Map )
        {
            return new StringRepresentation( convertToJson( value ) );
        }

        if ( value instanceof byte[] )
        {
            return new ByteArrayRepresentation( (byte[]) value );
        }

        if ( value == null )
        {
            return new EmptyRepresentation();
        }

        return new StringRepresentation( value.toString() );
    }

    private String convertToJson( final Object value )
    {
        return new Gson().toJson( value );
    }
}
