package com.enonic.wem.api;

import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Icon
{
    private final byte[] iconData;

    private final String mimeType;

    private Icon( final byte[] iconData, final String mimeType )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( iconData, "iconData is mandatory" );
        Preconditions.checkArgument( iconData.length > 0, "iconData cannot be empty" );
        this.iconData = iconData;
        this.mimeType = mimeType;
    }

    public byte[] getData()
    {
        return iconData;
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

        if ( !( o instanceof Icon ) )
        {
            return false;
        }

        final Icon that = (Icon) o;
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

    public static Icon from( final byte[] iconData, final String mimeType )
    {
        return new Icon( iconData, mimeType );
    }

    public static Icon copyOf( final Icon icon )
    {
        return new Icon( Arrays.copyOf( icon.iconData, icon.iconData.length ), icon.mimeType );
    }
}
