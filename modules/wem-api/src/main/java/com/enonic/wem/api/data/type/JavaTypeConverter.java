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

    public abstract T convertFrom( Object value );

    public abstract T convertFromString( String value );

    @Override
    public final String toString()
    {
        return this.type.getSimpleName();
    }
}
