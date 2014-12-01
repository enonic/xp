package com.enonic.wem.api.convert;

public interface Converter<T>
{
    public Class<T> getType();

    public T convert( Object value );
}
