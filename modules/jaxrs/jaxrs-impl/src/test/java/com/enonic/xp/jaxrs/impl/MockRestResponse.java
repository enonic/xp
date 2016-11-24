package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.base.Charsets;

public final class MockRestResponse
{
    private byte[] data;

    private int status;

    private MultivaluedMap<String, Object> headers;

    public byte[] getData()
    {
        return this.data;
    }

    public String getDataAsString()
    {
        return new String( this.data, Charsets.UTF_8 );
    }

    public void setData( final byte[] data )
    {
        this.data = data;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus( final int status )
    {
        this.status = status;
    }

    public String getAsString()
    {
        return new String( this.data, Charsets.UTF_8 );
    }

    public String getHeader( final String name )
    {
        final Object value = this.headers.getFirst( name );
        return value != null ? value.toString() : null;
    }

    public void setHeaders( final MultivaluedMap<String, Object> headers )
    {
        this.headers = headers;
    }
}
