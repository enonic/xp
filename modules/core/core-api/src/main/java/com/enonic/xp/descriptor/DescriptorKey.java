package com.enonic.xp.descriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.NameValidator;

@PublicApi
@NullMarked
public final class DescriptorKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    /**
     * DescriptorKey name validator.
     * Must be a valid file because name-descriptors are usually a file on a file system.
     * HTML illegal characters historically prohibited.
     * Length is limited to 63 to align with other systems limits: Database identifiers, etc...
     */
    private static final NameValidator DESCRIPTOR_NAME = NameValidator.NAME.extend( DescriptorKey.class )
        .maxLength( 63 )
        .invalidChars( NameValidator.HTML_SPECIAL_CHARACTERS + NameValidator.FILENAME_ILLEGAL_CHARACTERS  + " " )
        .build();

    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private DescriptorKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = Objects.requireNonNull( applicationKey );
        this.name = Objects.requireNonNull( name );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final DescriptorKey that = (DescriptorKey) o;
        return applicationKey.equals( that.applicationKey ) && name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, name );
    }

    @Override
    public String toString()
    {
        return applicationKey + SEPARATOR + name;
    }

    public static DescriptorKey from( final String key )
    {
        Objects.requireNonNull( key, "DescriptorKey cannot be null" );
        final int index = key.indexOf( SEPARATOR );
        if ( index == -1 )
        {
            throw new IllegalArgumentException( "DescriptorKey must contain application key and descriptor name" );
        }
        final String applicationKey = key.substring( 0, index );
        final String descriptorName = key.substring( index + 1 );
        return from( ApplicationKey.from( applicationKey ), descriptorName );
    }

    public static DescriptorKey from( final ApplicationKey applicationKey, final String name )
    {
        return new DescriptorKey( applicationKey, DESCRIPTOR_NAME.withSubject( "DescriptorKey name" ).validate( name  ) );
    }
}
