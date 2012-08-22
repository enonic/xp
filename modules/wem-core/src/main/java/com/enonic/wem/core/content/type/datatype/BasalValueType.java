package com.enonic.wem.core.content.type.datatype;


import org.joda.time.DateMidnight;

public enum BasalValueType
{
    STRING( String.class ),
    DATE( DateMidnight.class );

    private Class javaType;

    private BasalValueType( final Class javaType )
    {
        this.javaType = javaType;
    }

    public Class getJavaType()
    {
        return javaType;
    }

    public static BasalValueType resolveType( Object o )
    {
        for ( BasalValueType type : values() )
        {
            if ( type.getJavaType().isInstance( o ) )
            {
                return type;
            }
        }
        return null;
    }
}
