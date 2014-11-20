package com.enonic.wem.script.internal.convert;

public interface Converter<T>
{
    public Class<T> getType();

    public T convert( Object value );
}
