package com.enonic.wem.api.schema;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public final class SchemaIcon
{
    private final byte[] iconData;

    private final String mimeType;

    private SchemaIcon( final byte[] iconData, final String mimeType )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( iconData, "iconData is mandatory" );
        Preconditions.checkArgument( iconData.length > 0, "iconData cannot be empty" );
        this.iconData = iconData;
        this.mimeType = mimeType;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof SchemaIcon ) )
        {
            return false;
        }

        final SchemaIcon that = (SchemaIcon) o;
        return Objects.equal( this.mimeType, that.mimeType ) && Arrays.equals( this.iconData, that.iconData );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( mimeType, Arrays.hashCode( iconData ) );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "mimeType", mimeType ).
            add( "iconData", iconData ).
            add( "size", getSize() ).
            toString();
    }

    public static SchemaIcon from( final byte[] iconData, final String mimeType )
    {
        return new SchemaIcon( iconData, mimeType );
    }

    public static SchemaIcon from( final InputStream dataStream, final String mimeType )
    {
        Preconditions.checkNotNull( dataStream, "dataStream is mandatory" );
        try (InputStream is = dataStream)
        {
            return new SchemaIcon( ByteStreams.toByteArray( is ), mimeType );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load icon from input stream", e );
        }
    }
}
