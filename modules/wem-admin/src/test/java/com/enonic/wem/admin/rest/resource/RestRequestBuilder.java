package com.enonic.wem.admin.rest.resource;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public final class RestRequestBuilder
{
    private WebResource resource;

    private byte[] entity;

    private MediaType entityType;

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
        this.entityType = type;
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

    @SuppressWarnings("unchecked")
    private <T> T convert( final Class<T> type, final MockRestResponse response )
    {
        if ( type == String.class )
        {
            return (T) response.getAsString();
        }

        if ( type == byte[].class )
        {
            return (T) response.getData();
        }

        throw new IllegalArgumentException( "Type [" + type.getName() + "] not supported" );
    }

    public <T> T get( final Class<T> type )
        throws Exception
    {
        return convert( type, get() );
    }

    public <T> T post( final Class<T> type )
        throws Exception
    {
        return convert( type, post() );
    }

    private MockRestResponse toResponse( final ClientResponse from )
    {
        final MockRestResponse to = new MockRestResponse();
        to.setStatus( from.getStatus() );
        to.setData( from.getEntity( byte[].class ) );
        return to;
    }
}
