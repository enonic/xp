package com.enonic.wem.core.content.datatype;


import org.joda.time.DateMidnight;

import com.google.common.base.Objects;

import com.enonic.wem.core.content.data.Data;

import com.enonic.cms.framework.blob.BlobKey;

public enum JavaType
{
    STRING( String.class ),
    BLOB( BlobKey[].class ),
    LONG( Long.class ),
    DOUBLE( Double.class ),
    DATE( DateMidnight.class ),
    DATA_SET( com.enonic.wem.core.content.data.DataSet.class );

    private Class value;

    private JavaType( final Class value )
    {
        this.value = value;
    }

    public Class getValue()
    {
        return value;
    }

    public String toString( Data data )
    {
        if ( data.getDataType().isConvertibleTo( STRING ) )
        {
            return data.getDataType().convertToString( data.getValue() );
        }

        return null;
    }

    public DateMidnight toDate( final Data data )
    {
        if ( data.getDataType().isConvertibleTo( DATE ) )
        {
            data.getDataType().convertToString( data.getValue() );
        }
        return null;
    }


    public boolean isInstance( final Object value )
    {
        return this.value.isInstance( value );
    }

    public static JavaType resolveType( Object o )
    {
        for ( JavaType type : values() )
        {
            if ( type.getValue().isInstance( o ) )
            {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "value", value );
        return s.toString();
    }
}
