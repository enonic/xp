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

    public boolean isInstance( final Object value )
    {
        return this.value.isInstance( value );
    }

    public boolean isConvertibleTo( final JavaType type )
    {
        if ( this == STRING )
        {
            return type == STRING || type == LONG || type == DOUBLE || type == DATE;
        }
        else if ( this == BLOB )
        {
            return type == BLOB;
        }
        else if ( this == LONG )
        {
            return type == LONG || type == STRING || type == DOUBLE || type == DATE;
        }
        else if ( this == DOUBLE )
        {
            return type == DOUBLE || type == STRING || type == LONG;
        }
        else if ( this == DATE )
        {
            return type == DATE || type == STRING || type == LONG;
        }
        return false;
    }

    public String toString( Data data )
    {
        final BaseDataType dataType = (BaseDataType) data.getDataType();
        if ( dataType.isConvertibleTo( STRING ) )
        {
            return dataType.convertToString( data.getValue() );
        }

        return null;
    }

    public DateMidnight toDate( final Data data )
    {
        final BaseDataType dataType = (BaseDataType) data.getDataType();
        if ( dataType.isConvertibleTo( DATE ) )
        {
            dataType.convertToString( data.getValue() );
        }
        return null;
    }


    public Double toDouble( final Data data )
    {
        final BaseDataType dataType = (BaseDataType) data.getDataType();
        if ( dataType.isConvertibleTo( DOUBLE ) )
        {
            return dataType.convertToDouble( data.getValue() );
        }
        return null;
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
