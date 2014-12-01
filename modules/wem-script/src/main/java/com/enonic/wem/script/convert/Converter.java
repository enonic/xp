package com.enonic.wem.script.convert;

public interface Converter<T>
{
    public Class<T> getType();

    public T convert( Object value );
}
