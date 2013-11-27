package com.enonic.wem.api.blob;

public final class BlobKey
{
    private final static char[] HEX = "0123456789abcdef".toCharArray();

    private final String key;

    public BlobKey( final String key )
    {
        this.key = key;
    }

    public BlobKey( final byte[] key )
    {
        char[] buffer = new char[key.length * 2];
        for ( int i = 0; i < key.length; i++ )
        {
            buffer[2 * i] = HEX[( key[i] >> 4 ) & 0x0f];
            buffer[2 * i + 1] = HEX[key[i] & 0x0f];
        }

        this.key = new String( buffer );
    }

    public String toString()
    {
        return this.key;
    }

    public int hashCode()
    {
        return this.key.hashCode();
    }

    public boolean equals( final Object object )
    {
        return ( object instanceof BlobKey ) && this.key.equals( ( (BlobKey) object ).key );
    }
}
