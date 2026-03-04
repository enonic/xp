package com.enonic.xp.convert;

public interface Converter<T>
{
    Class<T> getType();

    T convert( Object value );
}
