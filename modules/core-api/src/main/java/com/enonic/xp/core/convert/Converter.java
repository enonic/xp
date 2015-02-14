package com.enonic.xp.core.convert;

public interface Converter<T>
{
    public Class<T> getType();

    public T convert( Object value );
}
