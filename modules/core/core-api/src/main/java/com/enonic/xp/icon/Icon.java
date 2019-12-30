package com.enonic.xp.icon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class Icon
{
    private final byte[] iconData;

    private final String mimeType;

    private final Instant modifiedTime;

    private Icon( final byte[] iconData, final String mimeType, final Instant modifiedTime )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( iconData, "iconData is mandatory" );
        Preconditions.checkArgument( iconData.length > 0, "iconData cannot be empty" );
        this.iconData = iconData;
        this.mimeType = mimeType;
        this.modifiedTime = modifiedTime;
    }

    public byte[] toByteArray()
    {
        return Arrays.copyOf( iconData, iconData.length );
    }

    public InputStream asInputStream()
    {
        return new ByteArrayInputStream( this.iconData );
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public int getSize()
    {
        return iconData.length;
    }

    public Instant getModifiedTime()
    {
        return this.modifiedTime;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Icon ) )
        {
            return false;
        }

        final Icon that = (Icon) o;
        return Objects.equals( this.mimeType, that.mimeType ) && Objects.equals( this.modifiedTime, that.modifiedTime ) &&
            Arrays.equals( this.iconData, that.iconData );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( mimeType, modifiedTime, Arrays.hashCode( iconData ) );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "mimeType", mimeType ).
            add( "iconData", iconData ).
            add( "size", getSize() ).
            add( "modifiedTime", modifiedTime ).
            toString();
    }

    public static Icon from( final byte[] iconData, final String mimeType, final Instant modifiedTime )
    {
        return new Icon( iconData, mimeType, modifiedTime );
    }

    public static Icon from( final InputStream dataStream, final String mimeType, final Instant modifiedTime )
    {
        Preconditions.checkNotNull( dataStream, "dataStream is mandatory" );
        try
        {
            return new Icon( ByteStreams.toByteArray( dataStream ), mimeType, modifiedTime );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load icon from input stream", e );
        }
    }
}
