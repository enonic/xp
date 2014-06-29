package com.enonic.wem.admin.rest.resource;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public final class RestRequestBuilder
{
    private WebResource resource;

    private byte[] entity;

    private String entityType;

    public RestRequestBuilder( final WebResource resource )
    {
        this.resource = resource;
    }

    public RestRequestBuilder path( final String path )
    {
        this.resource = this.resource.path( path );
        return this;
    }

    public RestRequestBuilder queryParam( final String name, final String key )
    {
        this.resource = this.resource.queryParam( name, key );
        return this;
    }

    public RestRequestBuilder entity( final String data, final MediaType type )
    {
        return entity( data.getBytes(), type );
    }

    public RestRequestBuilder entity( final byte[] data, final MediaType type )
    {
        this.entity = data;
        this.entityType = type.toString();
        return this;
    }

    public RestRequestBuilder multipart( final String name, final String fileName, final byte[] data, final MediaType type )
        throws Exception
    {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody( name, data, ContentType.create( type.toString() ), fileName );
        final HttpEntity entity = builder.build();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        entity.writeTo( out );

        this.entity = out.toByteArray();
        this.entityType = entity.getContentType().getValue();
        return this;
    }

    public MockRestResponse get()
        throws Exception
    {
        final ClientResponse response = this.resource.get( ClientResponse.class );
        return toResponse( response );
    }

    public MockRestResponse post()
        throws Exception
    {
        final ClientResponse response = this.resource.entity( this.entity, this.entityType ).post( ClientResponse.class );
        return toResponse( response );
    }

    private MockRestResponse toResponse( final ClientResponse from )
    {
        final MockRestResponse to = new MockRestResponse();
        to.setStatus( from.getStatus() );
        to.setData( from.getEntity( byte[].class ) );
        return to;
    }
}
