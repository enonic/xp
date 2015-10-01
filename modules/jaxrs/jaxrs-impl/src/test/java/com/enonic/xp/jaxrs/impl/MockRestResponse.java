package com.enonic.xp.jaxrs.impl;

import com.google.common.base.Charsets;

public final class MockRestResponse
{
    private byte[] data;

    private int status;

    public byte[] getData()
    {
        return this.data;
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
}
