package com.enonic.wem.api.data.type;

abstract class JavaTypeConverter<T>
{
    private final Class<T> type;

    public JavaTypeConverter( final Class<T> type )
    {
        this.type = type;
    }

    public final Class<T> getType()
    {
        return this.type;
    }

    public final boolean isInstance( final Object value )
    {
        return this.type.isInstance( value );
    }

    public abstract T convertFrom( Object value );

    public abstract T convertFromString( java.lang.String value );

    @Override
    public final java.lang.String toString()
    {
        return this.type.getSimpleName();
    }
}
