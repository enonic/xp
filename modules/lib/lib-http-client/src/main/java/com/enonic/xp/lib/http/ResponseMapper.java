package com.enonic.xp.lib.http;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Response;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ResponseMapper
    implements MapSerializable
{
    private final static ImmutableSet<String> SKIP_HEADERS =
        ImmutableSet.of( "okhttp-received-millis", "okhttp-selected-protocol", "okhttp-sent-millis" );

    private final Response response;

    public ResponseMapper( final Response response )
    {
        this.response = response;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "status", this.response.code() );
        gen.value( "message", this.response.message() );
        gen.value( "body", getResponseBody() );
        gen.value( "contentType", this.response.header( "content-type" ) );
        serializeHeaders( "headers", gen, this.response.headers() );
    }

    private String getResponseBody()
    {
        try
        {
            return this.response.body().string();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    private void serializeHeaders( final String name, final MapGenerator gen, final Headers headers )
    {
        gen.map( name );
        for ( final String headerName : headers.names() )
        {
            if ( SKIP_HEADERS.contains( headerName.toLowerCase() ) )
            {
                continue;
            }
            gen.value( headerName, headers.get( headerName ) );
        }
        gen.end();
    }

}
