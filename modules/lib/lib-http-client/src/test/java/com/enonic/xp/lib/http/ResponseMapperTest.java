package com.enonic.xp.lib.http;

import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.enonic.xp.testing.JsonAssert;

public class ResponseMapperTest
{
    @Test
    public void serialize()
        throws Exception
    {
        final Request.Builder request = new Request.Builder();
        request.url( "http://host/some/path" );
        request.get();

        final Response.Builder response = new Response.Builder();
        final ResponseBody body = ResponseBody.create( MediaType.parse( "application/json" ), "{\"ok\": true}" );
        response.body( body );
        response.code( 200 );
        response.message( "Ok" );
        response.protocol( Protocol.HTTP_1_1 );
        response.request( request.build() );
        response.header( "Content-Type", "application/json" );
        ResponseMapper mapper = new ResponseMapper( response.build() );

        JsonAssert.assertJson( getClass(), "response", mapper );
    }

    @Test
    public void serializeNoContentType()
        throws Exception
    {
        final Request.Builder request = new Request.Builder();
        request.url( "http://host/some/path" );
        request.get();

        final Response.Builder response = new Response.Builder();
        final ResponseBody body = ResponseBody.create( MediaType.parse( "application/json" ), "{\"ok\": true}" );
        response.body( body );
        response.code( 200 );
        response.message( "Ok" );
        response.protocol( Protocol.HTTP_1_1 );
        response.request( request.build() );
        ResponseMapper mapper = new ResponseMapper( response.build() );

        JsonAssert.assertJson( getClass(), "response-no-type", mapper );
    }
}